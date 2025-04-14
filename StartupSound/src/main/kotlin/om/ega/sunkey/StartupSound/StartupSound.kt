package om.ega.sunkey.StartupSound

import android.content.Context
import android.media.AudioAttributes
import android.media.MediaPlayer
import com.aliucord.Utils
import com.aliucord.annotations.AliucordPlugin
import com.aliucord.entities.Plugin
import com.aliucord.patcher.*

@AliucordPlugin(requiresRestart = false)
class StartupSound : Plugin() {
    private var mediaPlayer: MediaPlayer? = null
    
    init {
        settingsTab = SettingsTab(PluginSettings::class.java).withArgs(settings)
    }
    
    override fun start(context: Context) {
        startupdiscord()
    }

    private val defaultSoundUrl = "https://github.com/OmegaSunkey/awesomeplugins/blob/main/Discord%20Startup%20Sound%20HQ.mp3?raw=true"

    private fun startupdiscord() {
        try {
            releaseMediaPlayer()
            
            val sonido = settings.getString("sonido", defaultSoundUrl)
            
            Utils.threadPool.execute {
                mediaPlayer = MediaPlayer().apply {
                    setAudioAttributes(
                        AudioAttributes.Builder()
                            .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                            .setUsage(AudioAttributes.USAGE_MEDIA)
                            .build()
                    )
                    setDataSource(sonido)
                    
                    setOnCompletionListener {
                        releaseMediaPlayer()
                    }
                    
                    prepare()
                    start()
                }
            }
        } catch (e: Throwable) {
            logger.error("UNABLE to play audio", e)
            releaseMediaPlayer()
        }
    }
    
    private fun releaseMediaPlayer() {
        mediaPlayer?.let {
            if (it.isPlaying) {
                it.stop()
            }
            it.release()
            mediaPlayer = null
        }
    }

    override fun stop(context: Context) {
        releaseMediaPlayer()
        patcher.unpatchAll()
    }
}
