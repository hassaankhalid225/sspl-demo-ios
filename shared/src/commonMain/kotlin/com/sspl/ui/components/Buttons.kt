package com.sspl.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun AppRoundedCornerButton(modifier: Modifier = Modifier,title:String,isEnabled:Boolean=true,onClick:()->Unit) {
    Button(
        onClick = onClick,
        enabled =isEnabled ,
        shape = RoundedCornerShape(16), colors = ButtonDefaults.buttonColors(
            disabledContainerColor = Color.LightGray.copy(0.7f),
        ),
        modifier = modifier.fillMaxWidth().padding(horizontal = 10.dp)
    ) {
        AppTextLabel(
            title, color = if(isEnabled) Color.White else Color.DarkGray
        )
    }
}
@Composable
fun AppRoundedCornerButton(modifier: Modifier = Modifier,onClick:()->Unit,isEnabled:Boolean=true,content: @Composable RowScope.() -> Unit) {
    Button(
        onClick = onClick,
        enabled = isEnabled, colors = ButtonDefaults.buttonColors(
            disabledContainerColor = Color.LightGray.copy(0.7f),
        ),
        shape = RoundedCornerShape(16),
        modifier = modifier.fillMaxWidth().padding(horizontal = 10.dp)
    ) {
        content()
    }
}

@Composable
fun ButtonWithProgress(
    isEnabled: Boolean,
    isLoading: Boolean,
    buttonText: String,
    modifier: Modifier=Modifier,
    onClick: () -> Unit
) {
    AppRoundedCornerButton(
        onClick = { onClick() },
        isEnabled = isEnabled,
        modifier = modifier
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            AppTextLabel(
                text = buttonText,
                color = if (isEnabled) Color.White else Color.DarkGray
            )

            // Use AnimatedVisibility for the CircularProgressIndicator
            AnimatedVisibility(visible = isLoading) {
                Row {
                    Spacer(Modifier.size(8.dp))
                    CircularProgressIndicator(
                        color = Color.White,
                        modifier = Modifier.size(20.dp),
                        strokeWidth = 2.dp
                    )
                }
            }
        }
    }
}