package com.sspl.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.TransformedText
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.sspl.ui.components.countrypicker.Country
import com.sspl.ui.components.countrypicker.CountryCodePicker
import com.sspl.ui.components.countrypicker.CountryName

@Composable
fun PhoneWithCountrySelector(
    modifier: Modifier = Modifier,
    selectedCountry: Country,
    phone: String,showTransformation:Boolean=true,
    errorMessage: String?,
    onPhoneNumberChange: (String) -> Unit,
    showCountryChooser: Boolean,
    onCountryChange: (Country) -> Unit,
    toggleCountryPicker: () -> Unit
) {
    Row(modifier) {
        androidx.compose.animation.AnimatedVisibility(showCountryChooser) {
            CountryCodePicker(
                onCountrySelected = { country -> onCountryChange.invoke(country) },
                onDismiss = { toggleCountryPicker() },
            )

        }
        PhoneNumberTextField(selectedCountry = selectedCountry,
            phoneNumber = phone,showTransformation=showTransformation,
            errorMessage = errorMessage,
            onPhoneNumberChange = {
                onPhoneNumberChange.invoke(it)
            },
            toggleCountryPicker = {
                toggleCountryPicker.invoke()
            })
    }
}

@Composable
fun CountrySelectorField(
    modifier: Modifier = Modifier,
    country: String,
    errorMessage: String?,
    showCountryChooser: Boolean,
    onCountryChange: (Country?) -> Unit,
    toggleCountryPicker: () -> Unit
) {
    Row(modifier) {
        androidx.compose.animation.AnimatedVisibility(showCountryChooser) {
            CountryCodePicker(showCountryCodes = false,
                onCountrySelected = { country -> onCountryChange.invoke(country) },
                onDismiss = {
                    onCountryChange.invoke(null)
                            },
            )

        }
        CountryTextField(
            countryName = country,
            errorMessage = errorMessage,
            toggleCountryPicker = {
                toggleCountryPicker.invoke()
            })
    }
}

@Composable
fun PhoneNumberTextField(
    modifier: Modifier = Modifier,showTransformation: Boolean=true,
    selectedCountry: Country = Country("Pakistan", "PK", "+92", "🇵🇰", CountryName.PAKISTAN),
    phoneNumber: String,
    onPhoneNumberChange: (String) -> Unit,
    toggleCountryPicker: () -> Unit,
    placeholder: String = "Mobile Number",
    shape: Shape = RoundedCornerShape(22),
    keyboardType: KeyboardType = KeyboardType.Phone,
    errorMessage: String? = null,
    isError: Boolean = errorMessage != null,
    onFocusLost: () -> Unit = {}
) {
    val focusManager = LocalFocusManager.current

    OutlinedTextField(
        value = phoneNumber,
        onValueChange = { newText ->
            val trimmedText = newText.replace("\\s".toRegex(), "")
            onPhoneNumberChange(trimmedText)
        },
        modifier = modifier
            .fillMaxWidth()
            .onFocusChanged { if (!it.isFocused) onFocusLost() },
        placeholder = {
            Text(
                text = placeholder,
                style = MaterialTheme.typography.bodySmall
            )
        },
        shape = shape,
        keyboardOptions = KeyboardOptions(
            keyboardType = keyboardType,
            imeAction = ImeAction.Done
        ),
        keyboardActions = KeyboardActions(
            onDone = { focusManager.clearFocus() }
        ),
        singleLine = true,
        isError = isError,
        supportingText = if (isError && !errorMessage.isNullOrBlank()) {
            { Text(text = errorMessage) }
        } else null,
        leadingIcon = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .clickable(onClick = toggleCountryPicker)
                    .padding(start = 5.dp)
            ) {
                Spacer(Modifier.width(5.dp))
                AppTextTitle(
                    text = selectedCountry.flag,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = selectedCountry.code,
                    modifier = Modifier.padding(end = 4.dp)
                )
                Icon(
                    imageVector = Icons.Default.ArrowDropDown,
                    contentDescription = "Select country code"
                )
            }
        },
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = MaterialTheme.colorScheme.primary,
            unfocusedBorderColor = MaterialTheme.colorScheme.outline,
            errorBorderColor = MaterialTheme.colorScheme.error,
            focusedLeadingIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
            unfocusedLeadingIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
            focusedTrailingIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
            unfocusedTrailingIconColor = MaterialTheme.colorScheme.onSurfaceVariant
        ),
        visualTransformation =if (showTransformation) PhonePrefixTransformation(selectedCountry.dialCode) else VisualTransformation.None
    )
}

@Composable
fun CountryTextField(
    modifier: Modifier = Modifier,
    countryName: String,
    toggleCountryPicker: () -> Unit,
    placeholder: String = "Select Country",
    shape: Shape = RoundedCornerShape(22),
    errorMessage: String? = null,
    isError: Boolean = errorMessage != null,

    ) {


    OutlinedTextField(
        value = countryName, readOnly = true,
        onValueChange = {

        },
        modifier = modifier
            .fillMaxWidth()
       ,  placeholder = {
            Text(
                text = placeholder, textAlign = TextAlign.Start,
                style = MaterialTheme.typography.bodySmall,modifier=modifier.fillMaxWidth().clickable(onClick = toggleCountryPicker)
            )
        },
        shape = shape,
        singleLine = true,
        isError = isError,
        supportingText = if (isError && !errorMessage.isNullOrBlank()) {
            { Text(text = errorMessage) }
        } else null,
        trailingIcon = {
                Icon(
                    imageVector = Icons.Default.ArrowDropDown,
                    contentDescription = "Select country code",modifier = Modifier.clickable(onClick = toggleCountryPicker)
                )

        },
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = MaterialTheme.colorScheme.primary,
            unfocusedBorderColor = MaterialTheme.colorScheme.outline,
            errorBorderColor = MaterialTheme.colorScheme.error,
            focusedLeadingIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
            unfocusedLeadingIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
            focusedTrailingIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
            unfocusedTrailingIconColor = MaterialTheme.colorScheme.onSurfaceVariant
        ),

    )
}


// Assuming you have this class defined
class PhonePrefixTransformation(private val prefix: String) : VisualTransformation {
    override fun filter(text: AnnotatedString): TransformedText {
        val prefixOffset = prefix.length + 1 // +1 for the space
        val annotatedString = buildAnnotatedString {
            append(prefix)
            append(" ")
            append(text)
        }
        return TransformedText(annotatedString, PhonePrefixOffsetMapping(prefixOffset))
    }

    private class PhonePrefixOffsetMapping(private val prefixOffset: Int) : OffsetMapping {
        override fun originalToTransformed(offset: Int): Int {
            return offset + prefixOffset
        }

        override fun transformedToOriginal(offset: Int): Int {
            return (offset - prefixOffset).coerceAtLeast(0)
        }
    }
}