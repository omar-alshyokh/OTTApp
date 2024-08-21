package com.smarttech.ottapp

import android.net.Uri
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.source.hls.HlsMediaSource
import com.google.android.exoplayer2.ui.PlayerView
import com.google.android.exoplayer2.upstream.DefaultHttpDataSource
import com.smarttech.ottapp.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private var player: ExoPlayer? = null
    private lateinit var binding: ActivityMainBinding
    private var playbackPosition: Long = 0L
    private var playWhenReady = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        // Set up ViewBinding
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initializePlayer()


    }
    private fun initializePlayer() {
        // Ensure player is only initialized once
        if (player == null) {
            // Initialize the ExoPlayer instance
            player = ExoPlayer.Builder(this).build()

            // Attach the player to the PlayerView in the layout
            binding.playerView.player = player

            // Create a MediaItem for the video URL
            val mediaItem = MediaItem.fromUri(Uri.parse("https://test-streams.mux.dev/x36xhzz/x36xhzz.m3u8"))

            // Create an HLS MediaSource
            val mediaSource = HlsMediaSource.Factory(DefaultHttpDataSource.Factory())
                .createMediaSource(mediaItem)

            // Prepare the player with the media source
            player?.setMediaSource(mediaSource)
            player?.prepare()

            // Restore playback state
            player?.seekTo(playbackPosition)
            player?.playWhenReady = playWhenReady
        }
    }

    private fun releasePlayer() {
        player?.let {
            // Save the playback position and state
            playbackPosition = it.currentPosition
            playWhenReady = it.playWhenReady

            // Release the player
            it.release()
            player = null
        }
    }

    override fun onStart() {
        super.onStart()
        initializePlayer()
    }

    override fun onResume() {
        super.onResume()
        initializePlayer()
    }

    override fun onPause() {
        super.onPause()
        releasePlayer()
    }

    override fun onStop() {
        super.onStop()
        releasePlayer()
    }

    override fun onDestroy() {
        super.onDestroy()
        releasePlayer()
    }
}