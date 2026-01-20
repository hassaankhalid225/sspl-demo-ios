package com.sspl.ui.exhibition

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.sspl.resources.Res
import com.sspl.theme.windowBackGround
import com.sspl.ui.components.AppTextField
import com.sspl.ui.components.AppTextLabel
import com.sspl.ui.components.AppTextSubTitle
import com.sspl.ui.components.ButtonWithProgress
import org.koin.compose.viewmodel.koinViewModel
import shared.GalleryManager
import shared.rememberGalleryManager

/**
 * Created by M Imran
 * Senior Software Engineer at
 * BhimSoft on 12/02/2025.
 * se.muhammadimran@gmail.com
 */

@Composable
fun ExhibitionRegistrationScreen(
    navController: NavController,
    viewModel: ExhibitionRegistrationViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val keyboardController = LocalSoftwareKeyboardController.current

    val galleryManager = rememberGalleryManager { image ->
        viewModel.onEvent(ExhibitionRegistrationEvents.SetCompanyLogo(image))
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(windowBackGround)
            .verticalScroll(rememberScrollState())
            .imePadding()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Header Section
        HeaderSection()

        // Form Fields
        FormFieldsSection(
            uiState = uiState,
            onEvent = viewModel::onEvent,
            onLogoClick = { galleryManager.launch() }
        )

        // Company & Branding Section
        CompanyBrandingSection(
            logo = uiState.companyLogo,
            onLogoClick = { galleryManager.launch() }
        )

        // Products Section
        ProductsSection(
            products = uiState.products,
            onEvent = viewModel::onEvent
        )

        // Submit Button
        ButtonWithProgress(
            isEnabled = uiState.isSubmitEnabled,
            isLoading = uiState.isLoading,
            buttonText = "SUBMIT REGISTRATION",
            modifier = Modifier.fillMaxWidth(),
            onClick = {
                keyboardController?.hide()
                viewModel.onEvent(ExhibitionRegistrationEvents.SubmitRegistration)
            }
        )

        Spacer(modifier = Modifier.height(16.dp))
    }
    
    // Success Dialog
    if (uiState.showSuccessDialog) {
        androidx.compose.material3.AlertDialog(
            onDismissRequest = {
                viewModel.onEvent(ExhibitionRegistrationEvents.DismissDialog)
                navController.popBackStack()
            },
            title = {
                AppTextLabel(
                    text = "Success!",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                AppTextSubTitle(
                    text = "Your exhibition registration has been submitted successfully. We will contact you soon.",
                    fontSize = 14.sp
                )
            },
            confirmButton = {
                androidx.compose.material3.TextButton(
                    onClick = {
                        viewModel.onEvent(ExhibitionRegistrationEvents.DismissDialog)
                        navController.popBackStack()
                    }
                ) {
                    AppTextLabel(
                        text = "OK",
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
        )
    }
    
    // Error Dialog
    if (uiState.showErrorDialog) {
        androidx.compose.material3.AlertDialog(
            onDismissRequest = {
                viewModel.onEvent(ExhibitionRegistrationEvents.DismissDialog)
            },
            title = {
                AppTextLabel(
                    text = "Error",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.error
                )
            },
            text = {
                AppTextSubTitle(
                    text = uiState.errorMessage ?: "An error occurred. Please try again.",
                    fontSize = 14.sp
                )
            },
            confirmButton = {
                androidx.compose.material3.TextButton(
                    onClick = {
                        viewModel.onEvent(ExhibitionRegistrationEvents.DismissDialog)
                    }
                ) {
                    AppTextLabel(
                        text = "OK",
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
        )
    }
}

@Composable
private fun HeaderSection() {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        AppTextLabel(
            text = "Exhibition Registration",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.fillMaxWidth()
        )
        AppTextSubTitle(
            text = "Please fill out all text fields to reserve your stall.",
            fontSize = 14.sp,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Composable
private fun FormFieldsSection(
    uiState: ExhibitionRegistrationUiState,
    onEvent: (ExhibitionRegistrationEvents) -> Unit,
    onLogoClick: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Industry Category
        AppTextField(
            text = uiState.industryCategory,
            onTextChange = { onEvent(ExhibitionRegistrationEvents.SetIndustryCategory(it)) },
            placeholder = "Industry Category",
            errorMessage = uiState.industryCategoryError,
            leadingIcon = {
                androidx.compose.material3.Icon(
                    imageVector = Icons.Default.AccountCircle,
                    contentDescription = "Industry Category"
                )
            }
        )

        // Company Name
        AppTextField(
            text = uiState.companyName,
            onTextChange = { onEvent(ExhibitionRegistrationEvents.SetCompanyName(it)) },
            placeholder = "Company Name",
            errorMessage = uiState.companyNameError,
            leadingIcon = {
                androidx.compose.material3.Icon(
                    imageVector = Icons.Default.Home,
                    contentDescription = "Company Name"
                )
            }
        )

        // Contact Person
        AppTextField(
            text = uiState.contactPerson,
            onTextChange = { onEvent(ExhibitionRegistrationEvents.SetContactPerson(it)) },
            placeholder = "Contact Person",
            errorMessage = uiState.contactPersonError,
            leadingIcon = {
                androidx.compose.material3.Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = "Contact Person"
                )
            }
        )

        // Phone Number
        AppTextField(
            text = uiState.phoneNumber,
            onTextChange = { onEvent(ExhibitionRegistrationEvents.SetPhoneNumber(it)) },
            placeholder = "Phone Number",
            errorMessage = uiState.phoneNumberError,
            keyboardType = androidx.compose.ui.text.input.KeyboardType.Phone,
            leadingIcon = {
                androidx.compose.material3.Icon(
                    imageVector = Icons.Default.Phone,
                    contentDescription = "Phone Number"
                )
            }
        )

        // Email Address
        AppTextField(
            text = uiState.emailAddress,
            onTextChange = { onEvent(ExhibitionRegistrationEvents.SetEmailAddress(it)) },
            placeholder = "Email Address",
            errorMessage = uiState.emailAddressError,
            keyboardType = androidx.compose.ui.text.input.KeyboardType.Email,
            leadingIcon = {
                androidx.compose.material3.Icon(
                    imageVector = Icons.Default.Email,
                    contentDescription = "Email Address"
                )
            }
        )
    }
}

@Composable
private fun CompanyBrandingSection(
    logo: shared.SharedImage?,
    onLogoClick: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        AppTextLabel(
            text = "Company & Branding",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.fillMaxWidth()
        )

        // Logo Upload Area
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
                .clickable { onLogoClick() },
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
            ),
            border = BorderStroke(
                width = 2.dp,
                color = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)
            )
        ) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                if (logo != null) {
                    logo.toImageBitmap()?.let { bitmap ->
                        Image(
                            bitmap = bitmap,
                            contentDescription = "Company Logo",
                            modifier = Modifier
                                .fillMaxSize()
                                .clip(RoundedCornerShape(12.dp)),
                            contentScale = ContentScale.Crop
                        )
                    }
                } else {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        androidx.compose.material3.Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "Upload Logo",
                            modifier = Modifier.size(48.dp),
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        AppTextSubTitle(
                            text = "UPLOAD LOGO",
                            fontSize = 14.sp
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ProductsSection(
    products: List<ProductField>,
    onEvent: (ExhibitionRegistrationEvents) -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        products.forEachIndexed { index, product ->
            ProductFieldItem(
                product = product,
                index = index,
                canRemove = products.size > 1,
                onProductNameChange = { name ->
                    onEvent(ExhibitionRegistrationEvents.SetProductName(product.id, name))
                },
                onProductDescriptionChange = { description ->
                    onEvent(ExhibitionRegistrationEvents.SetProductDescription(product.id, description))
                },
                onRemove = {
                    onEvent(ExhibitionRegistrationEvents.RemoveProductField(product.id))
                }
            )
        }

        // Add Another Product Button
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onEvent(ExhibitionRegistrationEvents.AddProductField) },
            horizontalArrangement = Arrangement.Start,
            verticalAlignment = Alignment.CenterVertically
        ) {
            androidx.compose.material3.Icon(
                imageVector = Icons.Default.Add,
                contentDescription = "Add Product",
                tint = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.width(8.dp))
            AppTextLabel(
                text = "Add Another Product Field",
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}

@Composable
private fun ProductFieldItem(
    product: ProductField,
    index: Int,
    canRemove: Boolean,
    onProductNameChange: (String) -> Unit,
    onProductDescriptionChange: (String) -> Unit,
    onRemove: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Product Header with Remove Button
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                AppTextLabel(
                    text = "Product ${index + 1}",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
                if (canRemove) {
                    androidx.compose.material3.IconButton(onClick = onRemove) {
                        androidx.compose.material3.Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "Remove Product",
                            tint = MaterialTheme.colorScheme.error
                        )
                    }
                }
            }

            // Product Name
            AppTextField(
                text = product.name,
                onTextChange = onProductNameChange,
                placeholder = "Product Name",
                modifier = Modifier.fillMaxWidth()
            )

            // Product Description
            AppTextField(
                text = product.description,
                onTextChange = onProductDescriptionChange,
                placeholder = "Short Description",
                modifier = Modifier.fillMaxWidth(),
                singleLine = false
            )
        }
    }
}

