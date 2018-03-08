package edu.mriu.kankhol;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.PorterDuff;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;

import static edu.mriu.kankhol.HomeActivity.PREF_NAME;
import static edu.mriu.kankhol.Khelo.getResId;
import static edu.mriu.kankhol.SelectionActivity.PREF_CARTOON;
import static edu.mriu.kankhol.Sikho.getPrimaryColor;

/**
 * An activity showing the manual on How to use the Android App
 */

public class Manual extends AppCompatActivity {

    private ImageView home, help, info, report, content;
    private SharedPreferences pref;
    private int count = 0;
    private ImageButton back, next;
    private int[] contents = { R.drawable.manual, R.drawable.phon_aw_superviser_manual_2_02, R.drawable.phon_aw_superviser_manual_3_03 };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        pref = getSharedPreferences(PREF_NAME, MODE_PRIVATE);
        setTheme(getCurrentTheme());
        setContentView(R.layout.activity_manual);

        home = findViewById(R.id.btn_home);
        home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), HomeActivity.class));
                finish();
            }
        });
        content = findViewById(R.id.content);
        content.setImageResource(contents[count]);

        info = findViewById(R.id.btn_info);
        info.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), InfoActivity.class));
                finish();
            }
        });

        back = findViewById(R.id.backward);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(count>0){
                    count--;
                    content.setImageResource(contents[count]);
                }
            }
        });

        next = findViewById(R.id.next);
        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            if(count<contents.length - 1){
                count++;
                content.setImageResource(contents[count]);
            }
            }
        });

        help = findViewById(R.id.btn_document);
        int color = getPrimaryColor(this);
        help.setColorFilter(color);

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
