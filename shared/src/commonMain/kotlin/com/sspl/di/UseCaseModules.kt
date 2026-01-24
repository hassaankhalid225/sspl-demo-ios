package com.sspl.di

import com.sspl.core.usecases.AutoJoinSessionUseCase
import com.sspl.core.usecases.CommentOnPostUseCase
import com.sspl.core.usecases.DeleteAccountUseCase
import com.sspl.core.usecases.DislikePostUseCase
import com.sspl.core.usecases.EmailValidationUseCase
import com.sspl.core.usecases.GetBannersUseCase
import com.sspl.core.usecases.GetConferencesDetailsUseCase
import com.sspl.core.usecases.GetConferencesUseCase
import com.sspl.core.usecases.GetFeedbackFormUseCase
import com.sspl.core.usecases.GetPostDetailsUseCase
import com.sspl.core.usecases.GetPostsUseCase
import com.sspl.core.usecases.GetUserDetailsUseCase
import com.sspl.core.usecases.JoinSessionUseCase
import com.sspl.core.usecases.LikePostUseCase
import com.sspl.core.usecases.LoginUserUseCase
import com.sspl.core.usecases.MarkAttendanceViaImageUseCase
import com.sspl.core.usecases.MarkAttendanceViaLocationUseCase
import com.sspl.core.usecases.PasswordValidationUseCase
import com.sspl.core.usecases.PhoneNumberValidationUseCase
import com.sspl.core.usecases.PostUserDetailsUseCase
import com.sspl.core.usecases.InitResetPasswordUseCase
import com.sspl.core.usecases.PostFeedbackFormUseCase
import com.sspl.core.usecases.SignUpUserUseCase
import com.sspl.core.usecases.UpdatePasswordUseCase
import com.sspl.core.usecases.UpdateUserBasicInfoUseCase
import com.sspl.core.usecases.UpdateDeviceTokenUseCase
import com.sspl.core.usecases.ValidateUserCityUseCase
import com.sspl.core.usecases.ValidateUserCountryUseCase
import com.sspl.core.usecases.ValidateUserDesignationUseCase
import com.sspl.core.usecases.ValidateUserNameUseCase
import com.sspl.core.usecases.ValidateUserInstitutionUseCase
import com.sspl.core.usecases.ValidateUserPmdcNoUseCase
import com.sspl.core.usecases.ValidateUserRegistrationNumberUseCase
import com.sspl.core.usecases.CheckRegistrationUseCase
import com.sspl.core.usecases.RegisterUserUseCase
import com.sspl.core.usecases.InitiatePaymentUseCase
import com.sspl.core.usecases.GetConferenceRolesUseCase
import com.sspl.core.usecases.GetUserRegistrationsUseCase
import com.sspl.core.usecases.UpdateManualPaymentUseCase
import com.sspl.core.usecases.GetRegistrationDetailsUseCase
import org.koin.dsl.module

/**
 * Created by M Imran
 * Senior Software Engineer at
 * BhimSoft on 02/01/2025.
 * se.muhammadimran@gmail.com
 */

internal val useCaseModules = module {
//    single { Authenticate(get(), get()) }
//    single { CheckIsAuthenticated(get()) }
//    single { GetLiveStream(get(), get()) }
//    single { GetLiveStreamCategories(get(), get()) }
    single { EmailValidationUseCase() }
    single { PasswordValidationUseCase() }
    single { PhoneNumberValidationUseCase() }
    single { PhoneNumberValidationUseCase() }
    single { ValidateUserNameUseCase() }
    single { ValidateUserCityUseCase() }
    single { ValidateUserCountryUseCase() }
    single { ValidateUserInstitutionUseCase() }
    single { ValidateUserDesignationUseCase() }
    single { ValidateUserPmdcNoUseCase() }
    single { ValidateUserRegistrationNumberUseCase() }

    factory { GetFeedbackFormUseCase(get()) }
    factory { InitResetPasswordUseCase(get()) }
    factory { UpdatePasswordUseCase(get()) }
    factory { GetPostsUseCase(get()) }
    factory { LikePostUseCase() }
    factory { DislikePostUseCase() }
    factory { CommentOnPostUseCase() }
    factory { SignUpUserUseCase(get(), get()) }
    factory { LoginUserUseCase(get(), get(), get()) }
    factory { GetConferencesUseCase(get()) }
    factory { GetConferencesDetailsUseCase(get()) }
    factory { PostUserDetailsUseCase(get()) }
    factory { GetUserDetailsUseCase(get()) }
    factory { UpdateUserBasicInfoUseCase(get()) }
    factory { GetPostDetailsUseCase(get()) }
    factory { MarkAttendanceViaImageUseCase(get()) }
    factory { MarkAttendanceViaLocationUseCase(get()) }
    factory { DeleteAccountUseCase(get(), get()) }
    factory { PostFeedbackFormUseCase(get()) }
    factory { JoinSessionUseCase(get()) }
    factory { AutoJoinSessionUseCase(get()) }
    factory { UpdateDeviceTokenUseCase(get()) }
    factory { UpdateDeviceTokenUseCase(get()) }
    factory { GetBannersUseCase(get()) }
    factory { CheckRegistrationUseCase(get()) }
    factory { RegisterUserUseCase(get()) }
    factory { InitiatePaymentUseCase(get()) }
    factory { GetConferenceRolesUseCase(get()) }
    factory { GetUserRegistrationsUseCase(get()) }
    factory { UpdateManualPaymentUseCase(get()) }
    factory { GetRegistrationDetailsUseCase(get()) }
}

