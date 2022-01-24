package com.example.libvlc_custom.player.widget

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.FrameLayout
import android.widget.SeekBar
import android.widget.SeekBar.OnSeekBarChangeListener
import androidx.appcompat.widget.AppCompatImageButton
import androidx.appcompat.widget.AppCompatTextView
import androidx.appcompat.widget.Toolbar
import androidx.constraintlayout.widget.ConstraintLayout
import com.example.libvlc_custom.R
import com.example.libvlc_custom.player.utils.ThreadUtil
import com.example.libvlc_custom.player.utils.TimeUtil
import com.example.libvlc_custom.player.utils.ViewUtils

class PlayerControlOverlay @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
    defStyleRes: Int = 0
) : FrameLayout(context, attrs, defStyleAttr), OnSeekBarChangeListener {

    private val root: View
    private var toolbarsAreVisible = true
    private var isTrackingTouch = false
    private var hasSelectedRenderer = false
    private var showSubtitleMenuItem = false

    private val seekBarPosition: SeekBar
    private lateinit var callback: Callback
    private val textViewPosition: AppCompatTextView
    private val overlayContainer: ConstraintLayout
    private val textViewLength: AppCompatTextView
    private val imageButtonPlayPause: AppCompatImageButton


    init {
        root = LayoutInflater.from(context)
            .inflate(R.layout.player_overlay, this)

        readStyleAttributes(context, attrs)

        overlayContainer = root.findViewById(R.id.overlay_container)
        seekBarPosition = root.findViewById(R.id.seekbar_position)
        textViewPosition = root.findViewById(R.id.textview_position)
        textViewLength = root.findViewById(R.id.textview_length)
        imageButtonPlayPause = root.findViewById(R.id.imagebutton_play_pause)

        seekBarPosition.setOnSeekBarChangeListener(this)
        imageButtonPlayPause.setOnClickListener {
            callback.onPlayPauseButtonClicked()
        }
        root.setOnClickListener {
            handler.removeCallbacksAndMessages(null)
            toggleToolbarVisibility()
        }

    }

    interface Callback {
        fun onPlayPauseButtonClicked()
        fun onCastButtonClicked()
        fun onProgressChanged(progress: Int)
        fun onProgressChangeStarted()
        fun onSubtitlesButtonClicked()
    }

    private fun inflate(context: Context) {
        inflate(context, R.layout.player_overlay, this)
    }

    /**
     * Toggle the visibility of the toolbars by slide animating them.
     */
    private fun toggleToolbarVisibility() {
        // User is sliding seek bar, do not modify visibility.
        if (isTrackingTouch) {
            return
        }
        if (toolbarsAreVisible) {
            hideToolbars()
            return
        }
        showToolbars()
    }

    /**
     * Hide header and footer toolbars by translating them off the screen vertically.
     */
    private fun hideToolbars() {
        // Already hidden, do nothing.
        if (!toolbarsAreVisible) {
            return
        }
        toolbarsAreVisible = false
        ThreadUtil.onMain {
            ViewUtils.fadeOutViewAboveOrBelowParent(overlayContainer)
        }
    }

    /**
     * Show header and footer toolbars by translating them vertically.
     */
    private fun showToolbars() {
        // Already shown, do nothing.
        if (toolbarsAreVisible) {
            return
        }
        ThreadUtil.onMain {
            toolbarsAreVisible = true
            ViewUtils.fadeInViewAboveOrBelowParent(overlayContainer,false)
        }
    }


    private fun readStyleAttributes(context: Context, attrs: AttributeSet?) {
        if (attrs == null) {
            return
        }
        val styledAttributes = context.obtainStyledAttributes(
            attrs,
            R.styleable.PlayerControlComponent,
            0,
            0
        )
        hasSelectedRenderer = styledAttributes.getBoolean(
            R.styleable.PlayerControlComponent_showSubtitleMenuItem,
            false
        )
        showSubtitleMenuItem = styledAttributes.getBoolean(
            R.styleable.PlayerControlComponent_showSubtitleMenuItem,
            true
        )
        styledAttributes.recycle()
    }

    private fun getPlayPauseDrawableResourceId(isPlaying: Boolean): Int {
        return if (isPlaying) R.drawable.ic_pause_white_36dp else R.drawable.ic_play_arrow_white_36dp
    }

    fun configure(
        isPlaying: Boolean,
        time: Long,
        length: Long
    ) {
        val lengthText: String = TimeUtil.getTimeString(length)
        val positionText: String = TimeUtil.getTimeString(time)
        val progress = (time.toFloat() / length * 100).toInt()
        ThreadUtil.onMain {
            imageButtonPlayPause.setImageResource(
                getPlayPauseDrawableResourceId(isPlaying)
            )
            if (time < 0 || length < 0) {
                seekBarPosition.progress = 0
                textViewPosition.text = null
                textViewLength.text = null
                return@onMain
            }
            seekBarPosition.progress = progress
            textViewPosition.text = positionText
            textViewLength.text = lengthText
        }
    }

    fun registerCallback(callback: Callback) {
        this.callback = callback
    }

    override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
        TODO("Not yet implemented")
    }

    override fun onStartTrackingTouch(seekBar: SeekBar?) {
        isTrackingTouch = true
        callback.onProgressChangeStarted()
    }

    override fun onStopTrackingTouch(seekBar: SeekBar?) {
        isTrackingTouch = false
        callback.onProgressChanged(seekBar!!.progress)
    }

}