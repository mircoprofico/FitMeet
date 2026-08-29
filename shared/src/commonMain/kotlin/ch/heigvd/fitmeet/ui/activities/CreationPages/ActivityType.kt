package ch.heigvd.fitmeet.ui.activities
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeContentPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.max
import androidx.compose.ui.unit.sp
import ch.heigvd.fitmeet.ui.theme.Sport
import org.jetbrains.compose.resources.painterResource

@Preview
@Composable
fun ActivityType() {
    MaterialTheme {
        Column(
            modifier = Modifier
                .background(Color(0xFF102E53))
                .safeContentPadding()
                .fillMaxSize()
                .padding(horizontal = 28.dp, vertical=48.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ){

            /** New Activity title*/
            // Spacing before title
            Spacer(
                modifier = Modifier.height(70.dp)
            )

            // Title. This should be the same for every pages of the activity creation
            Text(
                "Nouvelle Activité",
                style = MaterialTheme.typography.headlineLarge,
                color = Color(0xFFFFFFFF),
                fontWeight = FontWeight.W900,
                fontSize = 40.sp
            )
            Spacer(
                modifier = Modifier.height(100.dp)
            )

            // current page title
            Text(
                "Quel type d'activité\nvoulez vous créer?",
                style = MaterialTheme.typography.headlineSmall,
                color = Color(0xFFAAAAAA),
                fontWeight = FontWeight.W500,
                fontSize = 32.sp
            )
            Spacer(
                modifier = Modifier.height(20.dp)
            )

            /** Activities */

            LazyVerticalGrid(
                columns = GridCells.Fixed(3) // show activities on 3 columns
            ){
                items(Sport.entries){sport ->
                    Button(
                        onClick = {activityData.type = sport.label},
                        enabled = (activityData.type != sport.label),
                        modifier = Modifier.width(100.dp).height(120.dp).padding(5.dp),
                        shape = RoundedCornerShape(6.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF555555),
                            disabledContainerColor = Color(0xFF3E8E68),
                            disabledContentColor = Color(0xFFFFFFFF),
                            contentColor = Color(0xFF999999)
                        ),
                        ){
                        Image(
                            painter = painterResource(sport.icon),
                            contentDescription = sport.label,
                            modifier = Modifier
                                .fillMaxWidth()
                                .weight(1f)
                                .alpha(if (activityData.type == sport.label) 1f else 0.6f),
                            contentScale = ContentScale.Fit
                        )
                    }
                }
            }

            /** Button for next page */
            // push the button to the bottom of the screen
            Spacer(
                modifier = Modifier.weight(1f)
            )


            // Next page Button
            Button(onClick = { currentScreen.value += 1 },
                enabled = activityData.type != "None",
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF3E8E68),
                    disabledContainerColor = Color(0xFF888888)
                ),
                shape = RoundedCornerShape(6.dp),
                modifier = Modifier.align(Alignment.End)
            )
            {
                 Text("Suivant",
                     style = MaterialTheme.typography.headlineLarge,
                     fontWeight = FontWeight.W600
                 )
            }
        }
    }
}



