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

public class CharacterAdapter extends RecyclerView.Adapter<CharacterAdapter.CharacterViewHolder> {

    private List<Character> characters;

    public CharacterAdapter(List<Character> characters) {
        this.characters = characters;
    }

    @NonNull
    @Override
    public CharacterViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_character, parent, false);
        return new CharacterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CharacterViewHolder holder, int position) {
        Character character = characters.get(position);

        holder.nameText.setText(character.getName());
        holder.speciesText.setText(character.getSpecies());
        holder.statusText.setText(character.getStatus());

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

    public static class CharacterViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        TextView nameText, speciesText, statusText;

        public CharacterViewHolder(@NonNull View itemView) {
            super(itemView);

            imageView = itemView.findViewById(R.id.characterImage);
            nameText = itemView.findViewById(R.id.characterName);
            speciesText = itemView.findViewById(R.id.characterSpecies);
            statusText = itemView.findViewById(R.id.characterStatus);
        }
    }
}