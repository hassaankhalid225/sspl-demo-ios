package com.sspl.di

import com.sspl.ui.about.AboutSocietyViewModel
import com.sspl.ui.conference.AttendanceViewModel
import com.sspl.ui.conference.ConferenceDetailViewModel
import com.sspl.ui.conference.ConferenceViewModel
import com.sspl.ui.feedback.FeedbackViewModel
import com.sspl.ui.homescreen.HomeScreenViewModel
import com.sspl.ui.kyc.ForgotPasswordViewModel
import com.sspl.ui.kyc.SignInViewModel
import com.sspl.ui.kyc.SignUpViewModel
import com.sspl.ui.otp.OtpViewModel
import com.sspl.ui.postcreation.PostCreationViewModel
import com.sspl.ui.posts.PostViewModel
import com.sspl.ui.settings.deleteaccount.DeleteAccountViewModel
import com.sspl.ui.exhibition.ExhibitionRegistrationViewModel
import com.sspl.ui.userdetails.EditProfileViewModel
import com.sspl.ui.userdetails.ProfileViewModel
import com.sspl.ui.userdetails.UserDetailViewModel
import com.sspl.ui.notifications.NotificationsViewModel
import com.sspl.ui.scenario.ScenarioJoinViewModel
import com.sspl.ui.conference.registration.ConferenceRegistrationViewModel
import org.koin.core.module.dsl.singleOf
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

/**
 * Created by M Imran
 * Senior Software Engineer at
 * BhimSoft on 02/01/2025.
 * se.muhammadimran@gmail.com
 */

/**
 * factory -> gives new instance each time you ask for this object type.
 *
 * single -> gives a single instance across the entire lifetime of the app.
 * (singleton used especially for Database or Network Managers)
 *
 * get() -> this will resolve the dependency, koin will find out the matching dependency
 */

internal val viewModelModules = module {
    singleOf(::ProfileViewModel)
    singleOf(::HomeScreenViewModel)

    viewModelOf(::ConferenceViewModel)
    viewModelOf(::ConferenceDetailViewModel)
    viewModelOf(::SignInViewModel)
    viewModelOf(::SignUpViewModel)
    viewModelOf(::PostViewModel)
    viewModelOf(::OtpViewModel)
    viewModelOf(::UserDetailViewModel)
    viewModelOf(::DeleteAccountViewModel)
    viewModelOf(::PostCreationViewModel)
    viewModelOf(::AttendanceViewModel)
    viewModelOf(::AboutSocietyViewModel)
    viewModelOf(::ForgotPasswordViewModel)
    viewModelOf(::FeedbackViewModel)
    viewModelOf(::EditProfileViewModel)
    viewModelOf(::ExhibitionRegistrationViewModel)
    viewModelOf(::NotificationsViewModel)
    viewModelOf(::ScenarioJoinViewModel)
    viewModelOf(::ConferenceRegistrationViewModel)


//    viewModelOf(::PlayerScreenViewModel)
//    viewModelOf(::SearchScreenViewModel)
}