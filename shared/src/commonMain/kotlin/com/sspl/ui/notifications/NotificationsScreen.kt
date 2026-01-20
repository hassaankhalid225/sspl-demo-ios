package com.sspl.ui.notifications

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.sspl.core.models.NotificationItem
import com.sspl.core.models.NotificationType
import com.sspl.theme.windowBackGround
import com.sspl.ui.components.AppTextLabel
import com.sspl.ui.components.AppTextSubTitle
import org.koin.compose.viewmodel.koinViewModel
import com.sspl.utils.DateTimeUtils
import com.sspl.platform.formatDateTime
import kotlinx.datetime.Instant



@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotificationsScreen(
    navController: NavController,
    viewModel: NotificationsViewModel = koinViewModel()
) {
    val notifications by viewModel.notifications.collectAsStateWithLifecycle()
    val unreadCount by viewModel.unreadCount.collectAsStateWithLifecycle()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(windowBackGround)
    ) {
        // Header with unread count
        if (notifications.isNotEmpty()) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                AppTextLabel(
                    text = "Notifications",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
                
                if (unreadCount > 0) {
                    TextButton(onClick = { viewModel.markAllAsRead() }) {
                        AppTextSubTitle(
                            text = "Mark all as read",
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }
        }

        // Notifications list
        if (notifications.isEmpty()) {
            EmptyNotificationsState()
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(
                    items = notifications,
                    key = { it.id }
                ) { notification ->
                    NotificationCard(
                        notification = notification,
                        onMarkAsRead = { viewModel.markAsRead(notification.id) },
                        onDelete = { viewModel.deleteNotification(notification.id) }
                    )
                }
            }
        }
    }
}

@Composable
private fun EmptyNotificationsState() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            AppTextLabel(
                text = "No Notifications",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )
            AppTextSubTitle(
                text = "You're all caught up!",
                fontSize = 14.sp
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun NotificationCard(
    notification: NotificationItem,
    onMarkAsRead: () -> Unit,
    onDelete: () -> Unit
) {
    val dismissState = rememberSwipeToDismissBoxState(
        confirmValueChange = { dismissValue ->
            if (dismissValue == SwipeToDismissBoxValue.EndToStart) {
                onDelete()
                true
            } else {
                false
            }
        }
    )

    SwipeToDismissBox(
        state = dismissState,
        backgroundContent = {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Red, RoundedCornerShape(12.dp))
                    .padding(horizontal = 20.dp),
                contentAlignment = Alignment.CenterEnd
            ) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Delete",
                    tint = Color.White
                )
            }
        },
        enableDismissFromStartToEnd = false
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .clickable {
                    if (!notification.isRead) {
                        onMarkAsRead()
                    }
                },
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(
                containerColor = if (notification.isRead) {
                    MaterialTheme.colorScheme.surface
                } else {
                    MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)
                }
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Notification icon/indicator
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(
                            when (notification.type) {
                                NotificationType.SESSION_LIVE -> MaterialTheme.colorScheme.primary
                                NotificationType.ANNOUNCEMENT -> MaterialTheme.colorScheme.tertiary
                                NotificationType.GENERAL -> MaterialTheme.colorScheme.secondary
                            }
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    AppTextLabel(
                        text = when (notification.type) {
                            NotificationType.SESSION_LIVE -> "📺"
                            NotificationType.ANNOUNCEMENT -> "📢"
                            NotificationType.GENERAL -> "🔔"
                        },
                        fontSize = 20.sp
                    )
                }

                // Notification content
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        AppTextLabel(
                            text = notification.title,
                            fontSize = 16.sp,
                            fontWeight = if (notification.isRead) FontWeight.Normal else FontWeight.Bold,
                            modifier = Modifier.weight(1f)
                        )
                        
                        if (!notification.isRead) {
                            Box(
                                modifier = Modifier
                                    .size(8.dp)
                                    .clip(CircleShape)
                                    .background(MaterialTheme.colorScheme.primary)
                            )
                        }
                    }

                    AppTextSubTitle(
                        text = notification.message,
                        fontSize = 14.sp,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )

                    // Session details if available
                    notification.sessionNotification?.let { session ->
                        Spacer(modifier = Modifier.height(4.dp))
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.5f)
                        ) {
                            Column(
                                modifier = Modifier.padding(8.dp),
                                verticalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                AppTextLabel(
                                    text = "Join Code: ${session.joinCode}",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                AppTextLabel(
                                    text = "Session ID: ${session.sessionId}",
                                    fontSize = 11.sp
                                )
                            }
                        }
                    }

                    AppTextSubTitle(
                        text = formatTimestamp(notification.timestamp),
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                    )
                }
            }
        }
    }
}

private fun formatTimestamp(timestamp: Long): String {
    val now = DateTimeUtils.timeInMilliNow()
    val diff = now - timestamp
    
    return when {
        diff < 60_000 -> "Just now"
        diff < 3600_000 -> "${diff / 60_000}m ago"
        diff < 86400_000 -> "${diff / 3600_000}h ago"
        diff < 604800_000 -> "${diff / 86400_000}d ago"
        else -> {
            val instant = Instant.fromEpochMilliseconds(timestamp)
            formatDateTime(
                dateTime = instant.toString(),
                inFormat = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'",
                outFormat = "MMM dd, yyyy"
            )
        }
    }
}
