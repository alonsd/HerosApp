package com.example.superheros.Adapter;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.superheros.Model.Hero;
import com.example.superheros.R;
import com.squareup.picasso.Callback;
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
    public void onBindViewHolder(@NonNull final HeroesViewHolder holder, int position) {

        final Hero currentHero = heroList.get(position);
        String str = String.join(", ", currentHero.abilities);

        holder.heroTitle.setText(currentHero.title);
        holder.heroAbilities.setText(str);
        Picasso.get()
                .load(currentHero.image)
                .error(R.drawable.ic_launcher_foreground)
                .into(holder.heroesImage, new Callback() {
                    @Override
                    public void onSuccess() {
                        holder.progressBar.setVisibility(View.GONE);
                    }

                    @Override
                    public void onError(Exception e) {

                    }
                });
        if (!currentHero.isFavorite()){
            Picasso.get()
                    .load(R.drawable.empty_heart)
                    .into(holder.heartImageview);
        } else {
            Picasso.get()
                    .load(R.drawable.full_heart)
                    .into(holder.heartImageview);
        }

        setFadeAnimation(holder.itemView);
    }

    private void setFadeAnimation(View view) {
        AlphaAnimation alphaAnimation = new AlphaAnimation(0.0f, 1.0f);
        alphaAnimation.setDuration(500);
        view.startAnimation(alphaAnimation);
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
        ProgressBar progressBar;


        public HeroesViewHolder(@NonNull View itemView) {
            super(itemView);
            heroesImage = itemView.findViewById(R.id.heroImage);
            heroTitle = itemView.findViewById(R.id.heroTitle);
            heroAbilities = itemView.findViewById(R.id.heroAbilities);
            heartImageview = itemView.findViewById(R.id.heartImageView);
            progressBar = itemView.findViewById(R.id.adapterProgressBar);

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
