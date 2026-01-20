package com.sspl.ui.feedback

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.sspl.core.ApiStates
import com.sspl.core.models.Conference
import com.sspl.core.models.FeedbackForm
import com.sspl.core.models.Option
import com.sspl.core.models.QuestionType
import com.sspl.core.models.Statement
import com.sspl.resources.Res
import com.sspl.resources.feedback_sub_title
import com.sspl.resources.whats_on_your_mind
import com.sspl.ui.components.AppProgressBar
import com.sspl.ui.components.AppTextLabel
import com.sspl.ui.components.AppTextSubTitle
import com.sspl.ui.components.ButtonWithProgress
import com.sspl.ui.components.Error
import com.sspl.ui.postcreation.DescriptionTextEditor
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel


@Composable
fun FeedbackScreen(navController: NavController, viewModel: FeedbackViewModel = koinViewModel()) {
    val conferenceID =
        navController.previousBackStackEntry?.savedStateHandle?.get<Long>("conferenceID")?:return
    val scaffoldState = remember { SnackbarHostState() }

    //Session ID will be null if user is coming from Conference directly.
    val sessionID = navController.previousBackStackEntry?.savedStateHandle?.get<Long>("sessionID")
    println(">>>Conference ID:$conferenceID , sessionID=$sessionID")
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
     LaunchedEffect(Unit) {
        viewModel.setConferenceId(conferenceID)
         viewModel.setSessionId(sessionID)
    }
    LaunchedEffect(Unit) {
        viewModel.isFeedbackSubmitted.collect{status->
            when(status){
                is ApiStates.Failure -> scaffoldState.showSnackbar(status.error.message.orEmpty())
                ApiStates.Loading -> Unit
                is ApiStates.Success -> {
                    scaffoldState.showSnackbar(status.data?.message.orEmpty())
                    navController.popBackStack()
                }
                ApiStates.Idle -> {} // Do nothing
            }

        }
    }
    val feedbackForm = viewModel.feedbackFormResponse.collectAsStateWithLifecycle().value
    when (feedbackForm) {
        is ApiStates.Loading -> {
            AppProgressBar()
        }

        is ApiStates.Failure -> {
            com.sspl.ui.components.Error(Modifier.fillMaxSize(),feedbackForm.error)
        }

        ApiStates.Idle -> {
            // Do nothing, waiting for data
        }

        is ApiStates.Success -> {
            Scaffold(
                snackbarHost = { SnackbarHost(scaffoldState) },
                containerColor = Color.Transparent
            ) { _ ->
                feedbackForm.data?.let { form ->
                    FeedbackScreenContent(
                        uiState = uiState,
                        feedbackForm = form,
                        onEvent = viewModel::onEvent
                    )
                }
            }
        }
    }

}


@Composable
fun FeedbackScreenContent(
    uiState: FeedbackUiState,
    feedbackForm: FeedbackForm,
    onEvent: (FeedbackUiEvents) -> Unit = {}
) {

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier.fillMaxSize().background(Color.White).verticalScroll(
            rememberScrollState()
        ).padding(16.dp)
    ) {
        Spacer(Modifier.height(4.dp))
        AppTextSubTitle(
            modifier = Modifier.padding(horizontal = 2.dp),
            text = stringResource(Res.string.feedback_sub_title),
            fontSize = 15.sp
        )
        feedbackForm.questionGroups.forEach { group ->
            //Spacer(Modifier.height(8.dp))
            ActivityCard(
                modifier = Modifier.fillMaxWidth(),
                title = group.title,
                content = {
                    group.questions.forEach { question ->
                        when (question.type) {
                            QuestionType.EVALUATION -> {
                                question.statements.forEach {
                                    QuestionSelector(
                                        statement = it,
                                        options = question.options,
                                        onAnswerSelected = { statement, option ->
                                            onEvent(
                                                FeedbackUiEvents.OnAnswerSelected(
                                                    statement, option
                                                )
                                            )
                                        })
                                }
                            }

                            QuestionType.YESNO -> {
                                question.statements.forEach {
                                    YesNoQuestion(
                                        statement = it,
                                        options = question.options,
                                        onAnswerSelected = { statement, option ->

                                            onEvent(
                                                FeedbackUiEvents.OnAnswerSelected(
                                                    statement, option
                                                )
                                            )
                                        }
                                    )
                                }
                            }

                            QuestionType.COMMENT -> {
                                CommentTextEditor(
                                     shape = RoundedCornerShape(4.dp),
                                    modifier = Modifier.fillMaxWidth()
                                        .heightIn(min = 100.dp, max = 300.dp),
                                    placeholderText = question.statement,
                                    onTextChanged = {
                                        onEvent(
                                            FeedbackUiEvents.OnAnswerSelected(
                                                statement = Statement(id = question.id, statement = question.statement),
                                                comment = it // Pass the comment as the value
                                            )
                                        )                                    })
                            }
                        }
                    }
                })
            Spacer(Modifier.height(4.dp))
        }
        ButtonWithProgress(
            isEnabled = uiState.isEnabled,
            isLoading = uiState.isLoading,
            modifier = Modifier.imePadding(),
            buttonText = "Submit",
            onClick = {
                onEvent(FeedbackUiEvents.OnSubmitClicked)
            })

    }

}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuestionSelector(
    modifier: Modifier = Modifier,
    statement: Statement,
    options: List<Option>,
    onAnswerSelected: (Statement, Option) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    var selectedOption by remember { mutableStateOf("") }

    Column(
        modifier = modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        AppTextLabel(
            text = statement.statement
        )

        ExposedDropdownMenuBox(expanded = expanded, onExpandedChange = { expanded = !expanded }) {
            OutlinedTextField(
                value = selectedOption,
                onValueChange = {},
                readOnly = true,
                shape = RoundedCornerShape(8.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    unfocusedBorderColor = MaterialTheme.colorScheme.outline,
                    errorBorderColor = MaterialTheme.colorScheme.error,
                    focusedLeadingIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    unfocusedLeadingIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    focusedTrailingIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    unfocusedTrailingIconColor = MaterialTheme.colorScheme.onSurfaceVariant
                ),
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                modifier = Modifier.menuAnchor(MenuAnchorType.PrimaryNotEditable, true)
                    .fillMaxWidth(),
                placeholder = {
                    Text("Select an option", style = MaterialTheme.typography.bodySmall)

                })

            ExposedDropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                options.forEach { option ->
                    DropdownMenuItem(text = { Text(option.label) }, onClick = {
                        selectedOption = option.label
                        expanded = false
                        onAnswerSelected(statement, option)
                    })
                }
            }
        }
    }
}


@Composable
fun ActivityCard(modifier: Modifier = Modifier, title: String, content: @Composable () -> Unit) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(8.dp),
        border = BorderStroke(1.dp, Color.LightGray.copy(alpha = 0.2f)),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.5.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            AppTextSubTitle(text = title, modifier.align(Alignment.Start))
            Spacer(Modifier.height(4.dp))
            content()
        }
    }
}

@Composable
private fun YesNoQuestion(
    modifier: Modifier = Modifier,
    statement: Statement,
    options: List<Option>,
    onAnswerSelected: (Statement, Option) -> Unit
) {
    var selectedOption by remember { mutableStateOf<Option?>(null) }

    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        AppTextLabel(
            text = statement.statement,
        )

        Row(
            modifier = Modifier.selectableGroup(),
            horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically
        ) {
            options.forEach { option ->
                RadioButton(
                    selected = selectedOption == option,
                    onClick = {
                        selectedOption = option
                        onAnswerSelected(statement, option)
                    },
                    modifier = Modifier.semantics { contentDescription = option.label }
                )
                Text(
                    text = option.label,
                    modifier = Modifier.clickable {
                        selectedOption = option
                        onAnswerSelected(statement, option)
                    }
                )
            }
        }
    }
}


@Composable
private fun RadioButton(
    text: String? = null, selected: Boolean, onClick: () -> Unit
) {
    Row(
        Modifier.selectable(
            selected = selected, onClick = onClick, role = Role.RadioButton
        ).padding(8.dp), verticalAlignment = Alignment.CenterVertically
    ) {
        RadioButton(
            selected = selected,
            onClick = null // null because we're handling the click on the parent
        )
        text?.let {
            Spacer(modifier = Modifier.width(8.dp))
            Text(text = text)
        }
    }
}

@Composable
fun CommentTextEditor(
    modifier: Modifier = Modifier, shape: RoundedCornerShape= RoundedCornerShape(16.dp), placeholderText:String=stringResource(Res.string.whats_on_your_mind),    onTextChanged: (String) -> Unit
) {
    var text by remember { mutableStateOf("") }
    Card(
        colors = CardDefaults.cardColors(
            containerColor = Color.LightGray.copy(alpha = 0.2f)
        ),
        shape = shape,
        border = BorderStroke(0.7.dp, Color.LightGray),
        modifier = Modifier.imePadding()
    ) {
        TextField(
            value = text,
            onValueChange = {
                onTextChanged(it)
                text = it
            },
            modifier = modifier,
            placeholder = {
                AppTextLabel(
                    placeholderText, color = Color.Gray
                )
            },
            textStyle = TextStyle(fontSize = 18.sp, color = Color.Black, textDecoration = null),
            colors = TextFieldDefaults.colors(
                focusedContainerColor = Color.Transparent,
                unfocusedContainerColor = Color.Transparent,
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
                disabledIndicatorColor = Color.Transparent
            ),
            shape = RoundedCornerShape(16.dp)
        )
    }
}






