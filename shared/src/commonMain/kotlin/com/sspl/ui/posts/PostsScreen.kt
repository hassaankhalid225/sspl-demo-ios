package com.sspl.ui.posts

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.sspl.core.ApiStates
import com.sspl.core.usecases.Comment
import com.sspl.resources.Res
import com.sspl.resources.demo4
import com.sspl.theme.dividerColor
import com.sspl.ui.components.AppTextTitle
import com.sspl.ui.components.BetterModalBottomSheet
import com.sspl.ui.components.Error
import com.sspl.ui.components.AppProgressBar
import com.sspl.ui.components.LoadMoreLazyColumn
import com.sspl.ui.components.NoItems
import org.koin.compose.viewmodel.koinViewModel

/**
 * Created by M Imran
 * Senior Software Engineer at
 * BhimSoft on 09/01/2025.
 * se.muhammadimran@gmail.com
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PostScreen(
    navController: NavController,
    modifier: Modifier = Modifier,
    viewModel: PostViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val isRefreshing by viewModel.isRefreshing.collectAsStateWithLifecycle()
    val isLoadingMore by viewModel.isLoadingMore.collectAsStateWithLifecycle()
    val postDetails by viewModel.postComments.collectAsStateWithLifecycle()
    val showBottomSheet by viewModel.showBottomSheet.collectAsStateWithLifecycle()

    if (showBottomSheet) {
        CommentsBottomSheet(postDetails = postDetails, onAddNewComment = {
            viewModel.onEvent(
                PostScreenEvents.CommentPost(
                    1
                )
            )
        }, onDismiss = { viewModel.onEvent(PostScreenEvents.ToggleBottomSheet) })
    }

    when (uiState) {
        is ApiStates.Loading -> {
            AppProgressBar()
        }

        is ApiStates.Failure -> {
            NoItems()
        }

        ApiStates.Idle -> {
            // Do nothing, waiting for data
        }

        is ApiStates.Success -> {
            PullToRefreshBox(
                isRefreshing = isRefreshing,
                onRefresh = { viewModel.onEvent(PostScreenEvents.RefreshPosts) },
                modifier = modifier
            ) {
                (uiState as ApiStates.Success).data?.let {
                    LoadMoreLazyColumn(
                        items = it,
                        isLoadingMore = isLoadingMore,
                        onLoadMore = { viewModel.onEvent(PostScreenEvents.LoadMorePosts) },
                        itemContent = { post ->
                            PostItem(post = post,
                                modifier = Modifier,
                                onLikePost = { viewModel.onEvent(PostScreenEvents.LikePost(post.postId)) },
                                onItemClick = {
//                                    navController.currentBackStackEntry?.savedStateHandle?.set(
//                                        "postId", post.postId
//                                    )
//                                    navController.navigate(Screen.PostDetailScreen.route)
                                }, onFirstImageClick = {

                                },onSecondImageClick = {

                                },
                                onDislikePost = {
                                    viewModel.onEvent(
                                        PostScreenEvents.DislikePost(
                                            post.postId
                                        )
                                    )
                                },
                                onLoadComments = {
                                    viewModel.onEvent(PostScreenEvents.LostCommentsForPost(post.postId))
                                })
                        },
                        modifier = modifier.fillMaxSize().padding(vertical = 4.dp),
                        contentPadding = PaddingValues(vertical = 4.dp),
                        verticalArrangement = Arrangement.spacedBy(2.dp)
                    )
                }
            }
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CommentsBottomSheet(
    modifier: Modifier = Modifier,
    postDetails: ApiStates<List<Comment>>,
    onDismiss: () -> Unit,
    onAddNewComment: (String) -> Unit = {}
) {

    val sheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = true,)
    BetterModalBottomSheet(
        onDismissRequest = onDismiss, sheetState = sheetState, containerColor = Color.White, modifier = modifier
    ) {

            Column (modifier=Modifier.fillMaxHeight(0.85f)){

                when (postDetails) {
                    is ApiStates.Failure -> {
                        Error(
                            modifier = Modifier.fillMaxSize(), error = postDetails.error
                        )
                    }

                    ApiStates.Loading -> {
                        AppProgressBar()
                    }

                    ApiStates.Idle -> {
                        // Do nothing, waiting for data
                    }

                    is ApiStates.Success -> {
                        postDetails.data?.let {
                            CommentsContent(it, onAddNewComment = { newComment ->
                                onAddNewComment(newComment)
                            })
                        }
                    }
                }
            }

    }
}


@Composable
fun CommentsContent(
    initialComments: List<Comment>, onAddNewComment: (String) -> Unit
) {
    var comments by remember { mutableStateOf(initialComments) }

    Column(
        modifier = Modifier.fillMaxSize().navigationBarsPadding().imePadding()
            .padding(horizontal = 8.dp), verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        AppTextTitle(
            "Comments", fontSize = 20.sp, modifier = Modifier.padding(16.dp)
        )
        LazyColumn(
            modifier = Modifier.weight(1f).padding(horizontal = 8.dp),
        ) {
            items(comments) { comment ->
                CommentItem(comment)
                HorizontalDivider(
                    Modifier.fillMaxWidth().padding(vertical = 8.dp), color = dividerColor
                )
            }
        }

        CommentEditor(onAddComment = { newCommentContent ->
            val newComment = Comment(
                userName = "Current User",
                userAvatar = Res.drawable.demo4,
                content = newCommentContent,
            )
            comments = comments + newComment
            onAddNewComment(newComment.content)
        })
    }
}


