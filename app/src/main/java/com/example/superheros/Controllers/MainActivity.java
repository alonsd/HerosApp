package com.example.superheros.Controllers;

import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.ImageView;

import com.example.superheros.Adapter.HeroesAdapter;
import com.example.superheros.Model.Hero;
import com.example.superheros.Networking.HttpRequest;
import com.example.superheros.R;
import com.google.gson.Gson;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Arrays;

public class MainActivity extends AppCompatActivity {

    //API constant -
    private static final String BASE_URL = "https://heroapps.co.il/employee-tests/android/androidexam.json";
    // Recyclerview variables -
    private RecyclerView heroesRecylerView;
    private ArrayList<Hero> heroesArrayList;
    private RecyclerView.LayoutManager layoutManager;
    private HeroesAdapter heroesAdapter;
    //Toolbar variables -
    private CollapsingToolbarLayout collapsingToolbarLayout;
    private ImageView toolbarImageView;
    //Persistence variables -
    private Hero selectedFavoriteHero;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        InitNetworking();
    }

    private void InitNetworking() {
        HttpRequest httpRequest = new HttpRequest(this, BASE_URL, new HttpRequest.OnHttpCompleteListener() {
            @Override
            public void onComplete(String response) {
                dataToModel(response);
                initViews();
            }

            @Override
            public void onError(String error) {
                Log.d("error", error);
            }
        });
        httpRequest.request();
    }


    private void initViews() {
        heroesRecylerView = findViewById(R.id.herosRecyclerView);
        collapsingToolbarLayout = findViewById(R.id.collapsingHeroToolbarLayout);
        toolbarImageView = findViewById(R.id.toolbarImageview);
        heroesRecylerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        heroesRecylerView.setLayoutManager(layoutManager);
        heroesAdapter = new HeroesAdapter(heroesArrayList, new HeroesAdapter.OnHeroListClickListener() {
            @Override
            public void onListItemClicked(int position) {
                changeFavoriteHero(position);
            }
        });
        heroesRecylerView.setAdapter(heroesAdapter);

    }

    private void changeFavoriteHero(int position) {
        Hero pressedHeroRow = heroesArrayList.get(position);
        selectedFavoriteHero = pressedHeroRow;
        Picasso.get().load(pressedHeroRow.image)
                .fit()
                .centerCrop()
                .placeholder(R.drawable.ic_launcher_background)
                .error(R.drawable.ic_launcher_foreground)
                .into(toolbarImageView);
        collapsingToolbarLayout.setTitle(pressedHeroRow.title);
        pressedHeroRow.setFavorite(!pressedHeroRow.isFavorite());
        for (int i = 0; i < heroesArrayList.size(); i++) {
            if (i == position) continue;
            heroesArrayList.get(i).setFavorite(false);
        }
        heroesAdapter.notifyDataSetChanged();
    }


    private void dataToModel(String json){
        Gson gson = new Gson();
        Hero[] heroesList = gson.fromJson(json, Hero[].class);
        heroesArrayList = new ArrayList<>(Arrays.asList(heroesList));
    }
}
