package com.sspl.ui.userdetails

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.sspl.Screen
import com.sspl.core.ApiStates
import com.sspl.core.models.Session
import com.sspl.core.models.User
import com.sspl.resources.Res
import com.sspl.resources.app_logo
import com.sspl.resources.designation
import com.sspl.resources.edit_profile
import com.sspl.resources.enter_first_name
import com.sspl.resources.enter_last_name
import com.sspl.resources.enter_pmdc_number
import com.sspl.resources.enter_registration_number
import com.sspl.resources.enter_your_mail
import com.sspl.resources.institution_name
import com.sspl.resources.login_screen_background
import com.sspl.resources.sign_up
import com.sspl.resources.update_info
import com.sspl.ui.components.AppTextField
import com.sspl.ui.components.ButtonWithProgress
import com.sspl.ui.components.PhoneWithCountrySelector
import com.sspl.ui.kyc.HeaderSection
import com.sspl.ui.kyc.UserEvents
import com.sspl.utils.IconResource
import kotlinx.serialization.json.Json
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun EditProfileScreen(
    navController: NavController,
    profileViewModel: ProfileViewModel = koinViewModel(),
    viewModel: EditProfileViewModel = koinViewModel()
) {

    val isPersonalDetails =
        navController.previousBackStackEntry?.savedStateHandle?.get<Boolean>("isPersonalDetails")
    val user = navController.previousBackStackEntry?.savedStateHandle?.get<String>("currentUser")
    val userObj: User? = user?.let { Json.decodeFromString<User>(it) }
    val basicInfoState by viewModel.basicProfileUiState.collectAsStateWithLifecycle()
    val professionalInfoState by viewModel.professionalProfileUiState.collectAsStateWithLifecycle()

    val scaffoldState = remember { SnackbarHostState() }
    LaunchedEffect(Unit) {
        viewModel.updateProfessionalProfileUiState(userObj)
    }
    LaunchedEffect(Unit) {
        viewModel.isUserBasicInfoUpdated.collect { isBasicInfoUpdated ->
            when (isBasicInfoUpdated) {


                is ApiStates.Failure -> {
                    scaffoldState.showSnackbar(
                        isBasicInfoUpdated.error.message
                    )
                }

                is ApiStates.Success -> {
                    isBasicInfoUpdated.data?.let { profileViewModel.updateUser(it) }
                    navController.navigateUp()
                    scaffoldState.showSnackbar("Updated Successfully")
                }

                else -> Unit
            }
        }
    }
    LaunchedEffect(Unit) {
        viewModel.isUserProfessionalInfoUpdated.collect { isProfessionalInfoUpdated ->
            when (isProfessionalInfoUpdated) {
                is ApiStates.Failure -> {
                    scaffoldState.showSnackbar(
                        isProfessionalInfoUpdated.error.message
                    )
                }

                is ApiStates.Success -> {
                    isProfessionalInfoUpdated.data?.let { profileViewModel.updateUserDetails(it) }
                    navController.navigateUp()
                    scaffoldState.showSnackbar("Updated Successfully")
                }

                else -> Unit

            }
        }
    }



    Scaffold(snackbarHost = { SnackbarHost(scaffoldState) }) { _ ->
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Image(
                painter = painterResource(Res.drawable.login_screen_background),
                contentDescription = null,
                contentScale = ContentScale.FillBounds
            )

            Column(
                modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState())
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                HeaderSection(
                    iconResource = IconResource.fromDrawableResource(drawable = Res.drawable.app_logo),
                    title = stringResource(Res.string.edit_profile),
                    subTitle = stringResource(Res.string.update_info)
                )
                if (isPersonalDetails == true) {
                    EditPersonalDetails(basicProfileUiState = basicInfoState, onEvent = {
                        viewModel.onBasicProfileEvent(it)
                    })

                } else {
                    EditProfessionalDetails(professionalInfoState, onEvent = {
                        viewModel.onProfessionalEvent(it)
                    })

                }
            }
        }
    }

}

@Composable
fun EditPersonalDetails(
    basicProfileUiState: BasicProfileUiState,
    onEvent: (BasicProfileEvents) -> Unit = {},
) {

    AppTextField(placeholder = stringResource(Res.string.enter_first_name),
        text = basicProfileUiState.firstName,
        errorMessage = basicProfileUiState.firstNameError,
        onTextChange = { onEvent(BasicProfileEvents.UpdateFirstName(it)) })

    AppTextField(placeholder = stringResource(Res.string.enter_last_name),
        text = basicProfileUiState.lastName,
        errorMessage = basicProfileUiState.lastNameError,
        onTextChange = { onEvent(BasicProfileEvents.UpdateLastName(it)) })

    PhoneWithCountrySelector(selectedCountry = basicProfileUiState.country,
        showTransformation = false,
        phone = basicProfileUiState.phone,
        errorMessage = basicProfileUiState.phoneNoError,
        onPhoneNumberChange = { onEvent(BasicProfileEvents.UpdatePhone(it)) },
        showCountryChooser = basicProfileUiState.showCountryChooser,
        onCountryChange = { onEvent(BasicProfileEvents.OnCountryChange(it)) },
        toggleCountryPicker = { onEvent(BasicProfileEvents.ToggleCountryChooser) })

    ButtonWithProgress(
        isEnabled = basicProfileUiState.isUpdateEnabled,
        isLoading = basicProfileUiState.isLoading,
        buttonText = "Update",
        modifier = Modifier.imePadding()
    ) {
        onEvent(BasicProfileEvents.OnSubmit)
    }

}

@Composable
fun EditProfessionalDetails(
    state: ProfessionalProfileUiState, onEvent: (ProfessionalProfileEvents) -> Unit
) {

    AppTextField(placeholder = stringResource(Res.string.enter_pmdc_number),
        text = state.pmDcNumber.orEmpty(),
        errorMessage = state.pmDcNumberError,
        onTextChange = { onEvent(ProfessionalProfileEvents.OnPmDcNumberTextChange(it)) })
    AppTextField(placeholder = stringResource(Res.string.institution_name),
        text = state.institution,
        errorMessage = state.institutionError,
        onTextChange = { onEvent(ProfessionalProfileEvents.OnInstitutionTextChange(it)) })
    AppTextField(placeholder = stringResource(Res.string.designation),
        text = state.designation,
        errorMessage = state.designationError,
        onTextChange = { onEvent(ProfessionalProfileEvents.OnDesignationTextChange(it)) })
    AppTextField(placeholder = stringResource(Res.string.enter_registration_number),
        text = state.registrationNumber ?: "",
        errorMessage = state.registrationNumberError,
        onTextChange = { onEvent(ProfessionalProfileEvents.OnRegistrationNumberTextChange(it)) })
    ButtonWithProgress(
        isEnabled = state.isUpdateEnabled,
        isLoading = state.isLoading,
        buttonText = "Update",
        modifier = Modifier.imePadding()
    ) {
        onEvent(ProfessionalProfileEvents.OnSave)
    }

}