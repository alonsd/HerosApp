package com.example.superheros.Controllers;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.example.superheros.Adapter.HeroesAdapter;
import com.example.superheros.Model.Hero;
import com.example.superheros.Networking.HttpRequest;
import com.example.superheros.R;
import com.google.gson.Gson;
import com.sdsmdg.tastytoast.TastyToast;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;


public class MainActivity extends AppCompatActivity {

    //API constant -
    private static final String BASE_URL = "https://heroapps.co.il/employee-tests/android/androidexam.json";
    public static final String SAVED_HERO_LIST = "heroesList";
    public static final String PASSED_HERO_IMAGE = "passedHeroImage";
    // Recyclerview variables -
    private RecyclerView heroesRecylerView;
    private ArrayList<Hero> heroesArrayList;
    private LinearLayoutManager layoutManager;
    private HeroesAdapter heroesAdapter;
    //Toolbar variables -
    private CollapsingToolbarLayout collapsingToolbarLayout;
    private ImageView toolbarImageView;
    private ProgressBar progressBar;
    //Persistence variables -
    private SharedPreferences sharedPreferences;
    private Gson gson;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initLocalData();

    }

    //Data fetching from local, if exists -

    private void initLocalData() {
        try {
            heroesArrayList = loadFromLocal();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        if (heroesArrayList != null && heroesArrayList.size() > 0) {
            for (int i = 0; i < heroesArrayList.size(); i++) {
                Hero currentHero = heroesArrayList.get(i);
                if (currentHero.isFavorite()) {
                    Collections.swap(heroesArrayList, i, 0);
                    initViews(true);
                    changeFavoriteHero(0, true);
                }
            }
        } else {
            initNetworking();
        }
    }

    //Data fetching & parsing from network

    private void initNetworking() {
        HttpRequest httpRequest = new HttpRequest(this, BASE_URL, new HttpRequest.OnHttpCompleteListener() {
            @Override
            public void onComplete(String response) {
                dataToModel(response, false);
                initViews(false);
            }

            @Override
            public void onError(String error) {
                Log.d("error", error);
            }
        });
        httpRequest.request();
    }

    private ArrayList<Hero> dataToModel(String json, boolean isLocalInfo) {
        Gson gson = new Gson();
        Hero[] heroesList = gson.fromJson(json, Hero[].class);
        heroesArrayList = new ArrayList<>(Arrays.asList(heroesList));
        if (!isLocalInfo) {
            saveToLocal(heroesArrayList);
        }
        return heroesArrayList;
    }


    //Views initialization & Recyclerview methods -

    private void initViews(boolean didComeFromLocalStorage) {
        heroesRecylerView = findViewById(R.id.herosRecyclerView);
        collapsingToolbarLayout = findViewById(R.id.collapsingHeroToolbarLayout);
        toolbarImageView = findViewById(R.id.toolbarImageview);
        progressBar = findViewById(R.id.activityMainProgressBar);
        if (!didComeFromLocalStorage)
            progressBar.setVisibility(View.GONE);
        initRecyclerview();

    }

    private void initRecyclerview() {
        heroesRecylerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        heroesRecylerView.setLayoutManager(layoutManager);
        heroesAdapter = new HeroesAdapter(heroesArrayList, new HeroesAdapter.OnHeroListClickListener() {
            @Override
            public void onListItemClicked(int position) {
                TastyToast.makeText(MainActivity.this,
                        heroesArrayList.get(position).title + " Choosen!", TastyToast.LENGTH_SHORT, TastyToast.SUCCESS);
                changeFavoriteHero(position, false);
            }

            @Override
            public void onListImageItemClicked(int position) {
                Hero selectedHero = heroesArrayList.get(position);
                Intent intent = new Intent(MainActivity.this, Activity_Fullscreen_Hero.class);
                intent.putExtra(PASSED_HERO_IMAGE, gson.toJson(selectedHero));
                startActivity(intent);
            }
        });
        heroesRecylerView.setAdapter(heroesAdapter);
    }

    //Method that changes the favorite hero and saves to Shared Prefs

    private void changeFavoriteHero(int position, boolean cameFromLocalStorage) {
        Hero currentHeroChosen = heroesArrayList.get(position);
        Picasso.get().load(currentHeroChosen.image)
                .fit()
                .centerCrop()
                .error(R.drawable.ic_launcher_foreground)
                .into(toolbarImageView, new Callback() {
                    @Override
                    public void onSuccess() {
                        progressBar.setVisibility(View.GONE);
                    }

                    @Override
                    public void onError(Exception e) {

                    }
                });
        collapsingToolbarLayout.setTitle(currentHeroChosen.title);
        if (!cameFromLocalStorage) {
            currentHeroChosen.setFavorite(!currentHeroChosen.isFavorite());
        } else {
            currentHeroChosen.setFavorite(true);
        }
        for (int i = 0; i < heroesArrayList.size(); i++) {
            if (i == position) {
                Collections.swap(heroesArrayList, i, 0);
                heroesAdapter.notifyItemMoved(position, 0);
                heroesRecylerView.smoothScrollToPosition(0);
                continue;
            }
            heroesArrayList.get(i).setFavorite(false);
        }
        saveToLocal(heroesArrayList);
        heroesAdapter.notifyDataSetChanged();
    }


    //Shared preferences load & save methods -

    private void saveToLocal(ArrayList<Hero> heroesArrayList) {
        sharedPreferences = getPreferences(MODE_PRIVATE);
        SharedPreferences.Editor prefsEditor = sharedPreferences.edit();
        gson = new Gson();
        String heroesList = gson.toJson(heroesArrayList);
        Log.d("heroArrayList", heroesList);
        prefsEditor.putString(SAVED_HERO_LIST, heroesList);
        prefsEditor.commit();
    }

    private ArrayList<Hero> loadFromLocal() {
        sharedPreferences = getPreferences(MODE_PRIVATE);
        try {
            String fetchedHeroesJson = sharedPreferences.getString(SAVED_HERO_LIST, null);
            Log.d("retrivedJSON", fetchedHeroesJson);
            ArrayList<Hero> heroArrayList = dataToModel(fetchedHeroesJson, true);
            return heroArrayList;
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;

    }
}
