package com.sspl.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.sspl.core.models.AddOn
import com.sspl.core.models.AddOnSet
import com.sspl.theme.orange

/**
 * Created by M Imran
 * Senior Software Engineer at
 * BhimSoft on 31/01/2025.
 * se.muhammadimran@gmail.com
 */
@Composable
fun ConferenceAddsOn(addOnSet: List<AddOnSet>?) {
    if (addOnSet.isNullOrEmpty()) return

    Column(modifier = Modifier.fillMaxWidth()) {
        addOnSet.forEach { addOnSet ->
            AppTextSubTitle(
                text = addOnSet.title.orEmpty().trim(),
                color = orange
            )
            addOnSet.addOns?.forEach { addOn ->
                Spacer(modifier = Modifier.size(4.dp))
                AddOnItem(addOn)
            }
        }
    }
}

@Composable
private fun AddOnItem(addOn: AddOn) {
    Row(modifier = Modifier.fillMaxWidth()) {
        Column(horizontalAlignment = Alignment.Start) {
            AppTextLabel(text = addOn.title.orEmpty().trim())
            if(!addOn.subtitle.isNullOrEmpty()){
                AppTextBody(text = addOn.subtitle.trim())
            }
        }
    }
}