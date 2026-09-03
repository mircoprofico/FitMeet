package ch.heigvd.fitmeet.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ch.heigvd.fitmeet.ui.theme.Sport
import fitmeet.shared.generated.resources.Res
import fitmeet.shared.generated.resources.ic_close
import fitmeet.shared.generated.resources.ic_filter
import org.jetbrains.compose.resources.painterResource

private val Green = Color(0xFF3E8E68)
private val Ink = Color(0xFF16261F)
private val Line = Color(0xFFDDE5DD)

/**
 * one pinned filter button with a badge, then scrolling sport pills.
 * no separate reset button: a picked pill shows a cross and drops itself,
 * which is how current apps do it.
 */
@Composable
fun SportFilterBar(
    selected: Set<Sport>,
    // every active filter, sports included, not just the pills
    activeFilters: Int = selected.size,
    onToggle: (Sport) -> Unit,
    onOpenFilters: () -> Unit = {},
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        FilterButton(
            count = activeFilters,
            onClick = onOpenFilters,
            modifier = Modifier.padding(start = 12.dp, end = 8.dp),
        )

        // LazyRow: nine sports do not fit on one screen, this one scrolls
        LazyRow(
            contentPadding = PaddingValues(end = 12.dp),
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            items(Sport.entries) { sport ->
                SportPill(
                    sport = sport,
                    picked = sport in selected,
                    onClick = { onToggle(sport) },
                )
            }
        }
    }
}

@Composable
private fun FilterButton(count: Int, onClick: () -> Unit, modifier: Modifier = Modifier) {
    Box(modifier = modifier) {
        Box(
            modifier = Modifier
                .size(34.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(if (count > 0) Green else Color.White)
                .border(1.dp, if (count > 0) Green else Line, RoundedCornerShape(10.dp))
                .clickable(onClick = onClick),
            contentAlignment = Alignment.Center,
        ) {
            Image(
                painter = painterResource(Res.drawable.ic_filter),
                contentDescription = "Filtres",
                colorFilter = ColorFilter.tint(if (count > 0) Color.White else Ink),
                modifier = Modifier.size(18.dp),
            )
        }
        // the badge sits on the corner, offset pushes it outside the square
        if (count > 0) {
            Box(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .offset(x = 5.dp, y = (-5).dp)
                    .size(16.dp)
                    .clip(CircleShape)
                    .background(Color(0xFFCF3838)),
                contentAlignment = Alignment.Center,
            ) {
                Text("$count", fontSize = 9.sp, color = Color.White, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
private fun SportPill(sport: Sport, picked: Boolean, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .clip(CircleShape)
            .background(if (picked) Green else Color.White)
            .border(1.dp, if (picked) Green else Line, CircleShape)
            .clickable(onClick = onClick)
            .padding(start = 10.dp, end = if (picked) 6.dp else 12.dp, top = 6.dp, bottom = 6.dp),
        horizontalArrangement = Arrangement.spacedBy(5.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Image(
            painter = painterResource(sport.icon),
            contentDescription = null, // the label is right next to it
            modifier = Modifier.size(15.dp),
        )
        Text(
            text = sport.label,
            fontSize = 12.sp,
            fontWeight = if (picked) FontWeight.Bold else FontWeight.Normal,
            color = if (picked) Color.White else Ink,
        )
        // the cross replaces a global reset button: you drop filters one by one
        if (picked) {
            Image(
                painter = painterResource(Res.drawable.ic_close),
                contentDescription = "Retirer ${sport.label}",
                colorFilter = ColorFilter.tint(Color.White),
                modifier = Modifier.size(13.dp),
            )
        }
    }
}

@Preview
@Composable
private fun SportFilterBarPreview() {
    SportFilterBar(selected = setOf(Sport.FOOTBALL, Sport.TENNIS), onToggle = {})
}

@Preview
@Composable
private fun SportFilterBarEmptyPreview() {
    SportFilterBar(selected = emptySet(), onToggle = {})
}
