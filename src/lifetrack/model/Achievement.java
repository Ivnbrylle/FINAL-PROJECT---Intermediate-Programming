package lifetrack.model;

public class Achievement {
    private int id;
    private int userId;
    private String category = "Award";
    private String title = "";
    private String description = "";
    private String dateEarned = "";
    private String phase = "Other";   // High School / College / Vocational / Work / Other
    private String skills = "";       // comma-separated tags

    public Achievement() {}

    public Achievement(int id, int userId, String category, String title,
                       String description, String dateEarned, String phase, String skills) {
        this.id = id;
        this.userId = userId;
        this.category = category;
        this.title = title;
        this.description = description;
        this.dateEarned = dateEarned;
        this.phase = phase;
        this.skills = skills;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }
    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category == null ? "" : category; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title == null ? "" : title; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description == null ? "" : description; }
    public String getDateEarned() { return dateEarned; }
    public void setDateEarned(String dateEarned) { this.dateEarned = dateEarned == null ? "" : dateEarned; }
    public String getPhase() { return phase; }
    public void setPhase(String phase) { this.phase = phase == null || phase.isBlank() ? "Other" : phase; }
    public String getSkills() { return skills; }
    public void setSkills(String skills) { this.skills = skills == null ? "" : skills; }

    @Override
    public String toString() {
        String t = title == null || title.isBlank() ? "(untitled)" : title;
        return "[" + category + "] " + t;
    }
}
