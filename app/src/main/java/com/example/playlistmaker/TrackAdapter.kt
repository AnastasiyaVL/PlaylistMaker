package com.example.playlistmaker

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import java.text.SimpleDateFormat
import java.util.Locale

class TrackAdapter(
    private var tracks: List<Track>,
    private val onItemClick: ((Track) -> Unit)? = null
) : RecyclerView.Adapter<TrackAdapter.TrackViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TrackViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_track, parent, false)
        return TrackViewHolder(view)
    }

    override fun onBindViewHolder(holder: TrackViewHolder, position: Int) {
        val track = tracks[position]
        holder.bind(track)
        holder.itemView.setOnClickListener {
            onItemClick?.invoke(track)
        }
    }

    override fun getItemCount(): Int = tracks.size

    fun updateTracks(newTracks: List<Track>) {
        tracks = newTracks
        notifyDataSetChanged()
    }

    class TrackViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        private val artworkImageView: ImageView = itemView.findViewById(R.id.artworkImageView)
        private val trackName: TextView = itemView.findViewById(R.id.trackName)
        private val artistName: TextView = itemView.findViewById(R.id.artistName)
        private val trackTime: TextView = itemView.findViewById(R.id.trackTime)

        private val dateFormat by lazy { SimpleDateFormat("mm:ss", Locale.getDefault()) }

        fun bind(track: Track) {
            trackName.text = track.trackName
            artistName.text = track.artistName
            val duration = dateFormat.format(track.trackTimeMillis)
            trackTime.text = duration
            Glide.with(itemView.context)
                .load(track.artworkUrl100)
                .placeholder(R.drawable.placeholder)
                .error(R.drawable.placeholder)
                .into(artworkImageView)
        }
    }
}
