package fpoly.md19304.app_moblie;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

public class SleepReminderReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        // Lấy thông điệp từ Intent
        String message = intent.getStringExtra("message");

        // Hiển thị thông báo
        Toast.makeText(context, message, Toast.LENGTH_LONG).show();
    }
}
