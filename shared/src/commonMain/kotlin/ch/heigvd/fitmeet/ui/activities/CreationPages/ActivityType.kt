package ch.heigvd.fitmeet.ui.activities
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeContentPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.max
import androidx.compose.ui.unit.sp
import ch.heigvd.fitmeet.ui.theme.Sport
import org.jetbrains.compose.resources.painterResource


private val Navy = Color(0xFF102E53)
private val Green = Color(0xFF429A72)
@Preview
@Composable
fun ActivityType() {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            "Quel type d'activité\nvoulez vous créer?",
            color = Color(0xFFAAAAAA),
            fontWeight = FontWeight.W500,
            fontSize = 32.sp
        )

        Spacer(modifier = Modifier.height(20.dp))
        /** Activities */
        LazyVerticalGrid(
            columns = GridCells.Fixed(3) , // show activities on 3 columns
            modifier = Modifier.width(300.dp)
        ){
            items(Sport.entries){sport ->
                Button(
                    onClick = {activityData.type = sport},
                    enabled = (activityData.type != sport),
                    modifier = Modifier.fillMaxWidth().height(90.dp).padding(5.dp),
                    shape = RoundedCornerShape(6.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF555555),
                        disabledContainerColor = Color(0xFF3E8E68),
                        disabledContentColor = Color(0xFFFFFFFF),
                        contentColor = Color(0xFF999999)
                    ),
                    contentPadding = PaddingValues(5.dp)
                    ){
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Image(
                            painter = painterResource(sport.icon),
                            contentDescription = sport.label,
                            modifier = Modifier
                                .fillMaxWidth()
                                .weight(1f)
                                .alpha(if (activityData.type == sport) 1f else 0.6f),
                            contentScale = ContentScale.Fit
                        )
                        Text(
                            sport.label,
                            maxLines = 1,
                            softWrap = false,
                            fontSize = 12.sp
                        )
                    }
                }
            }
        }

        Spacer(Modifier.height(20.dp))
        Text(
            "Pour combien de personnes?",
            color = Color(0xFFAAAAAA),
            fontWeight = FontWeight.W500,
            fontSize = 16.sp
        )
        Row(){
            Spacer(Modifier.width(10.dp))
            Slider(
                value = activityData.capacity.toFloat(),
                onValueChange = {activityData.capacity = it.toInt()},
                valueRange = 2f..10f,
                steps = 7,
                modifier = Modifier.weight(1f),
                colors = SliderDefaults.colors(
                    thumbColor = Green,
                    activeTrackColor = Green,
                    inactiveTrackColor = Color(0xFF888888),
                    activeTickColor = Color.White,
                    inactiveTickColor = Color(0xFF555555)
                ),
                thumb = {
                    Box(
                        modifier = Modifier
                            .size(24.dp)
                            .background(
                                color = Color(0xFF3E8E68),
                                shape = CircleShape
                            )
                    )
                }
            )
            Spacer(Modifier.width(10.dp))
            Text(
                text = activityData.capacity.toString(),
                textAlign = TextAlign.Center,
                color = Color(0xFFAAAAAA),
                fontWeight = FontWeight.W900,
                fontSize = 32.sp,
                modifier = Modifier.width(40.dp)
            )
            Spacer(Modifier.width(10.dp))
        }
    }
}




