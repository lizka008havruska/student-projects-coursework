package ua.edu.nupp.kpp.server;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ProjectDAO {
    private static final String COLS = "id, title, description, technology, difficulty";

    public void add(Project pr) throws SQLException {
        String sql = "INSERT INTO project(title, description, technology, difficulty) VALUES(?,?,?,?)";
        try (Connection c = DBUtil.getConnection();
             PreparedStatement p = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            p.setString(1, pr.getTitle());
            p.setString(2, pr.getDescription());
            p.setString(3, pr.getTechnology());
            p.setInt(4, pr.getDifficulty());
            p.executeUpdate();
            try (ResultSet keys = p.getGeneratedKeys()) {
                if (keys.next()) pr.setId(keys.getLong(1));
            }
        }
    }

    public List<Project> getAll() throws SQLException {
        List<Project> list = new ArrayList<>();
        String sql = "SELECT " + COLS + " FROM project ORDER BY id";
        try (Connection c = DBUtil.getConnection(); PreparedStatement p = c.prepareStatement(sql);
             ResultSet r = p.executeQuery()) {
            while (r.next()) list.add(map(r));
        }
        return list;
    }

    public Project getById(long id) throws SQLException {
        String sql = "SELECT " + COLS + " FROM project WHERE id=?";
        try (Connection c = DBUtil.getConnection(); PreparedStatement p = c.prepareStatement(sql)) {
            p.setLong(1, id);
            try (ResultSet r = p.executeQuery()) { return r.next() ? map(r) : null; }
        }
    }

    public void update(Project pr) throws SQLException {
        String sql = "UPDATE project SET title=?, description=?, technology=?, difficulty=? WHERE id=?";
        try (Connection c = DBUtil.getConnection(); PreparedStatement p = c.prepareStatement(sql)) {
            p.setString(1, pr.getTitle());
            p.setString(2, pr.getDescription());
            p.setString(3, pr.getTechnology());
            p.setInt(4, pr.getDifficulty());
            p.setLong(5, pr.getId());
            p.executeUpdate();
        }
    }

    public void delete(long id) throws SQLException {
        try (Connection c = DBUtil.getConnection();
             PreparedStatement p = c.prepareStatement("DELETE FROM project WHERE id=?")) {
            p.setLong(1, id);
            p.executeUpdate();
        }
    }

    private Project map(ResultSet r) throws SQLException {
        return new Project(r.getLong("id"), r.getString("title"), r.getString("description"),
                r.getString("technology"), r.getInt("difficulty"));
    }
}
