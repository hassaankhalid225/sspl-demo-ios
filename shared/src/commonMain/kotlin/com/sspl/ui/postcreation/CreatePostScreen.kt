package com.sspl.ui.postcreation

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SheetValue
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.preat.peekaboo.image.picker.SelectionMode
import com.preat.peekaboo.image.picker.rememberImagePickerLauncher
import com.sspl.platform.toBitmap
import com.sspl.resources.Res
import com.sspl.resources.add_first_image
import com.sspl.resources.add_second_image
import com.sspl.resources.camera
import com.sspl.resources.camera_ic
import com.sspl.resources.camera_permission_required
import com.sspl.resources.dismiss
import com.sspl.resources.gallery
import com.sspl.resources.gallery_ic
import com.sspl.resources.go_to_settings
import com.sspl.resources.image_source_hint
import com.sspl.resources.max_images_reached
import com.sspl.resources.open_setting
import com.sspl.resources.post
import com.sspl.resources.warning
import com.sspl.resources.whats_on_your_mind
import com.sspl.theme.primary
import com.sspl.ui.components.AppTextLabel
import com.sspl.ui.components.AppTextSubTitle
import com.sspl.ui.components.BetterModalBottomSheet
import com.sspl.ui.components.ButtonWithProgress
 import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel
import com.sspl.platform.shared.PermissionCallback
import shared.PermissionStatus
import shared.PermissionType
import com.sspl.platform.shared.createPermissionsManager
import shared.rememberCameraManager

@Composable
fun CreatePostScreen(
    navController: NavController,
    viewModel: PostCreationViewModel = koinViewModel(),
    modifier: Modifier = Modifier
) {
    val scope = rememberCoroutineScope()

    var launchCamera by remember { mutableStateOf(value = false) }
    var launchSetting by remember { mutableStateOf(value = false) }
    var permissionRationalDialog by remember { mutableStateOf(value = false) }
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val permissionsManager = createPermissionsManager(object : PermissionCallback {
        override fun onPermissionStatus(
            permissionType: PermissionType, status: PermissionStatus
        ) {
            when (status) {
                PermissionStatus.GRANTED -> {
                    when (permissionType) {
                        PermissionType.CAMERA -> launchCamera = true
                        else -> Unit
                    }
                }

                else -> {
                    permissionRationalDialog = true
                }
            }
        }
    })
    val cameraManager = rememberCameraManager {
        scope.launch {
            withContext(Dispatchers.Default) {
                it?.let { img ->
                    viewModel.onEvent(CreatePostUIEvents.OnImageLoading(true))
                    img.toByteArray()?.let { asByteArray ->
                        viewModel.onEvent(CreatePostUIEvents.OnImageAdd(listOf(asByteArray)))
                    }
                }
            }
        }
    }

    if (launchCamera) {
        if (permissionsManager.isPermissionGranted(PermissionType.CAMERA)) {
            cameraManager.launch()
        } else {
            permissionsManager.askPermission(PermissionType.CAMERA)
        }
        launchCamera = false
    }
    if (launchSetting) {
        permissionsManager.launchSettings()
        launchSetting = false
    }
    if (permissionRationalDialog) {
        CameraPermissionDeniedDialog(onDismiss = { permissionRationalDialog = false },
            onOpenSettings = {
                permissionRationalDialog = false
                launchSetting = true
            })
    }


    val imagePicker =
        rememberImagePickerLauncher(selectionMode = if (uiState.images.isEmpty()) SelectionMode.Multiple(
            maxSelection = 2
        ) else SelectionMode.Single, scope = scope, onResult = { byteArrays ->
            byteArrays.forEach { image ->
                viewModel.onEvent(CreatePostUIEvents.OnImageAdd(listOf(image)))
            }
        })
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        CreatePostContent(
            uiState = uiState, onEvent = viewModel::onEvent, onAddImage = {
                viewModel.onEvent(CreatePostUIEvents.ToggleBottomSheet(true))
            }, modifier = modifier.alpha(
                if (uiState.isLoadingImage) 0.5f else 1f
            )
        )
        if (uiState.isLoadingImage) {
            CircularProgressIndicator()
        }
    }

    AnimatedVisibility(uiState.showChooserBottomSheet) {
        ChooserBottomSheet(onCameraSelected = {
            viewModel.onEvent(CreatePostUIEvents.ToggleBottomSheet(false))
            launchCamera = true
        }, onGallerySelected = {
            viewModel.onEvent(CreatePostUIEvents.ToggleBottomSheet(false))
            imagePicker.launch()
        }, onDismiss = {
            viewModel.onEvent(CreatePostUIEvents.ToggleBottomSheet(false))
        })
    }

}

@Composable
fun CreatePostContent(
    uiState: PostCreationUiState,
    onEvent: (CreatePostUIEvents) -> Unit,
    onAddImage: () -> Unit,
    modifier: Modifier = Modifier
) {
    val keyboardController = LocalSoftwareKeyboardController.current
    Column(
        modifier = modifier.fillMaxSize().background(Color.White).padding(16.dp)
            .pointerInput(Unit) {
                detectTapGestures { keyboardController?.hide() }
            }, horizontalAlignment = Alignment.CenterHorizontally
    ) {
        DescriptionTextEditor(text = uiState.description, modifier = Modifier.fillMaxWidth().heightIn(min = 160.dp, max = 300.dp).padding(10.dp),
            onTextChanged = { onEvent(CreatePostUIEvents.OnDescriptionChange(it)) })
        Spacer(Modifier.height(20.dp))
        AddImages(images = uiState.images,
            onAddImageClick = onAddImage,
            onDeleteImageClick = { onEvent(CreatePostUIEvents.OnImageDelete(it)) })
        Spacer(Modifier.weight(0.15f))
        ButtonWithProgress(
            onClick = { onEvent(CreatePostUIEvents.UploadPost) },
            isEnabled = uiState.isUploadEnabled,
            isLoading = uiState.isLoading,
            buttonText = stringResource(Res.string.post),
            modifier = Modifier.imePadding()
        )
    }
}

@Composable
fun DescriptionTextEditor(
    modifier: Modifier = Modifier, shape: RoundedCornerShape= RoundedCornerShape(16.dp), placeholderText:String=stringResource(Res.string.whats_on_your_mind), text: String, onTextChanged: (String) -> Unit
) {
    Card(
        colors = CardDefaults.cardColors(
            containerColor = Color.LightGray.copy(alpha = 0.2f)
        ),
        shape = shape,
        border = BorderStroke(0.7.dp, Color.LightGray),
        modifier = Modifier.imePadding()
    ) {
        TextField(
            value = text,
            onValueChange = {
                onTextChanged(it)
            },
            modifier = modifier,
            placeholder = {
                AppTextLabel(
                    placeholderText, color = Color.Gray
                )
            },
            textStyle = TextStyle(fontSize = 18.sp, color = Color.Black, textDecoration = null),
            colors = TextFieldDefaults.colors(
                focusedContainerColor = Color.Transparent,
                unfocusedContainerColor = Color.Transparent,
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
                disabledIndicatorColor = Color.Transparent
            ),
            shape = RoundedCornerShape(16.dp)
        )
    }
}

@Composable
fun AddImages(
    images: List<ByteArray>, onAddImageClick: () -> Unit, onDeleteImageClick: (Int) -> Unit
) {
    val imageCount = images.size
    Card(
        modifier = Modifier.fillMaxWidth().height(150.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.LightGray.copy(alpha = 0.2f)
        ),
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(0.7.dp, Color.LightGray),
    ) {
        Box(modifier = Modifier.padding(8.dp)) {
            when (imageCount) {
                0 -> ImageAddState(onAddImageClick, imageCount = 0)
                1 -> OneImageWithAddState(images.first(), onAddImageClick, onDeleteImageClick)
                2 -> TwoImagesState(images, onDeleteImageClick)
                else -> ImageAddState(onAddImageClick, imageCount = 2)
            }
        }
    }
}

@Composable
fun ImageAddState(onAddImageClick: () -> Unit, imageCount: Int) {
    Column(
        modifier = Modifier.fillMaxSize().clickable { onAddImageClick() },
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = Icons.Default.Add,
            contentDescription = null,
            modifier = Modifier.size(30.dp)
        )
        Spacer(Modifier.height(8.dp))
        Text(
            text = stringResource(
                when (imageCount) {
                    0 -> Res.string.add_first_image
                    1 -> Res.string.add_second_image
                    else -> Res.string.max_images_reached
                }
            ), textAlign = TextAlign.Center
        )
        Text(
            text = stringResource(Res.string.image_source_hint),
            style = TextStyle(fontSize = 14.sp, color = Color.Gray),
            textAlign = TextAlign.Center
        )
    }
}


@Composable
fun OneImageWithAddState(
    image: ByteArray, onAddImageClick: () -> Unit, onDeleteImageClick: (Int) -> Unit
) {
    Row(Modifier.fillMaxSize()) {
        // Display the first image with delete option
        Box(
            modifier = Modifier.weight(1f).fillMaxHeight().padding(end = 4.dp)
        ) {
            RoundedImageView(
                image = image, modifier = Modifier.fillMaxSize()
            )
            // Add delete button on top of the image
            DeleteImageButton(modifier = Modifier.align(Alignment.TopEnd),
                onDeleteImageClick = { onDeleteImageClick(0) })
        }

        // Show the "Add second image" option
        Box(
            modifier = Modifier.weight(1f).fillMaxHeight().clickable { onAddImageClick() },
            contentAlignment = Alignment.Center
        ) {
            ImageAddState(onAddImageClick, imageCount = 1)
        }
    }
}

@Composable
fun TwoImagesState(images: List<ByteArray>, onDeleteImageClick: (Int) -> Unit) {
    Row(Modifier.fillMaxSize()) {
        images.take(2).forEachIndexed { index, image ->
            Box(
                modifier = Modifier.weight(1f).fillMaxHeight().padding(4.dp)
            ) {
                RoundedImageView(image = image, modifier = Modifier.fillMaxSize())
                // Add delete button on top of each image
                DeleteImageButton(modifier = Modifier.align(Alignment.TopEnd),
                    onDeleteImageClick = { onDeleteImageClick(index) })
            }
        }
    }
}

@Composable
fun DeleteImageButton(modifier: Modifier = Modifier, onDeleteImageClick: () -> Unit) {
    Icon(imageVector = Icons.Outlined.Delete,
        contentDescription = "Delete",
        tint = primary,
        modifier = modifier.padding(6.dp).size(30.dp)
            .clickable { onDeleteImageClick() } // Make the Icon clickable
            .background(Color.White.copy(alpha = 0.7f), shape = CircleShape)
            .padding(5.dp) // Optional background for better visibility
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChooserBottomSheet(
    modifier: Modifier = Modifier,
    onCameraSelected: () -> Unit,
    onGallerySelected: () -> Unit,
    onDismiss: () -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true,
        confirmValueChange = { it != SheetValue.Hidden })
    val scope = rememberCoroutineScope()

    BetterModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = Color.White,
        modifier = modifier
    ) {

        Row(modifier.padding(16.dp)) {
            // Gallery Column
            Column(
                modifier = Modifier.padding(16.dp).clickable {
                    scope.launch { sheetState.hide() }
                    onGallerySelected()
                }, verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Image(
                    painter = painterResource(Res.drawable.gallery_ic),
                    contentDescription = stringResource(Res.string.gallery),
                    modifier = Modifier.size(48.dp) // Adjust size if necessary
                )
                AppTextSubTitle(stringResource(Res.string.gallery))
            }

            Spacer(Modifier.width(16.dp))

            // Camera Column
            Column(
                modifier = Modifier.padding(16.dp).clickable {
                    scope.launch { sheetState.hide() }
                    onCameraSelected()

                }, verticalArrangement = Arrangement.spacedBy(6.dp)

            ) {
                Image(
                    painter = painterResource(Res.drawable.camera_ic),
                    contentDescription = stringResource(Res.string.camera),
                    modifier = Modifier.size(48.dp) // Adjust size if necessary
                )
                AppTextSubTitle(stringResource(Res.string.camera))
            }
        }
    }
}


@Composable
fun RoundedImageView(
    modifier: Modifier = Modifier,
    image: ByteArray,
    cornerRadius: Int = 12,
    contentScale: ContentScale = ContentScale.Crop
) {
    Image(
        bitmap = image.toBitmap(),
        contentDescription = null,
        contentScale = contentScale,
        modifier = modifier.clip(RoundedCornerShape(percent = cornerRadius))
    )
}


@Composable
fun CameraPermissionDeniedDialog(
    onDismiss: () -> Unit, onOpenSettings: () -> Unit
) {
    AlertDialog(onDismissRequest = onDismiss, title = {
        Text(
            text = stringResource(Res.string.camera_permission_required), style = MaterialTheme.typography.titleLarge
        )
    }, text = {
        Column {
            Icon(
                imageVector = Icons.Default.Warning,
                contentDescription = stringResource(Res.string.warning),
                modifier = Modifier.size(48.dp).align(Alignment.CenterHorizontally),
                tint = MaterialTheme.colorScheme.error
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = stringResource(Res.string.go_to_settings) ,  style = MaterialTheme.typography.bodyMedium
            )
        }
    }, confirmButton = {
        TextButton(onClick = onOpenSettings) {
            Text(stringResource(Res.string.open_setting))
        }
    }, dismissButton = {
        TextButton(onClick = onDismiss) {
            Text(stringResource(Res.string.dismiss))
        }
    })
}





