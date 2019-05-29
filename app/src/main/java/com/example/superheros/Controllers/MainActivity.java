package com.example.superheros.Controllers;

import android.content.SharedPreferences;
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
    public static final String SAVED_HERO_LIST = "heroesList";
    // Recyclerview variables -
    private RecyclerView heroesRecylerView;
    private ArrayList<Hero> heroesArrayList;
    private RecyclerView.LayoutManager layoutManager;
    private HeroesAdapter heroesAdapter;
    //Toolbar variables -
    private CollapsingToolbarLayout collapsingToolbarLayout;
    private ImageView toolbarImageView;
    //Persistence variables -
    private SharedPreferences sharedPreferences;
    private Gson gson;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initLocalData();

    }

    private void initLocalData() {
        try {
            heroesArrayList = loadFromLocal();
        }catch (Exception ex) {
            ex.printStackTrace();
        }
        if (heroesArrayList != null && heroesArrayList.size() > 0){
            initViews();
            for (int i = 0; i < heroesArrayList.size(); i++) {
                if (heroesArrayList.get(i).isFavorite()){
                    changeFavoriteHero(i, true);
                }
            }
        } else {
            initNetworking();
        }
    }

    private void initNetworking() {
        HttpRequest httpRequest = new HttpRequest(this, BASE_URL, new HttpRequest.OnHttpCompleteListener() {
            @Override
            public void onComplete(String response) {
                dataToModel(response, false);
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
                changeFavoriteHero(position, false);
            }
        });
        heroesRecylerView.setAdapter(heroesAdapter);

    }

    private void changeFavoriteHero(int position, boolean cameFromLocalStorage) {
        Hero currentHeroChosen = heroesArrayList.get(position);
        Picasso.get().load(currentHeroChosen.image)
                .fit()
                .centerCrop()
                .placeholder(R.drawable.ic_launcher_background)
                .error(R.drawable.ic_launcher_foreground)
                .into(toolbarImageView);
        collapsingToolbarLayout.setTitle(currentHeroChosen.title);
        if (!cameFromLocalStorage) {
            currentHeroChosen.setFavorite(!currentHeroChosen.isFavorite());
        } else {
            currentHeroChosen.setFavorite(true);
        }
        for (int i = 0; i < heroesArrayList.size(); i++) {
            if (i == position) continue;
            heroesArrayList.get(i).setFavorite(false);
        }
        saveToLocal(heroesArrayList);
        heroesAdapter.notifyDataSetChanged();
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

    private ArrayList<Hero> loadFromLocal(){
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
