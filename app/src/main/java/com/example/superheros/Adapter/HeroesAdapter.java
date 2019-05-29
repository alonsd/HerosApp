package com.example.superheros.Adapter;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.superheros.Model.Hero;
import com.example.superheros.R;
import com.squareup.picasso.Picasso;

import java.util.List;

public class HeroesAdapter extends RecyclerView.Adapter<HeroesAdapter.HeroesViewHolder> {

    private List<Hero> heroList;
    protected OnHeroListClickListener heroListClickListener;

    public HeroesAdapter(List<Hero> heroList, OnHeroListClickListener heroListClickListener) {
        this.heroList = heroList;
        this.heroListClickListener = heroListClickListener;
    }

    @NonNull
    @Override
    public HeroesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View rowView = LayoutInflater.from(parent.getContext()).inflate(R.layout.hero_row, parent, false);
        HeroesViewHolder holder = new HeroesViewHolder(rowView);
        holder.heroesImage = rowView.findViewById(R.id.heroImage);
        holder.heroTitle = rowView.findViewById(R.id.heroTitle);
        holder.heroAbilities = rowView.findViewById(R.id.heroAbilities);

        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull HeroesViewHolder holder, int position) {

        final Hero currentHero = heroList.get(position);
        String str = String.join(",", currentHero.abilities);

        holder.heroTitle.setText(currentHero.title);
        holder.heroAbilities.setText(str);
        Picasso.get()
                .load(currentHero.image)
                .resize(500, 500)
                .placeholder(R.drawable.ic_launcher_background)
                .error(R.drawable.ic_launcher_foreground)
                .into(holder.heroesImage);
        if (!currentHero.isFavorite()){
            Picasso.get()
                    .load(R.drawable.empty_heart)
                    .into(holder.heartImageview);
        } else {
            Picasso.get()
                    .load(R.drawable.full_heart)
                    .into(holder.heartImageview);
        }
    }

    @Override
    public int getItemCount() {
        return heroList.size();
    }

    public class HeroesViewHolder extends RecyclerView.ViewHolder {

        ImageView heroesImage;
        TextView heroTitle;
        TextView heroAbilities;
        ImageView heartImageview;


        public HeroesViewHolder(@NonNull View itemView) {
            super(itemView);
            heroesImage = itemView.findViewById(R.id.heroImage);
            heroTitle = itemView.findViewById(R.id.heroTitle);
            heroAbilities = itemView.findViewById(R.id.heroAbilities);
            heartImageview = itemView.findViewById(R.id.heartImageView);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (heroListClickListener == null) return;
                    heroListClickListener.onListItemClicked(getAdapterPosition());
                }
            });
            heroesImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (heroListClickListener == null) return;
                    heroListClickListener.onListImageItemClicked(getAdapterPosition());
                }
            });
        }
    }

    public interface OnHeroListClickListener {
        void onListItemClicked(int position);
        void onListImageItemClicked(int position);
    }
}
