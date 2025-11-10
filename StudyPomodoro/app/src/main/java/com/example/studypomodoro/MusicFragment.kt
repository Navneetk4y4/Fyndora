package com.example.studypomodoro

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Bundle
import android.os.IBinder
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.SeekBar
import android.widget.TextView
import androidx.fragment.app.Fragment

class MusicFragment : Fragment() {

    private var musicService: MusicService? = null
    private var isBound = false

    private lateinit var trackTitleTextView: TextView
    private lateinit var playPauseButton: ImageButton
    private lateinit var nextButton: ImageButton
    private lateinit var previousButton: ImageButton
    private lateinit var seekBar: SeekBar

    private val connection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            val binder = service as MusicService.MusicBinder
            musicService = binder.getService()
            isBound = true
            updateUI()
            setupProgressListener()
        }

        override fun onServiceDisconnected(name: ComponentName?) {
            musicService = null
            isBound = false
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_music, container, false)

        trackTitleTextView = view.findViewById(R.id.tv_track_title)
        playPauseButton = view.findViewById(R.id.btn_play_pause_music)
        nextButton = view.findViewById(R.id.btn_next_music)
        previousButton = view.findViewById(R.id.btn_previous_music)
        seekBar = view.findViewById(R.id.seek_bar)

        playPauseButton.setOnClickListener {
            musicService?.playOrPause()
            updateUI()
        }

        nextButton.setOnClickListener {
            musicService?.skipToNext()
            updateUI()
        }

        previousButton.setOnClickListener {
            musicService?.skipToPrevious()
            updateUI()
        }

        seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                if (fromUser) {
                    musicService?.seekTo(progress)
                }
            }
            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })

        return view
    }

    override fun onStart() {
        super.onStart()
        Intent(requireActivity(), MusicService::class.java).also {
            requireActivity().startService(it)
            requireActivity().bindService(it, connection, Context.BIND_AUTO_CREATE)
        }
    }

    override fun onStop() {
        super.onStop()
        if (isBound) {
            requireActivity().unbindService(connection)
            isBound = false
        }
    }

    private fun updateUI() {
        if (isBound) {
            val track = musicService?.getCurrentTrack()
            trackTitleTextView.text = track?.title
            if (musicService?.isPlaying() == true) {
                playPauseButton.setImageResource(R.drawable.ic_pause)
            } else {
                playPauseButton.setImageResource(R.drawable.ic_play_arrow)
            }
            seekBar.max = musicService?.getTrackDuration() ?: 0
        }
    }

    private fun setupProgressListener() {
        musicService?.progressListener = { progress ->
            seekBar.progress = progress
        }
    }
}