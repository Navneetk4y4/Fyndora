package com.example.studypomodoro

import android.app.Service
import android.content.Intent
import android.media.MediaPlayer
import android.os.Binder
import android.os.IBinder
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class MusicService : Service() {

    private var mediaPlayer: MediaPlayer? = null
    private var currentTrackIndex = 0
    private val tracks = MusicRepository.tracks

    private val binder = MusicBinder()
    private val scope = CoroutineScope(Job() + Dispatchers.Main)
    var progressListener: ((Int) -> Unit)? = null

    inner class MusicBinder : Binder() {
        fun getService(): MusicService = this@MusicService
    }

    override fun onBind(intent: Intent?): IBinder = binder

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        return START_STICKY
    }

    fun playOrPause() {
        if (mediaPlayer?.isPlaying == true) {
            mediaPlayer?.pause()
        } else {
            if (mediaPlayer == null) {
                playTrack(currentTrackIndex)
            } else {
                mediaPlayer?.start()
            }
        }
    }

    fun skipToNext() {
        currentTrackIndex = (currentTrackIndex + 1) % tracks.size
        playTrack(currentTrackIndex)
    }

    fun skipToPrevious() {
        currentTrackIndex = if (currentTrackIndex > 0) currentTrackIndex - 1 else tracks.size - 1
        playTrack(currentTrackIndex)
    }

    private fun playTrack(index: Int) {
        mediaPlayer?.release()
        mediaPlayer = MediaPlayer.create(this, tracks[index].resourceId).apply {
            setOnCompletionListener { skipToNext() }
            start()
        }
        startProgressUpdater()
    }

    private fun startProgressUpdater() {
        scope.launch {
            while (mediaPlayer?.isPlaying == true) {
                progressListener?.invoke(mediaPlayer!!.currentPosition)
                delay(1000)
            }
        }
    }

    fun getCurrentTrack(): MusicTrack = tracks[currentTrackIndex]

    fun isPlaying(): Boolean = mediaPlayer?.isPlaying ?: false

    fun getTrackDuration(): Int = mediaPlayer?.duration ?: 0

    fun seekTo(position: Int) {
        mediaPlayer?.seekTo(position)
    }

    override fun onDestroy() {
        super.onDestroy()
        mediaPlayer?.release()
        mediaPlayer = null
    }
}