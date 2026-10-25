package ua.edu.nupp.kpp.client;

public class Review {
    private Long id;
    private long submissionId;
    private String reviewer;
    private String comment;
    private double score;
    private String reviewDate;   // формат yyyy-MM-dd

    public Review() {}
    public Review(Long id, long submissionId, String reviewer, String comment, double score, String reviewDate) {
        this.id = id; this.submissionId = submissionId; this.reviewer = reviewer;
        this.comment = comment; this.score = score; this.reviewDate = reviewDate;
    }
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public long getSubmissionId() { return submissionId; }
    public void setSubmissionId(long submissionId) { this.submissionId = submissionId; }
    public String getReviewer() { return reviewer; }
    public void setReviewer(String reviewer) { this.reviewer = reviewer; }
    public String getComment() { return comment; }
    public void setComment(String comment) { this.comment = comment; }
    public double getScore() { return score; }
    public void setScore(double score) { this.score = score; }
    public String getReviewDate() { return reviewDate; }
    public void setReviewDate(String reviewDate) { this.reviewDate = reviewDate; }
}
