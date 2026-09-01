package ch.heigvd.fitmeet.ui.activities

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ch.heigvd.fitmeet.ui.map.MapScreen

@Composable
fun ActivityLocation() {
    var selectedLat by remember { mutableStateOf<Double?>(null) }
    var selectedLng by remember { mutableStateOf<Double?>(null) }

    Column(modifier = Modifier.fillMaxSize()) {
        Text(
            text = if (selectedLat == null)
                "Appuyez sur la carte pour choisir la localisation"
            else {
                val lat = (selectedLat!! * 100000).toLong().toDouble() / 100000.0
                val lng = (selectedLng!! * 100000).toLong().toDouble() / 100000.0
                "$lat, $lng"
            },
            color = if (selectedLat == null) Color(0xFFAAAAAA) else Color.White,
            fontWeight = FontWeight.W500,
            fontSize = 18.sp,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth(),
        )

        Spacer(modifier = Modifier.height(12.dp))

        Box(modifier = Modifier.weight(1f).fillMaxWidth()) {
            MapScreen(
                onMapClick = { lat, lng ->
                    selectedLat = lat
                    selectedLng = lng
                    activityData.position = "POINT($lng $lat)"
                },
                selectedLat = selectedLat,
                selectedLng = selectedLng,
            )
        }
    }
}