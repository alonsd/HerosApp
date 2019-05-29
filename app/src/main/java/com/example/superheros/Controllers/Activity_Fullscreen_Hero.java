package com.example.superheros.Controllers;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.example.superheros.Model.Hero;
import com.example.superheros.R;
import com.google.gson.Gson;
import com.jsibbold.zoomage.ZoomageView;
import com.squareup.picasso.Picasso;

public class Activity_Fullscreen_Hero extends AppCompatActivity {

    public static final String PASSED_HERO_IMAGE = "passedHeroImage";
    ZoomageView zoomageView;
    View backButtonView;
    TextView heroTitle;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fullscreen_hero);
        initViews();
        fetchDataFromIntent();
    }

    private void fetchDataFromIntent() {
        String passedHero = getIntent().getStringExtra(PASSED_HERO_IMAGE);
        if (passedHero != null && !passedHero.isEmpty()){
            Gson gson = new Gson();
            Hero hero = gson.fromJson(passedHero, Hero.class);
            Picasso.get()
                    .load(hero.image)
                    .error(R.drawable.ic_launcher_foreground)
                    //.resize(zoomageView.getMaxWidth(), zoomageView.getMaxHeight())
                    .into(zoomageView);
            heroTitle.setText(hero.title);
        }
    }

    private void initViews() {
        zoomageView = findViewById(R.id.zoomImageview);
        backButtonView = findViewById(R.id.backButton);
        heroTitle = findViewById(R.id.fullscreenHeroTitle);

        backButtonView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}
