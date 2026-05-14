package lifetrack.model;

public class Challenge {
    private int id;
    private int userId;
    private String category = "Personal";
    private String description = "";
    private String solution = "";
    private String lesson = "";

    public Challenge() {}

    public Challenge(int id, int userId, String category, String description,
                     String solution, String lesson) {
        this.id = id;
        this.userId = userId;
        this.category = category;
        this.description = description;
        this.solution = solution;
        this.lesson = lesson;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }
    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public String getSolution() { return solution; }
    public void setSolution(String solution) { this.solution = solution; }
    public String getLesson() { return lesson; }
    public void setLesson(String lesson) { this.lesson = lesson; }

    @Override
    public String toString() {
        String d = description == null || description.isBlank() ? "(no description)" : description;
        if (d.length() > 60) d = d.substring(0, 57) + "...";
        return "[" + category + "] " + d;
    }
}
