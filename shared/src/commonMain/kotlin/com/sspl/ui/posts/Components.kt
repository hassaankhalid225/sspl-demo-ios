package com.sspl.ui.posts

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material3.Card
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sspl.core.models.Post
import com.sspl.core.usecases.Comment
import com.sspl.resources.Res
import com.sspl.resources.comment_outline
import com.sspl.resources.demo1
import com.sspl.resources.demo2
import com.sspl.resources.place_holder
import com.sspl.resources.thumb_down_outline
import com.sspl.resources.thumb_up_outline
import com.sspl.theme.primary
import com.sspl.ui.components.AppTextBody
import com.sspl.ui.components.AppTextLabel
import com.sspl.ui.components.AppTextSmall
import com.sspl.ui.components.AppTextSubTitle
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.painterResource


@Composable
fun PostItem(
    modifier: Modifier = Modifier,
    post: Post,
    onLikePost: () -> Unit,
    onDislikePost: () -> Unit,
    onFirstImageClick:()->Unit={},
    onSecondImageClick:()->Unit={},
    cardCornerPercentage:Int=4,
    elevation:Dp=3.dp, imageAspectRatio:Float=1f,
    onLoadComments: () -> Unit, onItemClick:()->Unit
) {
    Card(onClick = onItemClick,
        modifier = modifier.fillMaxWidth().padding(horizontal = 8.dp, vertical = 4.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White), elevation = CardDefaults.cardElevation(defaultElevation = elevation),
        shape = RoundedCornerShape(cardCornerPercentage)
    ) {
        Column(
            modifier = Modifier.padding(vertical = 16.dp).fillMaxWidth()
        ) {
            // User info
            Row(
                verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.Start,
                modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp)
            ) {
                Image(
                    painter = (painterResource(Res.drawable.place_holder)),
                    contentDescription = "User profile picture",
                    modifier = Modifier.size(40.dp).clip(CircleShape)
                )
                Spacer(modifier = Modifier.width(8.dp))
                AppTextSubTitle(
                    text = post.userName ?: "",
                )
            }
            Spacer(modifier = Modifier.height(8.dp))

            HorizontalDivider(modifier.fillMaxWidth())
            Spacer(modifier = Modifier.height(8.dp))
            post.description?.let {
                AppTextBody(
                    text = post.description, modifier.padding(horizontal = 10.dp), fontSize = 14.sp, lineHeight = 20.sp
                )
                Spacer(modifier = Modifier.height(8.dp))
            }

            // Images
            when {
                post.images.isNullOrEmpty() -> {
                    // No images, so we don't add any image composable
                }

                post.images.size == 1 -> {
                    Image(
                        painter = painterResource(Res.drawable.demo1) /*(post.images[0])*/,
                        contentDescription = "Post image 1",
                        modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp)
                            .aspectRatio(imageAspectRatio) // Adjust this ratio as needed
                            .clip(RoundedCornerShape(3)).clickable(onClick = onFirstImageClick),
                        contentScale = ContentScale.Crop
                    )
                }

                else -> {
                    // Two images side by side
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Image(
                            painter = painterResource(Res.drawable.demo2) /*(post.images[0])*/,
                            contentDescription = "Post image 1",
                            modifier = Modifier.weight(1f)
                                .aspectRatio(imageAspectRatio) // Square aspect ratio for side-by-side images
                                .clip(RoundedCornerShape(4)).clickable(onClick = onFirstImageClick),
                            contentScale = ContentScale.Crop
                        )
                        Spacer(Modifier.width(5.dp))
                        Image(
                            painter = painterResource(Res.drawable.demo1) /*(post.images[1])*/,
                            contentDescription = "Post image 2",
                            modifier = Modifier.weight(1f)
                                .aspectRatio(imageAspectRatio) // Square aspect ratio for side-by-side images
                                .clip(RoundedCornerShape(4)).clickable(onClick = onSecondImageClick),
                            contentScale = ContentScale.Crop
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))
            PostInteractions(
                post,
                onLikePost = onLikePost,
                onDislikePost = onDislikePost,
                onCommentPost = onLoadComments
            )
        }
    }
}

@Composable
fun PostInteractions(
    post: Post,
    modifier: Modifier = Modifier,
    onLikePost: () -> Unit,
    onDislikePost: () -> Unit,
    onCommentPost: () -> Unit
) {
    Row(
        modifier = modifier.fillMaxWidth().padding(horizontal = 12.dp),
        horizontalArrangement = Arrangement.Start,
        verticalAlignment = Alignment.CenterVertically
    ) {
        InteractionButton(
            imageRes = Res.drawable.comment_outline,
            count = post.commentCount,
            contentDescription = "Comment",
            onClick = onCommentPost
        )
        Spacer(Modifier.weight(1f))

        InteractionButton(
            imageRes = Res.drawable.thumb_up_outline,
            count = post.likes,
            contentDescription = "Like",
            onClick = onLikePost
        )
        Spacer(Modifier.width(5.dp))
        InteractionButton(
            imageRes = Res.drawable.thumb_down_outline,
            count = post.dislikes,
            contentDescription = "Dislike",
            onClick = onDislikePost
        )
    }
}


@Composable
fun InteractionButton(
    imageRes: DrawableResource, count: Int?, contentDescription: String, onClick: () -> Unit
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(5.dp)
    ) {
        Image(
            painter = painterResource(imageRes),
            contentDescription = contentDescription,
            modifier = Modifier.size(24.dp).clickable(onClick = onClick), // Adjust size as needed
            contentScale = ContentScale.Fit // Adjust scale as needed
        )
        AppTextSmall(
            text = buildAnnotatedString {
                append(count?.toString() ?: "0")
            }, modifier = Modifier.wrapContentWidth()
        )
    }
}

@Composable
fun CommentItem(comment: Comment) {
    Row(
        modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.Top
    ) {
        Image(
            painter = painterResource(comment.userAvatar),
            contentDescription = "User Avatar",
            contentScale = ContentScale.Crop,
            modifier = Modifier.size(46.dp).aspectRatio(1f).clip(CircleShape)
        )
        Column(
            modifier = Modifier.weight(1f).padding(start = 8.dp)
        ) {
            AppTextSubTitle(
                text = comment.userName
            )
            AppTextBody(
                text = comment.content,
            )
        }
    }
}

@Composable
fun CommentEditor(onAddComment: (String) -> Unit) {
    var commentText by remember { mutableStateOf("") }
    val keyboardController = LocalSoftwareKeyboardController.current
    val focusManager = LocalFocusManager.current

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White)
            .imePadding()
            .padding(8.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    color = Color.LightGray.copy(alpha = 0.2f),
                    shape = RoundedCornerShape(24.dp)
                )
                .padding(horizontal = 16.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            BasicTextField(
                value = commentText,
                onValueChange = { commentText = it },
                modifier = Modifier
                    .weight(1f)
                    .padding(end = 8.dp),
                textStyle = TextStyle(color = Color.Black, fontSize = 16.sp),
                decorationBox = { innerTextField ->
                    Box {
                        if (commentText.isEmpty()) {
                            AppTextBody("Write a comment...", color = Color.Gray)
                        }
                        innerTextField()
                    }
                },
                maxLines = 3
            )
            IconButton(
                onClick = {
                    if (commentText.isNotBlank()) {
                        onAddComment(commentText)
                        commentText = ""
                        keyboardController?.hide()
                        focusManager.clearFocus()
                    }
                },
                modifier = Modifier.size(32.dp)
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.Send,
                    contentDescription = "Send Comment",
                    tint = if (commentText.isNotBlank()) primary else Color.Gray
                )
            }
        }
    }
}




//fun LazyListScope.posts(
//    items: List<Post>,
//    onLikePost: (Long) -> Unit,
//    onDislikePost: (Long) -> Unit,
//    onCommentPost: (Long) -> Unit,
//    modifier: Modifier = Modifier
//) = itemsIndexed(items = items, key = { index, _ -> index }, itemContent = { _, item ->
//    PostItem(post = item,
//        modifier = modifier,
//        onLikePost = { onLikePost(item.postId) },
//        onDislikePost = { onDislikePost(item.postId) },
//        onCommentPost = { onCommentPost(item.postId) })
//})

