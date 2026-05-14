package lifetrack.model;

public class Experience {
    private int id;
    private int userId;
    private String type = "Job";
    private String title = "";
    private String organization = "";
    private String startDate = "";
    private String endDate = "";
    private String responsibilities = "";
    private String notes = "";

    public Experience() {}

    public Experience(int id, int userId, String type, String title, String organization,
                      String startDate, String endDate, String responsibilities, String notes) {
        this.id = id;
        this.userId = userId;
        this.type = type;
        this.title = title;
        this.organization = organization;
        this.startDate = startDate;
        this.endDate = endDate;
        this.responsibilities = responsibilities;
        this.notes = notes;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getOrganization() { return organization; }
    public void setOrganization(String organization) { this.organization = organization; }
    public String getStartDate() { return startDate; }
    public void setStartDate(String startDate) { this.startDate = startDate; }
    public String getEndDate() { return endDate; }
    public void setEndDate(String endDate) { this.endDate = endDate; }
    public String getResponsibilities() { return responsibilities; }
    public void setResponsibilities(String responsibilities) { this.responsibilities = responsibilities; }
    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }

    @Override
    public String toString() {
        String t = title == null || title.isBlank() ? "(untitled)" : title;
        String o = organization == null || organization.isBlank() ? "" : " @ " + organization;
        return "[" + type + "] " + t + o;
    }
}
