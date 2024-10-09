package fpoly.md19304.app_moblie;

import static android.content.ContentValues.TAG;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class FaqActivity extends AppCompatActivity {
    private TextView userNameView;
    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;
    private FirebaseFirestore db;

    private ListView listViewFaq;

    private HashMap<String, String[]> faqMap = new HashMap<>();
    private List<String> headers = new ArrayList<>();
    private List<String> items = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_faq);

        listViewFaq = findViewById(R.id.list_view_faq);

        populateFaqData();
        setupListView();
        userNameView = findViewById(R.id.name_text);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        currentUser = mAuth.getCurrentUser();

        if (currentUser == null) {
            // Chuyển hướng về màn hình đăng nhập nếu chưa đăng nhập
            startActivity(new Intent(FaqActivity.this, Login.class));
            finish();
            return;
        }

        // Get the current user and display their name
        if (currentUser != null) {
            loadUserDetails(currentUser.getUid());
        }
        setupBottomNavigation();
    }

    private void populateFaqData() {
        // Tâm lý
        faqMap.put("Tư vấn tâm lý", new String[]{
                "Tôi cảm thấy căng thẳng, tôi nên làm gì?",
                "Tôi không thể tập trung vào công việc, có cách nào giúp tôi không?",
                "Làm thế nào để xử lý cảm giác buồn chán?",
                "Tôi cảm thấy lo lắng khi gặp người lạ, tôi có thể cải thiện điều này không?"
        });

        // Công việc
        faqMap.put("Tư vấn Công việc", new String[]{
                "Tôi cảm thấy không hài lòng với công việc hiện tại, tôi nên làm gì?",
                "Làm thế nào để thăng tiến trong sự nghiệp?"
        });

        // Tình yêu
        faqMap.put("Tư vấn Tình yêu", new String[]{
                "Tôi cảm thấy mối quan hệ của mình đang gặp trục trặc, làm thế nào để cải thiện?",
                "Tôi cảm thấy đơn độc trong tình yêu, tôi có thể làm gì để cải thiện điều này?"
        });

        // Bạn thân
        faqMap.put("Tư vấn Bạn thân", new String[]{
                "Bạn thân của tôi không còn dành thời gian cho tôi như trước, tôi nên làm gì?",
                "Làm thế nào để duy trì mối quan hệ bạn bè lâu dài?"
        });

        // Kinh tế
        faqMap.put("Tư vấn Kinh tế", new String[]{
                "Tôi cảm thấy áp lực về tài chính, tôi nên làm gì?",
                "Làm thế nào để quản lý ngân sách cá nhân hiệu quả?"
        });

        // Sức khỏe
        faqMap.put("Tư vấn Sức khỏe", new String[]{
                "Tôi cảm thấy mệt mỏi liên tục, tôi nên làm gì?",
                "Làm thế nào để duy trì lối sống lành mạnh?",
                "Tôi cảm thấy lo lắng về sức khỏe của mình, tôi có thể làm gì để cải thiện?"
        });

        // Thêm tiêu đề vào danh sách headers và items vào danh sách items
        for (String header : faqMap.keySet()) {
            headers.add(header);
            for (String question : faqMap.get(header)) {
                items.add("  " + question);  // Thụt lề để phân biệt câu hỏi
            }
        }
    }

    private void setupListView() {
        List<String> combinedList = new ArrayList<>();
        for (String header : headers) {
            combinedList.add(header);
            for (String question : faqMap.get(header)) {
                combinedList.add("  " + question);  // Thụt lề để phân biệt câu hỏi
            }
        }

        FaqAdapter adapter = new FaqAdapter(this, combinedList);
        listViewFaq.setAdapter(adapter);

        listViewFaq.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String item = combinedList.get(position);
                if (item.startsWith("  ")) { // Kiểm tra nếu đây là câu hỏi (bắt đầu bằng khoảng trắng)
                    String question = item.trim();
                    String answer = getAnswerForQuestion(question);
                    showDialog(question, answer);
                }
            }
        });
    }

    private String getAnswerForQuestion(String question) {
        for (String header : faqMap.keySet()) {
            for (String q : faqMap.get(header)) {
                if (q.equals(question)) {
                    return getAnswer(header, question);
                }
            }
        }
        return "Không có câu trả lời cho câu hỏi này.";
    }

    private String getAnswer(String header, String question) {
        switch (header) {
            case "Tư vấn tâm lý":
                return getAnswerForPsychology(question);
            case "Tư vấn Công việc":
                return getAnswerForWork(question);
            case "Tư vấn Tình yêu":
                return getAnswerForLove(question);
            case "Tư vấn Bạn thân":
                return getAnswerForFriends(question);
            case "Tư vấn Kinh tế":
                return getAnswerForFinance(question);
            case "Tư vấn Sức khỏe":
                return getAnswerForHealth(question);
            default:
                return "Không có câu trả lời cho câu hỏi này.";
        }
    }

    private String getAnswerForPsychology(String question) {
        switch (question) {
            case "Tôi cảm thấy căng thẳng, tôi nên làm gì?":
                return "Để giảm căng thẳng, bạn có thể thử thiền, tập thể dục, hoặc tìm sự hỗ trợ từ các chuyên gia.";
            case "Tôi không thể tập trung vào công việc, có cách nào giúp tôi không?":
                return "Cố gắng chia nhỏ công việc, lên kế hoạch và nghỉ giải lao thường xuyên có thể giúp cải thiện khả năng tập trung.";
            case "Làm thế nào để xử lý cảm giác buồn chán?":
                return "Tìm kiếm sở thích mới, kết nối với bạn bè hoặc tham gia các hoạt động xã hội có thể giúp giảm buồn chán.";
            case "Tôi cảm thấy lo lắng khi gặp người lạ, tôi có thể cải thiện điều này không?":
                return "Tập luyện kỹ năng giao tiếp, bắt đầu với những cuộc gặp gỡ nhỏ và tự tin hơn có thể giúp bạn cải thiện.";
            default:
                return "Không có câu trả lời cho câu hỏi này.";
        }
    }

    private String getAnswerForWork(String question) {
        switch (question) {
            case "Tôi cảm thấy không hài lòng với công việc hiện tại, tôi nên làm gì?":
                return "Xác định nguyên nhân không hài lòng và xem xét thay đổi môi trường làm việc hoặc tìm kiếm cơ hội mới.";
            case "Làm thế nào để thăng tiến trong sự nghiệp?":
                return "Phát triển kỹ năng, xây dựng mạng lưới quan hệ và làm việc chăm chỉ là các yếu tố quan trọng để thăng tiến.";
            default:
                return "Không có câu trả lời cho câu hỏi này.";
        }
    }

    private String getAnswerForLove(String question) {
        switch (question) {
            case "Tôi cảm thấy mối quan hệ của mình đang gặp trục trặc, làm thế nào để cải thiện?":
                return "Giao tiếp rõ ràng và chân thành, dành thời gian chất lượng bên nhau và tìm kiếm sự hỗ trợ từ chuyên gia nếu cần.";
            case "Tôi cảm thấy đơn độc trong tình yêu, tôi có thể làm gì để cải thiện điều này?":
                return "Nói chuyện với đối tác về cảm xúc của bạn, tìm cách tạo ra sự gắn kết và chia sẻ nhiều hơn.";
            default:
                return "Không có câu trả lời cho câu hỏi này.";
        }
    }

    private String getAnswerForFriends(String question) {
        switch (question) {
            case "Bạn thân của tôi không còn dành thời gian cho tôi như trước, tôi nên làm gì?":
                return "Giao tiếp thẳng thắn về cảm xúc của bạn và tìm cách duy trì mối quan hệ bằng các hoạt động chung.";
            case "Làm thế nào để duy trì mối quan hệ bạn bè lâu dài?":
                return "Luôn lắng nghe, chia sẻ và ủng hộ lẫn nhau, cũng như dành thời gian cho nhau thường xuyên.";
            default:
                return "Không có câu trả lời cho câu hỏi này.";
        }
    }

    private String getAnswerForFinance(String question) {
        switch (question) {
            case "Tôi cảm thấy áp lực về tài chính, tôi nên làm gì?":
                return "Lập kế hoạch tài chính, giảm chi tiêu không cần thiết và tìm cách tăng thu nhập có thể giúp giảm áp lực.";
            case "Làm thế nào để quản lý ngân sách cá nhân hiệu quả?":
                return "Tạo ngân sách chi tiêu, theo dõi các khoản chi và tiết kiệm một phần thu nhập hàng tháng.";
            default:
                return "Không có câu trả lời cho câu hỏi này.";
        }
    }

    private String getAnswerForHealth(String question) {
        switch (question) {
            case "Tôi cảm thấy mệt mỏi liên tục, tôi nên làm gì?":
                return "Đảm bảo bạn có đủ giấc ngủ, ăn uống lành mạnh và tập thể dục đều đặn. Nếu cần, hãy tìm sự tư vấn từ bác sĩ.";
            case "Làm thế nào để duy trì lối sống lành mạnh?":
                return "Ăn uống cân đối, tập thể dục thường xuyên và kiểm tra sức khỏe định kỳ.";
            case "Tôi cảm thấy lo lắng về sức khỏe của mình, tôi có thể làm gì để cải thiện?":
                return "Gặp bác sĩ để kiểm tra và nhận lời khuyên cụ thể. Tạo thói quen sinh hoạt lành mạnh và giảm stress.";
            default:
                return "Không có câu trả lời cho câu hỏi này.";
        }
    }

    private void showDialog(String question, String answer) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(question)
                .setMessage(answer)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();
                    }
                });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void loadUserDetails(String userId) {
        DocumentReference userRef = db.collection("taikhoan").document(userId);
        userRef.get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {
                String userName = documentSnapshot.getString("name");
                userNameView.setText(userName);
            } else {
                Log.d(TAG, "No such document");
            }
        }).addOnFailureListener(e -> Log.d(TAG, "get failed with ", e));
    }

    private void setupBottomNavigation() {
        ImageButton homeButton = findViewById(R.id.home_button);
        ImageButton calendarButton = findViewById(R.id.calendar_button);
        ImageButton notificationButton = findViewById(R.id.notification_button);
        ImageButton profileButton = findViewById(R.id.profile_button);

        homeButton.setOnClickListener(v -> startActivity(new Intent(FaqActivity.this, MainActivity2.class)));
        calendarButton.setOnClickListener(v -> startActivity(new Intent(FaqActivity.this, PostActivity.class)));
        notificationButton.setOnClickListener(v -> startActivity(new Intent(FaqActivity.this, activity_friends_list.class)));
        profileButton.setOnClickListener(v -> startActivity(new Intent(FaqActivity.this, trangcanhan.class)));
    }
}
