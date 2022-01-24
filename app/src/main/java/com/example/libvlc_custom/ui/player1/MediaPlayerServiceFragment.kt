package com.example.libvlc_custom.ui.player1

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.IBinder
import androidx.fragment.app.Fragment
import com.example.libvlc_custom.player.services.MediaPlayerService
import com.example.libvlc_custom.player.services.MediaPlayerServiceBinder

abstract class MediaPlayerServiceFragment : Fragment() {

    protected var serviceBinder: MediaPlayerServiceBinder? = null

    private val serviceConnection = object : ServiceConnection {
        override fun onServiceDisconnected(p0: ComponentName?) {
            serviceBinder = null
        }

        override fun onServiceConnected(componentName: ComponentName?, binder: IBinder?) {
            serviceBinder = binder as? MediaPlayerServiceBinder

            onServiceConnected()
        }
    }

    protected abstract fun onServiceConnected()

    private fun bindMediaPlayerService() = requireActivity().bindService(
        Intent(requireContext().applicationContext, MediaPlayerService::class.java)
        , serviceConnection
        , Context.BIND_AUTO_CREATE
    )

    override fun onStart() {
        super.onStart()

        bindMediaPlayerService()
    }

    override fun onStop() {
        activity?.unbindService(serviceConnection)

        super.onStop()
    }

}