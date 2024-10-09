package fpoly.md19304.app_moblie;

/**
 * Lớp StepLog đại diện cho một bản ghi bước chân.
 */
public class StepLog {
    private int id;            // ID của bản ghi
    private int steps;         // Số bước chân
    private String date;       // Ngày ghi nhận
    private int stepGoal;      // Mục tiêu bước chân

    /**
     * Constructor của StepLog.
     *
     * @param id       ID của bản ghi
     * @param steps    Số bước chân
     * @param date     Ngày ghi nhận
     * @param stepGoal Mục tiêu bước chân
     */
    public StepLog(int id, int steps, String date, int stepGoal) {
        this.id = id;
        this.steps = steps;
        this.date = date;
        this.stepGoal = stepGoal;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getSteps() {
        return steps;
    }

    public void setSteps(int steps) {
        this.steps = steps;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public int getStepGoal() {
        return stepGoal;
    }

    public void setStepGoal(int stepGoal) {
        this.stepGoal = stepGoal;
    }
}
