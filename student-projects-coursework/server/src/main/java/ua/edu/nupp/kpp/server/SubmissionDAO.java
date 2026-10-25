package ua.edu.nupp.kpp.server;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class SubmissionDAO {
    private static final String COLS =
            "id, project_id, student_id, state, repo_url, submitted_date, f1, f2, f3, integral_index";

    public void add(Submission s) throws SQLException {
        String sql = "INSERT INTO submission(project_id, student_id, state, repo_url, submitted_date, f1, f2, f3, integral_index) "
                + "VALUES(?,?,?,?,?,?,?,?,?)";
        try (Connection c = DBUtil.getConnection();
             PreparedStatement p = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            p.setLong(1, s.getProjectId());
            p.setLong(2, s.getStudentId());
            p.setString(3, s.getState().name());
            p.setString(4, s.getRepoUrl());
            setDate(p, 5, s.getSubmittedDate());
            p.setDouble(6, s.getF1());
            p.setDouble(7, s.getF2());
            p.setDouble(8, s.getF3());
            p.setDouble(9, s.getIntegralIndex());
            p.executeUpdate();
            try (ResultSet keys = p.getGeneratedKeys()) {
                if (keys.next()) s.setId(keys.getLong(1));
            }
        }
    }

    public List<Submission> getAll() throws SQLException {
        List<Submission> list = new ArrayList<>();
        String sql = "SELECT " + COLS + " FROM submission ORDER BY id";
        try (Connection c = DBUtil.getConnection(); PreparedStatement p = c.prepareStatement(sql);
             ResultSet r = p.executeQuery()) {
            while (r.next()) list.add(map(r));
        }
        return list;
    }

    public Submission getById(long id) throws SQLException {
        String sql = "SELECT " + COLS + " FROM submission WHERE id=?";
        try (Connection c = DBUtil.getConnection(); PreparedStatement p = c.prepareStatement(sql)) {
            p.setLong(1, id);
            try (ResultSet r = p.executeQuery()) { return r.next() ? map(r) : null; }
        }
    }

    /** Редагування «звичайних» даних подання (стан і фактори змінюються окремими методами). */
    public void update(Submission s) throws SQLException {
        String sql = "UPDATE submission SET project_id=?, student_id=?, repo_url=? WHERE id=?";
        try (Connection c = DBUtil.getConnection(); PreparedStatement p = c.prepareStatement(sql)) {
            p.setLong(1, s.getProjectId());
            p.setLong(2, s.getStudentId());
            p.setString(3, s.getRepoUrl());
            p.setLong(4, s.getId());
            p.executeUpdate();
        }
    }

    public void updateState(long id, SubmissionState state, String submittedDate) throws SQLException {
        String sql = "UPDATE submission SET state=?, submitted_date=? WHERE id=?";
        try (Connection c = DBUtil.getConnection(); PreparedStatement p = c.prepareStatement(sql)) {
            p.setString(1, state.name());
            setDate(p, 2, submittedDate);
            p.setLong(3, id);
            p.executeUpdate();
        }
    }

    public void updateFactors(long id, double f1, double f2, double f3, double integralIndex) throws SQLException {
        String sql = "UPDATE submission SET f1=?, f2=?, f3=?, integral_index=? WHERE id=?";
        try (Connection c = DBUtil.getConnection(); PreparedStatement p = c.prepareStatement(sql)) {
            p.setDouble(1, f1);
            p.setDouble(2, f2);
            p.setDouble(3, f3);
            p.setDouble(4, integralIndex);
            p.setLong(5, id);
            p.executeUpdate();
        }
    }

    public void delete(long id) throws SQLException {
        try (Connection c = DBUtil.getConnection();
             PreparedStatement p = c.prepareStatement("DELETE FROM submission WHERE id=?")) {
            p.setLong(1, id);
            p.executeUpdate();
        }
    }

    public int countByStudent(long studentId) throws SQLException { return count("student_id", studentId); }

    public int countByProject(long projectId) throws SQLException { return count("project_id", projectId); }

    private int count(String column, long id) throws SQLException {
        String sql = "SELECT COUNT(*) FROM submission WHERE " + column + "=?";
        try (Connection c = DBUtil.getConnection(); PreparedStatement p = c.prepareStatement(sql)) {
            p.setLong(1, id);
            try (ResultSet r = p.executeQuery()) { r.next(); return r.getInt(1); }
        }
    }

    private static void setDate(PreparedStatement p, int index, String value) throws SQLException {
        if (value == null || value.isBlank()) p.setNull(index, Types.DATE);
        else p.setString(index, value);
    }

    private Submission map(ResultSet r) throws SQLException {
        return new Submission(r.getLong("id"), r.getLong("project_id"), r.getLong("student_id"),
                SubmissionState.valueOf(r.getString("state")), r.getString("repo_url"),
                r.getString("submitted_date"), r.getDouble("f1"), r.getDouble("f2"),
                r.getDouble("f3"), r.getDouble("integral_index"));
    }
}
