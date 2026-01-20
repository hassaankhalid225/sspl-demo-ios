package com.sspl.ui.components.countrypicker

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SheetValue
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.sspl.ui.components.BetterModalBottomSheet
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CountryCodePicker(
    modifier: Modifier = Modifier,
    countries: List<Country> = allCountries,
    onCountrySelected: (Country) -> Unit,
    showCountryCodes: Boolean = true,
    onDismiss: () -> Unit
) {
    var searchQuery by remember { mutableStateOf("") }
    val filteredCountries by remember(searchQuery, countries) {
        derivedStateOf {
            countries.filter {
                it.name.contains(searchQuery, ignoreCase = true) ||
                        it.code.contains(searchQuery, ignoreCase = true) ||
                        it.dialCode.contains(searchQuery, ignoreCase = true)
            }
        }
    }
    val sheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = true,
        confirmValueChange = { it != SheetValue.Hidden })
    val coroutineScope = rememberCoroutineScope()

    BetterModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = Color.White,
        modifier = modifier
    ) {
        Column {
            SearchBar(
                query = searchQuery,
                onQueryChange = { searchQuery = it },
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
            )
            CountryList(countries = filteredCountries,showCountryCodes = showCountryCodes, onCountrySelected = { country ->
                onCountrySelected(country)
                coroutineScope.launch {
                    sheetState.hide()
                }
            })
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchBar(
    query: String, onQueryChange: (String) -> Unit, modifier: Modifier = Modifier
) {
    OutlinedTextField(
        value = query,
        onValueChange = onQueryChange,
        modifier = modifier.fillMaxWidth(),
        placeholder = { Text("Search country") },
        leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
        singleLine = true
    )
}

@Composable
fun CountryList(
    countries: List<Country>,showCountryCodes: Boolean, onCountrySelected: (Country) -> Unit
) {
    LazyColumn(modifier = Modifier.fillMaxHeight(0.5f)) {
        items(items = countries, key = { it.code }) { country ->
            CountryItem(country = country,showCountryCodes = showCountryCodes, onClick = { onCountrySelected(country) })
        }
    }
}

@Composable
fun CountryItem(
    country: Country, onClick: () -> Unit, modifier: Modifier = Modifier,showCountryCodes: Boolean
) {
    Row(
        modifier = modifier.fillMaxWidth().clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = country.flag, modifier = Modifier.padding(end = 12.dp)
        )
        if (showCountryCodes){
            Text(
                text = country.dialCode, modifier = Modifier, fontWeight = FontWeight.Bold
            )
        }
         Text(
            text = country.name, modifier = Modifier.weight(1f).padding(start =20.dp),
        )
    }
}
