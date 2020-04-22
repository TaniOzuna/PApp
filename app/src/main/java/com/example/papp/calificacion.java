package com.example.papp;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RatingBar;
import android.widget.TextView;

public class calificacion extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calificacion);

        final RatingBar ratingBar = (RatingBar) findViewById(R.id.rating_bar);
        Button Enviar = (Button) findViewById(R.id.enviar);
        final TextView ratingDisplayTextView = (TextView) findViewById(R.id.textView);

        Enviar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ratingDisplayTextView.setText("Your rating is: " + ratingBar.getRating());
            }
        });
    }
}
