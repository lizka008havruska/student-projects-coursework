package ua.edu.nupp.kpp.client;

public class Submission {
    private Long id;
    private long projectId;
    private long studentId;
    private SubmissionState state;
    private String repoUrl;
    private String submittedDate;   // формат yyyy-MM-dd
    private double f1;              // повнота реалізації
    private double f2;              // результат перевірок
    private double f3;              // якість документації
    private double integralIndex;   // I = 0,40·F1 + 0,35·F2 + 0,25·F3

    public Submission() {}
    public Submission(Long id, long projectId, long studentId, SubmissionState state, String repoUrl,
                      String submittedDate, double f1, double f2, double f3, double integralIndex) {
        this.id = id; this.projectId = projectId; this.studentId = studentId; this.state = state;
        this.repoUrl = repoUrl; this.submittedDate = submittedDate;
        this.f1 = f1; this.f2 = f2; this.f3 = f3; this.integralIndex = integralIndex;
    }
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public long getProjectId() { return projectId; }
    public void setProjectId(long projectId) { this.projectId = projectId; }
    public long getStudentId() { return studentId; }
    public void setStudentId(long studentId) { this.studentId = studentId; }
    public SubmissionState getState() { return state; }
    public void setState(SubmissionState state) { this.state = state; }
    public String getRepoUrl() { return repoUrl; }
    public void setRepoUrl(String repoUrl) { this.repoUrl = repoUrl; }
    public String getSubmittedDate() { return submittedDate; }
    public void setSubmittedDate(String submittedDate) { this.submittedDate = submittedDate; }
    public double getF1() { return f1; }
    public void setF1(double f1) { this.f1 = f1; }
    public double getF2() { return f2; }
    public void setF2(double f2) { this.f2 = f2; }
    public double getF3() { return f3; }
    public void setF3(double f3) { this.f3 = f3; }
    public double getIntegralIndex() { return integralIndex; }
    public void setIntegralIndex(double integralIndex) { this.integralIndex = integralIndex; }
}
