package ch.heigvd.fitmeet.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ch.heigvd.fitmeet.ui.theme.Level
import ch.heigvd.fitmeet.ui.theme.Sport
import org.jetbrains.compose.resources.painterResource

// the card used in the list, the detail sheet and the profile.
// everything comes from parameters, it does not know where the data comes from.
@Composable
fun ActivityCard(
    title: String,
    sport: Sport,
    dateTime: String,
    place: String,
    level: Level,
    participants: Int,
    capacity: Int,
    onClick: () -> Unit,
    onJoin: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Card(onClick = onClick, modifier = modifier.fillMaxWidth()) {
        Row(modifier = Modifier.height(88.dp)) {
            // left: sport icon on its tinted square
            Box(
                modifier = Modifier
                    .size(88.dp)
                    .background(sport.tint),
                contentAlignment = Alignment.Center,
            ) {
                Image(
                    painter = painterResource(sport.icon),
                        contentDescription = sport.label, // no text says the sport, so it needs a description
                    modifier = Modifier.size(46.dp),
                )
            }

            // middle: takes whatever width is left over
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 12.dp, vertical = 5.dp),
                verticalArrangement = Arrangement.Top,
            ) {
                Text(
                    text = title,
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis, // long titles would break the layout otherwise
                )
                Text(dateTime, fontSize = 12.sp)
                Text(place, fontSize = 12.sp)
                LevelChip(level, Modifier.padding(top = 12.dp))
            }

            // right: counter on top, buttons at the bottom
            Column(
                modifier = Modifier
                    .fillMaxHeight()
                    .padding(end = 10.dp, top = 8.dp, bottom = 8.dp),
                horizontalAlignment = Alignment.End,
                verticalArrangement = Arrangement.SpaceBetween, // needs fillMaxHeight above to work
            ) {
                Text(
                    text = "$participants/$capacity",
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                )
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    Button(
                        onClick = {}, // TODO: open the detail sheet
                        modifier = Modifier.height(30.dp),
                        // m3 buttons are 40dp min, too tall here, so we shrink the padding too
                        contentPadding = PaddingValues(horizontal = 10.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFFB9C2BC),
                        ),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text("i", fontSize = 12.sp)
                    }
                    Button(
                        onClick = onJoin,
                        modifier = Modifier.height(30.dp),
                        contentPadding = PaddingValues(horizontal = 12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF3E8E68),
                        ),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text("Rejoindre", fontSize = 12.sp)
                    }
                }
            }
        }
    }
}

@Preview
@Composable
private fun ActivityCardPreview() {
    ActivityCard(
        title = "Match de Foot",
        sport = Sport.FOOTBALL,
        dateTime = "Aujourd'hui - 14h30",
        place = "Morges, FC Forward",
        level = Level.ADVANCED,
        participants = 3,
        capacity = 10,
        onClick = {},
        onJoin = {},
    )
}
