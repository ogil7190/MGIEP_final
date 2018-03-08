package edu.mriu.kankhol;

import android.app.DownloadManager;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import es.dmoral.toasty.Toasty;
import ir.mahdi.mzip.zip.ZipArchive;

import static edu.mriu.kankhol.HomeActivity.PREF_COURSE_ALPHABETS;
import static edu.mriu.kankhol.HomeActivity.PREF_NAME;
import static edu.mriu.kankhol.Khelo.PREF_ANALYTICS_SESSION;
import static edu.mriu.kankhol.Khelo.getResId;
import static edu.mriu.kankhol.SelectionActivity.PREF_CARTOON;
import static edu.mriu.kankhol.Sikho.getCurrentTheme;
import static edu.mriu.kankhol.Sikho.getPrimaryColor;

/**
 * Profile activity is there to make a profile to keep track.
 * @input name age and sex of the participant
 * @input selecting the language to be tested on.
 * @uptput a working profile for the app.
 */

public class ProfileActivity extends AppCompatActivity {
    private ImageView cartoon;
    private EditText name, age;
    private Button submit;
    private SharedPreferences pref;
    private String var_age = "", var_gender = "F", var_name = "";
    private RadioButton male, female;
    private CheckBox tnc;
    private boolean isPermissionToCollectData = true;

    public static final String PREF_PROFILE_DONE = "profile";
    public static final String PREF_PROFILE_NAME = "name";
    public static final String PREF_PROFILE_AGE = "age";
    public static final String PREF_PROFILE_GENDER = "gender";
    public static final String PREF_PROFILE_ID = "profileId";

    private Spinner language, course;
    private boolean saveLocal = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        pref = getSharedPreferences(PREF_NAME, MODE_PRIVATE);
        setTheme(getCurrentTheme(pref));
        setContentView(R.layout.activity_profile);
        cartoon =  findViewById(R.id.cartoon);
        name = findViewById(R.id.name);
        tnc = findViewById(R.id.tnc);
        age = findViewById(R.id.age);
        submit = findViewById(R.id.submit);
        male = findViewById(R.id.male);
        female = findViewById(R.id.female);
        cartoon.setImageDrawable(getDrawableByName("alien"+pref.getInt(PREF_CARTOON, 1)));
        int color = getPrimaryColor(this);
        age.setTextColor(color);
        name.setTextColor(color);
        submit.setTextColor(color);
        female.setChecked(true);

        language = findViewById(R.id.language);
        course = findViewById(R.id.course);

        tnc.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                isPermissionToCollectData = b;
            }
        });

        male.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                female.setChecked(!female.isChecked());
                var_gender = "M";
            }
        });
        female.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                male.setChecked(!male.isChecked());
                var_gender = "F";
            }
        });

        dialog = new ProgressDialog(this);
        dialog.setCanceledOnTouchOutside(false);
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String a = age.getText().toString().trim();
                String n = name.getText().toString().trim();
                if (a.isEmpty() || a.length() == 0 || a.equals("") || a == null) {
                    Toasty.error(getApplicationContext(), "Enter Valid Age", Toast.LENGTH_LONG).show();
                } else if (n.isEmpty() || n.length() == 0 || n.equals("") || n == null) {
                    Toasty.error(getApplicationContext(), "Enter Valid Name", Toast.LENGTH_LONG).show();
                } else {
                    var_name = n;
                    var_age = a;
                    if(saveLocal){
                        pref.edit().putString(PREF_PROFILE_AGE,var_age).apply();
                        pref.edit().putString(PREF_PROFILE_NAME,var_name).apply();
                        pref.edit().putString(PREF_PROFILE_GENDER,var_gender).apply();

                        pref.edit().putBoolean(PREF_PROFILE_DONE,true).apply();
                        startActivity(new Intent(getApplicationContext(), Sikho.class));
                        finish();
                    } else {
                        registerUser(var_age, var_gender);
                    }
                }
            }
        });
        course.setVisibility(View.GONE);
        if(!pref.getString(PREF_PROFILE_ID, "").equals("")){
            course.setEnabled(false);
            language.setEnabled(false);
            saveLocal = true;
        } else{
            setUpCourse();
        }
    }

    private RequestQueue queue;

    private AdapterView.OnItemSelectedListener itemClickListener = new AdapterView.OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
            getCourses(languages.get(i));
            course.setVisibility(View.GONE);
        }

        @Override
        public void onNothingSelected(AdapterView<?> adapterView) {
            Toast.makeText(getApplicationContext(), "Select Language First", Toast.LENGTH_LONG).show();
        }
    };

    private List<String> languages;
    private void setUpCourse(){
        languages = new ArrayList<>();
        queue = Volley.newRequestQueue(this);
        String url ="http://www.figr.in/mgiep/api/getLanguages.php";
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d("TAG","Response:"+response);
                        /* sample response
                            [{"name":"telgu"},{"name":"hindi"}]
                         */
                        try {
                            JSONArray a = new JSONArray(response);
                            for(int i=0; i<a.length(); i++){
                                JSONObject obj = (JSONObject)a.get(i);
                                languages.add(obj.getString("name"));
                            }
                            ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_spinner_item, languages);
                            dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                            language.setAdapter(dataAdapter);
                            language.setOnItemSelectedListener(itemClickListener);

                        } catch(JSONException e){
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("App",error.toString());
            }
        });
        queue.add(stringRequest);
    }

    private static class Course{
        String name;
        String url;

        public Course(String name, String url){
            this.name = name;
            this.url = url;
        }

        public String getName(){ return name; }
        public String getURL(){ return url; }
    }

    private ArrayList<Course> courses;
    public static final String PREF_COURSE_LANGUAGE = "COURSE_LANG";
    public static final String PREF_COURSE_NAME = "COURSE_NAME";
    public static final String PREF_COURSE_URL = "COURSE_URL";

    private AdapterView.OnItemSelectedListener itemClickListenerCourse = new AdapterView.OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
            selectedCourse = courses.get(i);
            pref.edit().putString(PREF_COURSE_LANGUAGE, selectedLanguage).commit();
            pref.edit().putString(PREF_COURSE_NAME, selectedCourse.getName()).commit();
            pref.edit().putString(PREF_COURSE_URL, selectedCourse.getURL()).commit();
        }

        @Override
        public void onNothingSelected(AdapterView<?> adapterView) {

        }
    };

    private String selectedLanguage = "";
    private Course selectedCourse;
    private void getCourses(final String language){
        selectedLanguage = language;
        courses = new ArrayList<>();
        String url ="http://www.figr.in/mgiep/api/getCourses.php";
        final ArrayList<String> coursesNames = new ArrayList<>();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            Log.d("App","Response:"+response);
                            /* [{"name":"test","url":"https:\/\/mgiep.000webhostapp.com\/api\/data\/telgu\/test.zip"}] */
                            JSONArray a = new JSONArray(response);
                            for(int i=0; i<a.length(); i++){
                                JSONObject obj = (JSONObject)a.get(i);
                                courses.add(new Course(obj.getString("name"), obj.getString("url")));
                                coursesNames.add(obj.getString("name"));
                            }

                            ArrayAdapter<String> dataAdapter = new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_spinner_item, coursesNames);
                            dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                            course.setAdapter(dataAdapter);
                            course.setOnItemSelectedListener(itemClickListenerCourse);
                            course.setVisibility(View.VISIBLE);

                        } catch (Exception e) {
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
                params.put("language", language);
                return params;
            }
        };
        queue.add(stringRequest);
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    private ProgressDialog dialog;
    private void registerUser(final String age, final String gender){
        dialog.setMessage("Processing...");
        dialog.show();
        RequestQueue queue = Volley.newRequestQueue(this);
        String url ="http://www.figr.in/mgiep/api/register.php";
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        dialog.dismiss();
                        try {
                            JSONObject obj = new JSONObject(response);
                            Log.d("App",obj.toString());
                            if(!obj.getBoolean("error")){
                                pref.edit().putString(PREF_PROFILE_AGE,age).apply();
                                pref.edit().putString(PREF_PROFILE_NAME,var_name).apply();
                                pref.edit().putString(PREF_PROFILE_GENDER,gender).apply();

                                //{"error":false,"result":{"id":"mgiep-lxepg2v6","gender":"M","age":4}} format

                                JSONObject o = new JSONObject(obj.getString("result"));
                                pref.edit().putString(PREF_PROFILE_ID,o.getString("id")).apply();
                                goNext();
                            }
                            else
                                Toasty.error(getApplicationContext(),"Something Went Wrong :(",Toast.LENGTH_LONG).show();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("App",error.toString());
                dialog.dismiss();
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> params = new HashMap<>();
                params.put("age", age);
                params.put("gender",gender);
                return params;
            }
        };

        if(isNetworkAvailable()){
            queue.add(stringRequest);
        }
        else {
            dialog.dismiss();
            Toasty.error(getApplicationContext(), "Check your Internet Connection!", Toast.LENGTH_LONG).show();
        }
    }

    private void goNext(){
        downloadFiles();
    }

    private DownloadManager mgr;
    private long downloadId = -1L;

    private void downloadFiles(){
        dialog.setMessage("Downloading Course...");
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();
        mgr=(DownloadManager)getSystemService(DOWNLOAD_SERVICE);
        File folder = new File(Environment.getExternalStorageDirectory() + File.separator + "KanKhol");
        if(!folder.exists()){
            folder.mkdirs();
        }
        folder = new File(folder, "data.zip");
        if(folder.exists()){
            folder.delete();
        }

        DownloadManager.Request req=new DownloadManager.Request(Uri.parse(pref.getString(PREF_COURSE_URL, "")));
        req.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI
                | DownloadManager.Request.NETWORK_MOBILE)
                .setAllowedOverRoaming(true)
                .setTitle("Downloading")
                .setDescription("Getting your course Data.")
                .setDestinationUri(Uri.fromFile(folder));

        downloadId = mgr.enqueue(req);
        timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                queryStatus();
            }
        }, 0 , 1000);
    }

    private Timer timer;

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    public void queryStatus() {
        final Cursor c=mgr.query(new DownloadManager.Query().setFilterById(downloadId));

        if (c==null) {
            Toast.makeText(this, "Download not found!", Toast.LENGTH_LONG).show();
        }
        else {
            c.moveToFirst();
            coursePath = c.getString(c.getColumnIndex(DownloadManager.COLUMN_LOCAL_URI));

            Log.d(getClass().getName(), "COLUMN_BYTES_DOWNLOADED_SO_FAR: "+ c.getLong(c.getColumnIndex(DownloadManager.COLUMN_BYTES_DOWNLOADED_SO_FAR)));
            Log.d(getClass().getName(), "COLUMN_TOTAL_SIZE: "+ c.getLong(c.getColumnIndex(DownloadManager.COLUMN_TOTAL_SIZE_BYTES)));
            Log.d(getClass().getName(), "COLUMN_LOCAL_URI: "+ coursePath);

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Log.d("App", statusMessage(c));
                }
            });
        }
    }

    private String coursePath = "";
    public static final String PREF_COURSE_PATH = "CoursePath";

    private String statusMessage(Cursor c) {
        String msg="???";

        switch(c.getInt(c.getColumnIndex(DownloadManager.COLUMN_STATUS))) {
            case DownloadManager.STATUS_FAILED:
                msg="Download failed!";
                dialog.dismiss();
                break;

            case DownloadManager.STATUS_PAUSED:
                msg="Download paused!";
                break;

            case DownloadManager.STATUS_PENDING:
                msg="Download pending!";
                break;

            case DownloadManager.STATUS_RUNNING:
                msg="Download in progress!";
                dialog.setMessage(msg);
                break;

            case DownloadManager.STATUS_SUCCESSFUL:
                msg="Download complete!";
                dialog.setMessage(msg);
                timer.cancel();
                dialog.dismiss();
                pref.edit().putString(PREF_COURSE_PATH, coursePath).commit();
                pref.edit().putBoolean(PREF_PROFILE_DONE,true).apply();
                startActivity(new Intent(getApplicationContext(), HomeActivity.class));
                finish();
                break;

            default:
                msg="Download is nowhere in sight";
                break;
        }

        return(msg);
    }

    public Drawable getDrawableByName(String name){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            return getDrawable(getResId(name, R.drawable.class));
        }
        else
        {
            Resources resources = getResources();
            return resources.getDrawable(getResId(name, R.drawable.class));
        }
    }
}
