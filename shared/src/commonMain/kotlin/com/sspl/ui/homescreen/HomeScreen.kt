package com.sspl.ui.homescreen

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.sspl.Screen
import com.sspl.core.models.Banner
import com.sspl.platform.shared.UrlOpener
import com.sspl.resources.Res
import com.sspl.resources.about_us
import com.sspl.resources.exhibition
import com.sspl.resources.forum
import com.sspl.resources.ic_exhibition
import com.sspl.resources.ic_about_us
import com.sspl.resources.ic_forum
import com.sspl.resources.ic_notifications
import com.sspl.resources.ic_account_settings
import com.sspl.resources.ic_scientific_program
import com.sspl.resources.ic_user
import com.sspl.resources.ic_workshop
import com.sspl.resources.notifications
import com.sspl.resources.settings
import com.sspl.resources.profile
import com.sspl.resources.scientific_programs
import com.sspl.resources.workshops
import com.sspl.theme.boxColor
import com.sspl.theme.columnColor
import com.sspl.theme.platformConfig
import com.sspl.ui.components.AppHorizontalPager
import com.sspl.ui.components.AppTextSubTitle
import com.sspl.ui.components.GuestUserWarning
import com.sspl.ui.userdetails.ProfileViewModel
import com.sspl.utils.IconResource
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.koinInject
import org.koin.compose.viewmodel.koinViewModel

/**
 * Created by M Imran
 * Senior Software Engineer at
 * BhimSoft on 02/01/2025.
 * se.muhammadimran@gmail.com
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    navController: NavController,
    homeViewModel: HomeScreenViewModel = koinViewModel(),
    profileViewModel: ProfileViewModel = koinViewModel()
) {
    val isGuestUser = profileViewModel.isGuestUser.collectAsState()
    val urlOpener = koinInject<UrlOpener>()
    val scope = rememberCoroutineScope()
    val data = listOf(
        Item(
            title = stringResource(Res.string.scientific_programs),
            icon = IconResource.fromDrawableResource(Res.drawable.ic_scientific_program),
            toScreen = Screen.ScientificProgramsScreen
        ),
        Item(
            title = stringResource(Res.string.workshops),
            icon = IconResource.fromDrawableResource(Res.drawable.ic_workshop),
            toScreen = Screen.WorkshopsScreen
        ),
        Item(
            title = stringResource(Res.string.forum),
            icon = IconResource.fromDrawableResource(Res.drawable.ic_forum),
            toScreen = Screen.ForumScreen
        ),
        Item(
            title = stringResource(Res.string.profile),
            icon = IconResource.fromDrawableResource(Res.drawable.ic_user),
            toScreen = Screen.ProfileScreen
        ),
        Item(
            title = stringResource(Res.string.exhibition),
            icon = IconResource.fromDrawableResource(Res.drawable.ic_exhibition),
            toScreen = Screen.ExhibitionRegistrationScreen
        ),
        Item(
            title = stringResource(Res.string.about_us),
            icon = IconResource.fromDrawableResource(Res.drawable.ic_about_us),
            toScreen = Screen.AboutScreen
        ),
        Item(
            title = stringResource(Res.string.notifications),
            icon = IconResource.fromDrawableResource(Res.drawable.ic_notifications),
            toScreen = Screen.NotificationsScreen
        ),
        Item(
            title = stringResource(Res.string.settings),
            icon = IconResource.fromDrawableResource(Res.drawable.ic_account_settings),
            toScreen = Screen.SettingsScreen
        )
    )
    val banners = homeViewModel.banners.collectAsState()
    val sliderBanners = banners.value.filter { it.type == "SLIDER" || it.type == null }
    val rawBaseUrl = com.sspl.utils.BASE_URL // No trailing slash here
    
    LaunchedEffect(Unit) {
        homeViewModel.fetchBanners()
    }

    val pagerState = rememberPagerState(pageCount = {
        sliderBanners.size
    })

    LaunchedEffect(sliderBanners) {
        if (sliderBanners.isNotEmpty()) {
            while (true) {
                delay(4000L)
                val currentPage = pagerState.currentPage
                scope.launch {
                    if (currentPage + 1 == sliderBanners.size) {
                        pagerState.animateScrollToPage(0)
                    } else {
                        pagerState.animateScrollToPage(currentPage + 1)
                    }
                }
            }
        }
    }
    

    

    LazyColumn(modifier = Modifier.fillMaxSize()) {
        if (isGuestUser.value == true) {
            item {
                GuestUserWarning(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 16.dp, end = 16.dp, top = 16.dp)
                )
            }
        }
        if (sliderBanners.isNotEmpty()) {
            item {
                AppHorizontalPager(
                    list = sliderBanners,
                    pagerState = pagerState,
                    showIndicator = false
                ) { modifier, item, page ->
                    val fullImageUrl = if (item.imageUrl?.startsWith("/") == true) {
                        "$rawBaseUrl${item.imageUrl}"
                    } else {
                        "$rawBaseUrl/${item.imageUrl}"
                    }
                    
                    coil3.compose.AsyncImage(
                        model = fullImageUrl,
                        contentDescription = item.title ?: "",
                        modifier = modifier.then(
                            Modifier.fillMaxWidth().clip(
                                shape = RoundedCornerShape(8)
                            )
                        ),
                        contentScale = ContentScale.FillWidth
                    )
                }
            }
        }

        item {
            LazyVerticalGrid(
                columns = GridCells.Fixed(2), // 3 columns
                modifier = Modifier.fillMaxWidth()
                    .wrapContentHeight()
                    .heightIn(min = 200.dp, max = 2800.dp),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                userScrollEnabled = false
            ) {
                itemsIndexed(data, key = { i, _ -> i }) { index, it ->
                    SquareItem(it) {
                        if (index == 2) {
                            println("Do nothing now")
                        } else if (index == 4) {
                            // Exhibition: Always navigate to registration screen
                            navController.navigate(it.toScreen.route)
                        } else {
                            navController.navigate(it.toScreen.route)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun SquareItem(item: Item, onItemClick: () -> Unit) {
    Card(
        modifier = Modifier
            .aspectRatio(1F)
            .fillMaxWidth(),
        shape = RoundedCornerShape(8),
        colors = CardDefaults.cardColors().copy(
            containerColor = boxColor.copy(alpha = .3F)
        ),
        border = BorderStroke(width = 1.dp, color = columnColor)
    ) {
        Box(
            modifier = Modifier.fillMaxSize().clickable {
                onItemClick.invoke()
            },
            contentAlignment = Alignment.Center
        ) {
            Icon(
                modifier = Modifier.fillMaxSize(.35F),
                painter = item.icon.icon(),
                contentDescription = item.title,
                tint = MaterialTheme.colorScheme.secondary
            )
            AppTextSubTitle(
                text = item.title,
                fontSize = 11.sp,
                modifier = Modifier.padding(vertical = 12.dp)
                    .align(alignment = Alignment.BottomCenter),
                isAllCapsStyled = true,
                fontSizeCap = 15.sp
            )
        }
    }
}

private data class Item(val title: String, val icon: IconResource, val toScreen: Screen)
