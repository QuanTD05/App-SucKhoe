package DTO;

import com.google.firebase.Timestamp;

public class Post {
    private String id; // Thêm trường ID nếu chưa có
    private String title;
    private String content;
    private String imageUrl;
    private Timestamp timestamp;
    private String username;

    // Constructor mặc định
    public Post() {}

    // Constructor với tất cả các tham số
    public Post(String id, String title, String content, String imageUrl, Timestamp timestamp, String username) {
        this.id = id;
        this.title = title;
        this.content = content;
        this.imageUrl = imageUrl;
        this.timestamp = timestamp;
        this.username = username;
    }

    // Getter và setter cho id
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    // Các getter và setter khác
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public Timestamp getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Timestamp timestamp) {
        this.timestamp = timestamp;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
