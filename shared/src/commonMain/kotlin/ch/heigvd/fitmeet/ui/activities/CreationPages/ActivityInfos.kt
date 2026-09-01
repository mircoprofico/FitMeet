package ch.heigvd.fitmeet.ui.activities
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Preview
@Composable
fun ActivityInfos() {
    MaterialTheme {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
        ){
            // Activity Name
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

            // Activity Description
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
        }
    }
}