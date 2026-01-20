package com.sspl.ui.exhibition

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sspl.core.repositories.ExhibitionRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import com.sspl.utils.randomUUID

class ExhibitionRegistrationViewModel(
    private val exhibitionRepository: ExhibitionRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow(ExhibitionRegistrationUiState())
    val uiState: StateFlow<ExhibitionRegistrationUiState> = _uiState.asStateFlow()

    fun onEvent(event: ExhibitionRegistrationEvents) {
        when (event) {
            is ExhibitionRegistrationEvents.SetIndustryCategory -> {
                _uiState.value = _uiState.value.copy(
                    industryCategory = event.value,
                    industryCategoryError = validateIndustryCategory(event.value)
                )
                updateSubmitEnabled()
            }

            is ExhibitionRegistrationEvents.SetCompanyName -> {
                _uiState.value = _uiState.value.copy(
                    companyName = event.value,
                    companyNameError = validateCompanyName(event.value)
                )
                updateSubmitEnabled()
            }

            is ExhibitionRegistrationEvents.SetContactPerson -> {
                _uiState.value = _uiState.value.copy(
                    contactPerson = event.value,
                    contactPersonError = validateContactPerson(event.value)
                )
                updateSubmitEnabled()
            }

            is ExhibitionRegistrationEvents.SetPhoneNumber -> {
                _uiState.value = _uiState.value.copy(
                    phoneNumber = event.value,
                    phoneNumberError = validatePhoneNumber(event.value)
                )
                updateSubmitEnabled()
            }

            is ExhibitionRegistrationEvents.SetEmailAddress -> {
                _uiState.value = _uiState.value.copy(
                    emailAddress = event.value,
                    emailAddressError = validateEmail(event.value)
                )
                updateSubmitEnabled()
            }

            is ExhibitionRegistrationEvents.SetCompanyLogo -> {
                _uiState.value = _uiState.value.copy(companyLogo = event.image)
            }

            is ExhibitionRegistrationEvents.SetProductName -> {
                val updatedProducts = _uiState.value.products.map { product ->
                    if (product.id == event.productId) {
                        product.copy(name = event.name)
                    } else {
                        product
                    }
                }
                _uiState.value = _uiState.value.copy(products = updatedProducts)
            }

            is ExhibitionRegistrationEvents.SetProductDescription -> {
                val updatedProducts = _uiState.value.products.map { product ->
                    if (product.id == event.productId) {
                        product.copy(description = event.description)
                    } else {
                        product
                    }
                }
                _uiState.value = _uiState.value.copy(products = updatedProducts)
            }

            is ExhibitionRegistrationEvents.AddProductField -> {
                val newProduct = ProductField(id = randomUUID())
                _uiState.value = _uiState.value.copy(
                    products = _uiState.value.products + newProduct
                )
            }

            is ExhibitionRegistrationEvents.RemoveProductField -> {
                if (_uiState.value.products.size > 1) {
                    _uiState.value = _uiState.value.copy(
                        products = _uiState.value.products.filter { it.id != event.productId }
                    )
                }
            }

            is ExhibitionRegistrationEvents.SubmitRegistration -> {
                submitRegistration()
            }

            is ExhibitionRegistrationEvents.DismissDialog -> {
                _uiState.value = _uiState.value.copy(
                    showSuccessDialog = false,
                    showErrorDialog = false,
                    errorMessage = null
                )
            }
        }
    }

    private fun validateIndustryCategory(value: String): String? {
        return if (value.isBlank()) {
            "Industry category is required"
        } else null
    }

    private fun validateCompanyName(value: String): String? {
        return if (value.isBlank()) {
            "Company name is required"
        } else null
    }

    private fun validateContactPerson(value: String): String? {
        return if (value.isBlank()) {
            "Contact person is required"
        } else null
    }

    private fun validatePhoneNumber(value: String): String? {
        return if (value.isBlank()) {
            "Phone number is required"
        } else if (value.length < 10) {
            "Please enter a valid phone number"
        } else null
    }

    private fun validateEmail(value: String): String? {
        val emailRegex = Regex("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Z|a-z]{2,}$")
        return if (value.isBlank()) {
            "Email address is required"
        } else if (!emailRegex.matches(value)) {
            "Please enter a valid email address"
        } else null
    }

    private fun updateSubmitEnabled() {
        val state = _uiState.value
        val isEnabled = state.industryCategory.isNotBlank() &&
                state.companyName.isNotBlank() &&
                state.contactPerson.isNotBlank() &&
                state.phoneNumber.isNotBlank() &&
                state.emailAddress.isNotBlank() &&
                state.industryCategoryError == null &&
                state.companyNameError == null &&
                state.contactPersonError == null &&
                state.phoneNumberError == null &&
                state.emailAddressError == null

        _uiState.value = state.copy(isSubmitEnabled = isEnabled)
    }

    private fun submitRegistration() {
        viewModelScope.launch {
            try {
                _uiState.value = _uiState.value.copy(isLoading = true)

                val state = _uiState.value
                
                // Convert products to API model
                val products = state.products
                    .filter { it.name.isNotBlank() || it.description.isNotBlank() }
                    .map { product ->
                        com.sspl.core.models.ExhibitionProduct(
                            name = product.name,
                            description = product.description
                        )
                    }

                // Convert company logo to ByteArray if available
                val logoBytes = state.companyLogo?.toByteArray()
                val logoFileName = state.companyLogo?.let { "company_logo.png" }

                // Make API call
                val response = exhibitionRepository.createExhibitionStall(
                    companyName = state.companyName,
                    contactPerson = state.contactPerson,
                    email = state.emailAddress,
                    contactNum = state.phoneNumber,
                    stallNo = "", // Optional field
                    category = com.sspl.core.models.SponsorCategory.BASIC, // Default category
                    companyLogo = logoBytes,
                    logoFileName = logoFileName,
                    products = products
                )

                // Success - show success dialog and reset form
                _uiState.value = ExhibitionRegistrationUiState(
                    showSuccessDialog = true,
                    isLoading = false
                )
                
            } catch (e: Exception) {
                // Network or other error
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    showErrorDialog = true,
                    errorMessage = e.message ?: "An unexpected error occurred. Please check your connection and try again."
                )
            }
        }
    }
}
