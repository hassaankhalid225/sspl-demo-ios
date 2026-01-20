package com.sspl.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import coil3.compose.LocalPlatformContext
import coil3.compose.rememberAsyncImagePainter
import coil3.request.ImageRequest
import coil3.request.crossfade
import com.sspl.resources.Res
import com.sspl.resources.ad_sspl
import com.sspl.resources.place_holder
import com.sspl.resources.visibility_icon
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.painterResource


@Composable
fun AppAsyncImage(
    model: Any,
    modifier: Modifier = Modifier,
    contentDescription: String? = null,
    contentScale: ContentScale = ContentScale.FillBounds,
    errorImage: DrawableResource = Res.drawable.place_holder,
) {
    AsyncImage(
        model = ImageRequest.Builder(LocalPlatformContext.current).data(model).crossfade(true)
            .crossfade(500).build(),
        contentScale = contentScale, modifier = modifier, contentDescription = contentDescription,
        error = rememberAsyncImagePainter(errorImage),
    )
}


@Composable
fun RoundedImageView(
    modifier: Modifier = Modifier,
    image: Any,
    cornerRadius: Int = 12,
    contentScale: ContentScale = ContentScale.Crop
) {
    AsyncImage(
        model = ImageRequest.Builder(LocalPlatformContext.current).data(image).crossfade(true)
            .crossfade(500).build(),
        contentDescription = null,
        error = rememberAsyncImagePainter(Res.drawable.place_holder),
        contentScale = contentScale,
        modifier = modifier
            .clip(RoundedCornerShape(percent = cornerRadius))
    )
}
