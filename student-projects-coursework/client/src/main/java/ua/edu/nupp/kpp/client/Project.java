package ua.edu.nupp.kpp.client;

public class Project {
    private Long id;
    private String title;
    private String description;
    private String technology;
    private int difficulty;

    public Project() {}
    public Project(Long id, String title, String description, String technology, int difficulty) {
        this.id = id; this.title = title; this.description = description;
        this.technology = technology; this.difficulty = difficulty;
    }
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public String getTechnology() { return technology; }
    public void setTechnology(String technology) { this.technology = technology; }
    public int getDifficulty() { return difficulty; }
    public void setDifficulty(int difficulty) { this.difficulty = difficulty; }
}
