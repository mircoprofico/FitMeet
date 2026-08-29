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
    FOOTBALL("Football", Res.drawable.sport_football, Color(0xFFDCF7D0)),
    BASKETBALL("Basketball", Res.drawable.sport_basketball, Color(0xFFFBE0D0)),
    VOLLEYBALL("Volleyball", Res.drawable.sport_volleyball, Color(0xFFFFF6D0)),
    TENNIS("Tennis", Res.drawable.sport_tennis, Color(0xFFF4F7D0)),
    BADMINTON("Badminton", Res.drawable.sport_badminton, Color(0xFFE0EAFA)),
    RUNNING("Course", Res.drawable.sport_running, Color(0xFFFADDE6)),
    CYCLING("Vélo", Res.drawable.sport_cycling, Color(0xFFEDE2F5)),
    HIKING("Randonnée", Res.drawable.sport_hiking, Color(0xFFE5DFD0)),
    OTHER("Autre", Res.drawable.sport_other, Color(0xFFECEEEC)),
}
