package edu.mriu.kankhol;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.PorterDuff;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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
import com.daimajia.androidanimations.library.YoYo;
import com.thunderrise.animations.PulseAnimation;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import static edu.mriu.kankhol.Khelo.PREF_ANALYTICS_SESSION;
import static edu.mriu.kankhol.Khelo.PREF_DATA;
import static edu.mriu.kankhol.Khelo.UP_LIMIT;
import static edu.mriu.kankhol.Khelo.getResId;
import static edu.mriu.kankhol.ProfileActivity.PREF_COURSE_PATH;
import static edu.mriu.kankhol.ProfileActivity.PREF_PROFILE_DONE;
import static edu.mriu.kankhol.ProfileActivity.PREF_PROFILE_GENDER;
import static edu.mriu.kankhol.ProfileActivity.PREF_PROFILE_ID;
import static edu.mriu.kankhol.ProfileActivity.PREF_PROFILE_NAME;
import static edu.mriu.kankhol.SelectionActivity.PREF_CARTOON;
import static edu.mriu.kankhol.Sikho.getPrimaryColor;

/**
 * The very first control Activity
 * @input Button to start the main working part.
 * @input Initialize the setup if not done.
 * @input Reset the App to fresh state.
 */

public class HomeActivity extends AppCompatActivity {

    private Button start;
    private ImageView settings, help, info, report;
    public static final String PREF_NAME = "ogil";
    public static final String PREF_COUNT_KEY = "count";
    private YoYo animator;
    private SharedPreferences pref;

    public static final String PREF_COURSE_MATERIAL_EXTRACTED = "MaterialExtracted";

    private void setupCourseData(){
        String coursePath = pref.getString(PREF_COURSE_PATH, "");
        if(unpackZip(coursePath))
            goNext();
    }

    private boolean unpackZip(String coursePath)
    {
        File f = new File(URI.create(coursePath));
        String path = f.getParent();

        InputStream is;
        ZipInputStream zis;
        try
        {
            String filename;
            is = new FileInputStream(f);
            zis = new ZipInputStream(new BufferedInputStream(is));
            ZipEntry ze;
            byte[] buffer = new byte[1024];
            int count;

            while ((ze = zis.getNextEntry()) != null)
            {
                filename = ze.getName();
                if (ze.isDirectory()) {
                    File fmd = new File(path + File.separator+ filename);
                    fmd.mkdirs();
                    continue;
                }

                FileOutputStream fout = new FileOutputStream(path + File.separator+ filename);

                while ((count = zis.read(buffer)) != -1)
                {
                    fout.write(buffer, 0, count);
                }

                fout.close();
                zis.closeEntry();
            }

            zis.close();
        }
        catch(IOException e)
        {
            e.printStackTrace();
            return false;
        }

        getWords(path);
        pref.edit().putBoolean(PREF_COURSE_MATERIAL_EXTRACTED, true).commit();
        return true;
    }

    public void getStoragePermission(){
        if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {

        }
        else {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 7190);
        }
    }

    public static final String PREF_COURSE_ALPHABETS = "course_alphabets";
    public static final String PREF_COURSE_WORDS = "course_words";

    private void getWords(String path){
        int i = 0;
        try{
            BufferedReader br = new BufferedReader(new FileReader(path + File.separator + "data"+File.separator+"words.txt"));
            String line = br.readLine();
            while (line != null) {
                if(i==0)
                    pref.edit().putString(PREF_COURSE_ALPHABETS, line).commit();
                else if(i==1)
                    pref.edit().putString(PREF_COURSE_WORDS, line).commit();
                i++;
                line = br.readLine();
            }
            br.close();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    private int getCurrentTheme(){
        int x = getResId("car"+pref.getInt(PREF_CARTOON, 0),R.style.class);
        if (x == -1) return R.style.AppTheme;
        else return x;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        pref = getSharedPreferences(PREF_NAME, MODE_PRIVATE);
        setTheme(getCurrentTheme());
        setContentView(R.layout.activity_home);
        getIntent().setAction("Created");

        start = findViewById(R.id.start);

        PulseAnimation.create().with(start)
                .setDuration(6000)
                .setRepeatCount(PulseAnimation.INFINITE)
                .setRepeatMode(PulseAnimation.REVERSE)
                .start();

        start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goNext();
            }
        });

        settings = findViewById(R.id.btn_settings);
        settings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), SettingsActivity.class));
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
        getStoragePermission();
        themeUp();
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

    @Override
    public void onBackPressed() {

    }

    private void themeUp(){
        start.getBackground().setColorFilter(getPrimaryColor(this), PorterDuff.Mode.MULTIPLY);
        pushData();
    }

    public static final String PREF_CARTOON_SELECTED = "cartoonSelection";
    private void goNext(){
        if(!pref.getBoolean(PREF_CARTOON_SELECTED, false)){
            startActivity(new Intent(getApplicationContext(), SelectionActivity.class));
        }
        else if(!pref.getBoolean(PREF_PROFILE_DONE, false)){
            startActivity(new Intent(getApplicationContext(), ProfileActivity.class));
        } else if(!pref.getBoolean(PREF_COURSE_MATERIAL_EXTRACTED, false)){

            setupCourseData();
            if(pref.getString(PREF_COURSE_WORDS, "").length() > 0) {
                startActivity(new Intent(getApplicationContext(), Sikho.class));
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            }
        }
        else {
            startActivity(new Intent(getApplicationContext(), Sikho.class));
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        }
    }

    @Override
    protected void onResume() {
        String action = getIntent().getAction();
        if(action == null || !action.equals("Created")) {
            Intent intent = new Intent(this, HomeActivity.class);
            startActivity(intent);
            finish();
        }
        else
            getIntent().setAction(null);
        super.onResume();
    }
}