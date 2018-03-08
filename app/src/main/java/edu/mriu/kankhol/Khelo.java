package edu.mriu.kankhol;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.PorterDuff;
import android.graphics.drawable.Animatable;
import android.graphics.drawable.Animatable2;
import android.graphics.drawable.Drawable;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.content.res.AppCompatResources;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.thunderrise.animations.PulseAnimation;
import com.thunderrise.animations.ShakeAnimation;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.lang.reflect.Field;
import java.net.URI;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import es.dmoral.toasty.Toasty;

import static android.view.View.GONE;
import static edu.mriu.kankhol.HomeActivity.PREF_COUNT_KEY;
import static edu.mriu.kankhol.HomeActivity.PREF_COURSE_ALPHABETS;
import static edu.mriu.kankhol.HomeActivity.PREF_COURSE_WORDS;
import static edu.mriu.kankhol.HomeActivity.PREF_NAME;
import static edu.mriu.kankhol.ProfileActivity.PREF_COURSE_PATH;
import static edu.mriu.kankhol.ProfileActivity.PREF_PROFILE_GENDER;
import static edu.mriu.kankhol.ProfileActivity.PREF_PROFILE_ID;
import static edu.mriu.kankhol.ProfileActivity.PREF_PROFILE_NAME;
import static edu.mriu.kankhol.SelectionActivity.PREF_CARTOON;
import static edu.mriu.kankhol.Sikho.PREF_DEMO;
import static edu.mriu.kankhol.Sikho.PREF_LEARN_DONE;
import static edu.mriu.kankhol.Sikho.getPrimaryColor;

/**
 * Main Activity to test the Child's Learning ability
 */

public class Khelo extends AppCompatActivity {

    private SharedPreferences pref;
    private ArrayList<SessionState> sessionArray = new ArrayList<>();
    private SessionState sessionState;
    private SQLiteHelper sqLiteHelper;
    private String[] alphabets;
    private String[] words;
    private ArrayList<String> list, list2;
    private MediaPlayer player;
    private CardView cardView;
    private int count = 0;
    private ImageButton next, back;
    private ImageView one, two, three;
    private Button one_textView, two_textView, three_textView;
    private int score = 0;
    private int lev = 1;
    private ImageView alien;

    private YoYo animator;
    private RelativeLayout rl1, rl2, rl3;
    private Drawable drawable1, drawable2, drawable3, d1, d2;
    private final Handler handler = new Handler();
    private int color;
    private int growth = 0;
    private int factor = 6;
    private int correct = 0;
    private String wrong = "[]";
    public static final String PREF_CARTOON_GROWTH = "growth";
    private String var = "";
    private ImageView soundControl, help, info, report;
    private boolean bool = true, bool1 = true;
    private int session = 0;
    public static final int UP_LIMIT = 3;
    private String coursePath;
    private int volume_level;
    private double volCon;
    private AudioManager am;
    private boolean isGameDone = false;

    private int answer = 0;
    private int prevAns = 0;
    private int x, y;
    private int currentPos = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        pref = getSharedPreferences(PREF_NAME, MODE_PRIVATE);
        setTheme(getCurrentTheme());
        setContentView(R.layout.activity_khelo);
        getIntent().setAction("Created");

        session = getCurrentCount()/UP_LIMIT;

        color = getPrimaryColor(this);
        soundControl = findViewById(R.id.btn_play_pause);
        cardView = findViewById(R.id.CARDVIEW1);
        cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ogilPlay(lev-1);
            }
        });

        am = (AudioManager) getSystemService(AUDIO_SERVICE);

        words = pref.getString(PREF_COURSE_WORDS, "").split(",");
        alphabets = pref.getString(PREF_COURSE_ALPHABETS, "").split(",");

        SQLiteHelper abc = new SQLiteHelper(Khelo.this);
        abc.createWordList(alphabets, words);

        if(Build.VERSION.SDK_INT >= 20){
            d1 = getResources().getDrawable(R.drawable.unmute_to_mute_meetesh);
            d2 = getResources().getDrawable(R.drawable.mute_to_unmute_meetesh);
        }
        else {
            d1 = AppCompatResources.getDrawable(getApplicationContext(), R.drawable.unmute_to_mute_meetesh);
            d2 = AppCompatResources.getDrawable(getApplicationContext(), R.drawable.mute_to_unmute_meetesh);
        }

        soundControl.setVisibility(View.VISIBLE);
        soundControl.setImageDrawable(d1);
        soundControl.setColorFilter(color, PorterDuff.Mode.MULTIPLY);
        soundControl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(bool) {
                    changeState();
                }
            }
        });

        alien = findViewById(R.id.alien);
        alien.setImageDrawable(getDrawableByName("alien"+pref.getInt(PREF_CARTOON, 1)));
        alien.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ShakeAnimation.create().with(alien)
                        .setRepeatCount(0)
                        .setDuration(300)
                        .start();
            }
        });
        growth = pref.getInt(PREF_CARTOON_GROWTH, 0);
        alien.getLayoutParams().height = alien.getLayoutParams().height + growth;
        alien.getLayoutParams().width = alien.getLayoutParams().width + growth;

        one_textView = findViewById(R.id.one_textView);
        one_textView.getBackground().setColorFilter(color, PorterDuff.Mode.MULTIPLY);
        two_textView = findViewById(R.id.two_textView);
        two_textView.getBackground().setColorFilter(color, PorterDuff.Mode.MULTIPLY);
        three_textView = findViewById(R.id.three_textView);
        three_textView.getBackground().setColorFilter(color, PorterDuff.Mode.MULTIPLY);

        rl1 = findViewById(R.id.RL_L);
        rl2 = findViewById(R.id.RL_M);
        rl3 = findViewById(R.id.RL_R);

        one = findViewById(R.id.one);
        one.setImageResource(R.drawable.tick_anim);
        drawable1 = one.getDrawable();

        one_textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(answer == 0){
                    one_textView.setText("");
                    one_textView.setVisibility(View.GONE);

                    one.setImageResource(R.drawable.tick_right_anim1);
                    rightAnswer(one.getDrawable());
                }
                else{
                    one_textView.setText("");
                    one_textView.setVisibility(View.GONE);

                    ShakeAnimation.create().with(v)
                            .setDuration(300)
                            .setRepeatCount(0)
                            .start();

                    one.setImageResource(R.drawable.tick_wrong_anim1);
                    wrongAnswer(one.getDrawable());
                }
            }
        });
        two = findViewById(R.id.two);
        two.setImageResource(R.drawable.tick_anim);
        drawable2 = two.getDrawable();

        coursePath = pref.getString(PREF_COURSE_PATH, "");

        two_textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(answer == 1){
                    two_textView.setText("");
                    two_textView.setVisibility(View.GONE);
                    if(drawable2 instanceof Animatable)
                        ((Animatable) drawable2).start();

                    two.setImageResource(R.drawable.tick_right_anim2);
                    rightAnswer(two.getDrawable());
                }
                else{
                    two_textView.setText("");
                    two_textView.setVisibility(View.GONE);

                    ShakeAnimation.create().with(v)
                            .setDuration(300)
                            .setRepeatCount(0)
                            .start();

                    two.setImageResource(R.drawable.tick_wrong_anim2);
                    wrongAnswer(two.getDrawable());
                }
            }
        });

        three = findViewById(R.id.three);
        three.setImageResource(R.drawable.tick_anim);
        drawable3 = three.getDrawable();

        three_textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(answer == 2){
                    three_textView.setText("");
                    three_textView.setVisibility(View.GONE);
                    if(drawable3 instanceof Animatable)
                        ((Animatable) drawable3).start();

                    three.setImageResource(R.drawable.tick_right_anim3);
                    rightAnswer(three.getDrawable());
                }
                else{
                    three_textView.setText("");
                    three_textView.setVisibility(View.GONE);

                    ShakeAnimation.create().with(v)
                            .setDuration(300)
                            .setRepeatCount(0)
                            .start();

                    three.setImageResource(R.drawable.tick_wrong_anim3);
                    wrongAnswer(three.getDrawable());
                }
            }
        });

        PulseAnimation.create().with(rl1)
                .setDuration(6000)
                .setRepeatCount(PulseAnimation.INFINITE)
                .setRepeatMode(PulseAnimation.REVERSE)
                .start();

        PulseAnimation.create().with(rl2)
                .setDuration(6000)
                .setRepeatCount(PulseAnimation.INFINITE)
                .setRepeatMode(PulseAnimation.REVERSE)
                .start();

        PulseAnimation.create().with(rl3)
                .setDuration(6000)
                .setRepeatCount(PulseAnimation.INFINITE)
                .setRepeatMode(PulseAnimation.REVERSE)
                .start();


        player = new MediaPlayer();

        next = findViewById(R.id.next);
        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isGameDone) {
                    if (score < 50) {
                        int x = pref.getInt(PREF_COUNT_KEY, 0) > 0 ? pref.getInt(PREF_COUNT_KEY, 0) - 3 : 0;
                        pref.edit().putInt(PREF_COUNT_KEY, x).commit();
                    }
                    startActivity(new Intent(getApplicationContext(), Sikho.class));
                    overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                    finish();
                } else {
                    Toasty.error(getApplicationContext(), "Finish Game First!", Toast.LENGTH_SHORT).show();
                }
            }
        });
        help = findViewById(R.id.btn_document);
        help.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        info = findViewById(R.id.btn_info);
        info.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), InfoActivity.class));
            }
        });

        report = findViewById(R.id.btn_report);
        report.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), ResultActivity.class));
            }
        });

        back = findViewById(R.id.backward);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Khelo.super.onBackPressed();
            }
        });

        if(!isGameDone) {
            makeHashMap();
            pref.edit().putBoolean(PREF_GAME_MODE_KEY, true).commit();
            startPlaying();
        }
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_VOLUME_UP) {
            am.adjustVolume(1,AudioManager.FLAG_VIBRATE);
            return true;
        } else if (keyCode == KeyEvent.KEYCODE_VOLUME_DOWN ) {
            am.adjustVolume(-1,AudioManager.FLAG_VIBRATE);
            return true;
        }

        return super.onKeyUp(keyCode, event);
    }

    private void changeState() {
        if(player != null) {
            bool = false;
            final Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    if(bool1) {
                        soundControl.setImageDrawable(d1);
                        if(Build.VERSION.SDK_INT>=20) {
                            if (d1 instanceof Animatable2) {
                                ((Animatable2) d1).start();
                            }
                        }
                        else {
                            if (d1 instanceof Animatable) {
                                ((Animatable) d1).start();
                            }
                        }

                        player.setVolume(0,0);
                        bool1 = false;
                    } else {
                        soundControl.setImageDrawable(d2);


                        if(Build.VERSION.SDK_INT>=20) {
                            if(d2 instanceof Animatable2) {
                                ((Animatable2) d2).start();
                                player.setVolume(1,1);
                            }
                        }
                        else {
                            if(d2 instanceof Animatable) {
                                ((Animatable) d2).start();
                                player.setVolume(1,1);
                            }
                        }

                        bool1 = true;
                    }
                    bool = true;
                }
            }, 500);
        }
    }

    private void wrongAnswer(Drawable d){
        sessionState = new SessionState(getSessionId(), getOption1(),getOption2(),getRightAnswer(), getRightAnswer());
        sessionState.setTimeTakenInMs(getTimeTaken());
        sessionState.setResult(false);
        sessionArray.add(sessionState);

        animator.with(Techniques.Bounce).duration(1000).playOn(cardView);
        startAnimation(d);
        count++;

        try {
            JSONArray a = new JSONArray(wrong);
            JSONObject o = new JSONObject();
            o.put("syllable",getRightAnswer());
            o.put("Option1", getOption1());
            o.put("Option2", getOption2());
            a.put(o);
            wrong = a.toString();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        Toasty.error(getApplicationContext(), "गलत", Toast.LENGTH_SHORT).show();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {

                startPlaying();
            }
        }, 1000);
    }

    private String getSessionId(){
        String id = session+""+lev+""+count;
        return id;
    }

    private void startAnimation(Drawable d) {
        if(d instanceof Animatable)
            ((Animatable) d).start();
    }

    private void rightAnswer(Drawable d){

        sessionState = new SessionState(getSessionId(), getOption1(),getOption2(),getRightAnswer(), getRightAnswer());
        sessionState.setTimeTakenInMs(getTimeTaken());
        sessionState.setResult(true);
        sessionArray.add(sessionState);

        animator.with(Techniques.Tada).duration(1000).playOn(alien);
        startAnimation(d);
        final Animation anim = AnimationUtils.loadAnimation(this, R.anim.grow);
        count++;
        correct++;
        score = score + 10;
        alien.startAnimation(anim);
        alien.getLayoutParams().width = alien.getLayoutParams().width + factor;
        alien.getLayoutParams().height = alien.getLayoutParams().height + factor;
        growth += factor;
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                startPlaying();
            }
        }, 1000);
    }


    private int getCurrentTheme(){
        return getResId("car"+pref.getInt(PREF_CARTOON, 1),R.style.class);
    }

    private int getCurrentCount(){
        return pref.getInt(PREF_COUNT_KEY, 3);
    }
    private int c;
    private void makeHashMap(){
        list = new ArrayList<>();
        list2 = new ArrayList<>();
        c = getCurrentCount();

        for(int i=(c-3); i<c; i++) {
            for (int j = 0; j < 2; j++) {
                if(!list.contains(words[i].charAt(j)+"")){
                    list.add(words[i].charAt(j)+"");
                }
            }
            list2.add(words[i]);
        }
        Collections.shuffle(list);
        Collections.shuffle(list2);
    }

    private int getAlphabetPos(String s){
        for(int i=0 ; i<alphabets.length; i++){
            if(s.equals(alphabets[i]))
                return i;
        }
        return 0;
    }

    private int getWordPos(String s) {
        for (int i = 0; i < words.length; i++) {
            if (s.equals(words[i]))
                return i;
        }
        return 0;
    }

    private int setAnswerRandomly(Random random){
        int x = random.nextInt(3);
        if(prevAns == x)
            setAnswerRandomly(random);
        else
            return x;
        return 0;
    }

    private void startPlaying(){
        if(!pref.getBoolean(PREF_DEMO, false)){
            next.setVisibility(GONE);
        }

        animator.with(Techniques.FadeIn).delay(100).duration(500).playOn(rl1);
        animator.with(Techniques.FadeIn).delay(100).duration(500).playOn(rl2);
        animator.with(Techniques.FadeIn).delay(100).duration(500).playOn(rl3);
        switch(lev) {
            case 1:
                if (count < list.size()) {
                    one.setVisibility(View.VISIBLE);
                    two.setVisibility(View.VISIBLE);
                    three.setVisibility(View.VISIBLE);
                    one_textView.setVisibility(View.VISIBLE);
                    two_textView.setVisibility(View.VISIBLE);
                    three_textView.setVisibility(View.VISIBLE);

                    Random random = new Random();
                    x = random.nextInt(alphabets.length);
                    y = random.nextInt(alphabets.length);
                    answer = setAnswerRandomly(random);
                    prevAns = answer;
                    currentPos = getAlphabetPos(list.get(count));
                    var = list.get(count);
                    manageOptions();
                    prepareButtonsCaseA();
                    ogilPlay(0);
                } else {
                    lev = 2;
                    count = 0;
                    startPlaying();
                }
                break;
            case 2:
                if (count < list2.size()) {
                    one.setVisibility(View.VISIBLE);
                    two.setVisibility(View.VISIBLE);
                    three.setVisibility(View.VISIBLE);
                    one_textView.setVisibility(View.VISIBLE);
                    two_textView.setVisibility(View.VISIBLE);
                    three_textView.setVisibility(View.VISIBLE);

                    Random random = new Random();
                    x = random.nextInt(words.length);
                    y = random.nextInt(words.length);
                    answer = setAnswerRandomly(random);
                    prevAns = answer;
                    currentPos = getWordPos(list2.get(count));
                    var = list2.get(count);
                    manageOptions();
                    prepareButtonsCaseB();
                    ogilPlay(1);
                } else {
                    one.setVisibility(View.GONE);
                    two.setVisibility(View.GONE);
                    three.setVisibility(View.GONE);

                    one_textView.setVisibility(View.GONE);
                    two_textView.setVisibility(View.GONE);
                    three_textView.setVisibility(View.GONE);
                    next.setVisibility(View.VISIBLE);

                    pref.edit().putBoolean(PREF_GAME_MODE_KEY, false).commit();
                    if(score<50){
                        pref.edit().putInt(PREF_COUNT_KEY, pref.getInt(PREF_COUNT_KEY, 3) - 3).commit();
                        animator.with(Techniques.SlideOutLeft).duration(3000).playOn(alien);
                    }
                    else{
                        pref.edit().putInt(PREF_CARTOON_GROWTH, growth).commit();
                        pref.edit().putString(PREF_DATA+session,"Correct=>"+correct+" Wrong=>"+wrong).commit();
                        animator.with(Techniques.SlideOutRight).duration(3000).playOn(alien);

                        /* setting variables on finishing training after last version finished */
                        if(getCurrentCount() == words.length)
                            pref.edit().putBoolean(PREF_LEARN_DONE, true).commit();
                        pushData();
                        localDatabase();
                        if(session==1){
                            pref.edit().putBoolean(PREF_FIRST_GAME_DONE, true).commit();
                        }
                    }
                    /* flag to check game is finished or not */
                    isGameDone = true;
                }
                break;
        }
        /* storing time for calculating time taken to response */
        timeStart = System.currentTimeMillis();
    }
    private long timeStart = 0;

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    private RequestQueue queue;
    public static final String PREF_ANALYTICS_SESSION = "analysis_session";

    private void localDatabase() {
        String ts = ""+ session;
        sqLiteHelper = new SQLiteHelper(Khelo.this);
        sqLiteHelper.createSession(ts);

        for(SessionState i:sessionArray) {
            sqLiteHelper.insertSessionStateElement(i.getId(),i);
            sqLiteHelper.insertCumWordAnalysis(i.getSubject(),i.isResult()?1:0,i.getTimeTakenInMs());
        }
    }

    private void pushData(){
        queue = Volley.newRequestQueue(this);
        if(isNetworkAvailable()){
            int last_session = pref.getInt(PREF_ANALYTICS_SESSION, 0);

            for(int i=last_session; i<session; i++){
                putData(""+ (i+1));
            }
        }
    }

    public static final String PREF_DATA = "data-";
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

    private int z = 0;
    /* function to set random options */
    private void manageOptions(){
        if(currentPos ==x){
            if (x == (words.length - 1))
                x = x - 1;
            else if (x == 0)
                x = x + 1;
            else {
                x--;
            }
        }
        else if(currentPos == y){
            if (y == (words.length - 1))
                y = y - 1;
            else if (y == 0)
                y = y + 1;
            else {
                y--;
            }
        }
        else if(x == y){
            if (x == (words.length - 1))
                x--;
            else if (x == 0)
                x++;
            else {
                x--;
                y++;
            }
        }

        if(z==0){
            z = -1;
            manageOptions();
        }
        else
            z = 0;
    }

    public static final String PREF_GAME_MODE_KEY = "isGameDone";
    public static final String PREF_FIRST_GAME_DONE = "firstGameDone";
    private void prepareButtonsCaseA(){
        try {
            if (answer == 0) {
                one_textView.setText(alphabets[currentPos]);
                two_textView.setText(alphabets[x]);
                three_textView.setText(alphabets[y]);
            } else if (answer == 1) {
                two_textView.setText(alphabets[currentPos]);
                one_textView.setText(alphabets[x]);
                three_textView.setText(alphabets[y]);

            } else if (answer == 2) {
                three_textView.setText(alphabets[currentPos]);
                two_textView.setText(alphabets[x]);
                one_textView.setText(alphabets[y]);
            }
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }

    private String getOption1(){
        if(lev==1){
            return alphabets[x];
        }
        else{
            return words[x];
        }
    }

    private String getOption2(){
        if(lev==1){
            return alphabets[y];
        }
        else{
            return words[y];
        }
    }

    private String getRightAnswer(){
        if(lev==1){
            return alphabets[currentPos];
        }
        else{
            return words[currentPos];
        }
    }

    private float getTimeTaken(){
        return ((float)((System.currentTimeMillis() - timeStart)/1000));
    }

    @Override
    public void onBackPressed() {

    }

    private void prepareButtonsCaseB(){
        try {
            if (answer == 0) {
                one_textView.setText(words[currentPos]);
                two_textView.setText(words[x]);
                three_textView.setText(words[y]);
            } else if (answer == 1) {
                two_textView.setText(words[currentPos]);
                one_textView.setText(words[x]);
                three_textView.setText(words[y]);
            } else if (answer == 2) {
                three_textView.setText(words[currentPos]);
                two_textView.setText(words[x]);
                one_textView.setText(words[y]);
            }
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }

    private void ogilPlay(int selector){
        File f = new File(URI.create(coursePath));
        String path = f.getParent();
        FileInputStream fileInputStream;
        String file = "";
        switch(selector) {
            case 0:
                file = path + File.separator + "data" + File.separator + "s" + currentPos + ".wav";
                break;
            case 1:
                file = path + File.separator + "data" + File.separator + "k"+currentPos + ".wav";
                break;
        }

        try {
            player.reset();
            fileInputStream = new FileInputStream(file);
            player.setDataSource(fileInputStream.getFD());
            fileInputStream.close();
            player.prepare();
            player.start();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        if(selector==0){
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    try {
                        player.stop();
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }
            }, 1500);
        }
    }


    public static int getResId(String resName, Class<?> c) {
        try {
            Field idField = c.getDeclaredField(resName);
            return idField.getInt(idField);
        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        }
    }

    @Override
    protected void onDestroy() {
        try{
            player.stop();
            player.release();
        }
        catch(Exception e){
            e.printStackTrace();
        }
        super.onDestroy();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        try{
            player.stop();
        } catch(Exception e){
            e.printStackTrace();
        }
        super.onPause();
    }
}