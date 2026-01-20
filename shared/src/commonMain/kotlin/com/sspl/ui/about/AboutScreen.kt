package com.sspl.ui.about

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.items
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.sspl.core.models.SocietyMember
import com.sspl.ui.components.AppHorizontalPager
import com.sspl.ui.components.AppTextBody
import com.sspl.ui.components.AppTextLabel
import com.sspl.ui.components.AppTextSubTitle
import org.jetbrains.compose.resources.painterResource
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun AboutScreen(navController: NavController, viewModel: AboutSocietyViewModel = koinViewModel()) {
    val members = viewModel.executiveMembers.collectAsState()
    val executiveMembers = viewModel.officeBearers.collectAsState()
    LazyColumn(modifier = Modifier.fillMaxSize()) {
        item {
            OfficeBearers(members.value)
        }
        item {

            LazyVerticalStaggeredGrid(
                columns = StaggeredGridCells.Fixed(2), // 3 columns
                modifier = Modifier.fillMaxWidth()
                    .wrapContentHeight()
                    .heightIn(min = 200.dp, max = 2400.dp),
                contentPadding = PaddingValues(horizontal = 20.dp, vertical = 16.dp),
                verticalItemSpacing = 8.dp,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                userScrollEnabled = false
            ) {
                items(executiveMembers.value) { item ->
                    ExecutiveMemberCard(
                        member = item
                    )
                }

            }
        }
    }

}

@Composable
private fun OfficeBearers(members: List<SocietyMember>) {
    val pagerState = rememberPagerState(pageCount = {
        members.size
    })
    AppHorizontalPager(
        list = members,
        pagerState = pagerState,
        showIndicator = false
    ) { modifier, item, page ->
        OfficeBearerMemberCard(
            modifier = modifier,
            member = item
        )
    }
}

@Composable
fun MessageParagraph(
    text: String,
    modifier: Modifier = Modifier,
) {
    val firstLetter = text.take(1)
    val restOfText = text.drop(1)

    Row(modifier = modifier) {
        AppTextSubTitle(
            text = firstLetter, style = TextStyle(
                fontSize = 15.sp,
            ), modifier = Modifier.alignByBaseline()
        )
        AppTextBody(
            text = restOfText, modifier = Modifier.alignByBaseline()
        )
    }
}

@Composable
fun ExecutiveMemberCard(
    member: SocietyMember,
) {
    Card(
        colors = CardDefaults.cardColors(
            containerColor = Color.Transparent
        ),
        border = BorderStroke(1.dp, Color.LightGray.copy(alpha = 0.3f)),
        shape = RoundedCornerShape(8.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            Image(
                painter = painterResource(member.picture),
                contentDescription = null,
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(4)),
                contentScale = ContentScale.FillWidth
            )
            AppTextLabel(
                text = member.name + " " + member.designation,
                modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp, horizontal = 4.dp)
            )
        }
    }
}

@Composable
fun OfficeBearerMemberCard(
    modifier: Modifier,
    member: SocietyMember
) {
    Card(
        colors = CardDefaults.cardColors(
            containerColor = Color.Transparent
        ),
        border = BorderStroke(1.dp, Color.LightGray.copy(alpha = 0.3f)),
        shape = RoundedCornerShape(8.dp),
        modifier = modifier.then(Modifier.fillMaxWidth())
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Image(
                painter = painterResource(member.picture),
                contentDescription = null,
                modifier = Modifier
                    .fillMaxWidth(.4F)
                    .clip(RoundedCornerShape(4)),
                contentScale = ContentScale.FillWidth
            )
            Column(
                modifier = Modifier.fillMaxWidth(1F).padding(vertical = 10.dp, horizontal = 12.dp)
            ) {
                AppTextSubTitle(
                    text = member.designation,
                    color = MaterialTheme.colorScheme.tertiary
                )
                Spacer(Modifier.height(2.dp))
                AppTextLabel(text = member.name)
            }
        }
    }
}