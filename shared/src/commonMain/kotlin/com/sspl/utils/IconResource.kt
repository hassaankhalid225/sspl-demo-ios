package com.sspl.utils

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.painterResource

/**
 * Created by M Imran
 * Senior Software Engineer at
 * BhimSoft on 02/01/2025.
 * se.muhammadimran@gmail.com
 */
class IconResource private constructor(
    private val drawable: DrawableResource? = null,
    private val imageVector: ImageVector? = null
) {

    @Composable
    fun icon(): Painter {
        drawable?.let {
            return painterResource(drawable)
        }
        return rememberVectorPainter(image = imageVector!!)
    }

    companion object {
        fun fromDrawableResource(drawable: DrawableResource): IconResource {
            return IconResource(drawable, null)
        }

        fun fromImageVector(imageVector: ImageVector): IconResource {
            return IconResource(null, imageVector)
        }
    }
}