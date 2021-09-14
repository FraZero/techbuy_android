package com.unimol.prova_upload_image;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.card.MaterialCardView;
import com.unimol.prova_upload_image.products.Accessories;
import com.unimol.prova_upload_image.products.Notebook;
import com.unimol.prova_upload_image.products.PcDesktop;
import com.unimol.prova_upload_image.products.Smartphone;
import com.unimol.prova_upload_image.products.Smartwatch;
import com.unimol.prova_upload_image.products.Tablet;


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

            tablet.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getContext(), Tablet.class);
                    startActivity(intent);
                }
            });

            smartphone.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getContext(), Smartphone.class);
                    startActivity(intent);
                }
            });

        smartwatch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), Smartwatch.class);
                startActivity(intent);
            }
        });

        accessories.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), Accessories.class);
                startActivity(intent);
            }
        });


            // Inflate the layout for this fragment
            return fragmentHome;
    }
}