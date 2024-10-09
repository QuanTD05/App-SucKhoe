package fpoly.md19304.app_moblie;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivity2 extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main2);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
//        String username = getIntent().getStringExtra("USERNAME");
//
//        // Display username in header TextView
//        TextView txtUsername = findViewById(R.id.greetingText);
//        txtUsername.setText(username);

        // Bottom navigation buttons
        ImageButton homeButton = findViewById(R.id.home_button);
        ImageButton calendarButton = findViewById(R.id.calendar_button);
        ImageButton notificationButton = findViewById(R.id.notification_button);
        ImageButton profileButton = findViewById(R.id.profile_button);

        homeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity2.this, MainActivity2.class);
                startActivity(intent);
            }
        });

        calendarButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity2.this, DisplayActivity.class);
                startActivity(intent);
            }
        });

        notificationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity2.this, activity_friends_list.class);
                startActivity(intent);
            }
        });

        profileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity2.this, trangcanhan.class);
                startActivity(intent);
            }
        });

        // Grid layout icons
        ImageView iconPhysicalActivity = findViewById(R.id.icon_physical_activity);
        ImageView iconMentalActivity = findViewById(R.id.icon_mental_activity);
        ImageView iconSleepActivity = findViewById(R.id.icon_sleep_activity);
        ImageView iconEatingActivity = findViewById(R.id.icon_eating_activity);
        ImageView iconHealthExercise = findViewById(R.id.icon_health_exercise);
        ImageView iconPsychologicalActivity = findViewById(R.id.icon_psychological_activity);

        iconPhysicalActivity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity2.this, VanDongTheChat.class);
                startActivity(intent);
            }
        });

        iconMentalActivity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity2.this, long_biet_on.class);
                startActivity(intent);
            }
        });

        iconSleepActivity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity2.this, SleepSummary.class);
                startActivity(intent);
            }
        });

        iconEatingActivity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity2.this, an_uong.class);
                startActivity(intent);
            }
        });

        iconHealthExercise.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity2.this,Suc_khoe_tinh_than.class);
                startActivity(intent);
            }
        });

        iconPsychologicalActivity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity2.this, FaqActivity.class);
                startActivity(intent);
            }
        });
    }
}
