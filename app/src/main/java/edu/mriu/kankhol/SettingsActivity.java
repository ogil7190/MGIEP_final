package edu.mriu.kankhol;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import es.dmoral.toasty.Toasty;

import static edu.mriu.kankhol.HomeActivity.PREF_CARTOON_SELECTED;
import static edu.mriu.kankhol.HomeActivity.PREF_COUNT_KEY;
import static edu.mriu.kankhol.HomeActivity.PREF_COURSE_MATERIAL_EXTRACTED;
import static edu.mriu.kankhol.HomeActivity.PREF_NAME;
import static edu.mriu.kankhol.Khelo.PREF_ANALYTICS_SESSION;
import static edu.mriu.kankhol.Khelo.PREF_CARTOON_GROWTH;
import static edu.mriu.kankhol.Khelo.PREF_DATA;
import static edu.mriu.kankhol.Khelo.PREF_FIRST_GAME_DONE;
import static edu.mriu.kankhol.Khelo.PREF_GAME_MODE_KEY;
import static edu.mriu.kankhol.Khelo.UP_LIMIT;
import static edu.mriu.kankhol.ProfileActivity.PREF_PROFILE_AGE;
import static edu.mriu.kankhol.ProfileActivity.PREF_PROFILE_DONE;
import static edu.mriu.kankhol.ProfileActivity.PREF_PROFILE_GENDER;
import static edu.mriu.kankhol.ProfileActivity.PREF_PROFILE_ID;
import static edu.mriu.kankhol.ProfileActivity.PREF_PROFILE_NAME;
import static edu.mriu.kankhol.Sikho.PREF_DEMO;
import static edu.mriu.kankhol.Sikho.PREF_LEARN_DONE;
import static edu.mriu.kankhol.Sikho.getCurrentTheme;
import static edu.mriu.kankhol.Sikho.getPrimaryColor;

/**
 * Contains some options for changing the working of the Apps
 */

public class SettingsActivity extends AppCompatActivity {
    private Button changePlayer, changeCartoon, reset, pushData;
    private SharedPreferences pref;
    private ImageView home, help, info, report;
    private CardView card;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        pref = getSharedPreferences(PREF_NAME, MODE_PRIVATE);
        setTheme(getCurrentTheme(pref));
        setContentView(R.layout.activity_settings);
        card = findViewById(R.id.card);

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
        info.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), InfoActivity.class));
                finish();
            }
        });
        report = findViewById(R.id.btn_report);
        report.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), ResultActivity.class));
                finish();
            }
        });

        changeCartoon = findViewById(R.id.changeCartoon);
        changeCartoon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pref.edit().putBoolean(PREF_CARTOON_SELECTED, false).commit();
                startActivity(new Intent(getApplicationContext(), SelectionActivity.class));
                finish();
            }
        });

        changePlayer = findViewById(R.id.changePlayer);
        changePlayer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pref.edit().putInt(PREF_COUNT_KEY, 0).commit();
                pref.edit().putBoolean(PREF_LEARN_DONE, false).commit();
                pref.edit().putBoolean(PREF_GAME_MODE_KEY, false).commit();
                pref.edit().putBoolean(PREF_PROFILE_DONE, false).commit();
                pref.edit().putInt(PREF_CARTOON_GROWTH, 0).commit();
                pref.edit().putBoolean(PREF_CARTOON_SELECTED, false).commit();
                pref.edit().putInt(PREF_ANALYTICS_SESSION, 0).commit();
                pref.edit().putBoolean(PREF_COURSE_MATERIAL_EXTRACTED, false).commit();
                pref.edit().putBoolean(PREF_FIRST_GAME_DONE, false).commit();
                pref.edit().putString(PREF_PROFILE_NAME, "Register Again").commit();
                pref.edit().putString(PREF_PROFILE_AGE, "0").commit();
                new SQLiteHelper(getApplicationContext()).flushData();
                startActivity(new Intent(getApplicationContext(), HomeActivity.class));
                finish();
            }
        });

        pushData = findViewById(R.id.pushData);
        pushData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pushData();
                Toasty.normal(getApplicationContext(), "Pushing in Background...").show();
            }
        });

        reset = findViewById(R.id.reset);
        reset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pref.edit().putInt(PREF_COUNT_KEY, 0).commit();
                pref.edit().putBoolean(PREF_LEARN_DONE, false).commit();
                pref.edit().putBoolean(PREF_GAME_MODE_KEY, false).commit();
                pref.edit().putInt(PREF_CARTOON_GROWTH, 0).commit();
                pref.edit().putInt(PREF_ANALYTICS_SESSION, 0).commit();
                pref.edit().putBoolean(PREF_COURSE_MATERIAL_EXTRACTED, false).commit();
                pref.edit().putBoolean(PREF_FIRST_GAME_DONE, false).commit();
                new SQLiteHelper(getApplicationContext()).flushData();
                startActivity(new Intent(getApplicationContext(), HomeActivity.class));
                finish();
            }
        });

        card.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                pref.edit().putBoolean(PREF_DEMO, pref.getBoolean(PREF_DEMO, false) ? false : true).apply();
                Toasty.normal(getApplicationContext(), "Demo Mode Enabled :"+pref.getBoolean(PREF_DEMO, false)).show();
                return false;
            }
        });
    }

    @Override
    public void onBackPressed() {

    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    private int getCurrentCount(){
        return pref.getInt(PREF_COUNT_KEY, 3);
    }
    private int session = 0;
    private RequestQueue queue;

    private void pushData(){
        session = getCurrentCount() / UP_LIMIT;
        queue = Volley.newRequestQueue(this);
        if(isNetworkAvailable()){
            int last_session = pref.getInt(PREF_ANALYTICS_SESSION, 0);
            if(last_session!=0)
                for(int i=last_session; i<session; i++){
                    putData(""+ (i+1));
                }
        }
    }

    private void putData(final String s){
        final String data = pref.getString(PREF_DATA+s,"");
        String url ="http://www.figr.in/mgiep/api/putData.php";
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            pref.edit().putInt(PREF_ANALYTICS_SESSION,Integer.parseInt(s)).commit();
                            JSONObject obj = new JSONObject(response);
                            Log.d("App",obj.toString());
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("App",error.toString());
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> params = new HashMap<>();
                params.put("id", pref.getString(PREF_PROFILE_ID, "") + "(" + pref.getString(PREF_PROFILE_NAME, "") + "-" +pref.getString(PREF_PROFILE_GENDER, "") + ")");
                params.put("data",data);
                params.put("session",s);
                return params;
            }
        };
        queue.add(stringRequest);
    }

}
