package com.example.java_lms_group_01.Repository;

import com.example.java_lms_group_01.model.Course;
import com.example.java_lms_group_01.model.EnrollmentRecord;
import com.example.java_lms_group_01.model.Notice;
import com.example.java_lms_group_01.model.Timetable;
import com.example.java_lms_group_01.model.UserRecord;
import com.example.java_lms_group_01.util.DBConnection;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class AdminRepository {

    private final UserRepository userRepository = new UserRepository();
    private final CourseRepository courseRepository = new CourseRepository();
    private final NoticeRepository noticeRepository = new NoticeRepository();
    private final TimetableRepository timetableRepository = new TimetableRepository();

    public List<UserRecord> findAdmins() throws SQLException {
        return userRepository.findAdmins();
    }

    public List<UserRecord> findLecturers() throws SQLException {
        return userRepository.findLecturers();
    }

    public List<UserRecord> findStudents() throws SQLException {
        return userRepository.findStudents();
    }

    public List<UserRecord> findTechnicalOfficers() throws SQLException {
        return userRepository.findTechnicalOfficers();
    }

    public boolean createAdmin(UserRecord row) throws SQLException {
        return userRepository.createAdmin(row);
    }

    public boolean createLecturer(UserRecord row) throws SQLException {
        return userRepository.createLecturer(row);
    }

    public boolean createStudent(UserRecord row) throws SQLException {
        return userRepository.createStudent(row);
    }

    public boolean createTechnicalOfficer(UserRecord row) throws SQLException {
        return userRepository.createTechnicalOfficer(row);
    }

    public boolean updateAdmin(UserRecord row) throws SQLException {
        return userRepository.updateAdmin(row);
    }

    public boolean updateLecturer(UserRecord row) throws SQLException {
        return userRepository.updateLecturer(row);
    }

    public boolean updateStudent(UserRecord row) throws SQLException {
        return userRepository.updateStudent(row);
    }

    public boolean updateTechnicalOfficer(UserRecord row) throws SQLException {
        return userRepository.updateTechnicalOfficer(row);
    }

    public boolean deleteAdmin(String userId) throws SQLException {
        return userRepository.deleteAdmin(userId);
    }

    public boolean deleteLecturer(String userId) throws SQLException {
        return userRepository.deleteLecturer(userId);
    }

    public boolean deleteStudent(String userId) throws SQLException {
        return userRepository.deleteStudent(userId);
    }

    public boolean deleteTechnicalOfficer(String userId) throws SQLException {
        return userRepository.deleteTechnicalOfficer(userId);
    }

    public List<Course> findCoursesByFilters(String department, String keyword) throws SQLException {
        return courseRepository.findByFilters(department, keyword);
    }

    public List<String> findAllCourseDepartments() throws SQLException {
        return courseRepository.findAllDepartments();
    }

    public boolean saveCourse(Course course) throws SQLException {
        return courseRepository.save(course);
    }

    public boolean updateCourse(Course course) throws SQLException {
        return courseRepository.update(course);
    }

    public boolean deleteCourseByCode(String courseCode) throws SQLException {
        return courseRepository.deleteByCourseCode(courseCode);
    }

    public List<Notice> findAllNotices() throws SQLException {
        return noticeRepository.findAll();
    }

    public List<Notice> findNoticesByKeyword(String keyword) throws SQLException {
        return noticeRepository.findByKeyword(keyword);
    }

    public boolean saveNotice(Notice notice) throws SQLException {
        return noticeRepository.save(notice);
    }

    public boolean updateNotice(Notice notice) throws SQLException {
        return noticeRepository.update(notice);
    }

    public boolean deleteNoticeById(int noticeId) throws SQLException {
        return noticeRepository.deleteById(noticeId);
    }

    public List<Timetable> findTimetablesByFilters(String department, String day, String keyword) throws SQLException {
        return timetableRepository.findByFilters(department, day, keyword);
    }

    public List<String> findAllTimetableDepartments() throws SQLException {
        return timetableRepository.findAllDepartments();
    }

    public List<String> findAllTimetableDays() throws SQLException {
        return timetableRepository.findAllDays();
    }

    public boolean saveTimetable(Timetable timetable) throws SQLException {
        return timetableRepository.save(timetable);
    }

    public boolean updateTimetable(Timetable timetable) throws SQLException {
        return timetableRepository.update(timetable);
    }

    public boolean deleteTimetableById(String timetableId) throws SQLException {
        return timetableRepository.deleteById(timetableId);
    }

    public List<String> findStudentBatches() throws SQLException {

        List<String> list = new ArrayList<>();

        Connection con = DBConnection.getInstance().getConnection();

        String sql = "SELECT DISTINCT batch FROM student WHERE batch IS NOT NULL ORDER BY batch";

        PreparedStatement stm = con.prepareStatement(sql);
        ResultSet rs = stm.executeQuery();

        while (rs.next()) {
            String batch = rs.getString("batch");
            if (batch != null && !batch.isEmpty()) {
                list.add(batch);
            }
        }

        return list;
    }

    public List<Course> findAvailableCoursesForStudent(String studentReg) throws SQLException {

        List<Course> list = new ArrayList<>();

        Connection con = DBConnection.getInstance().getConnection();

        String sql = "SELECT * FROM course WHERE courseCode NOT IN " +
                "(SELECT courseCode FROM enrollment WHERE studentReg = ?)";

        PreparedStatement stm = con.prepareStatement(sql);
        stm.setString(1, studentReg);

        ResultSet rs = stm.executeQuery();

        while (rs.next()) {
            list.add(new Course(
                    rs.getString("courseCode"),
                    rs.getString("name"),
                    rs.getString("lecturerRegistrationNo"),
                    rs.getString("department"),
                    rs.getString("semester"),
                    rs.getInt("credit"),
                    rs.getString("course_type")
            ));
        }

        return list;
    }

    public List<EnrollmentRecord> findEnrollments(String keyword, String batch) throws SQLException {

        List<EnrollmentRecord> list = new ArrayList<>();

        Connection con = DBConnection.getInstance().getConnection();

        String sql = "SELECT s.registrationNo, u.firstName, u.lastName, s.batch, " +
                "e.enrollment_id, e.courseCode, e.enrollment_date, e.status " +
                "FROM student s " +
                "JOIN users u ON u.user_id = s.registrationNo " +
                "LEFT JOIN enrollment e ON e.studentReg = s.registrationNo " +
                "WHERE 1=1";

        List<String> params = new ArrayList<>();

        if (keyword != null && !keyword.isEmpty()) {
            sql += " AND s.registrationNo LIKE ?";
            params.add("%" + keyword + "%");
        }

        if (batch != null && !batch.isEmpty() && !batch.equalsIgnoreCase("All")) {
            sql += " AND s.batch = ?";
            params.add(batch);
        }

        PreparedStatement stm = con.prepareStatement(sql);

        for (int i = 0; i < params.size(); i++) {
            stm.setString(i + 1, params.get(i));
        }

        ResultSet rs = stm.executeQuery();

        while (rs.next()) {

            Date date = rs.getDate("enrollment_date");

            list.add(new EnrollmentRecord(
                    rs.getInt("enrollment_id"),
                    rs.getString("registrationNo"),
                    rs.getString("firstName") + " " + rs.getString("lastName"),
                    rs.getString("batch"),
                    rs.getString("courseCode"),
                    "",
                    date == null ? null : date.toLocalDate(),
                    rs.getString("status")
            ));
        }

        return list;
    }

    public boolean createEnrollment(String studentReg, String courseCode) throws SQLException {

        Connection con = DBConnection.getInstance().getConnection();

        // check student
        PreparedStatement s1 = con.prepareStatement("SELECT 1 FROM student WHERE registrationNo=?");
        s1.setString(1, studentReg);
        if (!s1.executeQuery().next()) {
            throw new SQLException("Student not found");
        }

        // check course
        PreparedStatement s2 = con.prepareStatement("SELECT 1 FROM course WHERE courseCode=?");
        s2.setString(1, courseCode);
        if (!s2.executeQuery().next()) {
            throw new SQLException("Course not found");
        }

        // check existing
        PreparedStatement s3 = con.prepareStatement(
                "SELECT 1 FROM enrollment WHERE studentReg=? AND courseCode=?"
        );
        s3.setString(1, studentReg);
        s3.setString(2, courseCode);

        if (s3.executeQuery().next()) {
            throw new IllegalArgumentException("Already enrolled");
        }

        // insert
        PreparedStatement stm = con.prepareStatement(
                "INSERT INTO enrollment(studentReg, courseCode, enrollment_date, status) VALUES (?, ?, ?, ?)"
        );

        stm.setString(1, studentReg);
        stm.setString(2, courseCode);
        stm.setDate(3, Date.valueOf(LocalDate.now()));
        stm.setString(4, "active");

        return stm.executeUpdate() > 0;
    }

    public boolean updateEnrollmentStatus(int enrollmentId, String status) throws SQLException {

        Connection con = DBConnection.getInstance().getConnection();

        PreparedStatement check = con.prepareStatement(
                "SELECT status FROM enrollment WHERE enrollment_id=?"
        );
        check.setInt(1, enrollmentId);

        ResultSet rs = check.executeQuery();

        if (!rs.next()) {
            throw new SQLException("Enrollment not found");
        }

        if (rs.getString("status").equalsIgnoreCase(status)) {
            throw new IllegalArgumentException("Same status already set");
        }

        PreparedStatement stm = con.prepareStatement(
                "UPDATE enrollment SET status=? WHERE enrollment_id=?"
        );

        stm.setString(1, status);
        stm.setInt(2, enrollmentId);

        return stm.executeUpdate() > 0;
    }

    public boolean enrollStudentToCourse(String studentReg, String courseCode, String status) throws SQLException {

        Connection con = DBConnection.getInstance().getConnection();

        PreparedStatement check = con.prepareStatement(
                "SELECT status FROM enrollment WHERE studentReg=? AND courseCode=?"
        );

        check.setString(1, studentReg);
        check.setString(2, courseCode);

        ResultSet rs = check.executeQuery();

        if (!rs.next()) {
            if (!status.equalsIgnoreCase("active")) {
                throw new IllegalArgumentException("New enrollment must be active");
            }
            return createEnrollment(studentReg, courseCode);
        }

        if (status.equalsIgnoreCase("active")) {
            throw new IllegalArgumentException("Already active");
        }

        PreparedStatement update = con.prepareStatement(
                "UPDATE enrollment SET status=? WHERE studentReg=? AND courseCode=?"
        );

        update.setString(1, status);
        update.setString(2, studentReg);
        update.setString(3, courseCode);

        return update.executeUpdate() > 0;
    }

}
