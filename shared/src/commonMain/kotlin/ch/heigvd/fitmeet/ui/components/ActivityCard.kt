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
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
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
        // fixed height so every card lines up in the list.
        // 100 and not 88: at 88 the level chip gets cut off, compose line
        // height is around 1.5x the font size and that adds up.
        Row(modifier = Modifier.height(100.dp)) {
            // left: sport icon on its tinted square
            Box(
                modifier = Modifier
                    .width(88.dp)
                    .fillMaxHeight()
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
                    .padding(horizontal = 12.dp, vertical = 4.dp),
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
                LevelChip(level, Modifier.padding(top = 6.dp))
            }

            // right: counter on top, buttons at the bottom
            Column(
                modifier = Modifier
                    .fillMaxHeight()
                    .padding(end = 15.dp, top = 8.dp, bottom = 7.dp),
                horizontalAlignment = Alignment.End,
                verticalArrangement = Arrangement.SpaceBetween, // needs fillMaxHeight above to work
            ) {
                Text(
                    text = "$participants/$capacity",
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                )
                Row(horizontalArrangement = Arrangement.spacedBy(3.dp)) {
                    Button(
                        onClick = {}, // TODO: open the detail sheet
                        // square 27x27, sizes measured on the mockup.
                        // m3 buttons are 40dp min so we force the size and
                        // zero the padding, otherwise the text pushes it wider
                        modifier = Modifier.size(27.dp),
                        contentPadding = PaddingValues(0.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFFE3E3E3),
                            contentColor = Color(0xFF8A8A8A),
                        ),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text("i", fontSize = 12.sp)
                    }
                    Button(
                        onClick = onJoin,
                        modifier = Modifier.width(111.dp).height(27.dp),
                        contentPadding = PaddingValues(0.dp),
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
