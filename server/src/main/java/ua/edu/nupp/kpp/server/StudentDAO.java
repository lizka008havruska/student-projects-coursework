package ua.edu.nupp.kpp.server;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class StudentDAO {
    private static final String COLS = "id, full_name, group_name, email";

    public void add(Student s) throws SQLException {
        String sql = "INSERT INTO student(full_name, group_name, email) VALUES(?,?,?)";
        try (Connection c = DBUtil.getConnection();
             PreparedStatement p = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            p.setString(1, s.getFullName());
            p.setString(2, s.getGroupName());
            p.setString(3, s.getEmail());
            p.executeUpdate();
            try (ResultSet keys = p.getGeneratedKeys()) {
                if (keys.next()) s.setId(keys.getLong(1));
            }
        }
    }

    public List<Student> getAll() throws SQLException {
        List<Student> list = new ArrayList<>();
        String sql = "SELECT " + COLS + " FROM student ORDER BY id";
        try (Connection c = DBUtil.getConnection(); PreparedStatement p = c.prepareStatement(sql);
             ResultSet r = p.executeQuery()) {
            while (r.next()) list.add(map(r));
        }
        return list;
    }

    public Student getById(long id) throws SQLException {
        String sql = "SELECT " + COLS + " FROM student WHERE id=?";
        try (Connection c = DBUtil.getConnection(); PreparedStatement p = c.prepareStatement(sql)) {
            p.setLong(1, id);
            try (ResultSet r = p.executeQuery()) { return r.next() ? map(r) : null; }
        }
    }

    public Student getByEmail(String email) throws SQLException {
        String sql = "SELECT " + COLS + " FROM student WHERE email=?";
        try (Connection c = DBUtil.getConnection(); PreparedStatement p = c.prepareStatement(sql)) {
            p.setString(1, email);
            try (ResultSet r = p.executeQuery()) { return r.next() ? map(r) : null; }
        }
    }

    public void update(Student s) throws SQLException {
        String sql = "UPDATE student SET full_name=?, group_name=?, email=? WHERE id=?";
        try (Connection c = DBUtil.getConnection(); PreparedStatement p = c.prepareStatement(sql)) {
            p.setString(1, s.getFullName());
            p.setString(2, s.getGroupName());
            p.setString(3, s.getEmail());
            p.setLong(4, s.getId());
            p.executeUpdate();
        }
    }

    public void delete(long id) throws SQLException {
        try (Connection c = DBUtil.getConnection();
             PreparedStatement p = c.prepareStatement("DELETE FROM student WHERE id=?")) {
            p.setLong(1, id);
            p.executeUpdate();
        }
    }

    private Student map(ResultSet r) throws SQLException {
        return new Student(r.getLong("id"), r.getString("full_name"), r.getString("group_name"), r.getString("email"));
    }
}
