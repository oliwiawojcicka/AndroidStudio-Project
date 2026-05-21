package com.salle.grup17.views.adapters;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.salle.grup17.R;
import com.salle.grup17.controllers.CharacterDetailActivity;
import com.salle.grup17.models.Character;
import java.util.List;

public class EpisodeCharacterAdapter extends RecyclerView.Adapter<EpisodeCharacterAdapter.ViewHolder> {

    private final List<Character> characters;

    public EpisodeCharacterAdapter(List<Character> characters) {
        this.characters = characters;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_character_horizontal, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Character character = characters.get(position);

        holder.nameText.setText(character.getName());

        Glide.with(holder.itemView.getContext())
                .load(character.getImage())
                .into(holder.imageView);

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(holder.itemView.getContext(), CharacterDetailActivity.class);
            intent.putExtra("character_id", character.getId());
            holder.itemView.getContext().startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return characters.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        TextView nameText;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.horizCharacterImage);
            nameText = itemView.findViewById(R.id.horizCharacterName);
        }
    }
}