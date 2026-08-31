package ch.heigvd.fitmeet.ui.messages

import androidx.compose.foundation.background
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

@Preview
@Composable
fun ConversationListScreen() {
    val backgroundColor = Color(0xFFDDDDDD)
    val separationColor = Color(0xFFBBBBBB)
    val textColor = Color(0xFF102E53)

    val discussions_temp = listOf(
        "Foot en 5v5",
        "minigolf",
        "Tour du lac","Foot en 5v5",
        "minigolf",
        "Tour du lac","Foot en 5v5",
        "minigolf",
        "Tour du lac","Foot en 5v5",
        "minigolf",
        "Tour du lac","Foot en 5v5",
        "minigolf",
        "Tour du lac","Foot en 5v5",
        "minigolf",
        "Tour du lac","Foot en 5v5",
        "minigolf",
        "Tour du lac"
    )

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
                    text = "Messagerie",
                    fontSize = 35.sp,
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
                            .height(70.dp)
                            .padding(horizontal = 10.dp, vertical = 5.dp)
                            .background(Color.White),
                        contentAlignment = Alignment.CenterStart
                    ) {
                        Text(
                            text = disc,
                            modifier = Modifier.padding(start = 15.dp),
                            fontSize = 20.sp
                        )

                        Button(
                            onClick = { },
                            modifier = Modifier.align(Alignment.CenterEnd)
                        ) {
                            Text("Ouvrir")
                        }
                    }
                }
            }



        }
    }
}
