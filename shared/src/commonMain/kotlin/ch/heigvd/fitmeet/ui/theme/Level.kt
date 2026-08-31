package ch.heigvd.fitmeet.ui.theme

import androidx.compose.ui.graphics.Color

enum class Level(val label: String, val color: Color) {
    ALL("Tous niveaux", Color(0xFF16B0D7)),

    BEGINNER("Débutant", Color(0xFF453794)),
    INTERMEDIATE("Intermédiaire", Color(0xFFE39643)),
    ADVANCED("Confirmé", Color(0xFFCF3838)),
}