package bf.be.android.hangman.model

import android.content.Context
import android.media.MediaPlayer
import android.preference.PreferenceManager

class Sounds (private val context: Context) {

    // --- Sound effects ---
    // Sound effects
    fun playSound(soundFile: Int) {
        val prefs = PreferenceManager.getDefaultSharedPreferences(context)
        if (prefs.getString("sound", "on").equals("on")) {
            val soundToPlay = MediaPlayer.create(context, soundFile)
            soundToPlay.start()
            soundToPlay.setOnCompletionListener { soundToPlay ->
                soundToPlay.stop()
                soundToPlay?.release()
            }
        }
    }
}