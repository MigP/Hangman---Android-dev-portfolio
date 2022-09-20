package bf.be.android.hangman.model.adapters

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import bf.be.android.hangman.R

class DefinitionsAdapter(val definitionsArray: ArrayList<String>, val passedContext: Context): RecyclerView.Adapter<DefinitionsAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DefinitionsAdapter.ViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.definitions_list_item, parent, false)
        return ViewHolder(v)
    }

    override fun getItemCount(): Int {
        return definitionsArray.size
    }

    override fun onBindViewHolder(holder: DefinitionsAdapter.ViewHolder, position: Int) {
        holder.definitionNr.text = (position + 1).toString() + "."
        holder.definition.text = definitionsArray[position]
    }

    inner class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        var definitionNr: TextView
        var definition: TextView

        init {
            definitionNr = itemView.findViewById(R.id.tv_item_number)
            definition = itemView.findViewById(R.id.tv_definition_item)
        }
    }
}