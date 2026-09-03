package ch.heigvd.fitmeet.ui.activities

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.foundation.layout.offset
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ch.heigvd.fitmeet.data.activities.sampleActivities
import ch.heigvd.fitmeet.model.Activity
import ch.heigvd.fitmeet.ui.components.AvatarStack
import ch.heigvd.fitmeet.ui.components.LevelChip
import org.jetbrains.compose.resources.painterResource

@Composable
fun ActivityDetailScreen(
    activity: Activity,
    organizer: String = "",
    description: String = activity.description,
    isJoined: Boolean = false,
    showJoinButton: Boolean = true,
    onJoin: () -> Unit = {},
    modifier: Modifier = Modifier,
) {
    val uriHandler = LocalUriHandler.current
    Column(
        modifier = modifier
            // fillMaxWidth and not fillMaxSize: inside a bottom sheet the
            // content decides the height, it does not fill the screen
            .fillMaxWidth()
            .background(Color.White)
            // a long description can go past the screen, without this the
            // bottom would simply be unreachable
            .verticalScroll(rememberScrollState()),
    ) {
        // header: the sport, like the mockup. tinted with the sport colour.
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(activity.sport.tint)
                .padding(20.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Image(
                painter = painterResource(activity.sport.icon),
                contentDescription = activity.sport.label,
                modifier = Modifier.size(56.dp),
            )
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text(activity.sport.label, fontWeight = FontWeight.Bold, fontSize = 20.sp)
                LevelChip(activity.level)
            }
        }

        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            Text(
                activity.title,
                fontWeight = FontWeight.Bold,
                fontSize = 22.sp,
                lineHeight = 26.sp,
            )

            if (description.isNotBlank()) {
                Text(description, fontSize = 14.sp, color = Color(0xFF4C5652), lineHeight = 20.sp)
            }

            // the practical details, boxed so they read as one group
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .border(1.dp, Color(0xFFDDE5DD), RoundedCornerShape(12.dp))
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                InfoLine("Quand", activity.dateTime)
                InfoLine(
                    label = "Où",
                    value = activity.place,
                    onClick = activity.mapUrl?.let { mapUrl -> { uriHandler.openUri(mapUrl) } },
                )
                if (organizer.isNotBlank()) InfoLine("Organisé par", organizer)
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text("Participants", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                Text(activity.attendance, fontWeight = FontWeight.Bold, fontSize = 15.sp)
            }
            AvatarStack(count = activity.participants, maxVisible = 5)

            if (showJoinButton) {
                // same three states as the card. the state comes from the
                // activity itself, so the sheet and the list never disagree.
                val joined = isJoined || activity.isJoined
                Button(
                    onClick = onJoin,
                    enabled = joined || !activity.isFull,
                    modifier = Modifier.fillMaxWidth().height(48.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (joined) Color(0xFFCF3838) else Color(0xFF3E8E68),
                        disabledContainerColor = Color(0xFFB9C2BC),
                    ),
                    shape = RoundedCornerShape(10.dp),
                ) {
                    Text(
                        text = when {
                            joined && activity.canLeave -> "Quitter l'activité"
                            joined -> "Vous participez"
                            activity.isFull -> "Complet"
                            else -> "Rejoindre"
                        },
                        fontSize = 15.sp,
                    )
                }
            }
        }
    }
}

// label on the left, value on the right, so the three lines line up
@Composable
private fun InfoLine(label: String, value: String, onClick: (() -> Unit)? = null) {
    Row {
        Text(
            text = label,
            fontSize = 13.sp,
            color = Color(0xFF6B7C74),
            modifier = Modifier.width(96.dp),
        )
        Text(
            text = value,
            fontSize = 13.sp,
            color = if (onClick == null) Color.Unspecified else Color(0xFF1565C0),
            textDecoration = if (onClick == null) null else TextDecoration.Underline,
            modifier = if (onClick == null) Modifier else Modifier.clickable(onClick = onClick),
        )
    }
}

@Preview
@Composable
private fun ActivityDetailScreenPreview() {
    ActivityDetailScreen(
        activity = sampleActivities.first(),
        organizer = "Pierre Gellet",
        description = "Match amical sur gazon. Prévoir des crampons et un maillot clair.",
    )
}
