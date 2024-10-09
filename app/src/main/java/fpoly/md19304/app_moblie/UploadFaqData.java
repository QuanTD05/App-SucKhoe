package fpoly.md19304.app_moblie;
import android.util.Log;

import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;

public class UploadFaqData {

    private FirebaseFirestore db;

    public UploadFaqData() {
        db = FirebaseFirestore.getInstance();
    }

    public void uploadData() {
        HashMap<String, Object> faqData = new HashMap<>();

        faqData.put("Tư vấn tâm lý", new String[]{
                "Tôi cảm thấy căng thẳng, tôi nên làm gì?",
                "Tôi không thể tập trung vào công việc, có cách nào giúp tôi không?",
                "Làm thế nào để xử lý cảm giác buồn chán?",
                "Tôi cảm thấy lo lắng khi gặp người lạ, tôi có thể cải thiện điều này không?"
        });
        faqData.put("Tư vấn Công việc", new String[]{
                "Tôi cảm thấy không hài lòng với công việc hiện tại, tôi nên làm gì?",
                "Làm thế nào để thăng tiến trong sự nghiệp?"
        });
        faqData.put("Tư vấn Tình yêu", new String[]{
                "Tôi cảm thấy mối quan hệ của mình đang gặp trục trặc, làm thế nào để cải thiện?",
                "Tôi cảm thấy đơn độc trong tình yêu, tôi có thể làm gì để cải thiện điều này?"
        });
        // Thêm dữ liệu cho các mục khác...

        // Lưu dữ liệu vào Firestore
        db.collection("faq").document("questions")
                .set(faqData)
                .addOnSuccessListener(aVoid -> {
                    Log.d("UploadFaqData", "Data successfully written!");
                })
                .addOnFailureListener(e -> {
                    Log.w("UploadFaqData", "Error writing document", e);
                });
    }
}
