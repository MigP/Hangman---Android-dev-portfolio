package bf.be.android.hangman.model

import android.app.Activity
import android.content.Context
import android.graphics.drawable.Drawable
import android.graphics.drawable.LayerDrawable
import android.os.CountDownTimer
import android.view.View
import android.widget.ImageView
import bf.be.android.hangman.R
import bf.be.android.hangman.view.GameActivity
import bf.be.android.hangman.viewModel.MainViewModel

class AvatarAnimations {
    var blinkTimerInit: CountDownTimer? = null
    var blinkTimerEnd: CountDownTimer? = null

    // --- Functional methods ---
    // Returns the drawable id of a string (image src name)
    private fun getStringIdentifier(context: Context, name: String?): Int {
        return context.resources.getIdentifier(name, "id", context.packageName)
    }

    // --- Methods to hide specific parts of the avatar ---
    // Hides all the graphics related to the avatar
    fun hideAvatarGraphics(context: Context) {
        // Gets the layers drawable
        val layerDrawable = context.resources.getDrawable(R.drawable.layers_gallows) as LayerDrawable
        val layerDrawableEyes = context.resources.getDrawable(R.drawable.layers_eyes) as LayerDrawable
        val layerDrawableEyebrows = context.resources.getDrawable(R.drawable.layers_eyebrows) as LayerDrawable
        val layerDrawableExtra = context.resources.getDrawable(R.drawable.layers_extra) as LayerDrawable
        val layerDrawableMouth = context.resources.getDrawable(R.drawable.layers_mouth) as LayerDrawable

        //Hides them all
        for (i in 1 until layerDrawable.numberOfLayers) { // starts with 1 because 0 is the gallows
            layerDrawable.getDrawable(i).alpha = 0
        }
        for (i in 0 until layerDrawableEyes.numberOfLayers) {
            layerDrawableEyes.getDrawable(i).alpha = 0
        }
        for (i in 0 until layerDrawableEyebrows.numberOfLayers) {
            layerDrawableEyebrows.getDrawable(i).alpha = 0
        }
        for (i in 0 until layerDrawableExtra.numberOfLayers) {
            layerDrawableExtra.getDrawable(i).alpha = 0
        }
        for (i in 0 until layerDrawableMouth.numberOfLayers) {
            layerDrawableMouth.getDrawable(i).alpha = 0
        }

        // Gets the ImageView where the layers drawable are by their id
        val layoutlist1 = (context as Activity).findViewById<View>(R.id.game_gallows_top) as ImageView
        val layoutlistEyes = context.findViewById<View>(R.id.game_eyes) as ImageView
        val layoutlistEyebrows = context.findViewById<View>(R.id.game_eyebrows) as ImageView
        val layoutlistExtra = context.findViewById<View>(R.id.game_extra) as ImageView
        val layoutlistMouth = context.findViewById<View>(R.id.game_mouth) as ImageView

        // Sets their src to the new updated layers drawable
        layoutlist1.setImageDrawable(layerDrawable)
        layoutlistEyes.setImageDrawable(layerDrawableEyes)
        layoutlistEyebrows.setImageDrawable(layerDrawableEyebrows)
        layoutlistExtra.setImageDrawable(layerDrawableExtra)
        layoutlistMouth.setImageDrawable(layerDrawableMouth)
    }

    // Hides all the graphics related to the avatar's eyes
    private fun hideAvatarEyesGraphics(context: Context) {
        // Gets the layer drawable
        val layerDrawableEyes = context.resources.getDrawable(R.drawable.layers_eyes) as LayerDrawable

        //Hides them all
        for (i in 0 until layerDrawableEyes.numberOfLayers) {
            layerDrawableEyes.getDrawable(i).alpha = 0
        }

        // Gets the ImageView where the layer drawable is by its id
        val layoutlistEyes = (context as Activity).findViewById<View>(R.id.game_eyes) as ImageView

        // Sets its src to the new updated layer drawable
        layoutlistEyes.setImageDrawable(layerDrawableEyes)
    }

    // Hides all the graphics related to the avatar's eyebrows
    private fun hideAvatarEyebrowsGraphics(context: Context) {
        // Gets the layer drawable
        val layerDrawableEyebrows = context.resources.getDrawable(R.drawable.layers_eyebrows) as LayerDrawable

        //Hides them all
        for (i in 0 until layerDrawableEyebrows.numberOfLayers) {
            layerDrawableEyebrows.getDrawable(i).alpha = 0
        }

        // Gets the ImageView where the layer drawable is by its id
        val layoutlistEyebrows = (context as Activity).findViewById<View>(R.id.game_eyebrows) as ImageView

        // Sets its src to the new updated layer drawable
        layoutlistEyebrows.setImageDrawable(layerDrawableEyebrows)
    }

    // Hides all the graphics related to the avatar's extras (glasses)
    private fun hideAvatarExtraGraphics(context: Context) {
        // Gets the layer drawable
        val layerDrawableExtra = context.resources.getDrawable(R.drawable.layers_extra) as LayerDrawable

        //Hides them all
        for (i in 0 until layerDrawableExtra.numberOfLayers) {
            layerDrawableExtra.getDrawable(i).alpha = 0
        }

        // Gets the ImageView where the layer drawable is by its id
        val layoutlistExtra = (context as Activity).findViewById<View>(R.id.game_extra) as ImageView

        // Sets its src to the new updated layer drawable
        layoutlistExtra.setImageDrawable(layerDrawableExtra)
    }

    // Hides all the graphics related to the avatar's mouth
    private fun hideAvatarMouthGraphics(context: Context) {
        // Gets the layer drawable
        val layerDrawableMouth = context.resources.getDrawable(R.drawable.layers_mouth) as LayerDrawable

        //Hides them all
        for (i in 0 until layerDrawableMouth.numberOfLayers) {
            layerDrawableMouth.getDrawable(i).alpha = 0
        }

        // Gets the ImageView where the layer drawable is by its id
        val layoutlistMouth = (context as Activity).findViewById<View>(R.id.game_mouth) as ImageView

        // Sets its src to the new updated layer drawable
        layoutlistMouth.setImageDrawable(layerDrawableMouth)
    }

    // --- Methods to prepare the specific parts of the avatar to be updated and displayed ---
    // Display dead avatar (updates the whole avatar because the last leg was added before calling this)
    suspend fun displayDeadAvatar(context: Context, viewModel: MainViewModel) {
        val tempAvatar = viewModel.activeAvatar!!.value
        viewModel.activeAvatar!!.value!!.eyesId = 6 // Closed eyes
        if (viewModel.activeAvatar!!.value?.complexion  == "light") {
            viewModel.activeAvatar!!.value!!.eyebrowsId = 6 // Sad light eyebrows
            viewModel.activeAvatar!!.value!!.mouthId = 6 // Side light mouth
        } else if (viewModel.activeAvatar!!.value?.complexion == "dark") {
            viewModel.activeAvatar!!.value!!.eyebrowsId = 5 // Sad dark eyebrows
            viewModel.activeAvatar!!.value!!.mouthId = 2 // Side dark mouth
        }

        viewModel._activeAvatar?.value = tempAvatar
        this.updateAvatar(context, viewModel)
    }

    // Display happy avatar (eyes, eyebrows, mouth)
    suspend fun displayHappyFaceEyesForwardAvatar(context: Context, viewModel: MainViewModel) {
        val activeEyesId = viewModel.activeAvatar!!.value!!.eyesId
        val activeEyebrowsId = viewModel.activeAvatar!!.value!!.eyebrowsId
        val activeMouthId = viewModel.activeAvatar!!.value!!.mouthId

        val tempAvatar = viewModel.activeAvatar!!.value
        viewModel.activeAvatar!!.value!!.eyesId = 9 // Eyes happy forward
        if (viewModel.activeAvatar!!.value?.complexion  == "light") {
            viewModel.activeAvatar!!.value!!.eyebrowsId = 4 // Neutral light eyebrows
            viewModel.activeAvatar!!.value!!.mouthId = 7 // Smiling light mouth
        } else if (viewModel.activeAvatar!!.value?.complexion == "dark") {
            viewModel.activeAvatar!!.value!!.eyebrowsId = 3 // Neutral dark eyebrows
            viewModel.activeAvatar!!.value!!.mouthId = 3 // Smiling dark mouth
        }

        viewModel._activeAvatar?.value = tempAvatar

        if (activeEyesId != 9) { // Prevents the refresh of the eyes if they don't change
            this.updateAvatarEyes(context, viewModel)
        }
        if (activeEyebrowsId != 3 && activeEyebrowsId != 4) { // Prevents the refresh of the eyebrows if they don't change
            this.updateAvatarEyebrows(context, viewModel)
        }
        if (activeMouthId != 3 && activeMouthId != 7) { // Prevents the refresh of the mouth if it doesn't change
            this.updateAvatarMouth(context, viewModel)
        }
    }

    // Display bored eyes up left avatar (eyes, eyebrows, mouth)
    suspend fun displayHappyFaceBoredEyesUpLeftAvatar(context: Context, viewModel: MainViewModel) {
        val tempAvatar = viewModel.activeAvatar!!.value
        viewModel.activeAvatar!!.value!!.eyesId = 10 // Eyes happy up left
        if (viewModel.activeAvatar!!.value?.complexion  == "light") {
            viewModel.activeAvatar!!.value!!.eyebrowsId = 4 // Neutral light eyebrows
            viewModel.activeAvatar!!.value!!.mouthId = 6 // Side light mouth
        } else if (viewModel.activeAvatar!!.value?.complexion == "dark") {
            viewModel.activeAvatar!!.value!!.eyebrowsId = 3 // Neutral dark eyebrows
            viewModel.activeAvatar!!.value!!.mouthId = 2 // Side dark mouth
        }

        viewModel._activeAvatar?.value = tempAvatar
        this.updateAvatarEyes(context, viewModel)
        this.updateAvatarEyebrows(context, viewModel)
        this.updateAvatarMouth(context, viewModel)
    }

    // Display bored eyes up right avatar (eyes, eyebrows, mouth)
    suspend fun displayHappyFaceBoredEyesUpRightAvatar(context: Context, viewModel: MainViewModel) {
        val tempAvatar = viewModel.activeAvatar!!.value
        viewModel.activeAvatar!!.value!!.eyesId = 11 // Eyes happy up right
        if (viewModel.activeAvatar!!.value?.complexion  == "light") {
            viewModel.activeAvatar!!.value!!.eyebrowsId = 4 // Neutral light eyebrows
            viewModel.activeAvatar!!.value!!.mouthId = 6 // Side light mouth
        } else if (viewModel.activeAvatar!!.value?.complexion == "dark") {
            viewModel.activeAvatar!!.value!!.eyebrowsId = 3 // Neutral dark eyebrows
            viewModel.activeAvatar!!.value!!.mouthId = 2 // Side dark mouth
        }

        viewModel._activeAvatar?.value = tempAvatar
        this.updateAvatarEyes(context, viewModel)
        this.updateAvatarEyebrows(context, viewModel)
        this.updateAvatarMouth(context, viewModel)
    }

    // Display bored eyes down left avatar (eyes, eyebrows, mouth)
    suspend fun displayHappyFaceBoredEyesDownLeftAvatar(context: Context, viewModel: MainViewModel) {
        val tempAvatar = viewModel.activeAvatar!!.value
        viewModel.activeAvatar!!.value!!.eyesId = 7 // Eyes happy down left
        if (viewModel.activeAvatar!!.value?.complexion  == "light") {
            viewModel.activeAvatar!!.value!!.eyebrowsId = 4 // Neutral light eyebrows
            viewModel.activeAvatar!!.value!!.mouthId = 6 // Side light mouth
        } else if (viewModel.activeAvatar!!.value?.complexion == "dark") {
            viewModel.activeAvatar!!.value!!.eyebrowsId = 3 // Neutral dark eyebrows
            viewModel.activeAvatar!!.value!!.mouthId = 2 // Side dark mouth
        }

        viewModel._activeAvatar?.value = tempAvatar
        this.updateAvatarEyes(context, viewModel)
        this.updateAvatarEyebrows(context, viewModel)
        this.updateAvatarMouth(context, viewModel)
    }

    // Display bored eyes down right avatar (eyes, eyebrows, mouth)
    suspend fun displayHappyFaceBoredEyesDownRightAvatar(context: Context, viewModel: MainViewModel) {
        val tempAvatar = viewModel.activeAvatar!!.value
        viewModel.activeAvatar!!.value!!.eyesId = 8 // Eyes happy down right
        if (viewModel.activeAvatar!!.value?.complexion  == "light") {
            viewModel.activeAvatar!!.value!!.eyebrowsId = 4 // Neutral light eyebrows
            viewModel.activeAvatar!!.value!!.mouthId = 6 // Side light mouth
        } else if (viewModel.activeAvatar!!.value?.complexion == "dark") {
            viewModel.activeAvatar!!.value!!.eyebrowsId = 3 // Neutral dark eyebrows
            viewModel.activeAvatar!!.value!!.mouthId = 2 // Side dark mouth
        }

        viewModel._activeAvatar?.value = tempAvatar
        this.updateAvatarEyes(context, viewModel)
        this.updateAvatarEyebrows(context, viewModel)
        this.updateAvatarMouth(context, viewModel)
    }

    // Display blink avatar (eyes)
    suspend fun displayBlinkAvatar(context: Context, viewModel: MainViewModel) {
        val tempAvatar = viewModel.activeAvatar!!.value
        viewModel.activeAvatar!!.value!!.eyesId = 6 // Closed eyes

        viewModel._activeAvatar?.value = tempAvatar
        this.updateAvatarEyes(context, viewModel)
    }

    // Display happy eyes avatar (eyes)
    suspend fun displayHappyEyesAvatar(context: Context, viewModel: MainViewModel) {
        val tempAvatar = viewModel.activeAvatar!!.value
        viewModel.activeAvatar!!.value!!.eyesId = 9 // Eyes happy forward

        viewModel._activeAvatar?.value = tempAvatar
        this.updateAvatarEyes(context, viewModel)
    }

    // --- Methods to display the updated parts of the avatar ---
    // Update displayed avatar (displays the relevant body parts only)
    suspend fun updateAvatar(context: Context, viewModel: MainViewModel) {
        val nrOfMisses = GameActivity.gameRound!!.letterMisses

        // Hides all displayed avatar graphics
        this.hideAvatarGraphics(context)

        // Gets relevant Ids from avatar object
        val avatarExtra = viewModel.activeAvatar!!.value!!.extrasId
        val avatarEyes = viewModel.activeAvatar!!.value!!.eyesId
        val avatarEyebrows = viewModel.activeAvatar!!.value!!.eyebrowsId
        val avatarMouth = viewModel.activeAvatar!!.value!!.mouthId

        // Gets the layers drawable
        val layerDrawable1 = context.resources.getDrawable(R.drawable.layers_gallows) as LayerDrawable
        val layerDrawableEyes = context.resources.getDrawable(R.drawable.layers_eyes) as LayerDrawable
        val layerDrawableEyebrows = context.resources.getDrawable(R.drawable.layers_eyebrows) as LayerDrawable
        val layerDrawableExtra = context.resources.getDrawable(R.drawable.layers_extra) as LayerDrawable
        val layerDrawableMouth = context.resources.getDrawable(R.drawable.layers_mouth) as LayerDrawable

        // Extra: Gets the resource id, then gets the item drawable by its id
        var extra: Drawable? = null
        if (avatarExtra > 0) {
            val extraResourceName = viewModel.findExtraById(context, avatarExtra.toLong())?.get(0)?.src
            extra = layerDrawableExtra.findDrawableByLayerId(getStringIdentifier(context, extraResourceName))
        }

        // Eyes: Gets the resource id, then gets the item drawable by its id
        val eyesResourceName = viewModel.findEyesById(context, avatarEyes.toLong())?.get(0)?.src
        val eyes = layerDrawableEyes.findDrawableByLayerId(getStringIdentifier(context, eyesResourceName))

        // Eyebrows: Gets the resource id, then gets the item drawable by its id
        val eyebrowsResourceName = viewModel.findEyebrowsById(context, avatarEyebrows.toLong())?.get(0)?.src
        val eyebrows = layerDrawableEyebrows.findDrawableByLayerId(getStringIdentifier(context, eyebrowsResourceName))

        // Mouth: Gets the resource id, then gets the item drawable by its id
        val mouthResourceName = viewModel.findMouthById(context, avatarMouth.toLong())?.get(0)?.src
        val mouth = layerDrawableMouth.findDrawableByLayerId(getStringIdentifier(context, mouthResourceName))

        // Body parts: Gets the item drawable by its id
        val head = layerDrawable1.findDrawableByLayerId(getStringIdentifier(context, viewModel.activeAvatar!!.value!!.headSrc))
        val leftArm = layerDrawable1.findDrawableByLayerId(getStringIdentifier(context, viewModel.activeAvatar!!.value!!.leftArmSrc))
        val rightArm = layerDrawable1.findDrawableByLayerId(getStringIdentifier(context, viewModel.activeAvatar!!.value!!.rightArmSrc))
        val leftLeg = layerDrawable1.findDrawableByLayerId(getStringIdentifier(context, viewModel.activeAvatar!!.value!!.leftLegSrc))
        val rightLeg = layerDrawable1.findDrawableByLayerId(getStringIdentifier(context, viewModel.activeAvatar!!.value!!.rightLegSrc))
        val torso = layerDrawable1.findDrawableByLayerId(getStringIdentifier(context, viewModel.activeAvatar!!.value!!.torsoSrc))

        when(nrOfMisses) {
            1 -> { // Show head
                // Sets the opacity of the avatar's parts
                head.alpha = 255
                leftArm.alpha = 0
                rightArm.alpha = 0
                leftLeg.alpha = 0
                rightLeg.alpha = 0
                torso.alpha = 0

                if (avatarExtra > 0) {
                    extra!!.alpha = 255
                }
                eyes.alpha = 255
                eyebrows.alpha = 255
                mouth.alpha = 255
            }
            2 -> { // Show head and torso
                // Sets the opacity of the avatar's parts
                head.alpha = 255
                leftArm.alpha = 0
                rightArm.alpha = 0
                leftLeg.alpha = 0
                rightLeg.alpha = 0
                torso.alpha = 255

                if (avatarExtra > 0) {
                    extra!!.alpha = 255
                }
                eyes.alpha = 255
                eyebrows.alpha = 255
                mouth.alpha = 255
            }
            3 -> { // Show head, torso, and left arm
                // Sets the opacity of the avatar's parts
                head.alpha = 255
                leftArm.alpha = 255
                rightArm.alpha = 0
                leftLeg.alpha = 0
                rightLeg.alpha = 0
                torso.alpha = 255

                if (avatarExtra > 0) {
                    extra!!.alpha = 255
                }
                eyes.alpha = 255
                eyebrows.alpha = 255
                mouth.alpha = 255
            }
            4 -> { // Show head, torso, and both arms
                // Sets the opacity of the avatar's parts
                head.alpha = 255
                leftArm.alpha = 255
                rightArm.alpha = 255
                leftLeg.alpha = 0
                rightLeg.alpha = 0
                torso.alpha = 255

                if (avatarExtra > 0) {
                    extra!!.alpha = 255
                }
                eyes.alpha = 255
                eyebrows.alpha = 255
                mouth.alpha = 255
            }
            5 -> { // Show head, torso, both arms, and left leg
                // Sets the opacity of the avatar's parts
                head.alpha = 255
                leftArm.alpha = 255
                rightArm.alpha = 255
                leftLeg.alpha = 255
                rightLeg.alpha = 0
                torso.alpha = 255

                if (avatarExtra > 0) {
                    extra!!.alpha = 255
                }
                eyes.alpha = 255
                eyebrows.alpha = 255
                mouth.alpha = 255
            }
            6 -> { // Show full avatar
                // Sets the opacity of the avatar's parts
                head.alpha = 255
                leftArm.alpha = 255
                rightArm.alpha = 255
                leftLeg.alpha = 255
                rightLeg.alpha = 255
                torso.alpha = 255

                if (avatarExtra > 0) {
                    extra!!.alpha = 255
                }
                eyes.alpha = 255
                eyebrows.alpha = 255
                mouth.alpha = 255
            }
        }

        // Gets the ImageViews where the layers drawable are by their id
        val layoutlist1 = (context as Activity).findViewById<View>(R.id.game_gallows_top) as ImageView
        val layoutlistEyes = context.findViewById<View>(R.id.game_eyes) as ImageView
        val layoutlistEyebrows = context.findViewById<View>(R.id.game_eyebrows) as ImageView
        val layoutlistExtra = context.findViewById<View>(R.id.game_extra) as ImageView
        val layoutlistMouth = context.findViewById<View>(R.id.game_mouth) as ImageView

        // Sets their src to the new updated layers drawable
        layoutlist1.setImageDrawable(layerDrawable1)
        layoutlistEyes.setImageDrawable(layerDrawableEyes)
        layoutlistEyebrows.setImageDrawable(layerDrawableEyebrows)
        layoutlistExtra.setImageDrawable(layerDrawableExtra)
        layoutlistMouth.setImageDrawable(layerDrawableMouth)
    }

    // Update displayed avatar's eyes (displays the relevant eyes only)
    private suspend fun updateAvatarEyes(context: Context, viewModel: MainViewModel) {
        val nrOfMisses = GameActivity.gameRound!!.letterMisses

        // Hides all displayed avatar's eyes graphics
        this.hideAvatarEyesGraphics(context)

        // Gets relevant Ids from avatar object
        val avatarEyes = viewModel.activeAvatar!!.value!!.eyesId

        // Gets the layer drawable
        val layerDrawableEyes = context.resources.getDrawable(R.drawable.layers_eyes) as LayerDrawable

        // Eyes: Gets the resource id, then gets the item drawable by its id
        val eyesResourceName = viewModel.findEyesById(context, avatarEyes.toLong())?.get(0)?.src
        val eyes = layerDrawableEyes.findDrawableByLayerId(getStringIdentifier(context, eyesResourceName))

        when(nrOfMisses) {
            1 -> {
                eyes.alpha = 255
            }
            2 -> {
                eyes.alpha = 255
            }
            3 -> {
                eyes.alpha = 255
            }
            4 -> {
                eyes.alpha = 255
            }
            5 -> {
                eyes.alpha = 255
            }
            6 -> {
                eyes.alpha = 255
            }
        }

        // Gets the ImageViews where the layers drawable are by their id
        val layoutlistEyes = (context as Activity).findViewById<View>(R.id.game_eyes) as ImageView

        // Sets their src to the new updated layers drawable
        layoutlistEyes.setImageDrawable(layerDrawableEyes)
    }

    // Update displayed avatar's eyebrows (displays the relevant eyebrows only)
    private suspend fun updateAvatarEyebrows(context: Context, viewModel: MainViewModel) {
        val nrOfMisses = GameActivity.gameRound!!.letterMisses

        // Hides all displayed avatar's eyebrows graphics
        this.hideAvatarEyebrowsGraphics(context)

        // Gets relevant Ids from avatar object
        val avatarEyebrows = viewModel.activeAvatar!!.value!!.eyebrowsId

        // Gets the layer drawable
        val layerDrawableEyebrows = context.resources.getDrawable(R.drawable.layers_eyebrows) as LayerDrawable

        // Eyes: Gets the resource id, then gets the item drawable by its id
        val eyebrowsResourceName = viewModel.findEyebrowsById(context, avatarEyebrows.toLong())?.get(0)?.src
        val eyebrows = layerDrawableEyebrows.findDrawableByLayerId(getStringIdentifier(context, eyebrowsResourceName))

        when(nrOfMisses) {
            1 -> {
                eyebrows.alpha = 255
            }
            2 -> {
                eyebrows.alpha = 255
            }
            3 -> {
                eyebrows.alpha = 255
            }
            4 -> {
                eyebrows.alpha = 255
            }
            5 -> {
                eyebrows.alpha = 255
            }
            6 -> {
                eyebrows.alpha = 255
            }
        }

        // Gets the ImageViews where the layers drawable are by their id
        val layoutlistEyebrows = (context as Activity).findViewById<View>(R.id.game_eyebrows) as ImageView

        // Sets their src to the new updated layers drawable
        layoutlistEyebrows.setImageDrawable(layerDrawableEyebrows)
    }

    // Update displayed avatar's extras (displays the relevant extras only)
    private suspend fun updateAvatarExtra(context: Context, viewModel: MainViewModel) {
        val nrOfMisses = GameActivity.gameRound!!.letterMisses

        // Hides all displayed avatar's extras graphics
        this.hideAvatarExtraGraphics(context)

        // Gets relevant Ids from avatar object
        val avatarExtra = viewModel.activeAvatar!!.value!!.extrasId

        // Gets the layer drawable
        val layerDrawableExtra = context.resources.getDrawable(R.drawable.layers_extra) as LayerDrawable

        // Eyes: Gets the resource id, then gets the item drawable by its id
        val extraResourceName = viewModel.findExtraById(context, avatarExtra.toLong())?.get(0)?.src
        val extras = layerDrawableExtra.findDrawableByLayerId(getStringIdentifier(context, extraResourceName))

        when(nrOfMisses) {
            1 -> {
                extras.alpha = 255
            }
            2 -> {
                extras.alpha = 255
            }
            3 -> {
                extras.alpha = 255
            }
            4 -> {
                extras.alpha = 255
            }
            5 -> {
                extras.alpha = 255
            }
            6 -> {
                extras.alpha = 255
            }
        }

        // Gets the ImageViews where the layers drawable are by their id
        val layoutlistExtra = (context as Activity).findViewById<View>(R.id.game_extra) as ImageView

        // Sets their src to the new updated layers drawable
        layoutlistExtra.setImageDrawable(layerDrawableExtra)
    }

    // Update displayed avatar's mouth (displays the relevant mouth only)
    private suspend fun updateAvatarMouth(context: Context, viewModel: MainViewModel) {
        val nrOfMisses = GameActivity.gameRound!!.letterMisses

        // Hides all displayed avatar's mouth graphics
        this.hideAvatarMouthGraphics(context)

        // Gets relevant Ids from avatar object
        val avatarMouth = viewModel.activeAvatar!!.value!!.mouthId

        // Gets the layer drawable
        val layerDrawableMouth = context.resources.getDrawable(R.drawable.layers_mouth) as LayerDrawable

        // Eyes: Gets the resource id, then gets the item drawable by its id
        val mouthResourceName = viewModel.findMouthById(context, avatarMouth.toLong())?.get(0)?.src
        val mouth = layerDrawableMouth.findDrawableByLayerId(getStringIdentifier(context, mouthResourceName))

        when(nrOfMisses) {
            1 -> {
                mouth.alpha = 255
            }
            2 -> {
                mouth.alpha = 255
            }
            3 -> {
                mouth.alpha = 255
            }
            4 -> {
                mouth.alpha = 255
            }
            5 -> {
                mouth.alpha = 255
            }
            6 -> {
                mouth.alpha = 255
            }
        }

        // Gets the ImageViews where the layers drawable are by their id
        val layoutlistMouth = (context as Activity).findViewById<View>(R.id.game_mouth) as ImageView

        // Sets their src to the new updated layers drawable
        layoutlistMouth.setImageDrawable(layerDrawableMouth)
    }

    // --- Methods that make use of timers to change the avatar's appearance
    // Avatar blink
    fun avatarBlink(viewModel: MainViewModel) {
        // Choose random mood
        val currentAvatarMood = viewModel._activeAvatarMood.value
        when ((1..14).random()) {
            1 -> {
                if (currentAvatarMood != AvatarMoods.FACE_BORED_EYES_UP_LEFT) viewModel._activeAvatarMood.value = AvatarMoods.FACE_BORED_EYES_UP_LEFT
            }
            2 -> {
                if (currentAvatarMood != AvatarMoods.FACE_BORED_EYES_UP_RIGHT) viewModel._activeAvatarMood.value = AvatarMoods.FACE_BORED_EYES_UP_RIGHT
            }
            3 -> {
                if (currentAvatarMood != AvatarMoods.FACE_BORED_EYES_DOWN_LEFT) viewModel._activeAvatarMood.value = AvatarMoods.FACE_BORED_EYES_DOWN_LEFT
            }
            4 -> {
                if (currentAvatarMood != AvatarMoods.FACE_BORED_EYES_DOWN_RIGHT) viewModel._activeAvatarMood.value = AvatarMoods.FACE_BORED_EYES_DOWN_RIGHT
            }
            else -> {
                if (currentAvatarMood != AvatarMoods.FACE_HAPPY_EYES_FORWARD) viewModel._activeAvatarMood.value = AvatarMoods.FACE_HAPPY_EYES_FORWARD
            }
        }

        // Cancels the timers if they already exist and are running
        if (blinkTimerInit != null) {
            (blinkTimerInit as CountDownTimer).cancel()
        }
        if (blinkTimerEnd != null) {
            (blinkTimerEnd as CountDownTimer).cancel()
        }

        // Sets a random duration
        val blinkTimerDuration = (1000..5000).random().toLong()

        // Defines the blinkTimerInit timer (avatar with open eyes)
        blinkTimerInit = object: CountDownTimer(blinkTimerDuration, blinkTimerDuration) {
            override fun onTick(millisUntilFinished: Long) {
            }

            override fun onFinish() {
                viewModel._activeAvatarMood.value = AvatarMoods.EYES_CLOSED

                // Starts the timer
                (blinkTimerEnd as CountDownTimer).start()
            }
        }

        // Defines the blinkTimerEnd timer (avatar with blinked eyes)
        blinkTimerEnd = object: CountDownTimer(150, 150) {
            override fun onTick(millisUntilFinished: Long) {
            }

            override fun onFinish() {
                viewModel._activeAvatarMood.value = AvatarMoods.FACE_HAPPY_EYES_FORWARD

                avatarBlink(viewModel)
            }
        }

        // Starts the timer
        (blinkTimerInit as CountDownTimer).start()
    }
}