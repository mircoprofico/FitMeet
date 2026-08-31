package ch.heigvd.fitmeet.ui.messages

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeContentPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import ch.heigvd.fitmeet.navigation.Conversation


// TODO change this with db pulls
data class message(val userID : Int, val text : String)
val disc = listOf<message>(
    message(0, "Salut à tous! on est ready pour ce match?"),
    message(1, "Oui! je suis trop content!")
)

val selfId = 0 // Todo set to user id

@Composable
fun ConversationScreen(
    activityId: String = "",
    navController : NavHostController
) {
        val backgroundColor = Color(0xFFDDDDDD)
        val separationColor = Color(0xFFBBBBBB)
        val textColor = Color(0xFF102E53)


        MaterialTheme {
            Column(
                modifier = Modifier
                    .background(backgroundColor)
                    .safeContentPadding()
                    .fillMaxSize()
                    .imePadding(),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(100.dp)
                        .background(
                            color = backgroundColor,
                        )
                        .drawBehind {
                            drawLine(
                                color = separationColor,
                                start = Offset(0f, size.height),
                                end = Offset(size.width, size.height),
                                strokeWidth = 5.dp.toPx()
                            )
                        },

                    contentAlignment = Alignment.CenterStart
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(10.dp, 0.dp)
                    ){
                        Button(
                            onClick = {
                                navController.popBackStack()
                            }
                        ) {
                            Text("Retour")
                        }
                        Text(
                            text = activityId, // todo change to name of activity
                            fontSize = 40.sp,
                            fontWeight = FontWeight.W900,
                            color = textColor,
                            modifier = Modifier.padding(10.dp, 0.dp)
                        )
                    }
                }

                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                ) {
                    items(disc) { disc ->
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(
                                    start = if (disc.userID == selfId) 60.dp else 10.dp,
                                    end = if (disc.userID == selfId) 10.dp else 60.dp,
                                    top = 5.dp,
                                    bottom = 5.dp
                                ),
                            contentAlignment = if (disc.userID == 0) {
                                Alignment.CenterEnd
                            } else {
                                Alignment.CenterStart
                            }
                        ) {
                            Box(
                                modifier = Modifier
                                    .background(
                                        color = if (disc.userID == 0) {
                                            Color(0xFF2196F3)
                                        } else {
                                            Color.White
                                        },
                                        shape = MaterialTheme.shapes.large
                                    )
                                    .padding(
                                        horizontal = 16.dp,
                                        vertical = 10.dp
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = disc.text,
                                    color = if (disc.userID == 0) {
                                        Color.White
                                    } else {
                                        Color.Black
                                    },
                                    fontSize = 20.sp,
                                    fontWeight = FontWeight.Normal
                                )
                            }
                        }
                    }
                }
            }
        }
}

