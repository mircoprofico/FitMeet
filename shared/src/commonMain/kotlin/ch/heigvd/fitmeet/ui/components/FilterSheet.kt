package ch.heigvd.fitmeet.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

private val Green = Color(0xFF3E8E68)
private val Ink = Color(0xFF16261F)
private val Line = Color(0xFFDDE5DD)

// the three date ranges the sheet offers. ALL means no date filter.
enum class DateRange(val label: String) {
    ALL("Toutes"),
    TODAY("Aujourd'hui"),
    THIS_WEEK("Cette semaine"),
}

/** what the funnel button opens: the filters that do not fit as pills. */
@Composable
fun FilterSheet(
    dateRange: DateRange,
    onDateRange: (DateRange) -> Unit,
    onlyWithSpots: Boolean,
    onOnlyWithSpots: (Boolean) -> Unit,
    onClearAll: () -> Unit,
    onClose: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(start = 20.dp, end = 20.dp, top = 4.dp, bottom = 28.dp),
        verticalArrangement = Arrangement.spacedBy(18.dp),
    ) {
        Text("Filtres", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = Ink)

        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text("Quand", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = Ink)
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                DateRange.entries.forEach { range ->
                    ChoicePill(
                        label = range.label,
                        picked = range == dateRange,
                        onClick = { onDateRange(range) },
                    )
                }
            }
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column {
                Text("Places disponibles", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = Ink)
                Text("Masquer les activités complètes", fontSize = 12.sp, color = Color(0xFF6B7C74))
            }
            Switch(
                checked = onlyWithSpots,
                onCheckedChange = onOnlyWithSpots,
                colors = SwitchDefaults.colors(checkedTrackColor = Green),
            )
        }

        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            Button(
                onClick = onClearAll,
                modifier = Modifier.weight(1f).height(46.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.White,
                    contentColor = Ink,
                ),
                border = androidx.compose.foundation.BorderStroke(1.dp, Line),
                shape = RoundedCornerShape(10.dp),
            ) {
                Text("Tout effacer", fontSize = 14.sp)
            }
            Button(
                onClick = onClose,
                modifier = Modifier.weight(1f).height(46.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Green),
                shape = RoundedCornerShape(10.dp),
            ) {
                Text("Voir les résultats", fontSize = 14.sp)
            }
        }
    }
}

@Composable
private fun ChoicePill(label: String, picked: Boolean, onClick: () -> Unit) {
    Text(
        text = label,
        fontSize = 13.sp,
        color = if (picked) Color.White else Ink,
        fontWeight = if (picked) FontWeight.Bold else FontWeight.Normal,
        modifier = Modifier
            .clip(CircleShape)
            .background(if (picked) Green else Color.White)
            .border(1.dp, if (picked) Green else Line, CircleShape)
            .clickable(onClick = onClick)
            .padding(horizontal = 14.dp, vertical = 8.dp),
    )
}

@Preview
@Composable
private fun FilterSheetPreview() {
    FilterSheet(
        dateRange = DateRange.THIS_WEEK,
        onDateRange = {},
        onlyWithSpots = true,
        onOnlyWithSpots = {},
        onClearAll = {},
        onClose = {},
    )
}
