package com.unimol.prova_upload_image;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.card.MaterialCardView;
import com.unimol.prova_upload_image.products.Notebook;
import com.unimol.prova_upload_image.products.PcDesktop;


public class Home extends Fragment {

    private MaterialCardView pcDesktop, notebook, tablet, smartphone, smartwatch, accessories;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
            final View fragmentHome = inflater.inflate(R.layout.fragment_home, container, false);

            pcDesktop = fragmentHome.findViewById(R.id.pc_desktop);
            notebook = fragmentHome.findViewById(R.id.notebook);
            tablet = fragmentHome.findViewById(R.id.tablet);
            smartphone = fragmentHome.findViewById(R.id.smartphone);
            smartwatch = fragmentHome.findViewById(R.id.smartwatch);
            accessories = fragmentHome.findViewById(R.id.accessories);

            pcDesktop.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getContext(), PcDesktop.class);
                    startActivity(intent);
                }
            });

            notebook.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getContext(), Notebook.class);
                    startActivity(intent);
                }
            });

            // Inflate the layout for this fragment
            return fragmentHome;
    }
}