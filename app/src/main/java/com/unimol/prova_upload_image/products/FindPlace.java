package com.unimol.prova_upload_image.products;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.unimol.prova_upload_image.R;

public class FindPlace extends AppCompatActivity {

    //Inizializzazione variabili
    private TextInputEditText etSource, etDestination;
    private MaterialButton btTrack;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_place);

        //Assegnazione Variabili
        etSource = findViewById(R.id.et_source);
        etDestination = findViewById(R.id.et_destination);
        String endpoint = (String) getIntent().getExtras().get("endpoint");
        etDestination.setText(endpoint);

        btTrack  =findViewById(R.id.bt_track);

        btTrack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Get value from edit text
                String sSource = etSource.getText().toString().trim();
                String sDestination = etDestination.getText().toString().trim();

                //Condizioni
                if(sSource.equals("") && sDestination.equals("")){
                    //se non vengono inseriti dati
                    Toast.makeText(getApplicationContext()
                            , "Inserisci entrambe le località", Toast.LENGTH_SHORT).show();
                }else{
                    DisplayTrack(sSource,sDestination);
                }
            }
        });

    }

    private void DisplayTrack(String sSource, String sDestination) {
        //Se non si ha google maps sul telefono, si apre la playstore
        try{
            //Dopo aver installato google maps, inizializza Uri
            Uri uri = Uri.parse("https://www.google.co.in/maps/dir/" + sSource + "/" + sDestination);

            //Inizializzazione INTENT con action view
            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
            //Set package
            intent.setPackage("com.google.android.apps.maps");
            //Start flag
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            //Start activity
            startActivity(intent);
        }catch (ActivityNotFoundException e){

            //Quando google maps non è installato
            Uri uri = Uri.parse("https://play.google.com/store/apps/details?id=com.google.android.apps.maps");

            //Inizializzazione INTENT con action view
            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
            //Start flag
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            //Start activity
            startActivity(intent);
        }














    }
}
