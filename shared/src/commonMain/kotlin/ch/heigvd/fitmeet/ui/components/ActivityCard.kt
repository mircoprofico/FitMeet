package ch.heigvd.fitmeet.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
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
import androidx.compose.ui.text.style.TextAlign
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
    isJoined: Boolean = false,
    canLeave: Boolean = false,
    onClick: () -> Unit,
    onJoin: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Card(onClick = onClick, modifier = modifier.fillMaxWidth()) {
        // 112 dp: the content needs 99.5 and compose rounds up, at 100 the
        // level chip was getting clipped by the bottom edge.
        Row(modifier = Modifier.height(112.dp)) {

            // left: sport icon on its tinted block
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

            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
                    .padding(start = 12.dp, end = 15.dp, top = 4.dp, bottom = 4.dp),
                // SpaceBetween pins the bottom row: the chip and the buttons
                // never move, whatever the title does above them
                verticalArrangement = Arrangement.SpaceBetween,
            ) {
                // top: the texts take the full width, only the counter limits them
                Row {
                    Column(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(4.dp), // title <-> block
                    ) {
                        Text(
                            text = title,
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp,
                            maxLines = 1, // one line only, long titles get an ellipsis
                            overflow = TextOverflow.Ellipsis,
                        )
                        // date and place go together, tighter than with the title
                        // tight: lineHeight does the separating, no extra gap needed
                        Column(verticalArrangement = Arrangement.spacedBy(0.dp)) {
                            // one line each too: a long place name would wrap
                            // and push the card past its fixed height
                            Text(
                                text = dateTime,
                                fontSize = 13.sp,
                                lineHeight = 17.sp,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                            )
                            Text(
                                text = place,
                                fontSize = 13.sp,
                                lineHeight = 17.sp,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                            )
                        }
                    }
                    Text(
                        text = "$participants/$capacity",
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        textAlign = TextAlign.End,
                        // fixed width so "12/12" does not steal room from the
                        // title: every card wraps at the same place
                        modifier = Modifier.padding(start = 8.dp).width(44.dp),
                    )
                }

                // bottom: level on the left, actions on the right
                Row(verticalAlignment = Alignment.CenterVertically) {
                    LevelChip(level)
                    Spacer(Modifier.weight(1f))
                    // three states: join, leave in red, or full and disabled
                    val full = participants >= capacity
                    Button(
                        onClick = onJoin,
                        enabled = isJoined || !full,
                        modifier = Modifier.width(111.dp).height(27.dp),
                        contentPadding = PaddingValues(0.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = when {
                                isJoined -> Color(0xFFCF3838)
                                else -> Color(0xFF3E8E68)
                            },
                            disabledContainerColor = Color(0xFFB9C2BC),
                        ),
                        shape = RoundedCornerShape(8.dp),
                    ) {
                        Text(
                            text = when {
                                isJoined && canLeave -> "Quitter"
                                isJoined -> "Inscrit"
                                full -> "Complet"
                                else -> "Rejoindre"
                            },
                            fontSize = 13.sp,
                        )
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
