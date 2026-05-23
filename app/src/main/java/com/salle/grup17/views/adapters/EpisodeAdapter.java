package com.salle.grup17.views.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.salle.grup17.R;
import com.salle.grup17.controllers.fragments.EpisodeDetailActivity;
import com.salle.grup17.models.Episode;
import java.util.List;

public class EpisodeAdapter extends RecyclerView.Adapter<EpisodeAdapter.EpisodeViewHolder> {

    private final List<Episode> episodeList;

    public EpisodeAdapter(List<Episode> episodeList) {
        this.episodeList = episodeList;
    }

    @NonNull
    @Override
    public EpisodeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_episode, parent, false);
        return new EpisodeViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull EpisodeViewHolder holder, int position) {
        Episode episode = episodeList.get(position);
        android.content.Context context = holder.itemView.getContext();

        holder.textEpisodeName.setText(episode.getName() != null ?
                episode.getName() :
                context.getString(R.string.loading_title));

        holder.textEpisodeCode.setText(episode.getEpisode() != null ?
                episode.getEpisode() :
                context.getString(R.string.default_episode_code));

        holder.textEpisodeAirDate.setText(episode.getAirDate() != null ?
                episode.getAirDate() :
                context.getString(R.string.unknown_date));

        holder.itemView.setOnClickListener(v -> {
            if (episode != null) {
                android.content.Intent intent = new android.content.Intent(v.getContext(), EpisodeDetailActivity.class);
                intent.putExtra("episode_name", episode.getName());
                intent.putExtra("episode_code", episode.getEpisode());
                intent.putExtra("episode_date", episode.getAirDate());
                intent.putStringArrayListExtra("character_urls", new java.util.ArrayList<>(episode.getCharacters()));
                v.getContext().startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return episodeList != null ? episodeList.size() : 0;
    }

    static class EpisodeViewHolder extends RecyclerView.ViewHolder {
        TextView textEpisodeCode, textEpisodeName, textEpisodeAirDate;

        public EpisodeViewHolder(@NonNull View itemView) {
            super(itemView);
            textEpisodeName = itemView.findViewById(R.id.textEpisodeName);
            textEpisodeCode = itemView.findViewById(R.id.textEpisodeCode);
            textEpisodeAirDate = itemView.findViewById(R.id.textEpisodeAirDate);
        }
    }
}