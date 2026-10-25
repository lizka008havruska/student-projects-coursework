package ua.edu.nupp.kpp.server;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ReviewDAO {
    private static final String COLS = "id, submission_id, reviewer, comment, score, review_date";

    public void add(Review r) throws SQLException {
        String sql = "INSERT INTO review(submission_id, reviewer, comment, score, review_date) VALUES(?,?,?,?,?)";
        try (Connection c = DBUtil.getConnection();
             PreparedStatement p = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            p.setLong(1, r.getSubmissionId());
            p.setString(2, r.getReviewer());
            p.setString(3, r.getComment());
            p.setDouble(4, r.getScore());
            setDate(p, 5, r.getReviewDate());
            p.executeUpdate();
            try (ResultSet keys = p.getGeneratedKeys()) {
                if (keys.next()) r.setId(keys.getLong(1));
            }
        }
    }

    public List<Review> getAll() throws SQLException {
        return query("SELECT " + COLS + " FROM review ORDER BY id", null);
    }

    public List<Review> getBySubmission(long submissionId) throws SQLException {
        return query("SELECT " + COLS + " FROM review WHERE submission_id=? ORDER BY id", submissionId);
    }

    public Review getById(long id) throws SQLException {
        String sql = "SELECT " + COLS + " FROM review WHERE id=?";
        try (Connection c = DBUtil.getConnection(); PreparedStatement p = c.prepareStatement(sql)) {
            p.setLong(1, id);
            try (ResultSet r = p.executeQuery()) { return r.next() ? map(r) : null; }
        }
    }

    public void update(Review r) throws SQLException {
        String sql = "UPDATE review SET reviewer=?, comment=?, score=?, review_date=? WHERE id=?";
        try (Connection c = DBUtil.getConnection(); PreparedStatement p = c.prepareStatement(sql)) {
            p.setString(1, r.getReviewer());
            p.setString(2, r.getComment());
            p.setDouble(3, r.getScore());
            setDate(p, 4, r.getReviewDate());
            p.setLong(5, r.getId());
            p.executeUpdate();
        }
    }

    public void delete(long id) throws SQLException {
        try (Connection c = DBUtil.getConnection();
             PreparedStatement p = c.prepareStatement("DELETE FROM review WHERE id=?")) {
            p.setLong(1, id);
            p.executeUpdate();
        }
    }

    public void deleteBySubmission(long submissionId) throws SQLException {
        try (Connection c = DBUtil.getConnection();
             PreparedStatement p = c.prepareStatement("DELETE FROM review WHERE submission_id=?")) {
            p.setLong(1, submissionId);
            p.executeUpdate();
        }
    }

    private List<Review> query(String sql, Long param) throws SQLException {
        List<Review> list = new ArrayList<>();
        try (Connection c = DBUtil.getConnection(); PreparedStatement p = c.prepareStatement(sql)) {
            if (param != null) p.setLong(1, param);
            try (ResultSet r = p.executeQuery()) {
                while (r.next()) list.add(map(r));
            }
        }
        return list;
    }

    private static void setDate(PreparedStatement p, int index, String value) throws SQLException {
        if (value == null || value.isBlank()) p.setNull(index, Types.DATE);
        else p.setString(index, value);
    }

    private Review map(ResultSet r) throws SQLException {
        return new Review(r.getLong("id"), r.getLong("submission_id"), r.getString("reviewer"),
                r.getString("comment"), r.getDouble("score"), r.getString("review_date"));
    }
}
