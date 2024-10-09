package DTO;

public class sleepDTO {
    private String date;
    private String sleepTime;
    private String wakeUpTime;
    private String duration;

    // Constructor mặc định
    public sleepDTO() {
        // Default constructor required for calls to DataSnapshot.getValue(sleepDTO.class)
    }

    // Constructor với tất cả các thuộc tính
    public sleepDTO(String date, String sleepTime, String wakeUpTime, String duration) {
        this.date = date;
        this.sleepTime = sleepTime;
        this.wakeUpTime = wakeUpTime;
        this.duration = duration;
    }

    // Getters và Setters
    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getSleepTime() {
        return sleepTime;
    }

    public void setSleepTime(String sleepTime) {
        this.sleepTime = sleepTime;
    }

    public String getWakeUpTime() {
        return wakeUpTime;
    }

    public void setWakeUpTime(String wakeUpTime) {
        this.wakeUpTime = wakeUpTime;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    @Override
    public String toString() {
        return "Date: " + date + ", Sleep Time: " + sleepTime + ", Wake Up Time: " + wakeUpTime + ", Duration: " + duration;
    }
}
