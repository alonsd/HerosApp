package com.example.superheros.Controllers;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatDialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.superheros.Model.Hero;
import com.example.superheros.R;
import com.jsibbold.zoomage.ZoomageView;
import com.squareup.picasso.Picasso;

public class HeroImageFragment extends AppCompatDialogFragment {

    private ZoomageView zoomageView;
    private Hero selectedHero;

    public void setSelectedHero(Hero selectedHero) {
        this.selectedHero = selectedHero;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.hero_image_layout_fragment, null);
        builder.setView(view);
        zoomageView = view.findViewById(R.id.zoomImage);
        Picasso.get()
                .load(selectedHero.image)
                .placeholder(R.drawable.ic_launcher_background)
                .error(R.drawable.ic_launcher_foreground)
                .into(zoomageView);

        return builder.create();
    }


}
