package com.sspl.ui.exhibition

import shared.SharedImage

/**
 * Created by M Imran
 * Senior Software Engineer at
 * BhimSoft on 12/02/2025.
 * se.muhammadimran@gmail.com
 */

data class ProductField(
    val id: String,
    val name: String = "",
    val description: String = ""
)

data class ExhibitionRegistrationUiState(
    val industryCategory: String = "",
    val companyName: String = "",
    val contactPerson: String = "",
    val phoneNumber: String = "",
    val emailAddress: String = "",
    val companyLogo: SharedImage? = null,
    val products: List<ProductField> = listOf(ProductField(id = "1")),
    
    // Error messages
    val industryCategoryError: String? = null,
    val companyNameError: String? = null,
    val contactPersonError: String? = null,
    val phoneNumberError: String? = null,
    val emailAddressError: String? = null,
    
    val isLoading: Boolean = false,
    val isSubmitEnabled: Boolean = false,
    
    // Dialog states
    val showSuccessDialog: Boolean = false,
    val showErrorDialog: Boolean = false,
    val errorMessage: String? = null
)

sealed interface ExhibitionRegistrationEvents {
    data class SetIndustryCategory(val value: String) : ExhibitionRegistrationEvents
    data class SetCompanyName(val value: String) : ExhibitionRegistrationEvents
    data class SetContactPerson(val value: String) : ExhibitionRegistrationEvents
    data class SetPhoneNumber(val value: String) : ExhibitionRegistrationEvents
    data class SetEmailAddress(val value: String) : ExhibitionRegistrationEvents
    data class SetCompanyLogo(val image: SharedImage?) : ExhibitionRegistrationEvents
    data class SetProductName(val productId: String, val name: String) : ExhibitionRegistrationEvents
    data class SetProductDescription(val productId: String, val description: String) : ExhibitionRegistrationEvents
    data object AddProductField : ExhibitionRegistrationEvents
    data class RemoveProductField(val productId: String) : ExhibitionRegistrationEvents
    data object SubmitRegistration : ExhibitionRegistrationEvents
    data object DismissDialog : ExhibitionRegistrationEvents
}

