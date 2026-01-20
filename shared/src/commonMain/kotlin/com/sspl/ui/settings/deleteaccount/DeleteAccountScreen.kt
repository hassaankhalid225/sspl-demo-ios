package com.sspl.ui.settings.deleteaccount

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.sspl.resources.Res
import com.sspl.resources.are_you_sure
import com.sspl.resources.delete_account
import com.sspl.resources.delete_account_permanently_message
import com.sspl.resources.are_you_sure_message
import com.sspl.resources.you_guest_user
import com.sspl.resources.yes
import com.sspl.theme.platformConfig
import com.sspl.ui.components.AppProgressBar
import com.sspl.ui.components.AppRoundedCornerButton
import com.sspl.ui.components.AppTextBody
import com.sspl.ui.components.AppTextLabel
import com.sspl.ui.components.BetterModalBottomSheet
import com.sspl.utils.EMAIL_GUEST_USER
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel

/**
 * Created by M Imran
 * Senior Software Engineer at
 * BhimSoft on 10/02/2025.
 * se.muhammadimran@gmail.com
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DeleteAccountScreen(
    navController: NavController,
    onParentGraph: () -> Unit = {},
    viewModel: DeleteAccountViewModel = koinViewModel()
) {
    val showSheet = remember {
        mutableStateOf(false)
    }
    val scope = rememberCoroutineScope()

    val uiState = viewModel.uiState.collectAsState()
    ScreenContent(
        showSheet = showSheet,
        viewModel = viewModel,
        scope = scope,
        uiState = uiState
    ) {
        onParentGraph()
    }

}

@Composable
private fun ScreenContent(
    showSheet: MutableState<Boolean>,
    viewModel: DeleteAccountViewModel,
    scope: CoroutineScope,
    uiState: State<UIState>,
    onLogout: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxSize()
            .padding(all = 16.dp),
        colors = CardDefaults.cardColors().copy(
            containerColor = MaterialTheme.colorScheme.onPrimaryContainer
        )
    ) {
        Column(
            modifier = Modifier.fillMaxWidth()
        ) {
            if (uiState.value.isLoading) {
                AppProgressBar(
                    modifier = Modifier.align(alignment = Alignment.CenterHorizontally)
                )
            }
            uiState.value.error?.let {
                AppTextBody(text = it.message)
            }
            uiState.value.user?.let {
                val isGuest = it.account?.email?.contains(EMAIL_GUEST_USER) == true
                DeleteAccountContent(showSheet, isGuest)
            }

            uiState.value.deleteAcRes?.let {
                AppTextBody(
                    modifier = Modifier.fillMaxWidth(.8F)
                        .align(alignment = Alignment.CenterHorizontally), text = it.message
                )
                onLogout.invoke()
            }
        }

    }
    DeleteAccountSheet(showSheet, scope, viewModel)
}

@Composable
private fun ColumnScope.DeleteAccountContent(
    showSheet: MutableState<Boolean>,
    isGuestUser: Boolean
) {
    AppTextBody(
        text = if (isGuestUser)
            stringResource(Res.string.you_guest_user)
        else stringResource(
            Res.string.delete_account_permanently_message
        )
    )
    AppRoundedCornerButton(
        modifier = Modifier.fillMaxWidth(.5F).align(alignment = Alignment.End),
        title = stringResource(Res.string.delete_account),
        isEnabled = isGuestUser.not()
    ) {
        showSheet.value = showSheet.value.not()
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DeleteAccountSheet(
    showSheet: MutableState<Boolean>,
    scope: CoroutineScope,
    viewModel: DeleteAccountViewModel
) {
    AnimatedVisibility(visible = showSheet.value) {
        BetterModalBottomSheet(
            sheetState = rememberModalBottomSheetState(
                skipPartiallyExpanded = true,
            ),
            onDismissRequest = {
                scope.launch { showSheet.value = false }
            }
        ) {

            val screenHeight = MaterialTheme.platformConfig.screenHeight
            val maxHeight = screenHeight * 0.3f
            Column(
                modifier = Modifier.fillMaxWidth().height(maxHeight).padding(horizontal = 24.dp)
            ) {
                AppTextLabel(text = stringResource(Res.string.are_you_sure))
                Spacer(modifier = Modifier.size(8.dp))
                AppTextBody(text = stringResource(Res.string.are_you_sure_message))
                Spacer(modifier = Modifier.size(16.dp))
                AppTextLabel(
                    modifier = Modifier.align(alignment = Alignment.End).clickable {
                        showSheet.value = false
                        viewModel.deleteAccount()
                    },
                    text = stringResource(Res.string.yes)
                )
            }
        }
    }
}