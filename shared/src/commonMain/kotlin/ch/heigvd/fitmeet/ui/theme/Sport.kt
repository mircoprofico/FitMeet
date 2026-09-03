package ch.heigvd.fitmeet.ui.theme

import androidx.compose.ui.graphics.Color
import fitmeet.shared.generated.resources.Res
import fitmeet.shared.generated.resources.sport_badminton
import fitmeet.shared.generated.resources.sport_basketball
import fitmeet.shared.generated.resources.sport_cycling
import fitmeet.shared.generated.resources.sport_football
import fitmeet.shared.generated.resources.sport_hiking
import fitmeet.shared.generated.resources.sport_other
import fitmeet.shared.generated.resources.sport_running
import fitmeet.shared.generated.resources.sport_tennis
import fitmeet.shared.generated.resources.sport_volleyball
import org.jetbrains.compose.resources.DrawableResource

enum class Sport(val label: String, val icon: DrawableResource, val tint: Color) {
    FOOTBALL("Football", Res.drawable.sport_football, Color(0xFFC9EDB8)),
    BASKETBALL("Basketball", Res.drawable.sport_basketball, Color(0xFFF8CFB2)),
    VOLLEYBALL("Volleyball", Res.drawable.sport_volleyball, Color(0xFFFCEDAE)),
    TENNIS("Tennis", Res.drawable.sport_tennis, Color(0xFFE9F0AF)),
    BADMINTON("Badminton", Res.drawable.sport_badminton, Color(0xFFC7DAF6)),
    RUNNING("Course", Res.drawable.sport_running, Color(0xFFF7C4D4)),
    CYCLING("Vélo", Res.drawable.sport_cycling, Color(0xFFDECBEF)),
    HIKING("Randonnée", Res.drawable.sport_hiking, Color(0xFFD8CDB2)),
    OTHER("Autre", Res.drawable.sport_other, Color(0xFFDDE2DD)),
}
