package edu.mriu.kankhol;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.PorterDuff;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import static edu.mriu.kankhol.HomeActivity.PREF_NAME;
import static edu.mriu.kankhol.Khelo.getResId;
import static edu.mriu.kankhol.SelectionActivity.PREF_CARTOON;
import static edu.mriu.kankhol.Sikho.getPrimaryColor;

/**
 * Activity showing the information of the Organisation and People Participated in Making the Android App
 */

public class InfoActivity extends AppCompatActivity {

    private ImageView home, help, info, report;
    private SharedPreferences pref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        pref = getSharedPreferences(PREF_NAME, MODE_PRIVATE);
        setTheme(getCurrentTheme());
        setContentView(R.layout.activity_info);

        home = findViewById(R.id.btn_home);
        home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), HomeActivity.class));
                finish();
            }
        });

        help = findViewById(R.id.btn_document);
        help.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), Manual.class));
                finish();
            }
        });

        info = findViewById(R.id.btn_info);
        int color = getPrimaryColor(this);
        info.setColorFilter(color);
        report = findViewById(R.id.btn_report);
        report.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), ResultActivity.class));
                finish();
            }
        });
    }

    @Override
    public void onBackPressed() {

    }

    private int getCurrentTheme(){
        int x = getResId("car"+pref.getInt(PREF_CARTOON, 0),R.style.class);
        if (x == -1) return R.style.AppTheme;
        else return x;
    }
}
