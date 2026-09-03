package ch.heigvd.fitmeet.ui.messages

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeContentPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldColors
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import ch.heigvd.fitmeet.data.messages.ConversationMessage
import ch.heigvd.fitmeet.data.messages.ConversationRepository
import ch.heigvd.fitmeet.data.messages.ConversationSummary
import ch.heigvd.fitmeet.navigation.ActivityList
import ch.heigvd.fitmeet.navigation.Conversation
import ch.heigvd.fitmeet.ui.activities.activityData
import ch.heigvd.fitmeet.ui.activities.reset
import kotlinx.coroutines.launch


private val Navy = Color(0xFF102E53)
private val Green = Color(0xFF429A72)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ConversationScreen(
    conversationId: String = "",
    conversationTitle: String = "",
    navController: NavHostController,
    conversationRepository: ConversationRepository,
) {
    var messages by remember { mutableStateOf<List<ConversationMessage>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var conversationSummary by remember { mutableStateOf<ConversationSummary?>(null) }
    var showDetails by remember { mutableStateOf(false) }
    var isLeaving by remember { mutableStateOf(false) }
    val currentUserId = conversationRepository.currentUserId()
    val listState = rememberLazyListState()
    val scope = rememberCoroutineScope()




    LaunchedEffect(conversationRepository, conversationId) {
        conversationRepository.getAccessibleConversations()
            .onSuccess { conversations ->
                conversationSummary = conversations.firstOrNull { it.conversationId == conversationId }
            }
        conversationRepository.getMessages(conversationId)
            .onSuccess {
                messages = it
                errorMessage = null
            }
            .onFailure {
                errorMessage = it.message ?: "Impossible de charger les messages."
            }
        isLoading = false

        conversationRepository.observeMessages(conversationId).collect { message ->
            if (messages.none { it.id == message.id }) {
                messages = messages + message
            }
        }
    }

    LaunchedEffect(messages.size) {
        if (messages.isNotEmpty()) {
            listState.scrollToItem(messages.lastIndex, scrollOffset = Int.MAX_VALUE)

        }
    }
    val backgroundColor = Color(0xFFDDDDDD)
    val separationColor = Color(0xFFBBBBBB)
    val textColor = Color(0xFF102E53)
    var currentMessage by remember { mutableStateOf("") }


    MaterialTheme {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(backgroundColor)
                .imePadding(),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(100.dp)
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
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(0.dp, 0.dp),
                ) {

                    Button(
                        onClick = { navController.popBackStack() },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFFFF0000),
                        ),
                        shape = RoundedCornerShape(6.dp),
                        modifier = Modifier.padding(16.dp).size(40.dp),
                        contentPadding = PaddingValues(0.dp)
                    ) {
                        Text(
                            "X",
                            style = MaterialTheme.typography.headlineLarge,
                            fontWeight = FontWeight.W900,
                            color = Color(0xFFFFFFFF),
                            fontSize = 18.sp
                        )
                    }


                    Text(
                        text = conversationTitle,
                        fontSize = 28.sp,
                        fontWeight = FontWeight.W900,
                        color = textColor,
                        modifier = Modifier.padding(10.dp, 0.dp),
                    )
                    Spacer(modifier = Modifier.weight(1f))

                    Button(
                        onClick = { showDetails = true },
                        enabled = conversationSummary != null,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFFEEEEEE),
                        ),
                        shape = RoundedCornerShape(20.dp),
                        modifier = Modifier.padding(16.dp).size(40.dp),
                        contentPadding = PaddingValues(0.dp)
                    ) {
                        Text(
                            "i",
                            style = MaterialTheme.typography.headlineLarge,
                            fontWeight = FontWeight.W900,
                            color = Color(0xFF999999),
                            fontSize = 18.sp
                        )
                    }
                }
            }

            when {
                isLoading -> Text("Chargement des messages...", modifier = Modifier.padding(24.dp))
                errorMessage != null -> Text(
                    text = errorMessage.orEmpty(),
                    modifier = Modifier.padding(24.dp),
                    color = MaterialTheme.colorScheme.error,
                )
                messages.isEmpty() -> Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Aucun message.",
                        modifier = Modifier.padding(24.dp)
                    )
                }
                else -> LazyColumn(
                    state = listState,
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    verticalArrangement = Arrangement.Bottom
                ) {
                    items(messages, key = { it.id }) { message ->
                        val isMine = message.senderId == currentUserId
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(
                                    start = if (isMine) 60.dp else 10.dp,
                                    end = if (isMine) 10.dp else 60.dp,
                                    top = 5.dp,
                                    bottom = 5.dp,
                                ),
                            contentAlignment = if (isMine) Alignment.CenterEnd else Alignment.CenterStart,
                        ) {
                            Box(
                                modifier = Modifier
                                    .background(
                                        color = if (isMine) Navy else Color.White,
                                        shape = MaterialTheme.shapes.large,
                                    )
                                    .padding(horizontal = 16.dp, vertical = 10.dp),
                            ) {
                                Column {
                                    if (!isMine) {
                                        Text(
                                            text = message.senderName ?: "Utilisateur",
                                            color = Color(0xFF999999),
                                            fontSize = 10.sp,
                                        )
                                    }
                                    Text(
                                        text = message.content,
                                        color = if (isMine) Color.White else Color.Black,
                                        fontSize = 20.sp,
                                    )
                                }
                            }
                        }
                    }
                }
            }

            /**
             * Input part
             * */
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                TextField(
                    value = currentMessage,
                    onValueChange = { currentMessage = it },
                    modifier = Modifier.weight(1f),
                    placeholder = {
                        Text("Écrire un message...", color = Color(0XFF888888))
                    },
                    minLines = 1,
                    shape = RoundedCornerShape(50.dp),
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = Color.White,
                        unfocusedContainerColor = Color.White,
                        disabledContainerColor = Color.White,
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent,
                    )
                )
                Spacer(modifier = Modifier.width(5.dp))
                Button(
                    onClick = {
                        if (currentUserId != null && currentMessage.isNotBlank()) {
                            scope.launch {
                                val result = conversationRepository.sendMessage(
                                    senderUserId = currentUserId,
                                    conversationId = conversationId,
                                    content = currentMessage,
                                )

                                if (result.isSuccess) {
                                    currentMessage = ""
                                } else {
                                    errorMessage = result.message
                                }
                            }
                        }
                    },
                    modifier = Modifier.size(52.dp),
                    shape = RoundedCornerShape(50),
                    contentPadding = PaddingValues(0.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Green,
                    ),
                ) {
                    Text(
                        text = "➤",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                    )
                }
            }
        }

        conversationSummary?.let { summary ->
            if (showDetails) {
                ModalBottomSheet(
                    onDismissRequest = { showDetails = false },
                    sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp),
                    ) {
                        Text(summary.title, fontSize = 24.sp, fontWeight = FontWeight.Bold)
                        summary.description?.takeIf { it.isNotBlank() }?.let { description ->
                            Text(description, color = Color(0xFF4C5652))
                        }
                        Text("Début : ${summary.startsAt}")
                        Text("Lieu : ${summary.locationName}")

                        if (!summary.isOrganizer) {
                            Button(
                                onClick = {
                                    scope.launch {
                                        isLeaving = true
                                        val result = conversationRepository.leaveActivity(summary.eventId)
                                        if (result.isSuccess) {
                                                showDetails = false
                                                navController.popBackStack()
                                        } else {
                                            errorMessage = result.message
                                        }
                                        isLeaving = false
                                    }
                                },
                                enabled = !isLeaving,
                                modifier = Modifier.fillMaxWidth().height(48.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFC62828)),
                            ) {
                                Text(if (isLeaving) "Sortie..." else "Quitter l'activité")
                            }
                        }
                    }
                }
            }
        }
    }
}
