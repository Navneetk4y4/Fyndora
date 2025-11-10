package com.example.studypomodoro

data class MusicTrack(val title: String, val resourceId: Int)

object MusicRepository {
    val tracks = listOf(
        MusicTrack("Lofi Beats", R.raw.lofi1),
        MusicTrack("Chill Vibes", R.raw.lofi2),
        MusicTrack("Ambient Focus", R.raw.lofi3),
        MusicTrack("Night Mood", R.raw.lofi4),
        MusicTrack("Study Session", R.raw.lofi5)
    )
}
