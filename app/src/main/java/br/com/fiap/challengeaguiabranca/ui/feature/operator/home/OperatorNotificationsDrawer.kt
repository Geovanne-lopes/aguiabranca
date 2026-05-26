package br.com.fiap.challengeaguiabranca.ui.feature.operator.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Campaign
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.PriorityHigh
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import br.com.fiap.challengeaguiabranca.R
import br.com.fiap.challengeaguiabranca.domain.model.OperatorNotification
import br.com.fiap.challengeaguiabranca.domain.model.OperatorNotificationType
import br.com.fiap.challengeaguiabranca.ui.theme.InnovatePrimary
import br.com.fiap.challengeaguiabranca.ui.theme.InnovateTextPrimary
import br.com.fiap.challengeaguiabranca.ui.theme.InnovateTextSecondary

@Composable
fun OperatorNotificationsDrawerSheet(
    notifications: List<OperatorNotification>,
    onClose: () -> Unit
) {
    ModalDrawerSheet(
        modifier = Modifier.width(320.dp),
        drawerContainerColor = Color.White
    ) {
        Column(
            modifier = Modifier
                .fillMaxHeight()
                .padding(horizontal = 16.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    stringResource(R.string.operator_notifications_title),
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = InnovateTextPrimary
                )
                IconButton(onClick = onClose) {
                    Icon(Icons.Default.Close, contentDescription = stringResource(R.string.profile_cancel))
                }
            }

            HorizontalDivider()

            if (notifications.isEmpty()) {
                Text(
                    stringResource(R.string.operator_notifications_empty),
                    color = InnovateTextSecondary,
                    modifier = Modifier.padding(vertical = 24.dp)
                )
            } else {
                LazyColumn(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    item { Spacer(modifier = Modifier.height(12.dp)) }
                    items(notifications, key = { it.id }) { notification ->
                        NotificationDrawerCard(notification = notification)
                    }
                    item { Spacer(modifier = Modifier.height(24.dp)) }
                }
            }
        }
    }
}

@Composable
private fun NotificationDrawerCard(notification: OperatorNotification) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF5F6FA)),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    notificationIcon(notification.type),
                    contentDescription = null,
                    tint = InnovatePrimary
                )
                Text(
                    notification.title,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold
                )
            }
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                notification.body,
                style = MaterialTheme.typography.bodySmall,
                color = InnovateTextSecondary
            )
        }
    }
}

private fun notificationIcon(type: OperatorNotificationType): ImageVector = when (type) {
    OperatorNotificationType.GUIDELINE -> Icons.Default.Campaign
    OperatorNotificationType.IDEA_PENDING -> Icons.Default.Schedule
    OperatorNotificationType.IDEA_APPROVED -> Icons.Default.CheckCircle
    OperatorNotificationType.IDEA_REJECTED -> Icons.Default.PriorityHigh
    OperatorNotificationType.IDEA_PRIORITIZED -> Icons.Default.Lightbulb
}
