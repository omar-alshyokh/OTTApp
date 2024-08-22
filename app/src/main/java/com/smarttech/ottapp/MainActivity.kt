package com.smarttech.ottapp

import android.net.Uri
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.drm.DefaultDrmSessionManager
import com.google.android.exoplayer2.drm.FrameworkMediaDrm
import com.google.android.exoplayer2.drm.HttpMediaDrmCallback
import com.google.android.exoplayer2.source.MediaSource
import com.google.android.exoplayer2.source.dash.DashMediaSource
import com.google.android.exoplayer2.source.hls.HlsMediaSource
import com.google.android.exoplayer2.ui.PlayerView
import com.google.android.exoplayer2.upstream.DefaultHttpDataSource
import com.google.android.exoplayer2.util.Util
import com.smarttech.ottapp.databinding.ActivityMainBinding
import java.util.UUID

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
//
//            // Set up the DRM session manager
//            val drmSessionManager = buildDrmSessionManager("https://proxy.uat.widevine.com/proxy")



            // Create a MediaItem for the video URL
            val mediaItem = MediaItem.fromUri(Uri.parse("https://test-streams.mux.dev/x36xhzz/x36xhzz.m3u8"))

            // Create an HLS MediaSource
            val mediaSource = HlsMediaSource.Factory(DefaultHttpDataSource.Factory())
                .createMediaSource(mediaItem)

//            // Create the MediaSource with DRM
//            val mediaSource = buildMediaSource(
//                Uri.parse("https://storage.googleapis.com/wvmedia/cenc/h264/tears/tears.mpd"),
//                drmSessionManager
//            )

            // Prepare the player with the media source
            player?.setMediaSource(mediaSource)
            player?.prepare()

            // Restore playback state
            player?.seekTo(playbackPosition)
            player?.playWhenReady = playWhenReady
        }
    }

//    private fun buildDrmSessionManager(licenseUrl: String): DefaultDrmSessionManager {
//        // Widevine UUID
//        val widevineUuid: UUID = Util.getDrmUuid("widevine") ?: throw IllegalStateException("Widevine DRM not supported")
//
//        // MediaDrm callback to handle license acquisition
//        val mediaDrmCallback = HttpMediaDrmCallback(licenseUrl, DefaultHttpDataSource.Factory())
//
//        // Create a DrmSessionManager using the Widevine UUID and MediaDrm callback
//        return DefaultDrmSessionManager.Builder()
//            .setUuidAndExoMediaDrmProvider(widevineUuid, FrameworkMediaDrm.DEFAULT_PROVIDER)
//            .build(mediaDrmCallback)
//    }
//
//
//    private fun buildMediaSource(uri: Uri, drmSessionManager: DefaultDrmSessionManager): MediaSource {
//        // DASH media source with DRM support
//        return DashMediaSource.Factory(DefaultHttpDataSource.Factory())
//            .setDrmSessionManager(drmSessionManager)
//            .createMediaSource(MediaItem.fromUri(uri))
//    }

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