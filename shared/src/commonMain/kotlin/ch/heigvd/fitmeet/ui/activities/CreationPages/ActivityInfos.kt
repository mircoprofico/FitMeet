package ch.heigvd.fitmeet.ui.activities
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeContentPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ch.heigvd.fitmeet.ui.theme.Sport
import org.jetbrains.compose.resources.painterResource


@Preview
@Composable
fun ActivityInfos() {
    MaterialTheme {
        Column(
            modifier = Modifier
                .background(Color(0xFF102E53))
                .safeContentPadding()
                .fillMaxSize()
                .padding(horizontal = 28.dp, vertical=48.dp)
                .imePadding(),
            horizontalAlignment = Alignment.CenterHorizontally,
        ){
            /** New Activity title*/
            // Spacing before title
            Spacer(
                modifier = Modifier.height(60.dp)
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
                modifier = Modifier.height(90.dp)
            )

            // current page title
            Text(
                "Quel nom voulez vous donner à cette activité?",
                style = MaterialTheme.typography.headlineSmall,
                color = Color(0xFFAAAAAA),
                fontWeight = FontWeight.W500,
                fontSize = 32.sp
            )
            TextField(
                value = activityData.name,
                onValueChange = {activityData.name = it},
                placeholder ={Text("Ex: match amical, tour du lac, etc...")},
                modifier = Modifier.fillMaxWidth().shadow(
                    elevation = 4.dp,
                    shape = RoundedCornerShape(10.dp)
                ),
                shape = RoundedCornerShape(20.dp)
            )
            Spacer(
                modifier = Modifier.height(20.dp)
            )

            Text(
                "Vous pouvez ajouter plus de détail si nécessaire ici",
                style = MaterialTheme.typography.headlineSmall,
                color = Color(0xFFAAAAAA),
                fontWeight = FontWeight.W500,
                fontSize = 24.sp
            )

            TextField(
                value = activityData.description,
                onValueChange = {activityData.description = it},
                placeholder ={Text("Donnez des détails supplémentaires...")},
                singleLine = false,
                minLines = 5,
                maxLines = 6,
                modifier = Modifier.fillMaxWidth().shadow(
                    elevation = 4.dp,
                    shape = RoundedCornerShape(10.dp)
                ),
                shape = RoundedCornerShape(20.dp)
            )


            /** Button for next page */
            // push the button to the bottom of the screen
            Spacer(
                modifier = Modifier.weight(1f)
            )


            // Next page Button
            Button(onClick = { currentScreen.value += 1 },
                enabled = activityData.name != "",
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



