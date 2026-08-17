package ch.heigvd.fitmeet

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform