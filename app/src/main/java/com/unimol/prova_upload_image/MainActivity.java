package com.unimol.prova_upload_image;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.MenuItem;

import com.google.android.material.bottomnavigation.BottomNavigationView;


public class MainActivity extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener {
    private BottomNavigationView bottomNavigationView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        bottomNavigationView = findViewById(R.id.bottom_navigation_view);

        //quando l'app si avvia entrerà nella schermata home
        bottomNavigationView.setOnNavigationItemSelectedListener(this);
        bottomNavigationView.setSelectedItemId(R.id.navigation_home);
    }

    Account accountfragment = new Account();
    Search searchfragment = new Search();
    Home homefragment = new Home();
    AddProduct addProductfragment = new AddProduct();
    MyProducts myProductsfragment = new MyProducts();


    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){

            case R.id.navigation_home:
                getSupportFragmentManager().beginTransaction().setCustomAnimations(R.anim.fade_in, R.anim.fade_out)
                        .replace(R.id.container_bar, homefragment).commit();
                return true;

            case R.id.navigation_search:
                getSupportFragmentManager().beginTransaction().setCustomAnimations(R.anim.fade_in, R.anim.fade_out)
                        .replace(R.id.container_bar, searchfragment).commit();
                return true;

            case R.id.navigation_add_product:
                getSupportFragmentManager().beginTransaction().setCustomAnimations(R.anim.fade_in, R.anim.fade_out)
                        .replace(R.id.container_bar, addProductfragment).commit();
                return true;

            case R.id.navigation_my_products:
                getSupportFragmentManager().beginTransaction().setCustomAnimations(R.anim.fade_in, R.anim.fade_out)
                        .replace(R.id.container_bar, myProductsfragment).commit();
                return true;

            case R.id.navigation_account:
                getSupportFragmentManager().beginTransaction().setCustomAnimations(R.anim.fade_in, R.anim.fade_out)
                        .replace(R.id.container_bar, accountfragment).commit();
                return true;

        }
        return false;
    }


    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }
}