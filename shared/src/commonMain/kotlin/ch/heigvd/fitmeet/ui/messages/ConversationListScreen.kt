package ch.heigvd.fitmeet.ui.messages

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeContentPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
import ch.heigvd.fitmeet.ui.theme.Sport


//TODO Connect with supabase get
private data class groupInfo(
    val groupName: String = "",
    val sportType: Sport = Sport.FOOTBALL,
    val groupID : String = "-1"
)

private val discussions_temp = mutableStateListOf<groupInfo>(
    groupInfo("Foot en 5v5", Sport.FOOTBALL),
    groupInfo("Tour du lac", Sport.RUNNING),
    groupInfo("match chill", Sport.BASKETBALL)
)

@Composable
fun ConversationListScreen(
    navController: NavHostController
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
                Text(
                    text = "Discussions",
                    fontSize = 40.sp,
                    fontWeight = FontWeight.W900,
                    color = textColor,
                    modifier = Modifier.padding(10.dp, 0.dp)
                )
            }

            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            ) {
                items(discussions_temp) { disc ->
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(100.dp)
                            .padding(horizontal = 10.dp, vertical = 5.dp)
                            .background(disc.sportType.tint)
                            .clickable{
                                navController.navigate(
                                    Conversation(disc.groupID)//todo change with sport id
                                )
                            },
                        contentAlignment = Alignment.CenterStart
                    ) {
                        Text(
                            text = disc.groupName,
                            modifier = Modifier.padding(start = 15.dp),
                            fontSize = 30.sp,
                            fontWeight = FontWeight.Black
                        )
                    }
                }
            }
        }
    }
}
