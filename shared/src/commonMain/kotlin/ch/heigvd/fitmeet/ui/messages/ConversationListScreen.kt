package ch.heigvd.fitmeet.ui.messages

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeContentPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import ch.heigvd.fitmeet.data.messages.ConversationRepository
import ch.heigvd.fitmeet.data.messages.ConversationSummary
import ch.heigvd.fitmeet.navigation.Conversation
import org.jetbrains.compose.resources.painterResource

@Composable
fun ConversationListScreen(
    navController: NavHostController,
    conversationRepository: ConversationRepository,
) {
    var conversations by remember { mutableStateOf<List<ConversationSummary>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(conversationRepository) {
        conversationRepository.getAccessibleConversations()
            .onSuccess {
                conversations = it
                errorMessage = null
            }
            .onFailure {
                errorMessage = it.message ?: "Impossible de charger les conversations."
            }
        isLoading = false
    }

    val backgroundColor = Color(0xFFFFFFFF)
    val separationColor = Color(0xFFBBBBBB)
    val textColor = Color(0xFF102E53)

    MaterialTheme {
        Column(
            modifier = Modifier
                .background(backgroundColor)
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(80.dp)
                    .drawBehind {
                        drawLine(
                            color = separationColor,
                            start = Offset(0f, size.height),
                            end = Offset(size.width, size.height),
                            strokeWidth = 5.dp.toPx(),
                        )
                    },
                contentAlignment = Alignment.CenterStart,
            ) {
                Text(
                    text = "Discussions",
                    fontSize = 35.sp,
                    fontWeight = FontWeight.W900,
                    color = textColor,
                    modifier = Modifier.padding(10.dp, 0.dp),
                )
            }
            Spacer(modifier = Modifier.height(10.dp))
            when {
                isLoading -> Text("Chargement des conversations...", modifier = Modifier.padding(24.dp))
                errorMessage != null -> Text(
                    text = errorMessage.orEmpty(),
                    modifier = Modifier.padding(24.dp),
                    color = MaterialTheme.colorScheme.error,
                )
                conversations.isEmpty() -> Text("Aucune conversation disponible.", modifier = Modifier.padding(24.dp))
                else -> LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .background(backgroundColor),
                    contentPadding = PaddingValues(bottom = 10.dp)

                ) {
                    items(conversations, key = { it.conversationId }) { conversation ->
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(100.dp)
                                .padding(horizontal = 25.dp, vertical = 5.dp)
                                .background(
                                    Color(0xFFEAEAEA),
                                    shape = RoundedCornerShape(10.dp)
                                )
                                .clickable {
                                    navController.navigate(
                                        Conversation(conversation.conversationId, conversation.activity.title),
                                    )
                                },
                            contentAlignment = Alignment.CenterStart,
                        ) {

                            val currentSport = conversation.activity.sport
                            Row(){
                                Box(
                                    modifier = Modifier
                                        .background(currentSport.tint,
                                            shape = RoundedCornerShape(
                                                topStart = 10.dp,
                                                bottomStart = 10.dp)
                                    )
                                ){
                                    Image(
                                        painter = painterResource(currentSport.icon),
                                        contentDescription = currentSport.label,
                                        modifier = Modifier
                                            .fillMaxHeight()
                                            .padding(10.dp),
                                        contentScale = ContentScale.Fit,
                                    )
                                }


                                Column(modifier = Modifier.padding(start = 15.dp)) {
                                Text(
                                    text = conversation.activity.title,
                                    fontSize = 20.sp,
                                    fontWeight = FontWeight.W600,
                                )
                                Text(
                                    text = if(conversation.isOrganizer)
                                        "Vous avez créé cet evenement" else "",
                                    fontSize = 14.sp,
                                    color = textColor,
                                )
                            }
                            }
                        }
                    }
                }
            }
        }
    }
}
