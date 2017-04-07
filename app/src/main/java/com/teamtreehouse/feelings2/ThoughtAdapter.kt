package com.teamtreehouse.feelings2

import android.graphics.Typeface
import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import org.jetbrains.anko.*

data class Thought(val condition: String, val notes: String)
class ThoughtAdapter : RecyclerView.Adapter<ThoughtAdapter.ViewHolder>() {
  val thoughts = mutableListOf<Thought>()

  override fun getItemCount() = thoughts.size

  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
    val view = with(parent.context) {
      verticalLayout {
        lparams {
          margin = dip(8)
        }
        textView {
          id = 1
          typeface = Typeface.DEFAULT_BOLD
        }
        textView { id = 2 }
      }
    }

    return ViewHolder(view)
  }

  override fun onBindViewHolder(holder: ViewHolder, position: Int) {
    holder.update(thoughts[position])
  }

  inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
    val t1 = view.find<TextView>(1)
    val t2 = view.find<TextView>(2)

    fun update(thought: Thought) {
      t1.text = thought.condition
      t2.text = thought.notes
    }
  }
}