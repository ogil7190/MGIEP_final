package edu.mriu.kankhol;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.PorterDuff;
import android.graphics.drawable.Animatable;
import android.graphics.drawable.Animatable2;
import android.graphics.drawable.Drawable;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.content.res.AppCompatResources;
import android.util.TypedValue;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.Field;
import java.net.URI;
import java.util.Timer;
import java.util.TimerTask;

import es.dmoral.toasty.Toasty;

import static android.view.View.GONE;
import static edu.mriu.kankhol.HomeActivity.PREF_COUNT_KEY;
import static edu.mriu.kankhol.HomeActivity.PREF_COURSE_ALPHABETS;
import static edu.mriu.kankhol.HomeActivity.PREF_COURSE_WORDS;
import static edu.mriu.kankhol.HomeActivity.PREF_NAME;
import static edu.mriu.kankhol.Khelo.PREF_GAME_MODE_KEY;
import static edu.mriu.kankhol.ProfileActivity.PREF_COURSE_PATH;
import static edu.mriu.kankhol.SelectionActivity.PREF_CARTOON;

/**
 * Main Activity for Making the child learn
 */

public class Sikho extends AppCompatActivity {

    private String[] words;
    private String[] alphabets;
    private int count = 0;
    private int LIMIT = 0;

    private TextView one , two;
    private ImageButton replay, next, play_pause, back;
    private SharedPreferences pref;
    public static final String PREF_LEARN_DONE = "learnModeDone";

    private ImageView soundControl, help, report, info;
    private Drawable drawable1, drawable2;
    private YoYo animator;
    private boolean bool, bool1;
    private int color;
    private String coursePath;
    private boolean isPlaying = true;
    private AudioManager am;

    private static Timer timer;
    private boolean done = false;

    private MediaPlayer player;
    private boolean notReleased = true;
    private int x = 0;
    private final Handler mHandler = new Handler();
    private static Runnable r;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        pref = getSharedPreferences(PREF_NAME, MODE_PRIVATE);
        setTheme(getCurrentTheme(pref));
        setContentView(R.layout.activity_sikho);
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        getIntent().setAction("Created");
        bool = true;
        bool1 = true;

        am = (AudioManager) getSystemService(AUDIO_SERVICE);

        coursePath = pref.getString(PREF_COURSE_PATH, "");

        alphabets = pref.getString(PREF_COURSE_ALPHABETS, "").split(",");
        words = pref.getString(PREF_COURSE_WORDS, "").split(",");

        color = getPrimaryColor(this);
        player = new MediaPlayer();

        soundControl = findViewById(R.id.btn_play_pause);
        if(Build.VERSION.SDK_INT >= 20){
            drawable1 = getResources().getDrawable(R.drawable.unmute_to_mute_meetesh);
            drawable2 = getResources().getDrawable(R.drawable.mute_to_unmute_meetesh);
        }
        else {
            drawable1 = AppCompatResources.getDrawable(getApplicationContext(), R.drawable.unmute_to_mute_meetesh);
            drawable2 = AppCompatResources.getDrawable(getApplicationContext(), R.drawable.mute_to_unmute_meetesh);
        }

        soundControl.setImageDrawable(drawable1);
        soundControl.setColorFilter(color, PorterDuff.Mode.MULTIPLY);
        soundControl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(bool) {
                    changeState();
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

        count = pref.getInt(PREF_COUNT_KEY, 0);
        LIMIT = count + 3;

        one = findViewById(R.id.one);
        two = findViewById(R.id.two);

        animator.with(Techniques.FadeOut).duration(700).playOn(one);
        animator.with(Techniques.FadeOut).duration(700).playOn(two);

        one.setTextColor(color);
        two.setTextColor(color);

        replay = findViewById(R.id.replay);
        replay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                timer.cancel();
                mHandler.removeCallbacks(r);
                count = LIMIT - 3;
                startLearning();
            }
        });
        next = findViewById(R.id.next);

        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(done)
                    finish();
                else {
                    pref.edit().putInt(PREF_COUNT_KEY, LIMIT).commit();
                    pref.edit().putBoolean(PREF_GAME_MODE_KEY, true).commit();
                    startActivity(new Intent(getApplicationContext(), Khelo.class));
                    overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                    finish();
                }
            }
        });

        back = findViewById(R.id.backward);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Sikho.super.onBackPressed();
            }
        });

        play_pause = findViewById(R.id.play_pause);
        play_pause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handleGameState();
            }
        });

        if(!pref.getBoolean(PREF_LEARN_DONE, false)) {
            if(!pref.getBoolean(PREF_GAME_MODE_KEY, false))
                startLearning();
            else {
                startActivity(new Intent(getApplicationContext(), Khelo.class));
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                finish();
            }
        }
        else {
            Toasty.success(getApplicationContext(), "Learning Done!", Toast.LENGTH_LONG).show();
            finish();
        }
    }

    @Override
    public void onBackPressed() {

    }

    public static int getPrimaryColor(Activity activity){
        TypedValue typedValue = new TypedValue();
        activity.getTheme().resolveAttribute(R.attr.colorPrimary, typedValue, true);
        return typedValue.data;
    }

    public static int getAccentColor(Activity activity){
        TypedValue typedValue = new TypedValue();
        activity.getTheme().resolveAttribute(R.attr.colorAccent, typedValue, true);
        return typedValue.data;
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

    private void handleGameState(){
        if(isPlaying){
            mHandler.removeCallbacks(r);
            timer.cancel();
            player.stop();
            isPlaying = false;
            play_pause.setImageResource(R.drawable.ic_play_circle_outline_black_24px);
        } else {
            count--;
            startLearning();
            play_pause.setImageResource(R.drawable.ic_pause_circle_filled_black_24dp);
        }
    }

    public static int getCurrentTheme(SharedPreferences pref){
        return getResId("car"+pref.getInt(PREF_CARTOON, 1),R.style.class);
    }

    private void changeState() {
        if(player != null) {
            bool = false;
            final Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    if(bool1) {
                        soundControl.setImageDrawable(drawable1);
                        if(Build.VERSION.SDK_INT>=20) {
                            if (drawable1 instanceof Animatable2) {
                                ((Animatable2) drawable1).start();
                            }
                        }
                        else {
                            if (drawable1 instanceof Animatable) {
                                ((Animatable) drawable1).start();
                            }
                        }


                        player.setVolume(0,0);
                        bool1 = false;
                    } else {
                        soundControl.setImageDrawable(drawable2);


                        if(Build.VERSION.SDK_INT>=20) {
                            if(drawable2 instanceof Animatable2) {
                                ((Animatable2) drawable2).start();
                                player.setVolume(1,1);
                            }
                        }
                        else {
                            if(drawable2 instanceof Animatable) {
                                ((Animatable) drawable2).start();
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

    public static final String PREF_DEMO = "demoMode";
    private void startLearning(){
        if(!pref.getBoolean(PREF_DEMO, false)){
            next.setVisibility(GONE);
        }

        isPlaying = true;
        timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        two.setText("" +words[count].charAt(0));
                        animator.with(Techniques.FadeOut).duration(20).playOn(one);
                        two.setVisibility(View.VISIBLE);
                        animator.with(Techniques.SlideInLeft).duration(700).playOn(two);
                        playAudio();
                        count++;
                        if(count == LIMIT){
                            timer.cancel();
                            mHandler.removeCallbacks(r);
                            next.setVisibility(View.VISIBLE);
                            pref.edit().putInt(PREF_COUNT_KEY, count).commit();
                            pref.edit().putBoolean(PREF_GAME_MODE_KEY, true).commit();
                        }
                    }
                });
            }
        }, 0, 24000);
    }

    private void playAudio(){
        x = 1;
        if(player!=null && notReleased){
            player.reset();
        }

        final int LIMIT = words[count].length();
        final int y = count;

        File f = new File(URI.create(coursePath));
        String path = f.getParent();
        FileInputStream fileInputStream;
        String file = path + File.separator + "data" + File.separator + "s"+getAlphabetPos("" + words[y].charAt(0)) + ".wav";

        try {
            player.reset();
            fileInputStream = new FileInputStream(file);
            player.setDataSource(fileInputStream.getFD());
            fileInputStream.close();
            player.prepare();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }

        r = new Runnable() {
            @Override
            public void run() {
                try {
                    if (x < LIMIT && player != null) {
                        int pos = getAlphabetPos("" + words[y].charAt(x));
                        File f = new File(URI.create(coursePath));
                        String path = f.getParent();
                        FileInputStream fileInputStream = null;
                        String file = path + File.separator + "data" + File.separator + "s" + pos + ".wav";

                        try {
                            player.reset();
                            fileInputStream = new FileInputStream(file);
                            player.setDataSource(fileInputStream.getFD());
                            fileInputStream.close();
                            player.prepare();
                            player.start();
                        } catch (FileNotFoundException e) {
                            e.printStackTrace();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                        if (x % 2 == 1) {
                            one.setText(alphabets[pos] + "");
                            one.setVisibility(View.VISIBLE);
                            animator.with(Techniques.SlideInRight).duration(700).playOn(one);
                            animator.with(Techniques.FadeOut).duration(700).playOn(two);
                        } else {
                            two.setText("" + alphabets[pos]);
                            two.setVisibility(View.VISIBLE);
                            animator.with(Techniques.SlideInLeft).duration(700).playOn(two);
                            animator.with(Techniques.FadeOut).duration(700).playOn(one);
                        }
                        x++;
                    } else if (x == LIMIT && player != null) {
                        File f = new File(URI.create(coursePath));
                        String path = f.getParent();
                        FileInputStream fileInputStream = null;
                        String file = path + File.separator + "data" + File.separator + "f" + y + ".wav";

                        try {
                            player.reset();
                            fileInputStream = new FileInputStream(file);
                            player.setDataSource(fileInputStream.getFD());
                            fileInputStream.close();
                            player.prepare();
                            player.start();
                        } catch (FileNotFoundException e) {
                            e.printStackTrace();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        animator.with(Techniques.SlideInRight).duration(2000).playOn(one);
                        animator.with(Techniques.SlideInLeft).duration(2000).playOn(two);
                        x++;
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }
            }};

        MediaPlayer.OnCompletionListener listener = new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                mHandler.postDelayed(r, 800);
            }
        };

        player.setOnCompletionListener(listener);
        try {
            player.start();
        }catch (Exception e){

        }
    }

    private int getAlphabetPos(String s){
        for(int i=0 ; i<alphabets.length; i++){
            if(s.equals(alphabets[i]))
                return i;
        }
        return 0;
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
        super.onDestroy();
        if(player!=null) {
            player.stop();
            player.setVolume(0,0);
            player.release();
            mHandler.removeCallbacks(r);
            notReleased = false;
        }
    }

    @Override
    protected void onResume() {
        if(getIntent().getAction().equals("Created")){
            isPlaying = false;
        }
        super.onResume();
    }

    @Override
    protected void onPause() {
        try{
            if(player !=null){
                player.stop();
                play_pause.setImageResource(R.drawable.ic_play_circle_outline_black_24px);
                mHandler.removeCallbacks(r);
                timer.cancel();
            }
        } catch(Exception e){
            e.printStackTrace();
        }
        super.onPause();
    }
}