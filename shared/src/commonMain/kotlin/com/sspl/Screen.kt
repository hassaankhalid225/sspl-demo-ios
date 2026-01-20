package com.sspl


import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import com.sspl.resources.Res
import com.sspl.resources.about_us
import com.sspl.resources.create_post
import com.sspl.resources.delete_account
import com.sspl.resources.edit_profile
import com.sspl.resources.feedback
import com.sspl.resources.forgot_password
import com.sspl.resources.forum
import com.sspl.resources.home
import com.sspl.resources.ic_forum
import com.sspl.resources.ic_user
import com.sspl.resources.log_in
import com.sspl.resources.notifications
import com.sspl.resources.exhibition
import com.sspl.resources.profile
import com.sspl.resources.program_detail
import com.sspl.resources.scientific_programs
import com.sspl.resources.session
import com.sspl.resources.session_detail
import com.sspl.resources.settings
import com.sspl.resources.sign_up
import com.sspl.resources.user_details
import com.sspl.resources.verify_otp
import com.sspl.resources.workshop
import com.sspl.resources.workshops
import com.sspl.ui.SplashScreen
import com.sspl.ui.about.AboutScreen
import com.sspl.ui.conference.programs.ProgramDetailScreen
import com.sspl.ui.conference.programs.ScientificProgramsScreen
import com.sspl.ui.conference.programs.SessionDetailScreen
import com.sspl.ui.conference.registration.ConferenceRegistrationScreen
import com.sspl.ui.conference.registration.PaymentStatusScreen
import com.sspl.ui.conference.workshops.WorkshopDetailScreen
import com.sspl.ui.conference.workshops.WorkshopsScreen
import com.sspl.ui.feedback.FeedbackScreen
import com.sspl.ui.homescreen.HomeScreen
import com.sspl.ui.kyc.ForgotPasswordScreen
import com.sspl.ui.kyc.LoginScreen
import com.sspl.ui.kyc.SignUpScreen
import com.sspl.ui.notifications.NotificationsScreen
import com.sspl.ui.otp.OtpScreen
import com.sspl.ui.postcreation.CreatePostScreen
import com.sspl.ui.posts.PostScreen
import com.sspl.ui.session.SessionScreen
import com.sspl.ui.settings.SettingsScreen
import com.sspl.ui.settings.deleteaccount.DeleteAccountScreen
import com.sspl.ui.exhibition.ExhibitionRegistrationScreen
import com.sspl.ui.userdetails.EditProfileScreen
import com.sspl.ui.userdetails.ProfileScreen
import com.sspl.ui.userdetails.UserDetailsScreen
import com.sspl.utils.IconResource
import org.jetbrains.compose.resources.StringResource


/**
 * Created by M Imran
 * Senior Software Engineer at
 * BhimSoft on 02/01/2025.
 * se.muhammadimran@gmail.com
 */
data class NavItem(
    val screen: Screen, val component: @Composable (navController: NavHostController) -> Unit
)

sealed class Screen(
    val route: String, val title: StringResource? = null, val icon: IconResource? = null
) {
    data object None : Screen(
        route = "none",
    )

    data object SplashScreen : Screen(route = "SplashScreen")
    data object MainScreen : Screen(route = "MainScreen")

    data object HomeScreen : Screen(
        title = Res.string.home,
        route = "HomeScreen",
        icon = IconResource.fromImageVector(imageVector = Icons.Default.Home)
    )


    data object ProfileScreen : Screen(
        title = Res.string.profile,
        route = "ProfileScreen",
        icon = IconResource.fromDrawableResource(drawable = Res.drawable.ic_user)
    )

    data object ForumScreen : Screen(
        title = Res.string.forum,
        route = "ForumScreen",
        icon = IconResource.fromDrawableResource(drawable = Res.drawable.ic_forum)
    )

    data object ScientificProgramsScreen : Screen(
        title = Res.string.scientific_programs,
        route = "ScientificPrograms",
    )

    data object WorkshopsScreen : Screen(
        title = Res.string.workshops,
        route = "WorkshopsScreen",
    )

    data object WorkshopDetailScreen : Screen(
        title = Res.string.workshop,
        route = "WorkshopDetailScreen",
    )


    data object ScientificProgramDetailScreen : Screen(
        title = Res.string.program_detail,
        route = "ScientificProgramDetailScreen",
    )

    data object SessionDetailScreen : Screen(
        title = Res.string.session_detail,
        route = "SessionDetailScreen",
    )


    data object SignInScreen : Screen(
        title = Res.string.log_in,
        route = "SignInScreen",
    )


    data object GetLocation : Screen(
        title = Res.string.log_in,
        route = "Location",
    )

    data object UserDetailsScreen : Screen(
        title = Res.string.user_details,
        route = "UserDetailsScreen",
    )

    data object SignUpScreen : Screen(
        title = Res.string.sign_up,
        route = "SignUpScreen",
    )

    data object ForgotPasswordScreen : Screen(
        title = Res.string.forgot_password,
        route = "ForgotPassword",
    )

    data object EditProfile : Screen(
        title = Res.string.edit_profile,
        route = "EditProfileScreen",
    )

    data object SessionScreen : Screen(
        title = Res.string.session,
        route = "SessionScreen",
    )

    data object CreatePostScreen : Screen(
        title = Res.string.create_post,
        route = "CreatePostScreen",
    )

    data object OtpScreen : Screen(
        title = Res.string.verify_otp,
        route = "OtpScreen",
    )

    data object AboutScreen : Screen(
        title = Res.string.about_us,
        route = "AboutScreen",
    )
    data object FeedbackScreen : Screen(
        title = Res.string.feedback,
        route = "FeedbackScreen",
    )

    data object NotificationsScreen : Screen(
        title = Res.string.notifications,
        route = "NotificationsScreen",
    )

    data object SettingsScreen : Screen(
        title = Res.string.settings,
        route = "SettingsScreen",
    )

    data object DeleteAccountScreen : Screen(
        title = Res.string.delete_account,
        route = "DeleteAccountScreen",
    )

    data object ExhibitionRegistrationScreen : Screen(
        title = Res.string.exhibition,
        route = "ExhibitionRegistrationScreen",
    )

    data object ConferenceRegistrationScreen : Screen(
        route = "ConferenceRegistrationScreen",
    )

    data object PaymentStatusScreen : Screen(
        route = "PaymentStatusScreen",
    )
}

fun screenByRoute(route: String?): Screen? =
    (navItems() + otherScreens()).firstOrNull { it.screen.route == route }?.screen

fun navItems(): List<NavItem> =
    listOf(
        NavItem(screen = Screen.HomeScreen, component = { HomeScreen(navController = it) }),
        NavItem(screen = Screen.ForumScreen, component = { PostScreen(navController = it) }),
    )

fun otherScreens(): List<NavItem> = listOf(
    NavItem(
        screen = Screen.ProfileScreen,
        component = { ProfileScreen(navController = it) }),
    NavItem(
        screen = Screen.AboutScreen,
        component = { AboutScreen(navController = it) }),
    NavItem(
        screen = Screen.ScientificProgramsScreen,
        component = { ScientificProgramsScreen(navController = it) }),
    NavItem(
        screen = Screen.ScientificProgramDetailScreen,
        component = { ProgramDetailScreen(navController = it) }),
    NavItem(
        screen = Screen.SessionDetailScreen,
        component = { SessionDetailScreen(navController = it) }),
    NavItem(
        screen = Screen.SessionScreen,
        component = { SessionScreen(navController = it) }),
    NavItem(
        screen = Screen.FeedbackScreen,
        component = { FeedbackScreen(navController = it) }),
    NavItem(
        screen = Screen.UserDetailsScreen,
        component = { UserDetailsScreen(navController = it) }),
    NavItem(
        screen = Screen.EditProfile,
        component = { EditProfileScreen(navController = it) }),

    NavItem(screen = Screen.CreatePostScreen, component = { CreatePostScreen(navController = it) }),
    NavItem(screen = Screen.WorkshopsScreen, component = { WorkshopsScreen(navController = it) }),
    NavItem(
        screen = Screen.WorkshopDetailScreen,
        component = { WorkshopDetailScreen(navController = it) }),
    NavItem(
        screen = Screen.NotificationsScreen,
        component = { NotificationsScreen(navController = it) }),
    NavItem(
        screen = Screen.SettingsScreen,
        component = { SettingsScreen(navController = it) }),
    NavItem(
        screen = Screen.DeleteAccountScreen,
        component = { DeleteAccountScreen(navController = it) }),
    NavItem(
        screen = Screen.ExhibitionRegistrationScreen,
        component = { ExhibitionRegistrationScreen(navController = it) }),
    NavItem(
        screen = Screen.ConferenceRegistrationScreen,
        component = { ConferenceRegistrationScreen(navController = it) }),
    NavItem(
        screen = Screen.PaymentStatusScreen,
        component = { PaymentStatusScreen(navController = it) }),
)

fun kycScreens(): List<NavItem> = listOf(
    NavItem(screen = Screen.SplashScreen, component = { SplashScreen(navController = it) }),
    NavItem(screen = Screen.SignUpScreen, component = { SignUpScreen(navController = it) }),
    NavItem(screen = Screen.SignInScreen, component = { LoginScreen(navController = it) }),
    NavItem(
        screen = Screen.ExhibitionRegistrationScreen,
        component = { ExhibitionRegistrationScreen(navController = it) }),
    NavItem(screen = Screen.OtpScreen, component = { OtpScreen(navController = it) }),
    NavItem(
        screen = Screen.ForgotPasswordScreen,
        component = { ForgotPasswordScreen(navController = it) }),


    NavItem(
        screen = Screen.UserDetailsScreen,
        component = { UserDetailsScreen(navController = it) }),
)
