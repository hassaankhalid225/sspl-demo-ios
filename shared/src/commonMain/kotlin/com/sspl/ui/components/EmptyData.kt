package com.sspl.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sspl.resources.Res
import com.sspl.resources.add_icon
import com.sspl.resources.arrow_no_record
import com.sspl.resources.no_record_found
import org.jetbrains.compose.resources.painterResource


@Composable
fun NoItems(
    modifier: Modifier = Modifier,
    title: String? = "Start by adding a new item",
    showFloatingActionButton: Boolean = false,
    onFloatingActionButtonClick: () -> Unit = {},
) {
    Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(
            verticalArrangement = Arrangement.spacedBy(3.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxWidth()
        ) {
            Spacer(modifier = Modifier.weight(0.5f))
            Image(
                painter = painterResource (Res.drawable.no_record_found),
                contentDescription = null,
                modifier = Modifier.size(150.dp)
            )
            Text(
                title?:"",
                 fontSize = 12.sp,
                color = Color(0xFF727272)
            )
            Spacer(modifier = Modifier.weight(0.15f))
            Row(modifier = Modifier.fillMaxWidth(0.7f), horizontalArrangement = Arrangement.End) {
                Image(
                    painter = painterResource( Res.drawable.arrow_no_record),
                    contentDescription = null,
                    modifier = Modifier.size(80.dp, 120.dp)
                )
            }
            AnimatedVisibility(showFloatingActionButton,modifier=Modifier.align(Alignment.End)
                .padding(end = 15.dp)) {
                AppFloatingActionButton(
                    modifier = Modifier
                    , onButtonClick = onFloatingActionButtonClick
                )
            }
            Spacer(modifier = Modifier.weight(0.10f))

        }
    }
}

@Composable
fun AppFloatingActionButton(
    modifier: Modifier = Modifier, onButtonClick: () -> Unit = {}
) {
    Image(
        painter = painterResource(Res.drawable.add_icon),
        contentDescription = null,
        modifier = modifier
            .clickable(onClick = onButtonClick)
            .border(3.dp, color = Color(0xFFFFE5EF), CircleShape)
            .padding(2.dp)
            .size(50.dp)
            .clip(
                CircleShape
            )
            .background(brush = Brush.verticalGradient(
                colors = listOf(
                    Color(
                        0xFFFACAD7
                    ), Color(0xFFEC96AE)
                ))
            ).padding(12.dp)

    )
}