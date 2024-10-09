package fpoly.md19304.app_moblie;

public class Step {
    private int id;
    private int steps;
    private String date;

    public Step(int id, int steps, String date) {
        this.id = id;
        this.steps = steps;
        this.date = date;
    }

    public int getId() {
        return id;
    }

    public int getSteps() {
        return steps;
    }

    public String getDate() {
        return date;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setSteps(int steps) {
        this.steps = steps;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
