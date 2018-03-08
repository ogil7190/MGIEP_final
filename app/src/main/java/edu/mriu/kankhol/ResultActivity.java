package edu.mriu.kankhol;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import static edu.mriu.kankhol.HomeActivity.PREF_COUNT_KEY;
import static edu.mriu.kankhol.HomeActivity.PREF_NAME;
import static edu.mriu.kankhol.Khelo.PREF_FIRST_GAME_DONE;
import static edu.mriu.kankhol.Khelo.PREF_GAME_MODE_KEY;
import static edu.mriu.kankhol.Khelo.UP_LIMIT;
import static edu.mriu.kankhol.Khelo.getResId;
import static edu.mriu.kankhol.ProfileActivity.PREF_COURSE_LANGUAGE;
import static edu.mriu.kankhol.ProfileActivity.PREF_COURSE_NAME;
import static edu.mriu.kankhol.ProfileActivity.PREF_PROFILE_AGE;
import static edu.mriu.kankhol.ProfileActivity.PREF_PROFILE_NAME;
import static edu.mriu.kankhol.SelectionActivity.PREF_CARTOON;
import static edu.mriu.kankhol.Sikho.getPrimaryColor;

/**
 * Activity to show the result.
 */
public class ResultActivity extends AppCompatActivity {

    private ImageView home, help, info, report;
    private SharedPreferences pref;

    private TextView name, age, course, language;
    private TextView current_correct, current_correct_percent, current_wrong, current_wrong_percent, current_avg_total_time, current_total_time;
    private TextView total_correct, total_correct_percent, total_wrong, total_wrong_percent, total_avg_time, total_time;

    private TextView dropDownProfile, dropDownCurrent, dropDownResult;
    private LinearLayout portionProfile, portionResult, portionCurrent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        pref = getSharedPreferences(PREF_NAME, MODE_PRIVATE);
        setTheme(getCurrentTheme());
        setContentView(R.layout.activity_result);
        int color = getPrimaryColor(this);

        dropDownProfile = findViewById(R.id.drop_down_profile);
        portionProfile = findViewById(R.id.portion_profile);
        dropDownProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                portionProfile.setVisibility(portionProfile.getVisibility() == View.VISIBLE ? View.GONE : View.VISIBLE);
            }
        });

        dropDownCurrent = findViewById(R.id.drop_down_current);
        portionCurrent = findViewById(R.id.portion_current);
        dropDownCurrent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                portionCurrent.setVisibility(portionCurrent.getVisibility() == View.VISIBLE ? View.GONE : View.VISIBLE);
            }
        });

        dropDownResult = findViewById(R.id.drop_down_result_total);
        portionResult = findViewById(R.id.portion_result);
        dropDownResult.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                portionResult.setVisibility(portionResult.getVisibility() == View.VISIBLE ? View.GONE : View.VISIBLE);
            }
        });

        name = findViewById(R.id.name);
        age = findViewById(R.id.age);
        course = findViewById(R.id.course);
        language = findViewById(R.id.language);

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
        report.setColorFilter(color);

        current_correct = findViewById(R.id.current_correct);
        current_correct_percent = findViewById(R.id.current_correct_percent);

        current_wrong = findViewById(R.id.current_wrong);
        current_wrong_percent = findViewById(R.id.current_wrong_percent);

        current_avg_total_time = findViewById(R.id.current_avg_time_taken);
        current_total_time = findViewById(R.id.current_total_time);

        total_correct = findViewById(R.id.total_correct);
        total_correct_percent = findViewById(R.id.total_correct_percent);

        total_wrong = findViewById(R.id.total_wrong);
        total_wrong_percent = findViewById(R.id.total_wrong_percent);

        total_avg_time = findViewById(R.id.total_avg_time_taken);
        total_time = findViewById(R.id.total_time);
        setUI();
    }

    @Override
    public void onBackPressed() {

    }

    private SQLiteHelper helper;
    private int session = 1;

    int cur_correct = 0;
    int cur_wrong = 0;
    float cur_total_time = 0f;
    float cur_avg_time = 0f;

    final float total = 7f;

    int tot_correct = 0;
    int tot_wrong = 0;
    float tot_time = 0f;
    float tot_avg_time = 0f;

    private void setUI(){
        helper = new SQLiteHelper(getApplicationContext());
        name.setText(pref.getString(PREF_PROFILE_NAME, "Register"));
        age.setText(pref.getString(PREF_PROFILE_AGE, "0"));
        course.setText(pref.getString(PREF_COURSE_NAME, "Course"));
        language.setText(pref.getString(PREF_COURSE_LANGUAGE, "Language"));

        if(pref.getBoolean(PREF_GAME_MODE_KEY, false)) {
            session = (pref.getInt(PREF_COUNT_KEY, 0) - UP_LIMIT) / UP_LIMIT;
        } else{
            session = pref.getInt(PREF_COUNT_KEY, 0) / UP_LIMIT;
        }

        if(pref.getBoolean(PREF_FIRST_GAME_DONE, false)) {
            for (int i = 1; i <= session; i++) {
                for (int j = 1; j <= 2; j++) {
                    if (j == 1)
                        for (int k = 0; k < 4; k++) {
                            String id = i + "" + j + "" + k;
                            SessionState s = helper.getSessionState(id);
                            Log.d("App",id);
                            Log.d("App",s.getSubject()+s.getId()+s.isResult());
                            if (i == session) {
                                if (s.isResult())
                                    cur_correct++;
                                else
                                    cur_wrong++;
                                cur_total_time += s.getTimeTakenInMs();
                            }

                            if (s.isResult())
                                tot_correct++;
                            else
                                tot_wrong++;
                            tot_time += s.getTimeTakenInMs();
                        }

                    else if (j == 2)
                        for (int k = 0; k < 3; k++) {
                            String id = i + "" + j + "" + k;
                            SessionState s = helper.getSessionState(id);
                            if (i == session) {
                                if (s.isResult())
                                    cur_correct++;
                                else
                                    cur_wrong++;
                                cur_total_time += s.getTimeTakenInMs();
                            }

                            if (s.isResult())
                                tot_correct++;
                            else
                                tot_wrong++;
                            tot_time += s.getTimeTakenInMs();
                        }
                }
            }

            cur_avg_time = cur_total_time / total;
            tot_avg_time = tot_time / (total * session) ;

            float cur_correct_percent = (cur_correct / total) * 100 ;
            float cur_wrong_percent = (cur_wrong / total) * 100 ;

            current_correct.setText(""+cur_correct);
            current_wrong.setText(""+cur_wrong+"");
            current_correct_percent.setText(String.format("%.02f", cur_correct_percent));
            current_wrong_percent.setText(String.format("%.02f", cur_wrong_percent));
            current_total_time.setText(String.format("%.02f", cur_total_time));
            current_avg_total_time.setText(String.format("%.02f", cur_avg_time));

            float tot_correct_percent = (tot_correct / (total * session)) * 100 ;
            float tot_wrong_percent = (tot_wrong / (total * session)) * 100 ;

            total_correct.setText(""+tot_correct);
            total_wrong.setText(""+tot_wrong);
            total_correct_percent.setText(String.format("%.02f", tot_correct_percent));
            total_wrong_percent.setText(String.format("%.02f", tot_wrong_percent));
            total_time.setText(String.format("%.02f", tot_time));
            total_avg_time.setText(String.format("%.02f", tot_avg_time));
        }
    }

    private int getCurrentTheme(){
        int x = getResId("car"+pref.getInt(PREF_CARTOON, 0),R.style.class);
        if (x == -1) return R.style.AppTheme;
        else return x;
    }
}
