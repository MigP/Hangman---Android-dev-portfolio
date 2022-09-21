package bf.be.android.hangman.model.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import bf.be.android.hangman.R
import bf.be.android.hangman.model.dal.entities.Highscore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class HighscoresAdapter (private val avatarsArray: ArrayList<String>, private val languagesArray: ArrayList<String>, private val usersArray: ArrayList<String>, private val highscoresArray: List<Highscore>, private val passedContext: Context): RecyclerView.Adapter<HighscoresAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HighscoresAdapter.ViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.highscores_list_item, parent, false)
        return ViewHolder(v)
    }

    override fun getItemCount(): Int {
        return highscoresArray.size
    }

    override fun onBindViewHolder(holder: HighscoresAdapter.ViewHolder, position: Int) {
        holder.highscorePosition.text = (position + 1).toString() + "."
        holder.highscoreScore.text = highscoresArray[position].score.toString()
        // Get user name
        CoroutineScope(Dispatchers.IO).launch {

        }
        holder.highscoreName.text = usersArray[position]
        holder.highscoreAvatarImg.setImageResource(passedContext.resources.getIdentifier(
            avatarsArray[position] + "_small",
            "drawable",
            passedContext.packageName
        ))
        holder.highscoreLanguageImg.setImageResource(passedContext.resources.getIdentifier(
            languagesArray[position],
            "drawable",
            passedContext.packageName
        ))
        holder.highscoreDate.text = highscoresArray[position].date
    }

    inner class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        var highscorePosition: TextView
        var highscoreScore: TextView
        var highscoreName: TextView
        var highscoreAvatarImg: ImageView
        var highscoreLanguageImg: ImageView
        var highscoreDate: TextView

        init {
            highscorePosition = itemView.findViewById(R.id.tv_item_number)
            highscoreScore = itemView.findViewById(R.id.tv_highscore_item)
            highscoreName = itemView.findViewById(R.id.tv_highscore_user_item)
            highscoreAvatarImg = itemView.findViewById(R.id.highscores_avatar_icon)
            highscoreLanguageImg = itemView.findViewById(R.id.highscores_language_icon)
            highscoreDate = itemView.findViewById(R.id.tv_highscore_date_item)
        }
    }
}