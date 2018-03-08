package edu.mriu.kankhol;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import es.dmoral.toasty.Toasty;

import static android.widget.Toast.LENGTH_LONG;
import static edu.mriu.kankhol.HomeActivity.PREF_CARTOON_SELECTED;
import static edu.mriu.kankhol.HomeActivity.PREF_NAME;
import static edu.mriu.kankhol.ProfileActivity.PREF_PROFILE_DONE;

/**
 * Selection activity is there to select the playing companion.
 * Here we select a cartoon
 * @input cartoon selection
 * @ouput on the basis of cartoon theme is setup.
 */

public class SelectionActivity extends AppCompatActivity implements View.OnClickListener{

    private ImageView i1, i2, i3, i4, i5, i6;
    private ImageView done;
    private int selection = 0;
    private SharedPreferences pref;
    public static final String PREF_CARTOON = "cartoon";
    private Activity activity;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Object obj = LocalDataStore.getObject("theme");
        setTheme( obj == null ? R.style.AppTheme : (Integer)obj);
        setContentView(R.layout.activity_selection);
        pref = getSharedPreferences(PREF_NAME, MODE_PRIVATE);
        activity = this;
        i1 = findViewById(R.id.image1);
        i2 = findViewById(R.id.image2);
        i3 = findViewById(R.id.image3);
        i4 = findViewById(R.id.image4);
        i5 = findViewById(R.id.image5);
        i6 = findViewById(R.id.image6);
        i1.setOnClickListener(this);
        i2.setOnClickListener(this);
        i3.setOnClickListener(this);
        i4.setOnClickListener(this);
        i5.setOnClickListener(this);
        i6.setOnClickListener(this);
        done = findViewById(R.id.done);
        done.setOnClickListener(this);
        if(LocalDataStore.getObject("selection") != null){
            isRecreate = false;
            selection = (Integer)LocalDataStore.getObject("selection");
            markSelection(selection);
            isRecreate = true;
        }
    }

    private boolean isRecreate = true;
    private void markSelection(int x){

        switch (x){
            case 1:
                i1.setAlpha(0.2f);
                i2.setAlpha(1f);
                i3.setAlpha(1f);
                i4.setAlpha(1f);
                i5.setAlpha(1f);
                i6.setAlpha(1f);
                LocalDataStore.putObject("selection", 1);
                LocalDataStore.putObject("theme",R.style.car1);
                if(isRecreate) if(isRecreate) recreate();
                break;
            case 2:
                i1.setAlpha(1f);
                i2.setAlpha(0.2f);
                i3.setAlpha(1f);
                i4.setAlpha(1f);
                i5.setAlpha(1f);
                i6.setAlpha(1f);
                LocalDataStore.putObject("selection", 2);
                LocalDataStore.putObject("theme",R.style.car2);
                if(isRecreate) recreate();
                break;
            case 3:
                i1.setAlpha(1f);
                i2.setAlpha(1f);
                i3.setAlpha(0.2f);
                i4.setAlpha(1f);
                i5.setAlpha(1f);
                i6.setAlpha(1f);
                LocalDataStore.putObject("selection", 3);
                LocalDataStore.putObject("theme",R.style.car3);
                if(isRecreate) recreate();
                break;
            case 4:
                i1.setAlpha(1f);
                i2.setAlpha(1f);
                i3.setAlpha(1f);
                i4.setAlpha(0.2f);
                i5.setAlpha(1f);
                i6.setAlpha(1f);
                LocalDataStore.putObject("selection", 4);
                LocalDataStore.putObject("theme",R.style.car4);
                if(isRecreate) recreate();
                break;
            case 5:
                i1.setAlpha(1f);
                i2.setAlpha(1f);
                i3.setAlpha(1f);
                i4.setAlpha(1f);
                i5.setAlpha(0.2f);
                i6.setAlpha(1f);
                LocalDataStore.putObject("selection", 5);
                LocalDataStore.putObject("theme",R.style.car5);
                if(isRecreate) recreate();
                break;
            case 6:
                i1.setAlpha(1f);
                i2.setAlpha(1f);
                i3.setAlpha(1f);
                i4.setAlpha(1f);
                i5.setAlpha(1f);
                i6.setAlpha(0.2f);
                LocalDataStore.putObject("selection", 6);
                LocalDataStore.putObject("theme",R.style.car6);
                if(isRecreate) recreate();
                break;
        }
    }

    @Override
    public void onClick(View view) {

        switch (view.getId()){
            case R.id.image1:
                markSelection(1);
                break;
            case R.id.image2:
                markSelection(2);
                break;
            case R.id.image3:
                markSelection(3);
                break;
            case R.id.image4:
                markSelection(4);
                break;
            case R.id.image5:
                markSelection(5);
                break;
            case R.id.image6:
                markSelection(6);
                break;

            case R.id.done:
                if(selection == 0){
                    Toasty.error(getApplicationContext(), "Select Cartoon", LENGTH_LONG).show();
                }
                else{
                    pref.edit().putBoolean(PREF_CARTOON_SELECTED, true).commit();
                    pref.edit().putInt(PREF_CARTOON, selection).commit();
                    goNext();
                }
                break;
        }
    }

    private void goNext(){
        if(!pref.getBoolean(PREF_PROFILE_DONE, false)) {
            startActivity(new Intent(getApplicationContext(), ProfileActivity.class));
        } else {
            startActivity(new Intent(getApplicationContext(), Sikho.class));
        }
        finish();
    }
}
