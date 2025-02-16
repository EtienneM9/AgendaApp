package com.example.tp1agenda

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView

data class Event(
    val title: String,
    val dep: String,
    val fin: String,
    val date: String,
    val category: EventCategory
)

class EventAdapter(private var eventList: List<Event>) : RecyclerView.Adapter<EventAdapter.EventViewHolder>() {

    class EventViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val title: TextView = itemView.findViewById(R.id.eventName)
        val deb: TextView = itemView.findViewById(R.id.heure_deb)
        val fin: TextView = itemView.findViewById(R.id.heure_fin)
        val categoryColorView: View = itemView.findViewById(R.id.categoryColorView)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EventViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_event, parent, false)
        return EventViewHolder(view)
    }

    override fun onBindViewHolder(holder: EventViewHolder, position: Int) {
        val event = eventList[position]
        holder.title.text = event.title
        holder.deb.text = event.dep
        holder.fin.text = event.fin

        val color = ContextCompat.getColor(holder.itemView.context, event.category.colorRes)
        holder.categoryColorView.setBackgroundColor(color)

    }

    // Fonction pour mettre à jour les événements affichés
    fun updateEvents(newEvents: List<Event>) {
        eventList = newEvents
        notifyDataSetChanged()
    }

    override fun getItemCount() = eventList.size
}
