package com.sspl.ui.userdetails

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import coil3.compose.AsyncImage
import com.preat.peekaboo.image.picker.SelectionMode
import com.preat.peekaboo.image.picker.rememberImagePickerLauncher
import com.sspl.Screen
import com.sspl.core.ApiStates
import com.sspl.core.models.User
import com.sspl.resources.Res
import com.sspl.resources.ic_user
import com.sspl.resources.sign_out
import com.sspl.theme.primary
import com.sspl.ui.components.AppProgressBar
import com.sspl.ui.components.AppRoundedCornerButton
import com.sspl.ui.components.AppTextBody
import com.sspl.ui.components.AppTextLabel
import com.sspl.ui.components.AppTextTitle
import com.sspl.ui.components.Error
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel
import kotlin.io.encoding.Base64
import kotlin.io.encoding.ExperimentalEncodingApi

@OptIn(ExperimentalEncodingApi::class)
@Composable
fun ProfileScreen(
    navController: NavController,
    viewModel: ProfileViewModel = koinViewModel(),
    onParentGraph: () -> Unit = {}
) {
    val userDetails by viewModel.userDetails.collectAsStateWithLifecycle()
    val onSignedOut by viewModel.onSignedOut.collectAsStateWithLifecycle()
    val isGuestUser by viewModel.isGuestUser.collectAsStateWithLifecycle()
    val profileImageBase64 by viewModel.profileImage.collectAsStateWithLifecycle()
    
    val scope = rememberCoroutineScope()
    val launcher = rememberImagePickerLauncher(
        selectionMode = SelectionMode.Single,
        scope = scope,
        onResult = { byteArrays ->
            byteArrays.firstOrNull()?.let {
                viewModel.updateProfileImage(it)
            }
        }
    )

    LaunchedEffect(Unit) {
        viewModel.getUserDetails()
    }

    LaunchedEffect(onSignedOut, isGuestUser) {
        if (onSignedOut || isGuestUser == true) {
            onParentGraph()
        }
    }

    if (isGuestUser == null || onSignedOut) {
        AppProgressBar()
        return
    }

    if (isGuestUser == true) {
        return
    }

    Column(modifier = Modifier.fillMaxSize()) {
        Box(modifier = Modifier.weight(1f)) {
            ProfileScreenContent(
                userDetails = userDetails,
                profileImageBase64 = profileImageBase64,
                onEditPersonalDetails = {
                    navController.currentBackStackEntry?.savedStateHandle?.set(
                        "isPersonalDetails", true
                    )
                    navController.currentBackStackEntry?.savedStateHandle?.set(
                        "currentUser", Json.encodeToString((userDetails as ApiStates.Success).data)
                    )
                    navController.navigate(Screen.EditProfile.route)
                },
                onEditProfessionalDetails = {
                    navController.currentBackStackEntry?.savedStateHandle?.set(
                        "isPersonalDetails", false
                    )
                    navController.currentBackStackEntry?.savedStateHandle?.set(
                        "currentUser", Json.encodeToString((userDetails as ApiStates.Success).data)
                    )
                    navController.navigate(Screen.EditProfile.route)
                },
                onChangeProfilePicture = {
                    launcher.launch()
                }
            )
        }
        
        SignOutButton(onSignOut = { viewModel.onSignOut() })
        Spacer(modifier = Modifier.height(16.dp))
    }
}

@OptIn(ExperimentalEncodingApi::class)
@Composable
fun ProfileScreenContent(
    userDetails: ApiStates<User?>,
    profileImageBase64: String?,
    onEditPersonalDetails: () -> Unit = {},
    onEditProfessionalDetails: () -> Unit = {},
    onChangeProfilePicture: () -> Unit = {}
) {
    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp).verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        when (userDetails) {
            is ApiStates.Failure -> Error(
                modifier = Modifier, userDetails.error
            )

            ApiStates.Loading -> AppProgressBar()

            ApiStates.Idle -> {
                // Do nothing, waiting for data
            }

            is ApiStates.Success -> userDetails.data?.let { data ->
                Spacer(Modifier.height(20.dp))
                ProfileImage(
                    imageBase64 = profileImageBase64,
                    onChangeProfilePicture = onChangeProfilePicture
                )
                Spacer(Modifier.height(20.dp))

                BasicInformationSection(data, onEditPersonalDetails = onEditPersonalDetails)
                Spacer(Modifier.height(16.dp))
                PersonalDetailsSection(data, onEditProfessionalDetails = onEditProfessionalDetails)
            }
        }
    }
}

@OptIn(ExperimentalEncodingApi::class)
@Composable
private fun ProfileImage(
    imageBase64: String? = null,
    onChangeProfilePicture: () -> Unit = {}
) {
    Box(
        modifier = Modifier.size(130.dp).aspectRatio(1f)
    ) {
        if (imageBase64 != null) {
            val imageBytes = Base64.decode(imageBase64)
            AsyncImage(
                model = imageBytes,
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize().clip(CircleShape)
            )
        } else {
            Image(
                painter = painterResource(Res.drawable.ic_user),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize().clip(CircleShape)
            )
        }

        Box(
            modifier = Modifier
                .size(34.dp)
                .align(Alignment.BottomEnd)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.primary)
                .clickable(onClick = onChangeProfilePicture),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.Edit,
                contentDescription = "Edit profile picture",
                tint = Color.White,
                modifier = Modifier.padding(8.dp)
            )
        }
    }
}


 @Composable
private fun BasicInformationSection(user: User?, onEditPersonalDetails: () -> Unit = {}) {
    Column(modifier = Modifier.fillMaxWidth()) {
        val isGuestUser = user?.account?.isGuestUser() == true
        Spacer(Modifier.height(4.dp))
        InfoCard {
            SectionTitle(
                "Personal Details",
                showIcon = isGuestUser.not(),
                onEdit = onEditPersonalDetails
            )
            InfoRow("Name", user?.fullName().orEmpty())
            InfoRow("Email", user?.account?.email.orEmpty())
            InfoRow("Phone", user?.account?.phone.orEmpty())
        }
    }
}

@Composable
private fun PersonalDetailsSection(userProfile: User, onEditProfessionalDetails: () -> Unit = {}) {
    val isGuestUser = userProfile.account?.isGuestUser() == true
    Column(modifier = Modifier.fillMaxWidth()) {
        Spacer(Modifier.height(4.dp))
        InfoCard {
            SectionTitle(
                "Professional Details",
                showIcon = isGuestUser.not(),
                onEdit = onEditProfessionalDetails
            )
            InfoRow("PMDC No", userProfile.profile?.pmdcNumber.orEmpty())
            InfoRow("Institute", userProfile.profile?.institute.orEmpty())
            InfoRow("Country", userProfile.profile?.country.orEmpty())
            InfoRow("Designation", userProfile.profile?.title.orEmpty())
            InfoRow("Registration No", userProfile.profile?.orgNumber.orEmpty())
        }
    }
}

@Composable
private fun SectionTitle(title: String, showIcon: Boolean = true, onEdit: () -> Unit = {}) {
    Row(
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        AppTextTitle(
            text = title, fontSize = 14.sp
        )
        Spacer(Modifier.weight(1f))
        AnimatedVisibility(visible = showIcon, modifier = Modifier.clickable(onClick = onEdit)) {
            Icon(
                imageVector = Icons.Default.Edit,
                contentDescription = "Edit details",
                tint = primary,
                modifier = Modifier.size(22.dp)
            )
        }
    }
}

@Composable
private fun SignOutButton(onSignOut: () -> Unit) {
    AppRoundedCornerButton(
        onClick = onSignOut, title = stringResource(Res.string.sign_out)
    )
}

@Composable
fun InfoRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        AppTextLabel(
            text = "$label:",
        )
        AppTextBody(
            text = value,
        )
    }
}

@Composable
fun InfoCard(
    modifier: Modifier = Modifier, content: @Composable () -> Unit
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            content()
        }
    }
}
