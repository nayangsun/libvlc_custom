package com.example.libvlc_custom.player

import android.net.Uri
import android.view.SurfaceView

import org.videolan.libvlc.Media
import org.videolan.libvlc.RendererItem
import org.videolan.libvlc.interfaces.IMedia
import org.videolan.libvlc.interfaces.IVLCVout

import java.io.FileDescriptor

interface VlcPlayer : MediaPlayer {

    val vOut: IVLCVout

    val selectedRendererItem: RendererItem?

    val selectedSubtitleUri: Uri?

    val currentVideoTrack: IMedia.VideoTrack?

    val media: Media?

    fun setMedia(fileDescriptor: FileDescriptor?)

    fun attachSurfaces(
            surfaceMedia: SurfaceView,
            surfaceSubtitles: SurfaceView,
            layoutListener: IVLCVout.OnNewVideoLayoutListener
    )

    fun detachSurfaces()

    fun setRendererItem(rendererItem: RendererItem?)

    fun setVolume(volume: Int)

    fun setAspectRatio(aspectRatio: String?)

    fun setScale(scale: Float)
}