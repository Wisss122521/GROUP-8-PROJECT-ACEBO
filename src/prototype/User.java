/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package prototype;

import javax.swing.JFrame;
import javax.swing.table.TableColumn;
import javax.swing.*;
import prototype.AttendanceChart;
import org.jfree.chart.ChartPanel;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;
import java.sql.*;
import java.io.File;
import java.util.HashMap;
import java.util.Map;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
import com.opencsv.CSVReader;
import java.awt.BorderLayout;
import javax.swing.ImageIcon;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Path;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import javax.swing.JEditorPane;
import javax.swing.JFileChooser;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

/**
 *
 * @author Wisss
 */
public class User extends javax.swing.JFrame {

    /**
     * Creates new form User
     */
    public User() {
        initComponents();
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setIcon();
        loadStudentsWithoutQR();
        loadDashboardData();
        loadStudentsWithQR();
        loadUsersToTable();
        loadAttendanceData();
        loadStudentsToTable();
        loadClassIds();
        loadSubjectToTable();
        loadTeachersToTable();
        loadClassesToTable();
        loadAssignSubjectToTable();
        loadClassToComboBox();
        loadSubjectToComboBox();
        loadAssignedTeachersToTable();
        loadTeacherToComboBox();
        loadClassesToComboBox();
        loadDropOutToTable();
        loadSMSLogsToTable();
        loadSMSLogsToTable1();
        loadClassToReports();
        loadClassList();
        loadClassIdsToQr();
        totalstudents();
        startClock(lblTimeNow);
        showDateToday(lblDateToday);
        showPieChart(jPanel9);
    }

    Connection con;
    PreparedStatement pst;
    ResultSet rs;
    private Timer timer;

    private void setIcon() {
        setIconImage(Toolkit.getDefaultToolkit().getImage(getClass().getResource("jframelogo.png")));
    }

    public void refreshData() {
        loadDashboardData();
        loadUsersToTable();
        loadStudentsWithoutQR();
        loadStudentsWithQR();
        loadAttendanceData();
        loadStudentsToTable();
        loadClassIdsToQr();
        loadClassIds();
        loadSubjectToTable();
        loadTeachersToTable();
        loadClassesToTable();
        loadAssignSubjectToTable();
        loadClassToComboBox();
        loadSubjectToComboBox();
        loadAssignedTeachersToTable();
        loadTeacherToComboBox();
        loadClassesToComboBox();
        loadSMSLogsToTable();
        loadSMSLogsToTable1();
        loadDropOutToTable();
        loadClassToReports();
        loadClassList();
        totalstudents();
        showPieChart(jPanel9);
    }

    private void showPieChart(JPanel panel) {
        ChartPanel chartPanel = AttendanceChart.getPieChartPanel();
        chartPanel.setPreferredSize(new Dimension(400, 400));
        panel.removeAll();
        panel.setLayout(new BorderLayout());
        panel.add(chartPanel, BorderLayout.CENTER);
        panel.revalidate();
        panel.repaint();
    }

    public void totalstudents() {
        loadStudentsToTable();
        int rowCount = tableStudent.getRowCount();
        lblStudentCount.setText("Total Students: " + rowCount);
    }
    
    public void updatestudentscount() {
        int rowCount = tableStudent.getRowCount();
        lblStudentCount.setText("Total Students: " + rowCount);
    }

    public void startClock(JLabel lblTime) {
        if (timer != null) {
            timer.stop();
        }

        timer = new Timer(1000, new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                LocalTime now = LocalTime.now();
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("hh:mm:ss a");
                lblTime.setText(now.format(formatter));
            }
        });
        timer.start();
    }

    public void showDateToday(JLabel lblDate) {
        LocalDate today = LocalDate.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMMM dd, yyyy");
        lblDate.setText(today.format(formatter));
    }

    private void loadClassList() {
        try {
            con = Prototype.getConnection();
            String query = "SELECT DISTINCT c.class_name FROM students s "
                    + "JOIN classes c ON s.class_id = c.class_id";
            PreparedStatement pst = con.prepareStatement(query);
            ResultSet rs = pst.executeQuery();

            cboxclass.removeAllItems();

            while (rs.next()) {
                cboxclass.addItem(rs.getString("class_name"));
            }

            rs.close();
            pst.close();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    private void sendSmsTimeOut(String parentNumber, String studentName) throws UnsupportedEncodingException {
        String apiKey = "uHONgvqZ2HmmJ17SzvkKzIrn5eMSIR";
        String apiSecret = "TIeSUMGwzJZUHvqnF22VMlkH3dXkq7";

        String message = "Hello, your child " + studentName + " has left the school.";

        String encodedMessage = java.net.URLEncoder.encode(message, "UTF-8");

        String requestUrl = "https://api.movider.co/v1/sms?api_key=" + apiKey
                + "&api_secret=" + apiSecret
                + "&to=" + parentNumber
                + "&text=" + encodedMessage
                + "&callback_url=https%3A%2F%2Fconsole.movider.co%2Fcampaign%2Fdashboard%2F"
                + "&callback_method=GET";

        try {

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(requestUrl))
                    .header("accept", "application/json")
                    .GET()
                    .build();

            HttpClient client = HttpClient.newHttpClient();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            logToDatabase(parentNumber, studentName, message, "sent");

            System.out.println("SMS Sent: " + response.body());

        } catch (java.net.ConnectException e) {
            logToDatabase(parentNumber, studentName, message, "failed - no internet");
            JOptionPane.showMessageDialog(null, "Failed to send SMS. No internet connection.", "Network Error", JOptionPane.ERROR_MESSAGE);

        } catch (java.net.UnknownHostException e) {
            logToDatabase(parentNumber, studentName, message, "failed - server unreachable");
            JOptionPane.showMessageDialog(null, "Failed to send SMS. SMS server is unreachable.", "Server Error", JOptionPane.ERROR_MESSAGE);

        } catch (Exception e) {
            logToDatabase(parentNumber, studentName, message, "failed - unknown error");
            JOptionPane.showMessageDialog(null, "An error occurred while sending SMS.", "Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    private void sendSmsTimeIn(String parentNumber, String studentName) throws UnsupportedEncodingException {
        String apiKey = "uHONgvqZ2HmmJ17SzvkKzIrn5eMSIR";
        String apiSecret = "TIeSUMGwzJZUHvqnF22VMlkH3dXkq7";

        String message = "Hello, your child " + studentName + " has arrived at school.";

        String encodedMessage = java.net.URLEncoder.encode(message, "UTF-8");

        String requestUrl = "https://api.movider.co/v1/sms?api_key=" + apiKey
                + "&api_secret=" + apiSecret
                + "&to=" + parentNumber
                + "&text=" + encodedMessage
                + "&callback_url=https%3A%2F%2Fconsole.movider.co%2Fcampaign%2Fdashboard%2F"
                + "&callback_method=GET";

        try {

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(requestUrl))
                    .header("accept", "application/json")
                    .GET()
                    .build();

            HttpClient client = HttpClient.newHttpClient();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            logToDatabase(parentNumber, studentName, message, "sent");

            System.out.println("SMS Sent: " + response.body());

        } catch (java.net.ConnectException e) {
            logToDatabase(parentNumber, studentName, message, "failed - no internet");
            JOptionPane.showMessageDialog(null, "Failed to send SMS. No internet connection.", "Network Error", JOptionPane.ERROR_MESSAGE);

        } catch (java.net.UnknownHostException e) {
            logToDatabase(parentNumber, studentName, message, "failed - server unreachable");
            JOptionPane.showMessageDialog(null, "Failed to send SMS. SMS server is unreachable.", "Server Error", JOptionPane.ERROR_MESSAGE);

        } catch (Exception e) {
            logToDatabase(parentNumber, studentName, message, "failed - unknown error");
            JOptionPane.showMessageDialog(null, "An error occurred while sending SMS.", "Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    private void markStudentPresent(String scannedQR) throws UnsupportedEncodingException {
        if (scannedQR == null || scannedQR.trim().isEmpty()) {
            lblStatus.setText("QR Code is empty.");
            return;
        }

        try (Connection con = Prototype.getConnection()) {
            String query = "SELECT students_name, parent_contact_number FROM students WHERE LOWER(qr_code_data) = LOWER(?)";
            try (PreparedStatement pst = con.prepareStatement(query)) {
                pst.setString(1, scannedQR.trim());
                ResultSet rs = pst.executeQuery();

                if (rs.next()) {
                    String studentName = rs.getString("students_name");
                    String parentNumber = rs.getString("parent_contact_number");
                    lblStatus.setText("QR Code Scanned!");

                    DefaultTableModel model = (DefaultTableModel) tableAttendance.getModel();
                    boolean foundInTable = false;

                    String todayDate = java.time.LocalDate.now().toString();
                    String currentTime = java.time.LocalTime.now().withNano(0).toString();

                    for (int i = 0; i < model.getRowCount(); i++) {
                        String tableStudentName = model.getValueAt(i, 0).toString();
                        if (tableStudentName.equalsIgnoreCase(studentName)) {
                            foundInTable = true;

                            model.setValueAt(todayDate, i, 2);
                            model.setValueAt(currentTime, i, 3);
                            model.setValueAt("Present", i, 5);

                            sendSmsTimeIn(parentNumber, studentName);
                            break;
                        }
                    }

                    if (!foundInTable) {
                        lblStatus.setText("Student is not in the attendance list!");
                    }
                    TxtQrcode.setText("");
                } else {
                    lblStatus.setText("QR Code not found in database!");
                }
            }
        } catch (SQLException ex) {
            Logger.getLogger(User.class.getName()).log(Level.SEVERE, null, ex);
            lblStatus.setText("Error processing QR code.");
        }
    }

    public static void generateQRCodeImage(String data, String filePath) throws Exception {
        int size = 250;
        String fileType = "png";
        File qrFile = new File(filePath);

        QRCodeWriter qrCodeWriter = new QRCodeWriter();
        BitMatrix bitMatrix = qrCodeWriter.encode(data, BarcodeFormat.QR_CODE, size, size);

        MatrixToImageWriter.writeToPath(bitMatrix, fileType, qrFile.toPath());
    }

    public void generateExcelSummaryFromJTable(JTable table) {
        try {
            java.util.Date fromRaw = dateChooserFromDate.getDate();
            java.util.Date toRaw = dateChooserEndDate.getDate();

            if (fromRaw == null || toRaw == null) {
                JOptionPane.showMessageDialog(null, "Please select both From and To dates.");
                return;
            }

            java.util.Date fromDate = stripTime(fromRaw);
            java.util.Date toDate = stripTime(toRaw);

            if (fromDate.after(toDate)) {
                JOptionPane.showMessageDialog(null, "Invalid date range: 'From' date must be before 'To' date.");
                return;
            }

            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            SimpleDateFormat displayFormat = new SimpleDateFormat("MMMM dd, yyyy");

            Map<String, Map<String, String>> attendanceMap = new LinkedHashMap<>();
            Set<String> dateSet = new TreeSet<>();

            for (int i = 0; i < table.getRowCount(); i++) {
                Object nameObj = table.getValueAt(i, 0);
                Object dateObjRaw = table.getValueAt(i, 2);
                Object statusObj = table.getValueAt(i, 5);

                if (nameObj == null || dateObjRaw == null || statusObj == null) {
                    continue;
                }

                String name = nameObj.toString();
                String dateStr = dateObjRaw.toString();
                String status = statusObj.toString();

                java.util.Date dateObj = stripTime(sdf.parse(dateStr));
                if (!dateObj.before(fromDate) && !dateObj.after(toDate)) {
                    dateSet.add(dateStr);
                    attendanceMap.putIfAbsent(name, new HashMap<>());
                    attendanceMap.get(name).put(dateStr, status);
                }
            }

            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setDialogTitle("Select folder to save Excel file");
            fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
            int result = fileChooser.showSaveDialog(null);

            if (result == JFileChooser.APPROVE_OPTION) {
                File folder = fileChooser.getSelectedFile();

                SimpleDateFormat timestampFormat = new SimpleDateFormat("yyyy-MM-dd_HHmm");
                String timestamp = timestampFormat.format(new java.util.Date());
                String filePath = folder.getAbsolutePath() + File.separator + "Student_Attendance_Summary_" + timestamp + ".xlsx";
                File file = new File(filePath);

                Workbook workbook = new XSSFWorkbook();
                Sheet sheet = workbook.createSheet("Attendance Summary");

                org.apache.poi.ss.usermodel.Font boldFont = workbook.createFont();
                boldFont.setBold(true);
                boldFont.setFontHeightInPoints((short) 14);

                CellStyle boldCenterStyle = workbook.createCellStyle();
                boldCenterStyle.setFont(boldFont);
                boldCenterStyle.setAlignment(HorizontalAlignment.CENTER);

                CellStyle boldRowStyle = workbook.createCellStyle();
                boldRowStyle.setFont(boldFont);

                CellStyle borderedStyle = workbook.createCellStyle();
                borderedStyle.setBorderTop(BorderStyle.THIN);
                borderedStyle.setBorderBottom(BorderStyle.THIN);
                borderedStyle.setBorderLeft(BorderStyle.THIN);
                borderedStyle.setBorderRight(BorderStyle.THIN);

                CellStyle wrappedHeaderStyle = workbook.createCellStyle();
                wrappedHeaderStyle.cloneStyleFrom(borderedStyle);
                wrappedHeaderStyle.setWrapText(true);
                wrappedHeaderStyle.setAlignment(HorizontalAlignment.CENTER);
                wrappedHeaderStyle.setVerticalAlignment(VerticalAlignment.CENTER);

                int totalColumns = dateSet.size() + 3;

                Row schoolRow = sheet.createRow(0);
                Cell schoolCell = schoolRow.createCell(0);
                schoolCell.setCellValue("Horacio Dela Costa Elementary School");
                schoolCell.setCellStyle(boldCenterStyle);
                sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, totalColumns));

                Row titleRow = sheet.createRow(1);
                Cell titleCell = titleRow.createCell(0);
                titleCell.setCellValue("Student Attendance Summary");
                titleCell.setCellStyle(boldCenterStyle);
                sheet.addMergedRegion(new CellRangeAddress(1, 1, 0, totalColumns));

                Row dateRow = sheet.createRow(2);
                Cell dateCell = dateRow.createCell(0);
                dateCell.setCellValue("Date Range: " + displayFormat.format(fromDate) + " - " + displayFormat.format(toDate));
                dateCell.setCellStyle(boldCenterStyle);
                sheet.addMergedRegion(new CellRangeAddress(2, 2, 0, totalColumns));

                Row infoRow = sheet.createRow(3);
                infoRow.createCell(0).setCellValue("Section:");
                infoRow.createCell(2).setCellValue("Adviser:");
                infoRow.createCell(4).setCellValue("School Year:");

                sheet.createRow(4);

                Row header = sheet.createRow(5);
                int col = 0;

                Cell nameHeader = header.createCell(col++);
                nameHeader.setCellValue("Student Name");
                nameHeader.setCellStyle(wrappedHeaderStyle);

                for (String date : dateSet) {
                    LocalDate parsed = LocalDate.parse(date);
                    String label = parsed.format(DateTimeFormatter.ofPattern("dd\n(E)"));
                    Cell dateHeader = header.createCell(col++);
                    dateHeader.setCellValue(label);
                    dateHeader.setCellStyle(wrappedHeaderStyle);
                }

                String[] extras = {"Present", "Absent"};
                for (String extra : extras) {
                    Cell cell = header.createCell(col++);
                    cell.setCellValue(extra);
                    cell.setCellStyle(wrappedHeaderStyle);
                }

                int rowNum = 6;
                int grandTotalPresent = 0, grandTotalAbsent = 0, grandTotalLate = 0;

                for (String name : attendanceMap.keySet()) {
                    Row row = sheet.createRow(rowNum++);
                    Map<String, String> dailyStatus = attendanceMap.get(name);

                    int present = 0, absent = 0;
                    col = 0;

                    Cell nameCell = row.createCell(col++);
                    nameCell.setCellValue(name);
                    nameCell.setCellStyle(borderedStyle);

                    for (String date : dateSet) {
                        String status = dailyStatus.getOrDefault(date, "Absent");
                        String symbol = "";

                        switch (status.toLowerCase()) {
                            case "present":
                                symbol = "/";
                                present++;
                                break;
                            case "absent":
                            default:
                                symbol = "x";
                                absent++;
                                break;
                        }

                        Cell cell = row.createCell(col++);
                        cell.setCellValue(symbol);
                        cell.setCellStyle(borderedStyle);
                    }

                    Cell presentCell = row.createCell(col++);
                    presentCell.setCellValue(present);
                    presentCell.setCellStyle(borderedStyle);

                    Cell absentCell = row.createCell(col++);
                    absentCell.setCellValue(absent);
                    absentCell.setCellStyle(borderedStyle);

                    Cell lateCell = row.createCell(col++);
                    lateCell.setCellStyle(borderedStyle);

                    grandTotalPresent += present;
                    grandTotalAbsent += absent;
                }

                Row totalRow = sheet.createRow(rowNum++);
                col = 0;

                Cell totalLabel = totalRow.createCell(col++);
                totalLabel.setCellValue("TOTAL ATTENDANCE");
                totalLabel.setCellStyle(boldRowStyle);
                totalLabel.setCellStyle(borderedStyle);

                for (int i = 0; i < dateSet.size(); i++) {
                    Cell filler = totalRow.createCell(col++);
                    filler.setCellStyle(borderedStyle);
                }

                Cell totalPresent = totalRow.createCell(col++);
                totalPresent.setCellValue(grandTotalPresent);
                totalPresent.setCellStyle(borderedStyle);

                Cell totalAbsent = totalRow.createCell(col++);
                totalAbsent.setCellValue(grandTotalAbsent);
                totalAbsent.setCellStyle(borderedStyle);

                Row totalStudentsRow = sheet.createRow(rowNum++);
                totalStudentsRow.createCell(0).setCellValue("TOTAL STUDENTS");
                totalStudentsRow.createCell(1).setCellValue(attendanceMap.size());
                totalStudentsRow.getCell(0).setCellStyle(borderedStyle);
                totalStudentsRow.getCell(1).setCellStyle(borderedStyle);

                Row legendRow = sheet.createRow(rowNum++);
                legendRow.createCell(0).setCellValue("Symbol: / = Present   x = Absent");

                for (int i = 0; i < header.getLastCellNum(); i++) {
                    sheet.autoSizeColumn(i);
                }

                header.setHeightInPoints(40);

                FileOutputStream out = new FileOutputStream(file);
                workbook.write(out);
                out.close();
                workbook.close();

                JOptionPane.showMessageDialog(null, "Excel summary generated:\n" + file.getAbsolutePath());
            }
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Error: " + e.getMessage());
        }
    }

    private java.util.Date stripTime(java.util.Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        return cal.getTime();
    }

    public void importFromCSV() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Select CSV File");
        int userSelection = fileChooser.showOpenDialog(null);

        if (userSelection == JFileChooser.APPROVE_OPTION) {
            File csvFile = fileChooser.getSelectedFile();

            try (CSVReader reader = new CSVReader(new FileReader(csvFile))) {
                String[] data;
                Connection con = Prototype.getConnection();

                boolean isFirstLine = true;
                boolean hasInvalidClass = false;
                StringBuilder invalidClasses = new StringBuilder();

                while ((data = reader.readNext()) != null) {
                    if (isFirstLine) {
                        isFirstLine = false;
                        continue;
                    }

                    if (data.length < 2) {
                        continue;
                    }

                    String studentName = data[0].trim();
                    String className = data[1].trim();
                    String parentNumber = data.length > 2 ? data[2].trim() : "";

                    int classId = -1;
                    String classQuery = "SELECT class_id FROM classes WHERE class_name = ?";
                    try (PreparedStatement psClass = con.prepareStatement(classQuery)) {
                        psClass.setString(1, className);
                        ResultSet rs = psClass.executeQuery();
                        if (rs.next()) {
                            classId = rs.getInt("class_id");
                        } else {
                            hasInvalidClass = true;
                            invalidClasses.append("- ").append(className).append("\n");
                            continue;
                        }
                    }

                    String insert = "INSERT INTO students (students_name, class_id, parent_contact_number) VALUES (?, ?, ?)";
                    try (PreparedStatement pstmt = con.prepareStatement(insert)) {
                        pstmt.setString(1, studentName);
                        pstmt.setInt(2, classId);
                        pstmt.setString(3, parentNumber);
                        pstmt.executeUpdate();
                    }
                }

                if (hasInvalidClass) {
                    JOptionPane.showMessageDialog(null,
                            "Some class names were not found:\n" + invalidClasses.toString()
                            + "\nStudents with valid classes were still saved.",
                            "Import Partially Successful", JOptionPane.WARNING_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(null, "Students imported successfully!");
                }

                loadStudentsToTable();

            } catch (Exception ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(null, "Error importing CSV: " + ex.getMessage());
            }
        }
    }

    public void loadStudentsWithQR() {
        try {
            Connection con = Prototype.getConnection();
            String query = "SELECT s.students_id, s.students_name, c.class_name, s.parent_contact_number, s.qr_code_data, s.status "
                    + "FROM students s "
                    + "JOIN classes c ON s.class_id = c.class_id "
                    + "WHERE s.qr_code_data IS NOT NULL AND s.qr_code_data != '' ORDER BY s.students_name ASC";

            PreparedStatement pst = con.prepareStatement(query);
            ResultSet rs = pst.executeQuery();

            DefaultTableModel model = (DefaultTableModel) tableWithQR.getModel();
            model.setRowCount(0);

            while (rs.next()) {
                model.addRow(new Object[]{
                    rs.getInt("students_id"),
                    rs.getString("students_name"),
                    rs.getString("class_name"),
                    rs.getString("parent_contact_number"),
                    rs.getString("qr_code_data"),
                    rs.getString("status")
                });
            }

            rs.close();
            pst.close();
            con.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void loadStudentsWithoutQR() {
        try {
            Connection con = Prototype.getConnection();
            String query = "SELECT s.students_id, s.students_name, c.class_name, s.parent_contact_number, s.qr_code_data, s.status "
                    + "FROM students s "
                    + "JOIN classes c ON s.class_id = c.class_id "
                    + "WHERE s.qr_code_data IS NULL OR s.qr_code_data = '' ORDER BY s.students_name ASC";

            PreparedStatement pst = con.prepareStatement(query);
            ResultSet rs = pst.executeQuery();

            DefaultTableModel model = (DefaultTableModel) tableWithoutQR.getModel();
            model.setRowCount(0);

            while (rs.next()) {
                model.addRow(new Object[]{
                    rs.getInt("students_id"),
                    rs.getString("students_name"),
                    rs.getString("class_name"),
                    rs.getString("parent_contact_number"),
                    rs.getString("qr_code_data"),
                    rs.getString("status")
                });
            }

            rs.close();
            pst.close();
            con.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void loadSMSLogsToTable1() {
        DefaultTableModel model = (DefaultTableModel) tableSms1.getModel();
        model.setRowCount(0);

        String query = "SELECT parent_number, student_name, message, created_at, status FROM sms_logs ORDER BY created_at DESC";

        try (Connection con = Prototype.getConnection(); Statement stmt = con.createStatement(); ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                Object[] row = {
                    rs.getString("parent_number"),
                    rs.getString("student_name"),
                    rs.getString("message"),
                    rs.getString("created_at"),
                    rs.getString("status"),};
                model.addRow(row);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void logToDatabase(String parentNumber, String studentName, String message, String status) {
        try {

            con = Prototype.getConnection();
            String sql = "INSERT INTO sms_logs (parent_number, student_name, message, status) VALUES (?, ?, ?, ?)";

            try (PreparedStatement stmt = con.prepareStatement(sql)) {
                stmt.setString(1, parentNumber);
                stmt.setString(2, studentName);
                stmt.setString(3, message);
                stmt.setString(4, status);

                stmt.executeUpdate();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void sendSmsManual(String recipientNumber, String customMessage) throws UnsupportedEncodingException {
        String apiKey = "uHONgvqZ2HmmJ17SzvkKzIrn5eMSIR";
        String apiSecret = "TIeSUMGwzJZUHvqnF22VMlkH3dXkq7";

        String encodedMessage = java.net.URLEncoder.encode(customMessage, "UTF-8");

        String requestUrl = "https://api.movider.co/v1/sms?api_key=" + apiKey
                + "&api_secret=" + apiSecret
                + "&to=" + recipientNumber
                + "&text=" + encodedMessage
                + "&callback_url=https%3A%2F%2Fconsole.movider.co%2Fcampaign%2Fdashboard%2F"
                + "&callback_method=GET";

        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(requestUrl))
                    .header("accept", "application/json")
                    .GET()
                    .build();

            HttpClient client = HttpClient.newHttpClient();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            logToDatabase(recipientNumber, "Manual", customMessage, "sent");

            JOptionPane.showMessageDialog(null, "SMS Sent Successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);

        } catch (Exception e) {
            logToDatabase(recipientNumber, "Manual", customMessage, "failed");
            JOptionPane.showMessageDialog(null, "Failed to send SMS.", "Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    public static void showTermsDialog() {

        String termsText = "<html>"
                + "<h1><b>Welcome to the Student Attendance Monitoring System. By using this system, you agree to comply with the following terms and conditions. Please read them carefully.</b></h1>"
                + "<br><br><b>1. Data Collection and Privacy</b><br>"
                + "<ul><li>The system collects and stores student attendance records.</li>"
                + "<li>Personal data is only used for attendance monitoring and is not given to those without authorization.</li>"
                + "<li>By using the system, you agree to the collection and processing of attendance data.</li></ul>"
                + "<br><br><b>2. User Responsibilities</b><br>"
                + "<ul><li>Students need to scan their QR Codes correctly to mark attendance.</li>"
                + "<li>Teachers' responsibility to verify the arrival and departure times of the students.</li>"
                + "<li>Teachers' is responsible for securing the important data stored in the system.</li></ul>"
                + "<br><br><b>3. System Usage Guidelines</b><r>"
                + "<ul><li>System is just for the student attendance monitoring.</li>"
                + "<li>System prohibits unauthorized access and data misuse among its users.</li>"
                + "<li>Anyone who attempts to manipulate the attendance records will face disciplinary action.</li></ul>"
                + "<br><br><b>4. Data Accuracy and Security</b><br>"
                + "<ul><li>System requires accuracy and data security for each piece of data created to better protect the content information.</li>"
                + "<li>Each user of the system is responsible for promptly reporting any errors in the system or unauthorized access.</li>"
                + "<li>School has reserves the right to modify or update the records in case any discrepancies arise in the system.</li></ul>"
                + "<br><br><b>5. Limitation of Liability</b><br>"
                + "<ul><li>School and the developer are not responsible for hardware malfunctions or power outages.</li>"
                + "<li>Teachers need to ensure that they properly scan the QR codes of their students to avoid attendance issues.</li></ul>"
                + "<br><br><b>6. Modifications and Updates</b><br>"
                + "<ul><li>School has the ability to update the terms and conditions at any time.</li>"
                + "<li>Users will be notified of any changes to the system policies.</li></ul>"
                + "<br><br><b>7. Agreement</b><br>"
                + "<ul><li>To avoid system disruptions, it is crucial to comply with or agree to the terms or conditions when using the system.</li></ul>"
                + "</html>";

        JEditorPane textPane = new JEditorPane("text/html", termsText);
        textPane.setEditable(false);

        JScrollPane scrollPane = new JScrollPane(textPane);
        scrollPane.setPreferredSize(new Dimension(800, 500));

        SwingUtilities.invokeLater(() -> scrollPane.getVerticalScrollBar().setValue(0));

        JOptionPane.showMessageDialog(null, scrollPane, "Terms and Conditions", JOptionPane.INFORMATION_MESSAGE);
    }

//    private void loadAttendanceDataToOverall() {
//        DefaultTableModel model = (DefaultTableModel) tableOverallAttendance.getModel();
//        model.setRowCount(0); 
//
//        try {
//            con = Prototype.getConnection();
//            PreparedStatement pst = con.prepareStatement("SELECT * FROM attendance_records");
//            ResultSet rs = pst.executeQuery();
//
//            while (rs.next()) {
//                String studentName = rs.getString("students_name");
//                String classId = rs.getString("class_id");
//                String date = rs.getString("date");
//                String timeIn = rs.getString("time_in");
//                String timeOut = rs.getString("time_out");
//                String status = rs.getString("status");
//
//                model.addRow(new Object[]{studentName, classId, date, timeIn, timeOut, status});
//            }
//
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
    public String getClassNameForId(int classId) {
        String query = "SELECT class_name FROM classes WHERE class_id = ?";
        try (Connection con = Prototype.getConnection(); PreparedStatement pst = con.prepareStatement(query)) {

            pst.setInt(1, classId);
            ResultSet rs = pst.executeQuery();

            if (rs.next()) {
                return rs.getString("class_name");
            } else {
                return "";
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            return "";
        }
    }

    public int getClassIdForName(String className) {

        String query = "SELECT class_id FROM classes WHERE class_name = ?";
        try (Connection con = Prototype.getConnection(); PreparedStatement pst = con.prepareStatement(query)) {

            pst.setString(1, className);
            ResultSet rs = pst.executeQuery();

            if (rs.next()) {
                return rs.getInt("class_id");
            } else {

                JOptionPane.showMessageDialog(rootPane, "Class not found!");
                return -1;
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            return -1;
        }
    }

    private int getClassId(String className) {
        int classId = -1;
        String query = "SELECT class_id FROM classes WHERE class_name = ?";

        try (
                Connection con = Prototype.getConnection(); PreparedStatement pst = con.prepareStatement(query)) {

            pst.setString(1, className);
            ResultSet rs = pst.executeQuery();

            if (rs.next()) {
                classId = rs.getInt("class_id");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return classId;
    }

    private int getTeacherId(String teacherName) {
        int teacherId = -1;
        String query = "SELECT teacher_id FROM teachers WHERE teacher_name = ?";

        try (
                Connection con = Prototype.getConnection(); PreparedStatement pst = con.prepareStatement(query)) {

            pst.setString(1, teacherName);
            ResultSet rs = pst.executeQuery();

            if (rs.next()) {
                teacherId = rs.getInt("teacher_id");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return teacherId;
    }

    public void loadSMSLogsToTable() {
        DefaultTableModel model = (DefaultTableModel) tableSms.getModel();
        model.setRowCount(0);

        String query = "SELECT parent_number, student_name, message, created_at, status FROM sms_logs ORDER BY created_at DESC";

        try (Connection con = Prototype.getConnection(); Statement stmt = con.createStatement(); ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                Object[] row = {
                    rs.getString("parent_number"),
                    rs.getString("student_name"),
                    rs.getString("message"),
                    rs.getString("created_at"),
                    rs.getString("status"),};
                model.addRow(row);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void loadClassToReports() {
        cboxClassReports.removeAllItems();
        cboxClassReports.addItem("Select All Classes");

        String query = "SELECT * FROM classes ORDER BY class_name ASC";

        try (
                Connection con = Prototype.getConnection(); Statement stmt = con.createStatement(); ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                cboxClassReports.addItem(rs.getString("class_name"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void loadTeacherToComboBox() {
        cmbsubject1.removeAllItems();
        cmbsubject1.addItem("Select Teacher");

        String query = "SELECT teacher_id, teacher_name FROM teachers ORDER BY teacher_name ASC";

        try (
                Connection con = Prototype.getConnection(); Statement stmt = con.createStatement(); ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                cmbsubject1.addItem(rs.getString("teacher_name"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void loadClassesToComboBox() {
        cmbClass1.removeAllItems();
        cmbClass1.addItem("Select Class");

        String query = "SELECT class_id, class_name FROM classes ORDER BY class_name ASC";

        try (
                Connection con = Prototype.getConnection(); Statement stmt = con.createStatement(); ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                cmbClass1.addItem(rs.getString("class_name"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void deleteAssignedClassTeacher(int id) {
        int confirm = JOptionPane.showConfirmDialog(null, "Are you sure you want to delete this assignment?", "Confirm", JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            String query = "DELETE FROM class_teacher WHERE id = ?";

            try (
                    Connection con = Prototype.getConnection(); PreparedStatement pst = con.prepareStatement(query)) {

                pst.setInt(1, id);
                pst.executeUpdate();

                JOptionPane.showMessageDialog(null, "Assignment deleted successfully!");
                loadAssignedTeachersToTable();

            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public void loadAssignedTeachersToTable() {
        DefaultTableModel model = (DefaultTableModel) tableAssignTeacher.getModel();
        model.setRowCount(0);

        String query = "SELECT ct.id, c.class_name, t.teacher_name "
                + "FROM class_teacher ct "
                + "JOIN classes c ON ct.class_id = c.class_id "
                + "JOIN teachers t ON ct.teacher_id = t.teacher_id "
                + "ORDER BY ct.id DESC";

        try (
                Connection con = Prototype.getConnection(); Statement stmt = con.createStatement(); ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                Object[] row = {
                    rs.getInt("id"),
                    rs.getString("class_name"),
                    rs.getString("teacher_name")
                };
                model.addRow(row);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void assignClassToTeacher() {
        String selectedClass = (String) cmbClass1.getSelectedItem();
        String selectedTeacher = (String) cmbsubject1.getSelectedItem();

        if (selectedClass.equals("Select Class") || selectedTeacher.equals("Select Teacher")) {
            JOptionPane.showMessageDialog(null, "Please select both Class and Teacher!", "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String query = "INSERT INTO class_teacher (class_id, teacher_id) VALUES (?, ?)";

        try (
                Connection con = Prototype.getConnection(); PreparedStatement pst = con.prepareStatement(query)) {

            int classId = getClassId(selectedClass);
            int teacherId = getTeacherId(selectedTeacher);

            pst.setInt(1, classId);
            pst.setInt(2, teacherId);

            pst.executeUpdate();
            JOptionPane.showMessageDialog(null, "Class assigned to teacher successfully!");
            loadAssignedTeachersToTable();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void updateAssignedClassTeacher(int id) {
        String selectedClass = (String) cmbClass1.getSelectedItem();
        String selectedTeacher = (String) cmbsubject1.getSelectedItem();

        if (selectedClass.equals("Select Class") || selectedTeacher.equals("Select Teacher")) {
            JOptionPane.showMessageDialog(null, "Please select both Class and Teacher!", "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String query = "UPDATE class_teacher SET class_id = ?, teacher_id = ? WHERE id = ?";

        try (
                Connection con = Prototype.getConnection(); PreparedStatement pst = con.prepareStatement(query)) {

            int classId = getClassId(selectedClass);
            int teacherId = getTeacherId(selectedTeacher);

            pst.setInt(1, classId);
            pst.setInt(2, teacherId);
            pst.setInt(3, id);

            pst.executeUpdate();
            JOptionPane.showMessageDialog(null, "Assignment updated successfully!");
            loadAssignedTeachersToTable();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public int getSubjectId(String subjectName) {
        String query = "SELECT subject_id FROM subject WHERE subject_name = ?";
        try (
                Connection con = Prototype.getConnection(); PreparedStatement pst = con.prepareStatement(query)) {

            pst.setString(1, subjectName);
            ResultSet rs = pst.executeQuery();

            if (rs.next()) {
                return rs.getInt("subject_id");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }

    public void deleteAssignedClassSubject(int id) {
        int confirm = JOptionPane.showConfirmDialog(null, "Are you sure you want to delete this assignment?", "Confirm", JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            String query = "DELETE FROM class_subject WHERE id = ?";

            try (
                    Connection con = Prototype.getConnection(); PreparedStatement pst = con.prepareStatement(query)) {

                pst.setInt(1, id);
                pst.executeUpdate();

                JOptionPane.showMessageDialog(null, "Assignment deleted successfully!");
                loadAssignSubjectToTable();

            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public void updateAssignedClassSubject(int id) {
        String selectedClass = (String) cmbClass.getSelectedItem();
        String selectedSubject = (String) cmbSubject.getSelectedItem();

        if (selectedClass.equals("Select Class") || selectedSubject.equals("Select Subject")) {
            JOptionPane.showMessageDialog(null, "Please select both Class and Subject!", "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String query = "UPDATE class_subject SET class_id = ?, subject_id = ? WHERE id = ?";

        try (
                Connection con = Prototype.getConnection(); PreparedStatement pst = con.prepareStatement(query)) {

            int classId = getClassId(selectedClass);
            int subjectId = getSubjectId(selectedSubject);

            pst.setInt(1, classId);
            pst.setInt(2, subjectId);
            pst.setInt(3, id);

            pst.executeUpdate();
            JOptionPane.showMessageDialog(null, "Assignment updated successfully!");
            loadAssignSubjectToTable();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void assignClassToSubject() {
        String selectedClass = (String) cmbClass.getSelectedItem();
        String selectedSubject = (String) cmbSubject.getSelectedItem();

        if (selectedClass.equals("Select Class") || selectedSubject.equals("Select Subject")) {
            JOptionPane.showMessageDialog(null, "Please select both Class and Subject!", "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String query = "INSERT INTO class_subject (class_id, subject_id) VALUES (?, ?)";

        try (
                Connection con = Prototype.getConnection(); PreparedStatement pst = con.prepareStatement(query)) {

            int classId = getClassId(selectedClass);
            int subjectId = getSubjectId(selectedSubject);

            pst.setInt(1, classId);
            pst.setInt(2, subjectId);

            pst.executeUpdate();
            JOptionPane.showMessageDialog(null, "Class assigned to subject successfully!");
            loadAssignSubjectToTable();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void loadAssignSubjectToTable() {
        DefaultTableModel model = (DefaultTableModel) tableAssignSub.getModel();
        model.setRowCount(0);

        String query = "SELECT cs.id, c.class_name, s.subject_name "
                + "FROM class_subject cs "
                + "JOIN classes c ON cs.class_id = c.class_id "
                + "JOIN subject s ON cs.subject_id = s.subject_id "
                + "ORDER BY cs.id DESC";

        try (
                Connection con = Prototype.getConnection(); Statement stmt = con.createStatement(); ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                Object[] row = {
                    rs.getInt("id"),
                    rs.getString("class_name"),
                    rs.getString("subject_name")
                };
                model.addRow(row);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void loadClassToComboBox() {
        cmbClass.removeAllItems();
        cmbClass.addItem("Select Class");

        String query = "SELECT class_id, class_name FROM classes ORDER BY class_name ASC";

        try (
                Connection con = Prototype.getConnection(); Statement stmt = con.createStatement(); ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                cmbClass.addItem(rs.getString("class_name"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void loadSubjectToComboBox() {
        cmbSubject.removeAllItems();
        cmbSubject.addItem("Select Subject");

        String query = "SELECT subject_id, subject_name FROM subject ORDER BY subject_name ASC";

        try (
                Connection con = Prototype.getConnection(); Statement stmt = con.createStatement(); ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                cmbSubject.addItem(rs.getString("subject_name"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void loadSubjectToTable() {
        DefaultTableModel model = (DefaultTableModel) tableSubject.getModel();
        model.setRowCount(0);

        String query = "SELECT * FROM subject ORDER BY subject_id DESC";

        try (
                Connection con = Prototype.getConnection(); Statement stmt = con.createStatement(); ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                Object[] row = {
                    rs.getInt("subject_id"),
                    rs.getString("subject_name"),};
                model.addRow(row);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void loadAttendanceData() {
        DefaultTableModel model = (DefaultTableModel) tableReport.getModel();
        model.setRowCount(0);

        try {
            con = Prototype.getConnection();
            PreparedStatement pst = con.prepareStatement("SELECT * FROM attendance_records ORDER BY students_name ASC");
            ResultSet rs = pst.executeQuery();

            while (rs.next()) {
                String studentName = rs.getString("students_name");
                String classId = rs.getString("class_id");
                String date = rs.getString("date");
                String timeIn = rs.getString("time_in");
                String timeOut = rs.getString("time_out");
                String status = rs.getString("status");

                model.addRow(new Object[]{studentName, classId, date, timeIn, timeOut, status});
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void loadClassesToTable() {
        DefaultTableModel model = (DefaultTableModel) tableClasses.getModel();
        model.setRowCount(0);

        String query = "SELECT * FROM classes ORDER BY class_id DESC";

        try (
                Connection con = Prototype.getConnection(); Statement stmt = con.createStatement(); ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                Object[] row = {
                    rs.getInt("class_id"),
                    rs.getString("class_name"),};
                model.addRow(row);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void loadDropOutToTable() {
        DefaultTableModel model = (DefaultTableModel) tableDropout.getModel();
        model.setRowCount(0);

        String query = "SELECT students_id, students_name, class_name, "
                + "parent_contact_number, qr_code_data, status FROM inactive_students ORDER BY students_id DESC";

        try (
                Connection con = Prototype.getConnection(); Statement stmt = con.createStatement(); ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                Object[] row = {
                    rs.getInt("students_id"),
                    rs.getString("students_name"),
                    rs.getString("class_name"),
                    rs.getString("parent_contact_number"),
                    rs.getString("qr_code_data"),
                    rs.getString("status")
                };
                model.addRow(row);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void loadClassIds() {
        try {
            con = Prototype.getConnection();
            PreparedStatement pst = con.prepareStatement("SELECT class_name FROM classes");
            ResultSet rs = pst.executeQuery();

            cmbClassId.removeAllItems();
            while (rs.next()) {
                cmbClassId.addItem(rs.getString("class_name"));
            }
        } catch (SQLException ex) {
            Logger.getLogger(User.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    private void loadClassIdsToQr() {
        try {
            con = Prototype.getConnection();
            PreparedStatement pst = con.prepareStatement("SELECT class_name FROM classes");
            ResultSet rs = pst.executeQuery();

            cboxNoQr.removeAllItems();
            cboxNoQr.addItem("Select All Classes");
            while (rs.next()) {
                cboxNoQr.addItem(rs.getString("class_name"));
            }
        } catch (SQLException ex) {
            Logger.getLogger(User.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void loadStudentsToTable() {
        DefaultTableModel model = (DefaultTableModel) tableStudent.getModel();
        model.setRowCount(0);

        String query = "SELECT s.students_id, s.students_name, c.class_name, "
                + "s.parent_contact_number, s.qr_code_data, s.status FROM students s "
                + "JOIN classes c ON s.class_id = c.class_id ORDER BY students_name ASC";

        try (
                Connection con = Prototype.getConnection(); Statement stmt = con.createStatement(); ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                Object[] row = {
                    rs.getInt("students_id"),
                    rs.getString("students_name"),
                    rs.getString("class_name"),
                    rs.getString("parent_contact_number"),
                    rs.getString("qr_code_data"),
                    rs.getString("status")
                };
                model.addRow(row);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void fetchClassesData(int class_id) {
        String query = "SELECT * FROM classes WHERE class_id = ?";

        try {
            con = Prototype.getConnection();
            PreparedStatement pst = con.prepareStatement(query);

            pst.setInt(1, class_id);
            ResultSet rs = pst.executeQuery();

            if (rs.next()) {
                String className = rs.getString("class_name");

                
                String[] parts = className.split(" - ");

                if (parts.length == 3) {
                    
                    
                   

                    String grade = parts[0].replace("GRADE ", "").trim();
                    String section = parts[1].trim();
                    String schoolYear = parts[2].replace("SY. ", "").trim();

                    comboGrade.setSelectedItem(grade);
                    txtSection.setText(section);
                    txtSchoolYear.setText(schoolYear);
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    private void fetchStudentsData(int students_id) {
        String query = "SELECT s.students_id, s.students_name, c.class_name, "
                + "s.parent_contact_number, s.qr_code_data FROM students s "
                + "JOIN classes c ON s.class_id = c.class_id WHERE s.students_id = ?";

        try {
            con = Prototype.getConnection();
            PreparedStatement pst = con.prepareStatement(query);

            pst.setInt(1, students_id);
            ResultSet rs = pst.executeQuery();

            if (rs.next()) {
                txtFname.setText(rs.getString("students_name"));
                cmbClassId.setSelectedItem(rs.getString("class_name"));
                txtNumber.setText(rs.getString("parent_contact_number"));
                txtQrData.setText(rs.getString("qr_code_data"));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void fetchSubjectData(int subjectId) {
        String query = "SELECT * FROM subject WHERE subject_id = ?";

        try {
            con = Prototype.getConnection();
            PreparedStatement pst = con.prepareStatement(query);

            pst.setInt(1, subjectId);
            ResultSet rs = pst.executeQuery();

            if (rs.next()) {
                Subjectname.setText(rs.getString("subject_name"));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void fetchUserData(int userId) {
        String query = "SELECT * FROM login_table WHERE user_id = ?";

        try {
            con = Prototype.getConnection();
            PreparedStatement pst = con.prepareStatement(query);

            pst.setInt(1, userId);
            ResultSet rs = pst.executeQuery();

            if (rs.next()) {
                fname.setText(rs.getString("firstname"));
                mname.setText(rs.getString("middlename"));
                lname.setText(rs.getString("lastname"));
                gend.setText(rs.getString("gender"));
                em.setText(rs.getString("email"));
                contactnumber.setText(rs.getString("contact"));
                user.setText(rs.getString("username"));
                pass.setText(rs.getString("password"));
                jComboRole.setSelectedItem(rs.getString("role"));

                String ageValue = rs.getString("age");

                try {
                    int age = Integer.parseInt(ageValue);
                    ages.setText(String.valueOf(age));
                } catch (NumberFormatException e) {
                    ages.setText("");
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void fetchTeachersData(int teacherId) {
        String query = "SELECT * FROM teachers WHERE teacher_id = ?";

        try {
            con = Prototype.getConnection();
            PreparedStatement pst = con.prepareStatement(query);

            pst.setInt(1, teacherId);
            ResultSet rs = pst.executeQuery();

            if (rs.next()) {
                Tcfname.setText(rs.getString("teacher_name"));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void loadTeachersToTable() {
        DefaultTableModel model = (DefaultTableModel) tableTeachers.getModel();
        model.setRowCount(0);

        String query = "SELECT teacher_id ,teacher_name FROM teachers ORDER BY teacher_id DESC";

        try (
                Connection con = Prototype.getConnection(); Statement stmt = con.createStatement(); ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                Object[] row = {
                    rs.getInt("teacher_id"),
                    rs.getString("teacher_name"),};
                model.addRow(row);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void loadUsersToTable() {
        DefaultTableModel model = (DefaultTableModel) tableAccount.getModel();
        model.setRowCount(0);

        String query = "SELECT user_id, email, contact, username, role FROM login_table ORDER BY user_id DESC";

        try (
                Connection con = Prototype.getConnection(); Statement stmt = con.createStatement(); ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                Object[] row = {
                    rs.getInt("user_id"),
                    rs.getString("email"),
                    rs.getString("contact"),
                    rs.getString("username"),
                    rs.getString("role")
                };
                model.addRow(row);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void loadDashboardData() {
        try {
            Connection con = Prototype.getConnection();

            PreparedStatement pst1 = con.prepareStatement("SELECT COUNT(*) FROM students");
            ResultSet rs1 = pst1.executeQuery();
            if (rs1.next()) {
                lblTotalStudents.setText(rs1.getString(1));
            }

            PreparedStatement pst2 = con.prepareStatement("SELECT COUNT(*) FROM teachers");
            ResultSet rs2 = pst2.executeQuery();
            if (rs2.next()) {
                lblTotalTeachers.setText(rs2.getString(1));
            }


            PreparedStatement pst4 = con.prepareStatement("SELECT COUNT(*) FROM inactive_students");
            ResultSet rs4 = pst4.executeQuery();
            if (rs4.next()) {
                lblInactive.setText(rs4.getString(1));
            }

//            PreparedStatement pst3 = con.prepareStatement("SELECT COUNT(*) FROM login_table");
//            ResultSet rs3 = pst3.executeQuery();
//            if (rs3.next()) {
//                lblTotalAccounts.setText(rs3.getString(1));
//            }
            rs1.close();
            rs2.close();
            rs4.close();
            rs4.close();
            pst1.close();
            pst2.close();
            pst4.close();
            con.close();

        } catch (SQLException ex) {
            Logger.getLogger(User.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        Header = new javax.swing.JPanel();
        AccountHeader = new javax.swing.JPanel();
        jLabel3 = new javax.swing.JLabel();
        jSeparator1 = new javax.swing.JSeparator();
        HeaderPNG = new javax.swing.JPanel();
        lblTimeNow = new javax.swing.JLabel();
        lblDateToday = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel10 = new javax.swing.JLabel();
        jLabel1 = new javax.swing.JLabel();
        Navbar = new javax.swing.JPanel();
        Main = new javax.swing.JPanel();
        jSeparator2 = new javax.swing.JSeparator();
        jDashboard = new javax.swing.JPanel();
        BDashboard = new javax.swing.JLabel();
        jStudents = new javax.swing.JPanel();
        BStudents = new javax.swing.JLabel();
        jClasses = new javax.swing.JPanel();
        BClasses = new javax.swing.JLabel();
        jReports = new javax.swing.JPanel();
        BReports = new javax.swing.JLabel();
        jLogout = new javax.swing.JPanel();
        jNotification = new javax.swing.JPanel();
        BNotification = new javax.swing.JLabel();
        jQrCodeGen = new javax.swing.JPanel();
        BQrCodeGen = new javax.swing.JLabel();
        jSubjects = new javax.swing.JPanel();
        BSubject = new javax.swing.JLabel();
        jAssignSub = new javax.swing.JPanel();
        BAssignSub = new javax.swing.JLabel();
        jAssignTc = new javax.swing.JPanel();
        BAssignTc = new javax.swing.JLabel();
        jTeachers = new javax.swing.JPanel();
        BTeachers = new javax.swing.JLabel();
        jDropOut = new javax.swing.JPanel();
        bDropOut = new javax.swing.JLabel();
        jSettings = new javax.swing.JPanel();
        bSettings = new javax.swing.JLabel();
        jAttendance = new javax.swing.JPanel();
        bAttendance = new javax.swing.JLabel();
        LOGOUT = new javax.swing.JPanel();
        BLogout = new javax.swing.JLabel();
        TermsAndCondition = new javax.swing.JPanel();
        jLabel14 = new javax.swing.JLabel();
        MainPanel = new javax.swing.JPanel();
        jTabbedPane1 = jTabbedPane1 = new NoTabHeaderTabbedPane();
        ;
        iDashboard = new javax.swing.JPanel();
        PInactive = new javax.swing.JPanel();
        lblInactive = new javax.swing.JLabel();
        jLabel51 = new javax.swing.JLabel();
        PClassses = new javax.swing.JPanel();
        lblTotalTeachers = new javax.swing.JLabel();
        jLabel49 = new javax.swing.JLabel();
        PStudent = new javax.swing.JPanel();
        lblTotalStudents = new javax.swing.JLabel();
        jLabel48 = new javax.swing.JLabel();
        jScrollPane3 = new javax.swing.JScrollPane();
        tableSms = new javax.swing.JTable();
        jPanel9 = new javax.swing.JPanel();
        jLabel11 = new javax.swing.JLabel();
        filler16 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 0), new java.awt.Dimension(0, 0), new java.awt.Dimension(32767, 32767));
        iAttendance = new javax.swing.JPanel();
        jLabel35 = new javax.swing.JLabel();
        TxtQrcode = new javax.swing.JTextField();
        lblStatus = new javax.swing.JLabel();
        btnTimeOut = new javax.swing.JButton();
        jScrollPane17 = new javax.swing.JScrollPane();
        tableAttendance = new javax.swing.JTable();
        jButton13 = new javax.swing.JButton();
        cboxclass = new javax.swing.JComboBox<>();
        jLabel36 = new javax.swing.JLabel();
        iStudents = new javax.swing.JPanel();
        btnAdd1 = new javax.swing.JButton();
        btnUpdate1 = new javax.swing.JButton();
        btnDelete1 = new javax.swing.JButton();
        jScrollPane4 = new javax.swing.JScrollPane();
        tableStudent = new javax.swing.JTable();
        jFirstname1 = new javax.swing.JLabel();
        jMiddlename1 = new javax.swing.JLabel();
        txtNumber = new javax.swing.JTextField();
        jLastname1 = new javax.swing.JLabel();
        jGender1 = new javax.swing.JLabel();
        cmbClassId = new javax.swing.JComboBox<>();
        txtFname = new javax.swing.JTextField();
        jButton9 = new javax.swing.JButton();
        jButton12 = new javax.swing.JButton();
        lblStudentCount = new javax.swing.JLabel();
        jPanel3 = new javax.swing.JPanel();
        jLabel37 = new javax.swing.JLabel();
        txtSearch1 = new javax.swing.JTextField();
        jLabel21 = new javax.swing.JLabel();
        filler17 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 0), new java.awt.Dimension(0, 0), new java.awt.Dimension(32767, 32767));
        filler18 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 0), new java.awt.Dimension(0, 0), new java.awt.Dimension(32767, 32767));
        txtQrData = new javax.swing.JLabel();
        iClasses = new javax.swing.JPanel();
        jScrollPane8 = new javax.swing.JScrollPane();
        tableClasses = new javax.swing.JTable();
        jLabel13 = new javax.swing.JLabel();
        txtSchoolYear = new javax.swing.JTextField();
        btnAdd4 = new javax.swing.JButton();
        btnUpdate4 = new javax.swing.JButton();
        btnDelete4 = new javax.swing.JButton();
        jLabel39 = new javax.swing.JLabel();
        txtSearch4 = new javax.swing.JTextField();
        jLabel24 = new javax.swing.JLabel();
        filler7 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 0), new java.awt.Dimension(0, 0), new java.awt.Dimension(32767, 32767));
        jLabel50 = new javax.swing.JLabel();
        txtSection = new javax.swing.JTextField();
        jLabel52 = new javax.swing.JLabel();
        filler6 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 0), new java.awt.Dimension(0, 0), new java.awt.Dimension(32767, 32767));
        comboGrade = new javax.swing.JComboBox<>();
        iReports = new javax.swing.JPanel();
        jLabel15 = new javax.swing.JLabel();
        dateChooserFromDate = new com.toedter.calendar.JDateChooser();
        jLabel16 = new javax.swing.JLabel();
        dateChooserEndDate = new com.toedter.calendar.JDateChooser();
        jScrollPane5 = new javax.swing.JScrollPane();
        tableReport = new javax.swing.JTable();
        jButton4 = new javax.swing.JButton();
        jLabel18 = new javax.swing.JLabel();
        jLabel19 = new javax.swing.JLabel();
        jLabel20 = new javax.swing.JLabel();
        txtStudentName = new javax.swing.JTextField();
        jButton7 = new javax.swing.JButton();
        txtStatus = new javax.swing.JTextField();
        jLabel28 = new javax.swing.JLabel();
        cboxClassReports = new javax.swing.JComboBox<>();
        filler4 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 0), new java.awt.Dimension(0, 0), new java.awt.Dimension(32767, 32767));
        filler5 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 0), new java.awt.Dimension(0, 0), new java.awt.Dimension(32767, 32767));
        iNotification = new javax.swing.JPanel();
        jScrollPane12 = new javax.swing.JScrollPane();
        tableSms1 = new javax.swing.JTable();
        jLabel30 = new javax.swing.JLabel();
        numberTextField = new javax.swing.JTextField();
        jLabel31 = new javax.swing.JLabel();
        jScrollPane13 = new javax.swing.JScrollPane();
        messageTextArea = new javax.swing.JTextArea();
        bSend = new javax.swing.JButton();
        filler3 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 0), new java.awt.Dimension(0, 0), new java.awt.Dimension(32767, 32767));
        iUsers = new javax.swing.JPanel();
        btnAdd = new javax.swing.JButton();
        btnUpdate = new javax.swing.JButton();
        btnDelete = new javax.swing.JButton();
        jLabel4 = new javax.swing.JLabel();
        jScrollPane2 = new javax.swing.JScrollPane();
        tableAccount = new javax.swing.JTable();
        jPanel1 = new javax.swing.JPanel();
        fname = new javax.swing.JTextField();
        jFirstname = new javax.swing.JLabel();
        jMiddlename = new javax.swing.JLabel();
        mname = new javax.swing.JTextField();
        lname = new javax.swing.JTextField();
        jLastname = new javax.swing.JLabel();
        jGender = new javax.swing.JLabel();
        gend = new javax.swing.JTextField();
        em = new javax.swing.JTextField();
        jLabel5 = new javax.swing.JLabel();
        jPanel2 = new javax.swing.JPanel();
        ages = new javax.swing.JTextField();
        jAge = new javax.swing.JLabel();
        jContact = new javax.swing.JLabel();
        contactnumber = new javax.swing.JTextField();
        user = new javax.swing.JTextField();
        jUsername = new javax.swing.JLabel();
        jPassword = new javax.swing.JLabel();
        pass = new javax.swing.JTextField();
        jComboRole = new javax.swing.JComboBox<>();
        jRole = new javax.swing.JLabel();
        txtSearch = new javax.swing.JTextField();
        jLabel40 = new javax.swing.JLabel();
        iTeachers = new javax.swing.JPanel();
        jScrollPane6 = new javax.swing.JScrollPane();
        tableTeachers = new javax.swing.JTable();
        jLabel6 = new javax.swing.JLabel();
        Tcfname = new javax.swing.JTextField();
        txtSearch2 = new javax.swing.JTextField();
        jLabel22 = new javax.swing.JLabel();
        btnAdd2 = new javax.swing.JButton();
        btnUpdate2 = new javax.swing.JButton();
        btnDelete2 = new javax.swing.JButton();
        jLabel41 = new javax.swing.JLabel();
        filler8 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 0), new java.awt.Dimension(0, 0), new java.awt.Dimension(32767, 32767));
        filler9 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 0), new java.awt.Dimension(0, 0), new java.awt.Dimension(32767, 32767));
        iSubjects = new javax.swing.JPanel();
        jScrollPane7 = new javax.swing.JScrollPane();
        tableSubject = new javax.swing.JTable();
        jLabel12 = new javax.swing.JLabel();
        Subjectname = new javax.swing.JTextField();
        btnAdd3 = new javax.swing.JButton();
        btnUpdate3 = new javax.swing.JButton();
        btnDelete3 = new javax.swing.JButton();
        txtSearch7 = new javax.swing.JTextField();
        jLabel38 = new javax.swing.JLabel();
        jLabel42 = new javax.swing.JLabel();
        filler10 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 0), new java.awt.Dimension(0, 0), new java.awt.Dimension(32767, 32767));
        filler11 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 0), new java.awt.Dimension(0, 0), new java.awt.Dimension(32767, 32767));
        iAssignTc = new javax.swing.JPanel();
        jScrollPane10 = new javax.swing.JScrollPane();
        tableAssignTeacher = new javax.swing.JTable();
        jLabel26 = new javax.swing.JLabel();
        jLabel27 = new javax.swing.JLabel();
        cmbClass1 = new javax.swing.JComboBox<>();
        cmbsubject1 = new javax.swing.JComboBox<>();
        jButton6 = new javax.swing.JButton();
        jButton8 = new javax.swing.JButton();
        jButton10 = new javax.swing.JButton();
        txtSearch3 = new javax.swing.JTextField();
        jLabel23 = new javax.swing.JLabel();
        jLabel43 = new javax.swing.JLabel();
        filler12 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 0), new java.awt.Dimension(0, 0), new java.awt.Dimension(32767, 32767));
        filler13 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 0), new java.awt.Dimension(0, 0), new java.awt.Dimension(32767, 32767));
        iAssignSub = new javax.swing.JPanel();
        jScrollPane9 = new javax.swing.JScrollPane();
        tableAssignSub = new javax.swing.JTable();
        jLabel17 = new javax.swing.JLabel();
        jLabel25 = new javax.swing.JLabel();
        cmbClass = new javax.swing.JComboBox<>();
        cmbSubject = new javax.swing.JComboBox<>();
        jButton2 = new javax.swing.JButton();
        jButton3 = new javax.swing.JButton();
        jButton5 = new javax.swing.JButton();
        txtSearch5 = new javax.swing.JTextField();
        jLabel29 = new javax.swing.JLabel();
        jLabel44 = new javax.swing.JLabel();
        filler14 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 0), new java.awt.Dimension(0, 0), new java.awt.Dimension(32767, 32767));
        filler15 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 0), new java.awt.Dimension(0, 0), new java.awt.Dimension(32767, 32767));
        iDropout = new javax.swing.JPanel();
        jScrollPane14 = new javax.swing.JScrollPane();
        tableDropout = new javax.swing.JTable();
        jButton11 = new javax.swing.JButton();
        txtSearch6 = new javax.swing.JTextField();
        jLabel32 = new javax.swing.JLabel();
        jLabel45 = new javax.swing.JLabel();
        iQrCodeGen = new javax.swing.JPanel();
        jLabel9 = new javax.swing.JLabel();
        jButton1 = new javax.swing.JButton();
        jPanel6 = new javax.swing.JPanel();
        qrCodeLabel = new javax.swing.JLabel();
        jScrollPane15 = new javax.swing.JScrollPane();
        tableWithQR = new javax.swing.JTable();
        jScrollPane16 = new javax.swing.JScrollPane();
        tableWithoutQR = new javax.swing.JTable();
        jLabel33 = new javax.swing.JLabel();
        jLabel34 = new javax.swing.JLabel();
        lblQrData = new javax.swing.JLabel();
        jLabel46 = new javax.swing.JLabel();
        txtSearch8 = new javax.swing.JTextField();
        jLabel47 = new javax.swing.JLabel();
        jButton14 = new javax.swing.JButton();
        filler1 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 0), new java.awt.Dimension(0, 0), new java.awt.Dimension(32767, 32767));
        filler2 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 0), new java.awt.Dimension(0, 0), new java.awt.Dimension(32767, 32767));
        cboxNoQr = new javax.swing.JComboBox<>();
        iSettings = new javax.swing.JPanel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setUndecorated(true);

        Header.setBackground(new java.awt.Color(255, 102, 102));
        Header.setForeground(new java.awt.Color(255, 153, 153));
        Header.setLayout(new java.awt.BorderLayout());

        AccountHeader.setBackground(new java.awt.Color(0, 0, 102));
        AccountHeader.setPreferredSize(new java.awt.Dimension(233, 130));
        AccountHeader.setLayout(new java.awt.BorderLayout());

        jLabel3.setBackground(new java.awt.Color(255, 255, 255));
        jLabel3.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        jLabel3.setForeground(new java.awt.Color(255, 255, 255));
        jLabel3.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel3.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/navbar/FOR ACCOUNT.png"))); // NOI18N
        jLabel3.setText("Teacher");
        jLabel3.setToolTipText("");
        jLabel3.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jLabel3.setIconTextGap(15);
        jLabel3.setPreferredSize(new java.awt.Dimension(90, 80));
        jLabel3.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        AccountHeader.add(jLabel3, java.awt.BorderLayout.CENTER);

        jSeparator1.setOrientation(javax.swing.SwingConstants.VERTICAL);
        AccountHeader.add(jSeparator1, java.awt.BorderLayout.LINE_END);

        Header.add(AccountHeader, java.awt.BorderLayout.LINE_START);

        HeaderPNG.setPreferredSize(new java.awt.Dimension(1687, 130));
        HeaderPNG.setLayout(new java.awt.GridBagLayout());

        lblTimeNow.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        lblTimeNow.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHEAST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(30, 0, 0, 5);
        HeaderPNG.add(lblTimeNow, gridBagConstraints);

        lblDateToday.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        lblDateToday.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHEAST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 0, 0, 5);
        HeaderPNG.add(lblDateToday, gridBagConstraints);

        jLabel2.setFont(new java.awt.Font("Tahoma", 1, 30)); // NOI18N
        jLabel2.setForeground(new java.awt.Color(255, 255, 255));
        jLabel2.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel2.setText("STUDENT ATTENDANCE                    MONITORING SYSTEM");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        HeaderPNG.add(jLabel2, gridBagConstraints);

        jLabel10.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/header/LOGO.png"))); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.VERTICAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 15, 0, 0);
        HeaderPNG.add(jLabel10, gridBagConstraints);

        jLabel1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/header/NOLOGO.png"))); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.ipadx = -23;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        HeaderPNG.add(jLabel1, gridBagConstraints);

        Header.add(HeaderPNG, java.awt.BorderLayout.CENTER);

        getContentPane().add(Header, java.awt.BorderLayout.PAGE_START);

        Navbar.setBackground(new java.awt.Color(0, 0, 102));
        Navbar.setLayout(new java.awt.BorderLayout(0, 10));

        Main.setBackground(new java.awt.Color(0, 0, 102));

        jDashboard.setBackground(new java.awt.Color(0, 0, 102));
        jDashboard.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        jDashboard.setLayout(new java.awt.BorderLayout());

        BDashboard.setBackground(new java.awt.Color(0, 0, 102));
        BDashboard.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        BDashboard.setForeground(new java.awt.Color(255, 255, 255));
        BDashboard.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        BDashboard.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/navbar/dashboard.png"))); // NOI18N
        BDashboard.setText("Dashboard");
        BDashboard.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        BDashboard.setIconTextGap(25);
        BDashboard.setOpaque(true);
        BDashboard.setRequestFocusEnabled(false);
        BDashboard.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                BDashboardMouseClicked(evt);
            }
        });
        jDashboard.add(BDashboard, java.awt.BorderLayout.CENTER);

        jStudents.setBackground(new java.awt.Color(0, 0, 102));
        jStudents.setLayout(new java.awt.BorderLayout());

        BStudents.setBackground(new java.awt.Color(0, 0, 102));
        BStudents.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        BStudents.setForeground(new java.awt.Color(255, 255, 255));
        BStudents.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        BStudents.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/navbar/students.png"))); // NOI18N
        BStudents.setText("Manage Students");
        BStudents.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        BStudents.setIconTextGap(25);
        BStudents.setOpaque(true);
        BStudents.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                BStudentsMouseClicked(evt);
            }
        });
        jStudents.add(BStudents, java.awt.BorderLayout.CENTER);

        jClasses.setBackground(new java.awt.Color(0, 0, 102));
        jClasses.setLayout(new java.awt.BorderLayout());

        BClasses.setBackground(new java.awt.Color(0, 0, 102));
        BClasses.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        BClasses.setForeground(new java.awt.Color(255, 255, 255));
        BClasses.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        BClasses.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/navbar/Classes.png"))); // NOI18N
        BClasses.setText("Classes/Sections");
        BClasses.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        BClasses.setIconTextGap(25);
        BClasses.setOpaque(true);
        BClasses.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                BClassesMouseClicked(evt);
            }
        });
        jClasses.add(BClasses, java.awt.BorderLayout.CENTER);

        jReports.setBackground(new java.awt.Color(0, 0, 102));
        jReports.setLayout(new java.awt.BorderLayout());

        BReports.setBackground(new java.awt.Color(0, 0, 102));
        BReports.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        BReports.setForeground(new java.awt.Color(255, 255, 255));
        BReports.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        BReports.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/navbar/summarize.png"))); // NOI18N
        BReports.setText("Generate Reports");
        BReports.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        BReports.setIconTextGap(25);
        BReports.setOpaque(true);
        BReports.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                BReportsMouseClicked(evt);
            }
        });
        jReports.add(BReports, java.awt.BorderLayout.CENTER);

        jLogout.setBackground(new java.awt.Color(0, 0, 102));
        jLogout.setLayout(new java.awt.BorderLayout());

        jNotification.setBackground(new java.awt.Color(0, 0, 102));
        jNotification.setLayout(new java.awt.BorderLayout());

        BNotification.setBackground(new java.awt.Color(0, 0, 102));
        BNotification.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        BNotification.setForeground(new java.awt.Color(255, 255, 255));
        BNotification.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        BNotification.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/navbar/notification.png"))); // NOI18N
        BNotification.setText("Notifications");
        BNotification.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        BNotification.setIconTextGap(25);
        BNotification.setOpaque(true);
        BNotification.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                BNotificationMouseClicked(evt);
            }
        });
        jNotification.add(BNotification, java.awt.BorderLayout.CENTER);

        jQrCodeGen.setBackground(new java.awt.Color(0, 0, 102));
        jQrCodeGen.setLayout(new java.awt.BorderLayout());

        BQrCodeGen.setBackground(new java.awt.Color(0, 0, 102));
        BQrCodeGen.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        BQrCodeGen.setForeground(new java.awt.Color(255, 255, 255));
        BQrCodeGen.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        BQrCodeGen.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/navbar/qrcode.png"))); // NOI18N
        BQrCodeGen.setText("Qr-Code Generate");
        BQrCodeGen.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        BQrCodeGen.setIconTextGap(25);
        BQrCodeGen.setOpaque(true);
        BQrCodeGen.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                BQrCodeGenMouseClicked(evt);
            }
        });
        jQrCodeGen.add(BQrCodeGen, java.awt.BorderLayout.CENTER);

        jSubjects.setBackground(new java.awt.Color(0, 0, 102));
        jSubjects.setLayout(new java.awt.BorderLayout());

        BSubject.setBackground(new java.awt.Color(0, 0, 102));
        BSubject.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        BSubject.setForeground(new java.awt.Color(255, 255, 255));
        BSubject.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        BSubject.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/navbar/Subject.png"))); // NOI18N
        BSubject.setText("Subjects");
        BSubject.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        BSubject.setIconTextGap(25);
        BSubject.setOpaque(true);
        BSubject.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                BSubjectMouseClicked(evt);
            }
        });
        jSubjects.add(BSubject, java.awt.BorderLayout.CENTER);

        jAssignSub.setBackground(new java.awt.Color(0, 0, 102));
        jAssignSub.setLayout(new java.awt.BorderLayout());

        BAssignSub.setBackground(new java.awt.Color(0, 0, 102));
        BAssignSub.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        BAssignSub.setForeground(new java.awt.Color(255, 255, 255));
        BAssignSub.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        BAssignSub.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/navbar/assign_teacher.png"))); // NOI18N
        BAssignSub.setText("Assign Subjects");
        BAssignSub.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        BAssignSub.setIconTextGap(25);
        BAssignSub.setOpaque(true);
        BAssignSub.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                BAssignSubMouseClicked(evt);
            }
        });
        jAssignSub.add(BAssignSub, java.awt.BorderLayout.CENTER);

        jAssignTc.setBackground(new java.awt.Color(0, 0, 102));
        jAssignTc.setLayout(new java.awt.BorderLayout());

        BAssignTc.setBackground(new java.awt.Color(0, 0, 102));
        BAssignTc.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        BAssignTc.setForeground(new java.awt.Color(255, 255, 255));
        BAssignTc.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        BAssignTc.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/navbar/assign_subject.png"))); // NOI18N
        BAssignTc.setText("Assign Teachers");
        BAssignTc.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        BAssignTc.setIconTextGap(25);
        BAssignTc.setOpaque(true);
        BAssignTc.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                BAssignTcMouseClicked(evt);
            }
        });
        jAssignTc.add(BAssignTc, java.awt.BorderLayout.CENTER);

        jTeachers.setBackground(new java.awt.Color(0, 0, 102));
        jTeachers.setLayout(new java.awt.BorderLayout());

        BTeachers.setBackground(new java.awt.Color(0, 0, 102));
        BTeachers.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        BTeachers.setForeground(new java.awt.Color(255, 255, 255));
        BTeachers.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        BTeachers.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/navbar/Manage_teacher.png"))); // NOI18N
        BTeachers.setText("Manage Teachers");
        BTeachers.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        BTeachers.setIconTextGap(25);
        BTeachers.setOpaque(true);
        BTeachers.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                BTeachersMouseClicked(evt);
            }
        });
        jTeachers.add(BTeachers, java.awt.BorderLayout.CENTER);

        jDropOut.setBackground(new java.awt.Color(0, 0, 102));
        jDropOut.setLayout(new java.awt.BorderLayout());

        bDropOut.setBackground(new java.awt.Color(0, 0, 102));
        bDropOut.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        bDropOut.setForeground(new java.awt.Color(255, 255, 255));
        bDropOut.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        bDropOut.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/navbar/dropout.png"))); // NOI18N
        bDropOut.setText("Inactive Students");
        bDropOut.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        bDropOut.setIconTextGap(25);
        bDropOut.setOpaque(true);
        bDropOut.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                bDropOutMouseClicked(evt);
            }
        });
        jDropOut.add(bDropOut, java.awt.BorderLayout.CENTER);

        jSettings.setBackground(new java.awt.Color(0, 0, 102));
        jSettings.setLayout(new java.awt.BorderLayout());

        bSettings.setBackground(new java.awt.Color(0, 0, 102));
        bSettings.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        bSettings.setForeground(new java.awt.Color(255, 255, 255));
        bSettings.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        bSettings.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/navbar/Settings.png"))); // NOI18N
        bSettings.setText("Settings");
        bSettings.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        bSettings.setIconTextGap(25);
        bSettings.setOpaque(true);
        bSettings.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                bSettingsMouseClicked(evt);
            }
        });
        jSettings.add(bSettings, java.awt.BorderLayout.CENTER);

        jAttendance.setBackground(new java.awt.Color(0, 0, 102));
        jAttendance.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        jAttendance.setLayout(new java.awt.BorderLayout());

        bAttendance.setBackground(new java.awt.Color(0, 0, 102));
        bAttendance.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        bAttendance.setForeground(new java.awt.Color(255, 255, 255));
        bAttendance.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        bAttendance.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/navbar/Check.png"))); // NOI18N
        bAttendance.setText("Mark Attendance");
        bAttendance.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        bAttendance.setIconTextGap(25);
        bAttendance.setOpaque(true);
        bAttendance.setRequestFocusEnabled(false);
        bAttendance.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                bAttendanceMouseClicked(evt);
            }
        });
        jAttendance.add(bAttendance, java.awt.BorderLayout.CENTER);

        LOGOUT.setBackground(new java.awt.Color(0, 0, 102));
        LOGOUT.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                LOGOUTMouseClicked(evt);
            }
        });
        LOGOUT.setLayout(new java.awt.BorderLayout());

        BLogout.setBackground(new java.awt.Color(0, 0, 102));
        BLogout.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        BLogout.setForeground(new java.awt.Color(255, 255, 255));
        BLogout.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        BLogout.setText("LOG OUT");
        BLogout.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        BLogout.setOpaque(true);
        BLogout.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                BLogoutMouseClicked(evt);
            }
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                BLogoutMouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                BLogoutMouseExited(evt);
            }
        });
        LOGOUT.add(BLogout, java.awt.BorderLayout.CENTER);

        TermsAndCondition.setBackground(new java.awt.Color(0, 0, 102));
        TermsAndCondition.setLayout(new java.awt.BorderLayout());

        jLabel14.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jLabel14.setForeground(new java.awt.Color(255, 255, 255));
        jLabel14.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel14.setText("Terms And Conditions");
        jLabel14.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        jLabel14.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jLabel14MouseClicked(evt);
            }
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                jLabel14MouseEntered(evt);
            }
        });
        TermsAndCondition.add(jLabel14, java.awt.BorderLayout.CENTER);

        javax.swing.GroupLayout MainLayout = new javax.swing.GroupLayout(Main);
        Main.setLayout(MainLayout);
        MainLayout.setHorizontalGroup(
            MainLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jSeparator2, javax.swing.GroupLayout.DEFAULT_SIZE, 233, Short.MAX_VALUE)
            .addGroup(MainLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addComponent(jDashboard, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jAttendance, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jStudents, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jTeachers, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jClasses, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jSubjects, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jAssignSub, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jAssignTc, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jReports, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jQrCodeGen, javax.swing.GroupLayout.DEFAULT_SIZE, 233, Short.MAX_VALUE)
                .addComponent(jNotification, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jDropOut, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, MainLayout.createSequentialGroup()
                    .addContainerGap()
                    .addComponent(jLogout, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addContainerGap())
                .addComponent(LOGOUT, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(TermsAndCondition, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jSettings, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        MainLayout.setVerticalGroup(
            MainLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(MainLayout.createSequentialGroup()
                .addComponent(jSeparator2, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(MainLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(MainLayout.createSequentialGroup()
                    .addContainerGap()
                    .addComponent(jDashboard, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                    .addComponent(jAttendance, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                    .addComponent(jStudents, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                    .addComponent(jTeachers, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                    .addComponent(jClasses, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                    .addComponent(jSubjects, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                    .addComponent(jAssignSub, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                    .addComponent(jAssignTc, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                    .addComponent(jReports, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                    .addComponent(jQrCodeGen, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                    .addComponent(jNotification, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                    .addComponent(jDropOut, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                    .addComponent(jSettings, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLogout, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                    .addComponent(LOGOUT, javax.swing.GroupLayout.PREFERRED_SIZE, 29, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                    .addComponent(TermsAndCondition, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addContainerGap()))
        );

        Navbar.add(Main, java.awt.BorderLayout.CENTER);

        getContentPane().add(Navbar, java.awt.BorderLayout.LINE_START);

        MainPanel.setPreferredSize(new java.awt.Dimension(2123, 566));
        MainPanel.setLayout(new java.awt.BorderLayout());

        iDashboard.setBackground(new java.awt.Color(255, 255, 255));
        iDashboard.setLayout(new java.awt.GridBagLayout());

        PInactive.setBackground(new java.awt.Color(0, 0, 102));
        PInactive.setPreferredSize(new java.awt.Dimension(300, 130));

        lblInactive.setFont(new java.awt.Font("Tahoma", 1, 48)); // NOI18N
        lblInactive.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblInactive.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        lblInactive.setOpaque(true);
        lblInactive.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                lblInactiveMouseClicked(evt);
            }
        });

        jLabel51.setFont(new java.awt.Font("Tahoma", 1, 24)); // NOI18N
        jLabel51.setForeground(new java.awt.Color(255, 255, 255));
        jLabel51.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel51.setText("TOTAL INACTIVE");

        javax.swing.GroupLayout PInactiveLayout = new javax.swing.GroupLayout(PInactive);
        PInactive.setLayout(PInactiveLayout);
        PInactiveLayout.setHorizontalGroup(
            PInactiveLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(lblInactive, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(jLabel51, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        PInactiveLayout.setVerticalGroup(
            PInactiveLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, PInactiveLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel51)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(lblInactive, javax.swing.GroupLayout.PREFERRED_SIZE, 94, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.ipadx = 60;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 0.1;
        gridBagConstraints.insets = new java.awt.Insets(0, 40, 0, 0);
        iDashboard.add(PInactive, gridBagConstraints);

        PClassses.setBackground(new java.awt.Color(0, 0, 102));
        PClassses.setPreferredSize(new java.awt.Dimension(300, 130));

        lblTotalTeachers.setFont(new java.awt.Font("Tahoma", 1, 48)); // NOI18N
        lblTotalTeachers.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblTotalTeachers.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        lblTotalTeachers.setOpaque(true);
        lblTotalTeachers.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                lblTotalTeachersMouseClicked(evt);
            }
        });

        jLabel49.setFont(new java.awt.Font("Tahoma", 1, 24)); // NOI18N
        jLabel49.setForeground(new java.awt.Color(255, 255, 255));
        jLabel49.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel49.setText("TOTAL TEACHERS");

        javax.swing.GroupLayout PClasssesLayout = new javax.swing.GroupLayout(PClassses);
        PClassses.setLayout(PClasssesLayout);
        PClasssesLayout.setHorizontalGroup(
            PClasssesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(lblTotalTeachers, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(jLabel49, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        PClasssesLayout.setVerticalGroup(
            PClasssesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, PClasssesLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel49)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(lblTotalTeachers, javax.swing.GroupLayout.PREFERRED_SIZE, 94, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.ipadx = 60;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 0.1;
        gridBagConstraints.insets = new java.awt.Insets(0, 40, 0, 0);
        iDashboard.add(PClassses, gridBagConstraints);

        PStudent.setBackground(new java.awt.Color(0, 0, 102));
        PStudent.setPreferredSize(new java.awt.Dimension(300, 130));

        lblTotalStudents.setFont(new java.awt.Font("Tahoma", 1, 48)); // NOI18N
        lblTotalStudents.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblTotalStudents.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        lblTotalStudents.setOpaque(true);
        lblTotalStudents.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                lblTotalStudentsMouseClicked(evt);
            }
        });

        jLabel48.setFont(new java.awt.Font("Tahoma", 1, 24)); // NOI18N
        jLabel48.setForeground(new java.awt.Color(255, 255, 255));
        jLabel48.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel48.setText("TOTAL STUDENTS");
        jLabel48.setMaximumSize(new java.awt.Dimension(230, 29));
        jLabel48.setPreferredSize(new java.awt.Dimension(230, 29));

        javax.swing.GroupLayout PStudentLayout = new javax.swing.GroupLayout(PStudent);
        PStudent.setLayout(PStudentLayout);
        PStudentLayout.setHorizontalGroup(
            PStudentLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(lblTotalStudents, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(jLabel48, javax.swing.GroupLayout.DEFAULT_SIZE, 342, Short.MAX_VALUE)
        );
        PStudentLayout.setVerticalGroup(
            PStudentLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, PStudentLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel48, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(lblTotalStudents, javax.swing.GroupLayout.PREFERRED_SIZE, 94, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.ipadx = 60;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 0.1;
        gridBagConstraints.insets = new java.awt.Insets(0, 40, 0, 0);
        iDashboard.add(PStudent, gridBagConstraints);

        tableSms.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        tableSms.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Parent Number", "Student Name", "Message", "Created At", "Status"
            }
        ));
        tableSms.setRowHeight(30);
        jScrollPane3.setViewportView(tableSms);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.gridwidth = 4;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.ipadx = 1660;
        gridBagConstraints.ipady = 460;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(10, 10, 10, 10);
        iDashboard.add(jScrollPane3, gridBagConstraints);

        jPanel9.setBackground(new java.awt.Color(255, 255, 255));

        javax.swing.GroupLayout jPanel9Layout = new javax.swing.GroupLayout(jPanel9);
        jPanel9.setLayout(jPanel9Layout);
        jPanel9Layout.setHorizontalGroup(
            jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );
        jPanel9Layout.setVerticalGroup(
            jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.ipadx = 400;
        gridBagConstraints.ipady = 400;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_END;
        gridBagConstraints.weightx = 0.2;
        gridBagConstraints.insets = new java.awt.Insets(10, 10, 0, 10);
        iDashboard.add(jPanel9, gridBagConstraints);

        jLabel11.setFont(new java.awt.Font("Tahoma", 1, 24)); // NOI18N
        jLabel11.setText("SMS HISTORY");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 10, 0, 0);
        iDashboard.add(jLabel11, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 4;
        gridBagConstraints.fill = java.awt.GridBagConstraints.VERTICAL;
        gridBagConstraints.weighty = 0.5;
        iDashboard.add(filler16, gridBagConstraints);

        jTabbedPane1.addTab("tab1", iDashboard);

        iAttendance.setBackground(new java.awt.Color(255, 255, 255));
        iAttendance.setLayout(new java.awt.GridBagLayout());

        jLabel35.setFont(new java.awt.Font("Tahoma", 1, 36)); // NOI18N
        jLabel35.setText("SCAN HERE");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.SOUTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(20, 140, 0, 0);
        iAttendance.add(jLabel35, gridBagConstraints);

        TxtQrcode.setFont(new java.awt.Font("Tahoma", 0, 48)); // NOI18N
        TxtQrcode.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                TxtQrcodeActionPerformed(evt);
            }
        });
        TxtQrcode.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                TxtQrcodeKeyPressed(evt);
            }
            public void keyReleased(java.awt.event.KeyEvent evt) {
                TxtQrcodeKeyReleased(evt);
            }
            public void keyTyped(java.awt.event.KeyEvent evt) {
                TxtQrcodeKeyTyped(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.gridheight = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.ipady = 40;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 140, 0, 0);
        iAttendance.add(TxtQrcode, gridBagConstraints);

        lblStatus.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        lblStatus.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblStatus.setText("Click the box below before you scan");
        lblStatus.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.SOUTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 140, 0, 0);
        iAttendance.add(lblStatus, gridBagConstraints);

        btnTimeOut.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        btnTimeOut.setText("TIME OUT");
        btnTimeOut.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnTimeOutActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.ipadx = 86;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(10, 20, 0, 0);
        iAttendance.add(btnTimeOut, gridBagConstraints);

        tableAttendance.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        tableAttendance.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Student Name", "Class", "Date", "Time_In", "Time_Out", "Status"
            }
        ));
        tableAttendance.setRowHeight(30);
        jScrollPane17.setViewportView(tableAttendance);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.SOUTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(10, 6, 10, 4);
        iAttendance.add(jScrollPane17, gridBagConstraints);

        jButton13.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        jButton13.setText("SAVE TO DATABASE");
        jButton13.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton13ActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 20, 0, 0);
        iAttendance.add(jButton13, gridBagConstraints);

        cboxclass.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        cboxclass.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Select Class", " " }));
        cboxclass.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cboxclassActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(6, 6, 0, 0);
        iAttendance.add(cboxclass, gridBagConstraints);

        jLabel36.setFont(new java.awt.Font("Tahoma", 1, 24)); // NOI18N
        jLabel36.setText("Classes");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 6, 0, 0);
        iAttendance.add(jLabel36, gridBagConstraints);

        jTabbedPane1.addTab("tab2", iAttendance);

        iStudents.setBackground(new java.awt.Color(255, 255, 255));
        iStudents.setLayout(new java.awt.GridBagLayout());

        btnAdd1.setFont(new java.awt.Font("Tahoma", 1, 24)); // NOI18N
        btnAdd1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/add.png"))); // NOI18N
        btnAdd1.setText("ADD");
        btnAdd1.setIconTextGap(20);
        btnAdd1.setMaximumSize(new java.awt.Dimension(177, 51));
        btnAdd1.setMinimumSize(new java.awt.Dimension(177, 51));
        btnAdd1.setPreferredSize(new java.awt.Dimension(177, 51));
        btnAdd1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAdd1ActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 30, 0, 0);
        iStudents.add(btnAdd1, gridBagConstraints);

        btnUpdate1.setFont(new java.awt.Font("Tahoma", 1, 24)); // NOI18N
        btnUpdate1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/update.png"))); // NOI18N
        btnUpdate1.setText("UPDATE");
        btnUpdate1.setIconTextGap(20);
        btnUpdate1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnUpdate1ActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 30, 0, 0);
        iStudents.add(btnUpdate1, gridBagConstraints);

        btnDelete1.setFont(new java.awt.Font("Tahoma", 1, 24)); // NOI18N
        btnDelete1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/delete.png"))); // NOI18N
        btnDelete1.setText("DELETE");
        btnDelete1.setIconTextGap(20);
        btnDelete1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnDelete1ActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 30, 0, 0);
        iStudents.add(btnDelete1, gridBagConstraints);

        tableStudent.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        tableStudent.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Student Id", "Student Name", "Class", "Parent Contact", "Qr Code Data", "Status"
            }
        ));
        tableStudent.setToolTipText("");
        tableStudent.setRowHeight(30);
        tableStudent.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tableStudentMouseClicked(evt);
            }
        });
        jScrollPane4.setViewportView(tableStudent);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.gridwidth = 6;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.ipadx = 800;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(10, 10, 10, 0);
        iStudents.add(jScrollPane4, gridBagConstraints);

        jFirstname1.setFont(new java.awt.Font("Tahoma", 1, 24)); // NOI18N
        jFirstname1.setText("Fullname:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.SOUTHEAST;
        iStudents.add(jFirstname1, gridBagConstraints);

        jMiddlename1.setFont(new java.awt.Font("Tahoma", 1, 24)); // NOI18N
        jMiddlename1.setText("Class:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.SOUTHEAST;
        iStudents.add(jMiddlename1, gridBagConstraints);

        txtNumber.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        txtNumber.setText("639");
        txtNumber.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                txtNumberKeyTyped(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.SOUTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 10, 0, 0);
        iStudents.add(txtNumber, gridBagConstraints);

        jLastname1.setFont(new java.awt.Font("Tahoma", 1, 24)); // NOI18N
        jLastname1.setText("Parent Number:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.SOUTHEAST;
        iStudents.add(jLastname1, gridBagConstraints);

        jGender1.setFont(new java.awt.Font("Tahoma", 1, 24)); // NOI18N
        jGender1.setText("QR Code Data:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.SOUTHEAST;
        iStudents.add(jGender1, gridBagConstraints);

        cmbClassId.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        cmbClassId.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmbClassIdActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.SOUTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 10, 0, 0);
        iStudents.add(cmbClassId, gridBagConstraints);

        txtFname.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.SOUTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 10, 0, 0);
        iStudents.add(txtFname, gridBagConstraints);

        jButton9.setFont(new java.awt.Font("Tahoma", 1, 24)); // NOI18N
        jButton9.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/navbar/dropoutb.png"))); // NOI18N
        jButton9.setText("INACTIVE");
        jButton9.setIconTextGap(20);
        jButton9.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton9ActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.SOUTHEAST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 6);
        iStudents.add(jButton9, gridBagConstraints);

        jButton12.setFont(new java.awt.Font("Tahoma", 1, 24)); // NOI18N
        jButton12.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/import.png"))); // NOI18N
        jButton12.setText("IMPORT CSV");
        jButton12.setIconTextGap(20);
        jButton12.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton12ActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 30, 0, 0);
        iStudents.add(jButton12, gridBagConstraints);

        lblStudentCount.setFont(new java.awt.Font("Tahoma", 1, 24)); // NOI18N
        lblStudentCount.setText("TOTAL STUDENT");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 10, 0, 0);
        iStudents.add(lblStudentCount, gridBagConstraints);

        jPanel3.setBackground(new java.awt.Color(255, 255, 255));

        jLabel37.setFont(new java.awt.Font("Tahoma", 1, 24)); // NOI18N
        jLabel37.setText("SEARCH:");

        txtSearch1.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        txtSearch1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtSearch1ActionPerformed(evt);
            }
        });
        txtSearch1.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                txtSearch1KeyTyped(evt);
            }
        });

        jLabel21.setFont(new java.awt.Font("Tahoma", 1, 24)); // NOI18N
        jLabel21.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/search/search.png"))); // NOI18N

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jLabel37)
                .addGap(10, 10, 10)
                .addComponent(txtSearch1, javax.swing.GroupLayout.PREFERRED_SIZE, 214, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel21)
                .addContainerGap())
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel21)
                    .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(jLabel37)
                        .addGroup(jPanel3Layout.createSequentialGroup()
                            .addGap(1, 1, 1)
                            .addComponent(txtSearch1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        iStudents.add(jPanel3, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridheight = 5;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 0.1;
        iStudents.add(filler17, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 4;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.gridheight = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 0.1;
        iStudents.add(filler18, gridBagConstraints);

        txtQrData.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.SOUTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 10, 0, 0);
        iStudents.add(txtQrData, gridBagConstraints);

        jTabbedPane1.addTab("tab3", iStudents);

        iClasses.setBackground(new java.awt.Color(255, 255, 255));
        iClasses.setLayout(new java.awt.GridBagLayout());

        tableClasses.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        tableClasses.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null},
                {null, null},
                {null, null},
                {null, null}
            },
            new String [] {
                "Class Id", "Class Name"
            }
        ));
        tableClasses.setRowHeight(30);
        tableClasses.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tableClassesMouseClicked(evt);
            }
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                tableClassesMouseEntered(evt);
            }
        });
        jScrollPane8.setViewportView(tableClasses);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.gridwidth = 7;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(10, 10, 10, 10);
        iClasses.add(jScrollPane8, gridBagConstraints);

        jLabel13.setFont(new java.awt.Font("Tahoma", 1, 36)); // NOI18N
        jLabel13.setText("School Year:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_END;
        gridBagConstraints.insets = new java.awt.Insets(0, 100, 0, 0);
        iClasses.add(jLabel13, gridBagConstraints);

        txtSchoolYear.setFont(new java.awt.Font("Tahoma", 0, 24)); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(0, 10, 0, 0);
        iClasses.add(txtSchoolYear, gridBagConstraints);

        btnAdd4.setFont(new java.awt.Font("Tahoma", 1, 36)); // NOI18N
        btnAdd4.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/add.png"))); // NOI18N
        btnAdd4.setText("ADD");
        btnAdd4.setIconTextGap(20);
        btnAdd4.setMaximumSize(new java.awt.Dimension(177, 51));
        btnAdd4.setMinimumSize(new java.awt.Dimension(177, 51));
        btnAdd4.setPreferredSize(new java.awt.Dimension(177, 51));
        btnAdd4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAdd4ActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_END;
        iClasses.add(btnAdd4, gridBagConstraints);

        btnUpdate4.setFont(new java.awt.Font("Tahoma", 1, 36)); // NOI18N
        btnUpdate4.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/update.png"))); // NOI18N
        btnUpdate4.setText("UPDATE");
        btnUpdate4.setIconTextGap(20);
        btnUpdate4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnUpdate4ActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(0, 10, 0, 0);
        iClasses.add(btnUpdate4, gridBagConstraints);

        btnDelete4.setFont(new java.awt.Font("Tahoma", 1, 36)); // NOI18N
        btnDelete4.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/delete.png"))); // NOI18N
        btnDelete4.setText("DELETE");
        btnDelete4.setIconTextGap(20);
        btnDelete4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnDelete4ActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(0, 10, 0, 0);
        iClasses.add(btnDelete4, gridBagConstraints);

        jLabel39.setFont(new java.awt.Font("Tahoma", 1, 24)); // NOI18N
        jLabel39.setText("SEARCH:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 4;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_END;
        gridBagConstraints.insets = new java.awt.Insets(10, 0, 0, 0);
        iClasses.add(jLabel39, gridBagConstraints);

        txtSearch4.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        txtSearch4.setPreferredSize(new java.awt.Dimension(10, 28));
        txtSearch4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtSearch4ActionPerformed(evt);
            }
        });
        txtSearch4.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                txtSearch4KeyTyped(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 5;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.ipadx = 150;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(10, 0, 0, 0);
        iClasses.add(txtSearch4, gridBagConstraints);

        jLabel24.setFont(new java.awt.Font("Tahoma", 1, 24)); // NOI18N
        jLabel24.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/search/search.png"))); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 6;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(10, 0, 0, 10);
        iClasses.add(jLabel24, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridheight = 5;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 0.5;
        iClasses.add(filler7, gridBagConstraints);

        jLabel50.setFont(new java.awt.Font("Tahoma", 1, 36)); // NOI18N
        jLabel50.setText("Section:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_END;
        gridBagConstraints.insets = new java.awt.Insets(0, 100, 0, 0);
        iClasses.add(jLabel50, gridBagConstraints);

        txtSection.setFont(new java.awt.Font("Tahoma", 0, 24)); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(0, 10, 0, 0);
        iClasses.add(txtSection, gridBagConstraints);

        jLabel52.setFont(new java.awt.Font("Tahoma", 1, 36)); // NOI18N
        jLabel52.setText("Grade:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_END;
        gridBagConstraints.insets = new java.awt.Insets(0, 100, 0, 0);
        iClasses.add(jLabel52, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 5;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 0.1;
        iClasses.add(filler6, gridBagConstraints);

        comboGrade.setFont(new java.awt.Font("Tahoma", 0, 24)); // NOI18N
        comboGrade.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "1", "2", "3", "4", "5", "6" }));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(0, 10, 0, 0);
        iClasses.add(comboGrade, gridBagConstraints);

        jTabbedPane1.addTab("tab4", iClasses);

        iReports.setBackground(new java.awt.Color(255, 255, 255));
        iReports.setLayout(new java.awt.GridBagLayout());

        jLabel15.setFont(new java.awt.Font("Tahoma", 1, 24)); // NOI18N
        jLabel15.setText("FROM DATE:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_END;
        gridBagConstraints.insets = new java.awt.Insets(10, 0, 0, 0);
        iReports.add(jLabel15, gridBagConstraints);

        dateChooserFromDate.setFont(new java.awt.Font("Tahoma", 0, 24)); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.ipadx = 150;
        gridBagConstraints.ipady = 5;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(10, 5, 0, 0);
        iReports.add(dateChooserFromDate, gridBagConstraints);

        jLabel16.setFont(new java.awt.Font("Tahoma", 1, 24)); // NOI18N
        jLabel16.setText("END DATE:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_END;
        gridBagConstraints.insets = new java.awt.Insets(10, 0, 0, 0);
        iReports.add(jLabel16, gridBagConstraints);

        dateChooserEndDate.setFont(new java.awt.Font("Tahoma", 0, 24)); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.ipadx = 150;
        gridBagConstraints.ipady = 5;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(10, 5, 0, 0);
        iReports.add(dateChooserEndDate, gridBagConstraints);

        tableReport.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        tableReport.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null}
            },
            new String [] {
                "Student Name", "Class", "Date", "Time_In", "Time_Out", "Status"
            }
        ));
        tableReport.setToolTipText("");
        tableReport.setRowHeight(30);
        jScrollPane5.setViewportView(tableReport);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 7;
        gridBagConstraints.gridwidth = 5;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(10, 10, 10, 10);
        iReports.add(jScrollPane5, gridBagConstraints);

        jButton4.setFont(new java.awt.Font("Tahoma", 1, 36)); // NOI18N
        jButton4.setText("EXCEL");
        jButton4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton4ActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 10);
        iReports.add(jButton4, gridBagConstraints);

        jLabel18.setFont(new java.awt.Font("Tahoma", 1, 24)); // NOI18N
        jLabel18.setText("Student Name:");
        jLabel18.setMaximumSize(new java.awt.Dimension(300, 29));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(50, 0, 0, 0);
        iReports.add(jLabel18, gridBagConstraints);

        jLabel19.setFont(new java.awt.Font("Tahoma", 1, 24)); // NOI18N
        jLabel19.setText("Class:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_END;
        gridBagConstraints.insets = new java.awt.Insets(10, 0, 0, 0);
        iReports.add(jLabel19, gridBagConstraints);

        jLabel20.setFont(new java.awt.Font("Tahoma", 1, 24)); // NOI18N
        jLabel20.setText(" Status:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_END;
        gridBagConstraints.insets = new java.awt.Insets(10, 0, 0, 0);
        iReports.add(jLabel20, gridBagConstraints);

        txtStudentName.setFont(new java.awt.Font("Tahoma", 0, 24)); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(50, 5, 0, 0);
        iReports.add(txtStudentName, gridBagConstraints);

        jButton7.setFont(new java.awt.Font("Tahoma", 1, 36)); // NOI18N
        jButton7.setText("FILTER");
        jButton7.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton7ActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(10, 0, 0, 0);
        iReports.add(jButton7, gridBagConstraints);

        txtStatus.setFont(new java.awt.Font("Tahoma", 0, 24)); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(10, 5, 0, 0);
        iReports.add(txtStatus, gridBagConstraints);

        jLabel28.setFont(new java.awt.Font("Tahoma", 1, 36)); // NOI18N
        jLabel28.setText("EXPORT:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_END;
        gridBagConstraints.insets = new java.awt.Insets(0, 10, 0, 0);
        iReports.add(jLabel28, gridBagConstraints);

        cboxClassReports.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        cboxClassReports.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        cboxClassReports.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cboxClassReportsActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(10, 5, 0, 0);
        iReports.add(cboxClassReports, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.gridheight = 6;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 0.1;
        iReports.add(filler4, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 4;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridheight = 6;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 0.2;
        iReports.add(filler5, gridBagConstraints);

        jTabbedPane1.addTab("tab13", iReports);

        iNotification.setBackground(new java.awt.Color(255, 255, 255));
        iNotification.setLayout(new java.awt.GridBagLayout());

        tableSms1.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        tableSms1.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null}
            },
            new String [] {
                "Parent Number", "Student Name", "Message", "Created At", "Status"
            }
        ));
        tableSms1.setRowHeight(30);
        jScrollPane12.setViewportView(tableSms1);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridheight = 5;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(10, 10, 10, 10);
        iNotification.add(jScrollPane12, gridBagConstraints);

        jLabel30.setFont(new java.awt.Font("Tahoma", 1, 24)); // NOI18N
        jLabel30.setText("Number:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.SOUTHEAST;
        iNotification.add(jLabel30, gridBagConstraints);

        numberTextField.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        numberTextField.setText("639");
        numberTextField.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                numberTextFieldActionPerformed(evt);
            }
        });
        numberTextField.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                numberTextFieldKeyTyped(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.ipadx = 140;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 10, 0, 0);
        iNotification.add(numberTextField, gridBagConstraints);

        jLabel31.setFont(new java.awt.Font("Tahoma", 1, 24)); // NOI18N
        jLabel31.setText("Message:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHEAST;
        gridBagConstraints.insets = new java.awt.Insets(10, 50, 0, 0);
        iNotification.add(jLabel31, gridBagConstraints);

        messageTextArea.setColumns(20);
        messageTextArea.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        messageTextArea.setRows(5);
        jScrollPane13.setViewportView(messageTextArea);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(10, 10, 0, 10);
        iNotification.add(jScrollPane13, gridBagConstraints);

        bSend.setFont(new java.awt.Font("Tahoma", 1, 36)); // NOI18N
        bSend.setText("SEND");
        bSend.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                bSendActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.ipadx = 200;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTH;
        gridBagConstraints.weighty = 0.1;
        gridBagConstraints.insets = new java.awt.Insets(10, 10, 0, 10);
        iNotification.add(bSend, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.gridheight = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.VERTICAL;
        gridBagConstraints.weighty = 0.1;
        iNotification.add(filler3, gridBagConstraints);

        jTabbedPane1.addTab("tab8", iNotification);

        iUsers.setBackground(new java.awt.Color(255, 255, 255));

        btnAdd.setFont(new java.awt.Font("Tahoma", 1, 36)); // NOI18N
        btnAdd.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/add.png"))); // NOI18N
        btnAdd.setText("ADD");
        btnAdd.setIconTextGap(20);
        btnAdd.setMaximumSize(new java.awt.Dimension(177, 51));
        btnAdd.setMinimumSize(new java.awt.Dimension(177, 51));
        btnAdd.setPreferredSize(new java.awt.Dimension(177, 51));
        btnAdd.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAddActionPerformed(evt);
            }
        });

        btnUpdate.setFont(new java.awt.Font("Tahoma", 1, 36)); // NOI18N
        btnUpdate.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/update.png"))); // NOI18N
        btnUpdate.setText("UPDATE");
        btnUpdate.setIconTextGap(20);
        btnUpdate.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnUpdateActionPerformed(evt);
            }
        });

        btnDelete.setFont(new java.awt.Font("Tahoma", 1, 36)); // NOI18N
        btnDelete.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/delete.png"))); // NOI18N
        btnDelete.setText("DELETE");
        btnDelete.setIconTextGap(20);
        btnDelete.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnDeleteActionPerformed(evt);
            }
        });

        jLabel4.setFont(new java.awt.Font("Tahoma", 1, 24)); // NOI18N
        jLabel4.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/search/search.png"))); // NOI18N

        tableAccount.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        tableAccount.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "User Id", "Email", "Contact", "Username", "Role"
            }
        ));
        tableAccount.setRowHeight(30);
        tableAccount.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tableAccountMouseClicked(evt);
            }
        });
        jScrollPane2.setViewportView(tableAccount);

        jPanel1.setBackground(new java.awt.Color(255, 255, 255));

        fname.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N

        jFirstname.setFont(new java.awt.Font("Tahoma", 1, 24)); // NOI18N
        jFirstname.setText("Firstname:");

        jMiddlename.setFont(new java.awt.Font("Tahoma", 1, 24)); // NOI18N
        jMiddlename.setText("Middlename:");

        mname.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        mname.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnameActionPerformed(evt);
            }
        });

        lname.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N

        jLastname.setFont(new java.awt.Font("Tahoma", 1, 24)); // NOI18N
        jLastname.setText("Lastname:");

        jGender.setFont(new java.awt.Font("Tahoma", 1, 24)); // NOI18N
        jGender.setText("Gender:");

        gend.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N

        em.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N

        jLabel5.setFont(new java.awt.Font("Tahoma", 1, 24)); // NOI18N
        jLabel5.setText("Email:");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jMiddlename)
                            .addComponent(jFirstname))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(mname, javax.swing.GroupLayout.PREFERRED_SIZE, 230, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(fname, javax.swing.GroupLayout.PREFERRED_SIZE, 230, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel5)
                            .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addGroup(jPanel1Layout.createSequentialGroup()
                                    .addGap(59, 59, 59)
                                    .addComponent(jGender))
                                .addComponent(jLastname, javax.swing.GroupLayout.Alignment.TRAILING)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(gend)
                            .addComponent(lname)
                            .addComponent(em, javax.swing.GroupLayout.PREFERRED_SIZE, 231, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jFirstname, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(fname, javax.swing.GroupLayout.PREFERRED_SIZE, 29, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jMiddlename, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(mname, javax.swing.GroupLayout.PREFERRED_SIZE, 29, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLastname)
                    .addComponent(lname, javax.swing.GroupLayout.PREFERRED_SIZE, 29, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jGender, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(gend, javax.swing.GroupLayout.PREFERRED_SIZE, 29, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jLabel5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(em, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel2.setBackground(new java.awt.Color(255, 255, 255));

        ages.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N

        jAge.setFont(new java.awt.Font("Tahoma", 1, 24)); // NOI18N
        jAge.setText("Age:");

        jContact.setFont(new java.awt.Font("Tahoma", 1, 24)); // NOI18N
        jContact.setText("Contact:");

        contactnumber.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        contactnumber.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                contactnumberActionPerformed(evt);
            }
        });

        user.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N

        jUsername.setFont(new java.awt.Font("Tahoma", 1, 24)); // NOI18N
        jUsername.setText("Username:");

        jPassword.setFont(new java.awt.Font("Tahoma", 1, 24)); // NOI18N
        jPassword.setText("Password:");

        pass.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N

        jComboRole.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jComboRole.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Admin", "Teacher" }));

        jRole.setFont(new java.awt.Font("Tahoma", 1, 24)); // NOI18N
        jRole.setText("Role:");

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jUsername)
                            .addComponent(jPassword)
                            .addComponent(jRole))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(user, javax.swing.GroupLayout.PREFERRED_SIZE, 231, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                .addComponent(pass)
                                .addComponent(jComboRole, javax.swing.GroupLayout.PREFERRED_SIZE, 231, javax.swing.GroupLayout.PREFERRED_SIZE))))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGap(27, 27, 27)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jContact)
                            .addComponent(jAge))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(contactnumber, javax.swing.GroupLayout.PREFERRED_SIZE, 231, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(ages, javax.swing.GroupLayout.PREFERRED_SIZE, 230, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addComponent(ages, javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jAge, javax.swing.GroupLayout.Alignment.LEADING))
                .addGap(18, 18, 18)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jContact)
                    .addComponent(contactnumber, javax.swing.GroupLayout.PREFERRED_SIZE, 29, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(user, javax.swing.GroupLayout.PREFERRED_SIZE, 29, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jUsername))
                .addGap(18, 18, 18)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(pass, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jPassword))
                .addGap(18, 18, 18)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jRole)
                    .addComponent(jComboRole, javax.swing.GroupLayout.PREFERRED_SIZE, 29, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );

        txtSearch.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        txtSearch.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                txtSearchKeyTyped(evt);
            }
        });

        jLabel40.setFont(new java.awt.Font("Tahoma", 1, 24)); // NOI18N
        jLabel40.setText("SEARCH:");

        javax.swing.GroupLayout iUsersLayout = new javax.swing.GroupLayout(iUsers);
        iUsers.setLayout(iUsersLayout);
        iUsersLayout.setHorizontalGroup(
            iUsersLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(iUsersLayout.createSequentialGroup()
                .addGroup(iUsersLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(iUsersLayout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 1677, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(iUsersLayout.createSequentialGroup()
                        .addGap(489, 489, 489)
                        .addComponent(btnAdd, javax.swing.GroupLayout.PREFERRED_SIZE, 232, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(btnUpdate)
                        .addGap(28, 28, 28)
                        .addComponent(btnDelete, javax.swing.GroupLayout.PREFERRED_SIZE, 232, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(iUsersLayout.createSequentialGroup()
                        .addGap(381, 381, 381)
                        .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(97, 97, 97)
                        .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, iUsersLayout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jLabel40)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtSearch, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel4)
                .addGap(48, 48, 48))
        );
        iUsersLayout.setVerticalGroup(
            iUsersLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(iUsersLayout.createSequentialGroup()
                .addGap(25, 25, 25)
                .addGroup(iUsersLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jLabel4, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(iUsersLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(txtSearch, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel40, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(iUsersLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(47, 47, 47)
                .addGroup(iUsersLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnUpdate)
                    .addComponent(btnDelete)
                    .addComponent(btnAdd, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(49, 49, 49)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 483, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        jTabbedPane1.addTab("tab6", iUsers);

        iTeachers.setBackground(new java.awt.Color(255, 255, 255));
        iTeachers.setLayout(new java.awt.GridBagLayout());

        tableTeachers.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        tableTeachers.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null},
                {null, null},
                {null, null},
                {null, null}
            },
            new String [] {
                "Teacher Id", "Teacher Name"
            }
        ));
        tableTeachers.setRowHeight(30);
        tableTeachers.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tableTeachersMouseClicked(evt);
            }
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                tableTeachersMouseEntered(evt);
            }
        });
        jScrollPane6.setViewportView(tableTeachers);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.gridwidth = 8;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(10, 10, 10, 10);
        iTeachers.add(jScrollPane6, gridBagConstraints);

        jLabel6.setFont(new java.awt.Font("Tahoma", 1, 36)); // NOI18N
        jLabel6.setText("Fullname:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_END;
        gridBagConstraints.insets = new java.awt.Insets(0, 50, 0, 0);
        iTeachers.add(jLabel6, gridBagConstraints);

        Tcfname.setFont(new java.awt.Font("Tahoma", 0, 24)); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(0, 10, 0, 0);
        iTeachers.add(Tcfname, gridBagConstraints);

        txtSearch2.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        txtSearch2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtSearch2ActionPerformed(evt);
            }
        });
        txtSearch2.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                txtSearch2KeyTyped(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 6;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.ipadx = 150;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_END;
        gridBagConstraints.insets = new java.awt.Insets(10, 0, 0, 0);
        iTeachers.add(txtSearch2, gridBagConstraints);

        jLabel22.setFont(new java.awt.Font("Tahoma", 1, 24)); // NOI18N
        jLabel22.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/search/search.png"))); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 7;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_END;
        gridBagConstraints.insets = new java.awt.Insets(10, 0, 0, 10);
        iTeachers.add(jLabel22, gridBagConstraints);

        btnAdd2.setFont(new java.awt.Font("Tahoma", 1, 36)); // NOI18N
        btnAdd2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/add.png"))); // NOI18N
        btnAdd2.setText("ADD");
        btnAdd2.setIconTextGap(20);
        btnAdd2.setMaximumSize(new java.awt.Dimension(177, 51));
        btnAdd2.setMinimumSize(new java.awt.Dimension(177, 51));
        btnAdd2.setPreferredSize(new java.awt.Dimension(177, 51));
        btnAdd2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAdd2ActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_END;
        gridBagConstraints.insets = new java.awt.Insets(10, 0, 0, 0);
        iTeachers.add(btnAdd2, gridBagConstraints);

        btnUpdate2.setFont(new java.awt.Font("Tahoma", 1, 36)); // NOI18N
        btnUpdate2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/update.png"))); // NOI18N
        btnUpdate2.setText("UPDATE");
        btnUpdate2.setIconTextGap(20);
        btnUpdate2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnUpdate2ActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.insets = new java.awt.Insets(10, 10, 0, 0);
        iTeachers.add(btnUpdate2, gridBagConstraints);

        btnDelete2.setFont(new java.awt.Font("Tahoma", 1, 36)); // NOI18N
        btnDelete2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/delete.png"))); // NOI18N
        btnDelete2.setText("DELETE");
        btnDelete2.setIconTextGap(20);
        btnDelete2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnDelete2ActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.insets = new java.awt.Insets(10, 10, 0, 0);
        iTeachers.add(btnDelete2, gridBagConstraints);

        jLabel41.setFont(new java.awt.Font("Tahoma", 1, 24)); // NOI18N
        jLabel41.setText("SEARCH:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 5;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_END;
        gridBagConstraints.insets = new java.awt.Insets(10, 0, 0, 0);
        iTeachers.add(jLabel41, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridheight = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 2.0;
        iTeachers.add(filler8, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 4;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridheight = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        iTeachers.add(filler9, gridBagConstraints);

        jTabbedPane1.addTab("tab9", iTeachers);

        iSubjects.setBackground(new java.awt.Color(255, 255, 255));
        iSubjects.setLayout(new java.awt.GridBagLayout());

        tableSubject.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        tableSubject.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null},
                {null, null},
                {null, null},
                {null, null}
            },
            new String [] {
                "Subject Id", "Subject Name"
            }
        ));
        tableSubject.setRowHeight(30);
        tableSubject.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tableSubjectMouseClicked(evt);
            }
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                tableSubjectMouseEntered(evt);
            }
        });
        jScrollPane7.setViewportView(tableSubject);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.gridwidth = 8;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(10, 10, 10, 10);
        iSubjects.add(jScrollPane7, gridBagConstraints);

        jLabel12.setFont(new java.awt.Font("Tahoma", 1, 36)); // NOI18N
        jLabel12.setText("Subject:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_END;
        gridBagConstraints.insets = new java.awt.Insets(0, 70, 0, 0);
        iSubjects.add(jLabel12, gridBagConstraints);

        Subjectname.setFont(new java.awt.Font("Tahoma", 0, 24)); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(0, 10, 0, 0);
        iSubjects.add(Subjectname, gridBagConstraints);

        btnAdd3.setFont(new java.awt.Font("Tahoma", 1, 36)); // NOI18N
        btnAdd3.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/add.png"))); // NOI18N
        btnAdd3.setText("ADD");
        btnAdd3.setIconTextGap(20);
        btnAdd3.setMaximumSize(new java.awt.Dimension(177, 51));
        btnAdd3.setMinimumSize(new java.awt.Dimension(177, 51));
        btnAdd3.setPreferredSize(new java.awt.Dimension(177, 51));
        btnAdd3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAdd3ActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(10, 0, 0, 0);
        iSubjects.add(btnAdd3, gridBagConstraints);

        btnUpdate3.setFont(new java.awt.Font("Tahoma", 1, 36)); // NOI18N
        btnUpdate3.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/update.png"))); // NOI18N
        btnUpdate3.setText("UPDATE");
        btnUpdate3.setIconTextGap(20);
        btnUpdate3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnUpdate3ActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.insets = new java.awt.Insets(10, 10, 0, 0);
        iSubjects.add(btnUpdate3, gridBagConstraints);

        btnDelete3.setFont(new java.awt.Font("Tahoma", 1, 36)); // NOI18N
        btnDelete3.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/delete.png"))); // NOI18N
        btnDelete3.setText("DELETE");
        btnDelete3.setIconTextGap(20);
        btnDelete3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnDelete3ActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.insets = new java.awt.Insets(10, 10, 0, 0);
        iSubjects.add(btnDelete3, gridBagConstraints);

        txtSearch7.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        txtSearch7.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtSearch7ActionPerformed(evt);
            }
        });
        txtSearch7.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                txtSearch7KeyTyped(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 6;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.ipadx = 150;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_END;
        gridBagConstraints.insets = new java.awt.Insets(10, 0, 0, 0);
        iSubjects.add(txtSearch7, gridBagConstraints);

        jLabel38.setFont(new java.awt.Font("Tahoma", 1, 24)); // NOI18N
        jLabel38.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/search/search.png"))); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 7;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_END;
        gridBagConstraints.insets = new java.awt.Insets(10, 0, 0, 10);
        iSubjects.add(jLabel38, gridBagConstraints);

        jLabel42.setFont(new java.awt.Font("Tahoma", 1, 24)); // NOI18N
        jLabel42.setText("SEARCH:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 5;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_END;
        gridBagConstraints.insets = new java.awt.Insets(10, 0, 0, 0);
        iSubjects.add(jLabel42, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 4;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridheight = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        iSubjects.add(filler10, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridheight = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 3.0;
        iSubjects.add(filler11, gridBagConstraints);

        jTabbedPane1.addTab("tab10", iSubjects);

        iAssignTc.setBackground(new java.awt.Color(255, 255, 255));
        iAssignTc.setLayout(new java.awt.GridBagLayout());

        tableAssignTeacher.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        tableAssignTeacher.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null}
            },
            new String [] {
                "Id", "Class Name", "Subject Name"
            }
        ));
        tableAssignTeacher.setRowHeight(30);
        tableAssignTeacher.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tableAssignTeacherMouseClicked(evt);
            }
        });
        jScrollPane10.setViewportView(tableAssignTeacher);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.gridwidth = 8;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(10, 10, 10, 10);
        iAssignTc.add(jScrollPane10, gridBagConstraints);

        jLabel26.setFont(new java.awt.Font("Tahoma", 1, 36)); // NOI18N
        jLabel26.setText("Class:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridheight = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_END;
        iAssignTc.add(jLabel26, gridBagConstraints);

        jLabel27.setFont(new java.awt.Font("Tahoma", 1, 36)); // NOI18N
        jLabel27.setText("Teacher:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.gridheight = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_END;
        gridBagConstraints.insets = new java.awt.Insets(10, 0, 0, 0);
        iAssignTc.add(jLabel27, gridBagConstraints);

        cmbClass1.setFont(new java.awt.Font("Tahoma", 0, 24)); // NOI18N
        cmbClass1.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        cmbClass1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmbClass1ActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridheight = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(0, 10, 0, 0);
        iAssignTc.add(cmbClass1, gridBagConstraints);

        cmbsubject1.setFont(new java.awt.Font("Tahoma", 0, 24)); // NOI18N
        cmbsubject1.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        cmbsubject1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmbsubject1ActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.gridheight = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(10, 10, 0, 0);
        iAssignTc.add(cmbsubject1, gridBagConstraints);

        jButton6.setFont(new java.awt.Font("Tahoma", 1, 36)); // NOI18N
        jButton6.setText("ASSIGN");
        jButton6.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton6ActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(10, 10, 0, 0);
        iAssignTc.add(jButton6, gridBagConstraints);

        jButton8.setFont(new java.awt.Font("Tahoma", 1, 36)); // NOI18N
        jButton8.setText("DELETE");
        jButton8.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton8ActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(10, 10, 0, 0);
        iAssignTc.add(jButton8, gridBagConstraints);

        jButton10.setFont(new java.awt.Font("Tahoma", 1, 36)); // NOI18N
        jButton10.setText("UPDATE");
        jButton10.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton10ActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(10, 10, 0, 0);
        iAssignTc.add(jButton10, gridBagConstraints);

        txtSearch3.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        txtSearch3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtSearch3ActionPerformed(evt);
            }
        });
        txtSearch3.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                txtSearch3KeyTyped(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 6;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.ipadx = 150;
        gridBagConstraints.insets = new java.awt.Insets(10, 0, 0, 0);
        iAssignTc.add(txtSearch3, gridBagConstraints);

        jLabel23.setFont(new java.awt.Font("Tahoma", 1, 24)); // NOI18N
        jLabel23.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/search/search.png"))); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 7;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.insets = new java.awt.Insets(10, 0, 0, 10);
        iAssignTc.add(jLabel23, gridBagConstraints);

        jLabel43.setFont(new java.awt.Font("Tahoma", 1, 24)); // NOI18N
        jLabel43.setText("SEARCH:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 5;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.insets = new java.awt.Insets(10, 0, 0, 0);
        iAssignTc.add(jLabel43, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridheight = 4;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 2.0;
        iAssignTc.add(filler12, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 4;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridheight = 4;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        iAssignTc.add(filler13, gridBagConstraints);

        jTabbedPane1.addTab("tab12", iAssignTc);

        iAssignSub.setBackground(new java.awt.Color(255, 255, 255));
        iAssignSub.setLayout(new java.awt.GridBagLayout());

        tableAssignSub.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        tableAssignSub.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null}
            },
            new String [] {
                "Id", "Class Name", "Subject Name"
            }
        ));
        tableAssignSub.setRowHeight(30);
        tableAssignSub.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tableAssignSubMouseClicked(evt);
            }
        });
        jScrollPane9.setViewportView(tableAssignSub);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.gridwidth = 8;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(10, 10, 10, 10);
        iAssignSub.add(jScrollPane9, gridBagConstraints);

        jLabel17.setFont(new java.awt.Font("Tahoma", 1, 36)); // NOI18N
        jLabel17.setText("Class:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridheight = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_END;
        iAssignSub.add(jLabel17, gridBagConstraints);

        jLabel25.setFont(new java.awt.Font("Tahoma", 1, 36)); // NOI18N
        jLabel25.setText("Subject:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.gridheight = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_END;
        gridBagConstraints.insets = new java.awt.Insets(10, 0, 0, 0);
        iAssignSub.add(jLabel25, gridBagConstraints);

        cmbClass.setFont(new java.awt.Font("Tahoma", 0, 24)); // NOI18N
        cmbClass.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        cmbClass.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmbClassActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridheight = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(0, 10, 0, 0);
        iAssignSub.add(cmbClass, gridBagConstraints);

        cmbSubject.setFont(new java.awt.Font("Tahoma", 0, 24)); // NOI18N
        cmbSubject.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.gridheight = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(10, 10, 0, 0);
        iAssignSub.add(cmbSubject, gridBagConstraints);

        jButton2.setFont(new java.awt.Font("Tahoma", 1, 36)); // NOI18N
        jButton2.setText("ASSIGN");
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(0, 10, 0, 0);
        iAssignSub.add(jButton2, gridBagConstraints);

        jButton3.setFont(new java.awt.Font("Tahoma", 1, 36)); // NOI18N
        jButton3.setText("DELETE");
        jButton3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton3ActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(10, 10, 0, 0);
        iAssignSub.add(jButton3, gridBagConstraints);

        jButton5.setFont(new java.awt.Font("Tahoma", 1, 36)); // NOI18N
        jButton5.setText("UPDATE");
        jButton5.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton5ActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(10, 10, 0, 0);
        iAssignSub.add(jButton5, gridBagConstraints);

        txtSearch5.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        txtSearch5.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtSearch5ActionPerformed(evt);
            }
        });
        txtSearch5.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                txtSearch5KeyTyped(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 6;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.ipadx = 150;
        gridBagConstraints.insets = new java.awt.Insets(10, 0, 0, 0);
        iAssignSub.add(txtSearch5, gridBagConstraints);

        jLabel29.setFont(new java.awt.Font("Tahoma", 1, 24)); // NOI18N
        jLabel29.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/search/search.png"))); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 7;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.insets = new java.awt.Insets(10, 0, 0, 10);
        iAssignSub.add(jLabel29, gridBagConstraints);

        jLabel44.setFont(new java.awt.Font("Tahoma", 1, 24)); // NOI18N
        jLabel44.setText("SEARCH:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 5;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.insets = new java.awt.Insets(10, 0, 0, 0);
        iAssignSub.add(jLabel44, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 4;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridheight = 4;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        iAssignSub.add(filler14, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridheight = 4;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 2.0;
        iAssignSub.add(filler15, gridBagConstraints);

        jTabbedPane1.addTab("tab11", iAssignSub);

        iDropout.setBackground(new java.awt.Color(255, 255, 255));
        iDropout.setLayout(new java.awt.GridBagLayout());

        tableDropout.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        tableDropout.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null}
            },
            new String [] {
                "Student Id", "Student Name", "Class Name", "Parent Number", "Qr Code Data", "Status"
            }
        ));
        tableDropout.setRowHeight(30);
        jScrollPane14.setViewportView(tableDropout);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.gridwidth = 4;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.ipadx = 1663;
        gridBagConstraints.ipady = 705;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(18, 6, 6, 2);
        iDropout.add(jScrollPane14, gridBagConstraints);

        jButton11.setFont(new java.awt.Font("Tahoma", 1, 36)); // NOI18N
        jButton11.setText("ACTIVE");
        jButton11.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton11ActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.SOUTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(10, 18, 0, 0);
        iDropout.add(jButton11, gridBagConstraints);

        txtSearch6.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        txtSearch6.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtSearch6ActionPerformed(evt);
            }
        });
        txtSearch6.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                txtSearch6KeyTyped(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.ipadx = 200;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(10, 6, 0, 0);
        iDropout.add(txtSearch6, gridBagConstraints);

        jLabel32.setFont(new java.awt.Font("Tahoma", 1, 24)); // NOI18N
        jLabel32.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/search/search.png"))); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(10, 6, 0, 10);
        iDropout.add(jLabel32, gridBagConstraints);

        jLabel45.setFont(new java.awt.Font("Tahoma", 1, 24)); // NOI18N
        jLabel45.setText("SEARCH:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.SOUTHEAST;
        gridBagConstraints.insets = new java.awt.Insets(10, 0, 0, 0);
        iDropout.add(jLabel45, gridBagConstraints);

        jTabbedPane1.addTab("tab13", iDropout);

        iQrCodeGen.setBackground(new java.awt.Color(255, 255, 255));
        iQrCodeGen.setLayout(new java.awt.GridBagLayout());

        jLabel9.setFont(new java.awt.Font("Tahoma", 1, 36)); // NOI18N
        jLabel9.setText("QR CODE DATA");
        jLabel9.setToolTipText("");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.PAGE_END;
        gridBagConstraints.insets = new java.awt.Insets(0, 30, 0, 0);
        iQrCodeGen.add(jLabel9, gridBagConstraints);

        jButton1.setFont(new java.awt.Font("Tahoma", 0, 36)); // NOI18N
        jButton1.setText("Generate");
        jButton1.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jButton1MouseClicked(evt);
            }
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                jButton1MouseEntered(evt);
            }
        });
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.insets = new java.awt.Insets(10, 30, 0, 0);
        iQrCodeGen.add(jButton1, gridBagConstraints);

        qrCodeLabel.setFont(new java.awt.Font("Tahoma", 1, 36)); // NOI18N
        qrCodeLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        qrCodeLabel.setText("QR CODE");
        qrCodeLabel.setVerticalAlignment(javax.swing.SwingConstants.TOP);
        qrCodeLabel.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        qrCodeLabel.setIconTextGap(20);
        qrCodeLabel.setVerticalTextPosition(javax.swing.SwingConstants.TOP);

        javax.swing.GroupLayout jPanel6Layout = new javax.swing.GroupLayout(jPanel6);
        jPanel6.setLayout(jPanel6Layout);
        jPanel6Layout.setHorizontalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel6Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(qrCodeLabel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        jPanel6Layout.setVerticalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(qrCodeLabel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.gridheight = 3;
        gridBagConstraints.ipadx = 100;
        gridBagConstraints.ipady = 50;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.PAGE_START;
        gridBagConstraints.insets = new java.awt.Insets(10, 30, 0, 0);
        iQrCodeGen.add(jPanel6, gridBagConstraints);

        tableWithQR.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        tableWithQR.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Students ID", "Students Name", "Class Name", "Parent Contact Number", "Qr Code Data", "Status"
            }
        ));
        tableWithQR.setRowHeight(30);
        tableWithQR.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tableWithQRMouseClicked(evt);
            }
        });
        jScrollPane15.setViewportView(tableWithQR);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = 5;
        gridBagConstraints.gridheight = 5;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 0.5;
        gridBagConstraints.insets = new java.awt.Insets(10, 30, 10, 10);
        iQrCodeGen.add(jScrollPane15, gridBagConstraints);

        tableWithoutQR.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        tableWithoutQR.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null}
            },
            new String [] {
                "Students Id", "Students Name", "Class Name", "Parent Contact Number", "QR Code Data", "Status"
            }
        ));
        tableWithoutQR.setRowHeight(30);
        tableWithoutQR.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tableWithoutQRMouseClicked(evt);
            }
        });
        jScrollPane16.setViewportView(tableWithoutQR);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 7;
        gridBagConstraints.gridwidth = 5;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.weighty = 0.5;
        gridBagConstraints.insets = new java.awt.Insets(10, 30, 10, 10);
        iQrCodeGen.add(jScrollPane16, gridBagConstraints);

        jLabel33.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        jLabel33.setText("WITH QR CODE DATA");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(10, 30, 0, 0);
        iQrCodeGen.add(jLabel33, gridBagConstraints);

        jLabel34.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        jLabel34.setText("NO QR CODE DATA");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(0, 30, 0, 0);
        iQrCodeGen.add(jLabel34, gridBagConstraints);

        lblQrData.setFont(new java.awt.Font("Tahoma", 1, 24)); // NOI18N
        lblQrData.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblQrData.setPreferredSize(new java.awt.Dimension(100, 0));
        lblQrData.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                lblQrDataKeyTyped(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.insets = new java.awt.Insets(10, 30, 0, 0);
        iQrCodeGen.add(lblQrData, gridBagConstraints);

        jLabel46.setFont(new java.awt.Font("Tahoma", 1, 24)); // NOI18N
        jLabel46.setText("SEARCH:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 4;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_END;
        gridBagConstraints.insets = new java.awt.Insets(10, 0, 0, 0);
        iQrCodeGen.add(jLabel46, gridBagConstraints);

        txtSearch8.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        txtSearch8.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtSearch8ActionPerformed(evt);
            }
        });
        txtSearch8.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                txtSearch8KeyTyped(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 5;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.ipadx = 150;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        iQrCodeGen.add(txtSearch8, gridBagConstraints);

        jLabel47.setFont(new java.awt.Font("Tahoma", 1, 24)); // NOI18N
        jLabel47.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/search/search.png"))); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 6;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.ipadx = 10;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_END;
        iQrCodeGen.add(jLabel47, gridBagConstraints);

        jButton14.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        jButton14.setText("GENERATE ALL");
        jButton14.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton14ActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 4;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(0, 10, 0, 0);
        iQrCodeGen.add(jButton14, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.VERTICAL;
        gridBagConstraints.weighty = 0.5;
        iQrCodeGen.add(filler1, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.ipadx = 700;
        gridBagConstraints.weightx = 1.0;
        iQrCodeGen.add(filler2, gridBagConstraints);

        cboxNoQr.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        cboxNoQr.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        cboxNoQr.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cboxNoQrActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        iQrCodeGen.add(cboxNoQr, gridBagConstraints);

        jTabbedPane1.addTab("tab9", iQrCodeGen);

        iSettings.setBackground(new java.awt.Color(255, 255, 255));
        iSettings.setLayout(new java.awt.GridBagLayout());
        jTabbedPane1.addTab("tab14", iSettings);

        MainPanel.add(jTabbedPane1, java.awt.BorderLayout.CENTER);

        getContentPane().add(MainPanel, java.awt.BorderLayout.CENTER);

        pack();
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void jLabel14MouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel14MouseEntered
        // TODO add your handling code here:
    }//GEN-LAST:event_jLabel14MouseEntered

    private void jLabel14MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel14MouseClicked
        // TODO add your handling code here:
        showTermsDialog();
    }//GEN-LAST:event_jLabel14MouseClicked

    private void BLogoutMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_BLogoutMouseExited
        // TODO add your handling code here:
        BLogout.setBackground(new Color(0, 0, 102));
    }//GEN-LAST:event_BLogoutMouseExited

    private void BLogoutMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_BLogoutMouseEntered
        // TODO add your handling code here
        BLogout.setBackground(new Color(0, 0, 153));
    }//GEN-LAST:event_BLogoutMouseEntered

    private void BLogoutMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_BLogoutMouseClicked
        // TODO add your handling code here:
        int confirm = JOptionPane.showConfirmDialog(null, 
            "Are you sure you want to Logout?", 
            "Confirm Logout", JOptionPane.YES_NO_OPTION);
       
        if (confirm == JOptionPane.YES_OPTION){
            Login up = new Login();
            up.setVisible(true);
            setVisible(false);
        }
    }//GEN-LAST:event_BLogoutMouseClicked

    private void bAttendanceMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_bAttendanceMouseClicked
        // TODO add your handling code here:
        refreshData();
        jTabbedPane1.setSelectedIndex(1);
        jSettings.setBackground(new Color(0, 0, 102));
        bSettings.setBackground(new Color(0, 0, 102));
        jAttendance.setBackground(new Color(0, 0, 153));
        bAttendance.setBackground(new Color(0, 0, 153));
        jDropOut.setBackground(new Color(0, 0, 102));
        bDropOut.setBackground(new Color(0, 0, 102));
        jTeachers.setBackground(new Color(0, 0, 102));
        BTeachers.setBackground(new Color(0, 0, 102));
        BSubject.setBackground(new Color(0, 0, 102));
        jSubjects.setBackground(new Color(0, 0, 102));
        BAssignTc.setBackground(new Color(0, 0, 102));
        jAssignTc.setBackground(new Color(0, 0, 102));
        BAssignSub.setBackground(new Color(0, 0, 102));
        jAssignSub.setBackground(new Color(0, 0, 102));
        jNotification.setBackground(new Color(0, 0, 102));
        BNotification.setBackground(new Color(0, 0, 102));
        BDashboard.setBackground(new Color(0, 0, 102));
        jDashboard.setBackground(new Color(0, 0, 102));
        BStudents.setBackground(new Color(0, 0, 102));
        jStudents.setBackground(new Color(0, 0, 102));
        BClasses.setBackground(new Color(0, 0, 102));
        jClasses.setBackground(new Color(0, 0, 102));
        BReports.setBackground(new Color(0, 0, 102));
        jReports.setBackground(new Color(0, 0, 102));
        BQrCodeGen.setBackground(new Color(0, 0, 102));
        jQrCodeGen.setBackground(new Color(0, 0, 102));
    }//GEN-LAST:event_bAttendanceMouseClicked

    private void bDropOutMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_bDropOutMouseClicked
        // TODO add your handling code here:
        refreshData();
        jTabbedPane1.setSelectedIndex(11);
        jSettings.setBackground(new Color(0, 0, 102));
        bSettings.setBackground(new Color(0, 0, 102));
        jAttendance.setBackground(new Color(0, 0, 102));
        bAttendance.setBackground(new Color(0, 0, 102));
        jDropOut.setBackground(new Color(0, 0, 153));
        bDropOut.setBackground(new Color(0, 0, 153));
        jTeachers.setBackground(new Color(0, 0, 102));
        BTeachers.setBackground(new Color(0, 0, 102));
        BSubject.setBackground(new Color(0, 0, 102));
        jSubjects.setBackground(new Color(0, 0, 102));
        BAssignTc.setBackground(new Color(0, 0, 102));
        jAssignTc.setBackground(new Color(0, 0, 102));
        BAssignSub.setBackground(new Color(0, 0, 102));
        jAssignSub.setBackground(new Color(0, 0, 102));
        jNotification.setBackground(new Color(0, 0, 102));
        BNotification.setBackground(new Color(0, 0, 102));
        BDashboard.setBackground(new Color(0, 0, 102));
        jDashboard.setBackground(new Color(0, 0, 102));
        BStudents.setBackground(new Color(0, 0, 102));
        jStudents.setBackground(new Color(0, 0, 102));
        BClasses.setBackground(new Color(0, 0, 102));
        jClasses.setBackground(new Color(0, 0, 102));
        BReports.setBackground(new Color(0, 0, 102));
        jReports.setBackground(new Color(0, 0, 102));
        BQrCodeGen.setBackground(new Color(0, 0, 102));
        jQrCodeGen.setBackground(new Color(0, 0, 102));
    }//GEN-LAST:event_bDropOutMouseClicked

    private void BTeachersMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_BTeachersMouseClicked
        // TODO add your handling code here:
        refreshData();
        jSettings.setBackground(new Color(0, 0, 102));
        bSettings.setBackground(new Color(0, 0, 102));
        jTabbedPane1.setSelectedIndex(7);
        jAttendance.setBackground(new Color(0, 0, 102));
        bAttendance.setBackground(new Color(0, 0, 102));
        jDropOut.setBackground(new Color(0, 0, 102));
        bDropOut.setBackground(new Color(0, 0, 102));
        jTeachers.setBackground(new Color(0, 0, 153));
        BTeachers.setBackground(new Color(0, 0, 153));
        BSubject.setBackground(new Color(0, 0, 102));
        jSubjects.setBackground(new Color(0, 0, 102));
        BAssignTc.setBackground(new Color(0, 0, 102));
        jAssignTc.setBackground(new Color(0, 0, 102));
        BAssignSub.setBackground(new Color(0, 0, 102));
        jAssignSub.setBackground(new Color(0, 0, 102));
        jNotification.setBackground(new Color(0, 0, 102));
        BNotification.setBackground(new Color(0, 0, 102));
        BDashboard.setBackground(new Color(0, 0, 102));
        jDashboard.setBackground(new Color(0, 0, 102));
        BStudents.setBackground(new Color(0, 0, 102));
        jStudents.setBackground(new Color(0, 0, 102));
        BClasses.setBackground(new Color(0, 0, 102));
        jClasses.setBackground(new Color(0, 0, 102));
        BReports.setBackground(new Color(0, 0, 102));
        jReports.setBackground(new Color(0, 0, 102));
        BQrCodeGen.setBackground(new Color(0, 0, 102));
        jQrCodeGen.setBackground(new Color(0, 0, 102));
    }//GEN-LAST:event_BTeachersMouseClicked

    private void BAssignTcMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_BAssignTcMouseClicked
        // TODO add your handling code here:
        refreshData();
        jTabbedPane1.setSelectedIndex(9);
        jSettings.setBackground(new Color(0, 0, 102));
        bSettings.setBackground(new Color(0, 0, 102));
        jAttendance.setBackground(new Color(0, 0, 102));
        bAttendance.setBackground(new Color(0, 0, 102));
        jDropOut.setBackground(new Color(0, 0, 102));
        bDropOut.setBackground(new Color(0, 0, 102));
        jTeachers.setBackground(new Color(0, 0, 102));
        BTeachers.setBackground(new Color(0, 0, 102));
        BSubject.setBackground(new Color(0, 0, 102));
        jSubjects.setBackground(new Color(0, 0, 102));
        BAssignTc.setBackground(new Color(0, 0, 153));
        jAssignTc.setBackground(new Color(0, 0, 153));
        BAssignSub.setBackground(new Color(0, 0, 102));
        jAssignSub.setBackground(new Color(0, 0, 102));
        jNotification.setBackground(new Color(0, 0, 102));
        BNotification.setBackground(new Color(0, 0, 102));
        BDashboard.setBackground(new Color(0, 0, 102));
        jDashboard.setBackground(new Color(0, 0, 102));
        BStudents.setBackground(new Color(0, 0, 102));
        jStudents.setBackground(new Color(0, 0, 102));
        BClasses.setBackground(new Color(0, 0, 102));
        jClasses.setBackground(new Color(0, 0, 102));
        BReports.setBackground(new Color(0, 0, 102));
        jReports.setBackground(new Color(0, 0, 102));
        BQrCodeGen.setBackground(new Color(0, 0, 102));
        jQrCodeGen.setBackground(new Color(0, 0, 102));
    }//GEN-LAST:event_BAssignTcMouseClicked

    private void BAssignSubMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_BAssignSubMouseClicked
        // TODO add your handling code here:
        refreshData();
        jTabbedPane1.setSelectedIndex(10);
        jSettings.setBackground(new Color(0, 0, 102));
        bSettings.setBackground(new Color(0, 0, 102));
        jAttendance.setBackground(new Color(0, 0, 102));
        bAttendance.setBackground(new Color(0, 0, 102));
        jDropOut.setBackground(new Color(0, 0, 102));
        bDropOut.setBackground(new Color(0, 0, 102));
        jTeachers.setBackground(new Color(0, 0, 102));
        BTeachers.setBackground(new Color(0, 0, 102));
        BSubject.setBackground(new Color(0, 0, 102));
        jSubjects.setBackground(new Color(0, 0, 102));
        BAssignTc.setBackground(new Color(0, 0, 102));
        jAssignTc.setBackground(new Color(0, 0, 102));
        BAssignSub.setBackground(new Color(0, 0, 153));
        jAssignSub.setBackground(new Color(0, 0, 153));
        jNotification.setBackground(new Color(0, 0, 102));
        BNotification.setBackground(new Color(0, 0, 102));
        BDashboard.setBackground(new Color(0, 0, 102));
        jDashboard.setBackground(new Color(0, 0, 102));
        BStudents.setBackground(new Color(0, 0, 102));
        jStudents.setBackground(new Color(0, 0, 102));
        BClasses.setBackground(new Color(0, 0, 102));
        jClasses.setBackground(new Color(0, 0, 102));
        BReports.setBackground(new Color(0, 0, 102));
        jReports.setBackground(new Color(0, 0, 102));
        BQrCodeGen.setBackground(new Color(0, 0, 102));
        jQrCodeGen.setBackground(new Color(0, 0, 102));
    }//GEN-LAST:event_BAssignSubMouseClicked

    private void BSubjectMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_BSubjectMouseClicked
        // TODO add your handling code here:
        refreshData();
        jTabbedPane1.setSelectedIndex(8);
        jSettings.setBackground(new Color(0, 0, 102));
        bSettings.setBackground(new Color(0, 0, 102));
        jAttendance.setBackground(new Color(0, 0, 102));
        bAttendance.setBackground(new Color(0, 0, 102));
        jDropOut.setBackground(new Color(0, 0, 102));
        bDropOut.setBackground(new Color(0, 0, 102));
        jTeachers.setBackground(new Color(0, 0, 102));
        BTeachers.setBackground(new Color(0, 0, 102));
        BSubject.setBackground(new Color(0, 0, 153));
        jSubjects.setBackground(new Color(0, 0, 153));
        BAssignTc.setBackground(new Color(0, 0, 102));
        jAssignTc.setBackground(new Color(0, 0, 102));
        BAssignSub.setBackground(new Color(0, 0, 102));
        jAssignSub.setBackground(new Color(0, 0, 102));
        jNotification.setBackground(new Color(0, 0, 102));
        BNotification.setBackground(new Color(0, 0, 102));
        BDashboard.setBackground(new Color(0, 0, 102));
        jDashboard.setBackground(new Color(0, 0, 102));
        BStudents.setBackground(new Color(0, 0, 102));
        jStudents.setBackground(new Color(0, 0, 102));
        BClasses.setBackground(new Color(0, 0, 102));
        jClasses.setBackground(new Color(0, 0, 102));
        BReports.setBackground(new Color(0, 0, 102));
        jReports.setBackground(new Color(0, 0, 102));
        BQrCodeGen.setBackground(new Color(0, 0, 102));
        jQrCodeGen.setBackground(new Color(0, 0, 102));
    }//GEN-LAST:event_BSubjectMouseClicked

    private void BQrCodeGenMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_BQrCodeGenMouseClicked
        // TODO add your handling code here:
        refreshData();
        jTabbedPane1.setSelectedIndex(12);
        jSettings.setBackground(new Color(0, 0, 102));
        bSettings.setBackground(new Color(0, 0, 102));
        jAttendance.setBackground(new Color(0, 0, 102));
        bAttendance.setBackground(new Color(0, 0, 102));
        jDropOut.setBackground(new Color(0, 0, 102));
        bDropOut.setBackground(new Color(0, 0, 102));
        jTeachers.setBackground(new Color(0, 0, 102));
        BTeachers.setBackground(new Color(0, 0, 102));
        BSubject.setBackground(new Color(0, 0, 102));
        jSubjects.setBackground(new Color(0, 0, 102));
        BAssignTc.setBackground(new Color(0, 0, 102));
        jAssignTc.setBackground(new Color(0, 0, 102));
        BAssignSub.setBackground(new Color(0, 0, 102));
        jAssignSub.setBackground(new Color(0, 0, 102));
        jNotification.setBackground(new Color(0, 0, 102));
        BNotification.setBackground(new Color(0, 0, 102));
        BDashboard.setBackground(new Color(0, 0, 102));
        jDashboard.setBackground(new Color(0, 0, 102));
        BStudents.setBackground(new Color(0, 0, 102));
        jStudents.setBackground(new Color(0, 0, 102));
        BClasses.setBackground(new Color(0, 0, 102));
        jClasses.setBackground(new Color(0, 0, 102));
        BReports.setBackground(new Color(0, 0, 102));
        jReports.setBackground(new Color(0, 0, 102));
        BQrCodeGen.setBackground(new Color(0, 0, 153));
        jQrCodeGen.setBackground(new Color(0, 0, 153));
    }//GEN-LAST:event_BQrCodeGenMouseClicked

    private void BNotificationMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_BNotificationMouseClicked
        // TODO add your handling code here:
        refreshData();
        jTabbedPane1.setSelectedIndex(5);
        jSettings.setBackground(new Color(0, 0, 102));
        bSettings.setBackground(new Color(0, 0, 102));
        jAttendance.setBackground(new Color(0, 0, 102));
        bAttendance.setBackground(new Color(0, 0, 102));
        jDropOut.setBackground(new Color(0, 0, 102));
        bDropOut.setBackground(new Color(0, 0, 102));
        jTeachers.setBackground(new Color(0, 0, 102));
        BTeachers.setBackground(new Color(0, 0, 102));
        BSubject.setBackground(new Color(0, 0, 102));
        jSubjects.setBackground(new Color(0, 0, 102));
        BAssignTc.setBackground(new Color(0, 0, 102));
        jAssignTc.setBackground(new Color(0, 0, 102));
        BAssignSub.setBackground(new Color(0, 0, 102));
        jAssignSub.setBackground(new Color(0, 0, 102));
        jNotification.setBackground(new Color(0, 0, 153));
        BNotification.setBackground(new Color(0, 0, 153));
        BDashboard.setBackground(new Color(0, 0, 102));
        jDashboard.setBackground(new Color(0, 0, 102));
        BStudents.setBackground(new Color(0, 0, 102));
        jStudents.setBackground(new Color(0, 0, 102));
        BClasses.setBackground(new Color(0, 0, 102));
        jClasses.setBackground(new Color(0, 0, 102));
        BReports.setBackground(new Color(0, 0, 102));
        jReports.setBackground(new Color(0, 0, 102));
        BQrCodeGen.setBackground(new Color(0, 0, 102));
        jQrCodeGen.setBackground(new Color(0, 0, 102));
    }//GEN-LAST:event_BNotificationMouseClicked

    private void BReportsMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_BReportsMouseClicked
        // TODO add your handling code here:
        refreshData();
        jTabbedPane1.setSelectedIndex(4);
        jSettings.setBackground(new Color(0, 0, 102));
        bSettings.setBackground(new Color(0, 0, 102));
        jAttendance.setBackground(new Color(0, 0, 102));
        bAttendance.setBackground(new Color(0, 0, 102));
        jDropOut.setBackground(new Color(0, 0, 102));
        bDropOut.setBackground(new Color(0, 0, 102));
        jTeachers.setBackground(new Color(0, 0, 102));
        BTeachers.setBackground(new Color(0, 0, 102));
        BSubject.setBackground(new Color(0, 0, 102));
        jSubjects.setBackground(new Color(0, 0, 102));
        BAssignTc.setBackground(new Color(0, 0, 102));
        jAssignTc.setBackground(new Color(0, 0, 102));
        BAssignSub.setBackground(new Color(0, 0, 102));
        jAssignSub.setBackground(new Color(0, 0, 102));
        jNotification.setBackground(new Color(0, 0, 102));
        BNotification.setBackground(new Color(0, 0, 102));
        BDashboard.setBackground(new Color(0, 0, 102));
        jDashboard.setBackground(new Color(0, 0, 102));
        BStudents.setBackground(new Color(0, 0, 102));
        jStudents.setBackground(new Color(0, 0, 102));
        BClasses.setBackground(new Color(0, 0, 102));
        jClasses.setBackground(new Color(0, 0, 102));
        BReports.setBackground(new Color(0, 0, 153));
        jReports.setBackground(new Color(0, 0, 153));
        BQrCodeGen.setBackground(new Color(0, 0, 102));
        jQrCodeGen.setBackground(new Color(0, 0, 102));
    }//GEN-LAST:event_BReportsMouseClicked

    private void BClassesMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_BClassesMouseClicked
        // TODO add your handling code here:
        refreshData();
        jTabbedPane1.setSelectedIndex(3);
        jSettings.setBackground(new Color(0, 0, 102));
        bSettings.setBackground(new Color(0, 0, 102));
        jAttendance.setBackground(new Color(0, 0, 102));
        bAttendance.setBackground(new Color(0, 0, 102));
        jDropOut.setBackground(new Color(0, 0, 102));
        bDropOut.setBackground(new Color(0, 0, 102));
        jTeachers.setBackground(new Color(0, 0, 102));
        BTeachers.setBackground(new Color(0, 0, 102));
        BSubject.setBackground(new Color(0, 0, 102));
        jSubjects.setBackground(new Color(0, 0, 102));
        BAssignTc.setBackground(new Color(0, 0, 102));
        jAssignTc.setBackground(new Color(0, 0, 102));
        BAssignSub.setBackground(new Color(0, 0, 102));
        jAssignSub.setBackground(new Color(0, 0, 102));
        jNotification.setBackground(new Color(0, 0, 102));
        BNotification.setBackground(new Color(0, 0, 102));
        BDashboard.setBackground(new Color(0, 0, 102));
        jDashboard.setBackground(new Color(0, 0, 102));
        BStudents.setBackground(new Color(0, 0, 102));
        jStudents.setBackground(new Color(0, 0, 102));
        BClasses.setBackground(new Color(0, 0, 153));
        jClasses.setBackground(new Color(0, 0, 153));
        BReports.setBackground(new Color(0, 0, 102));
        jReports.setBackground(new Color(0, 0, 102));
        BQrCodeGen.setBackground(new Color(0, 0, 102));
        jQrCodeGen.setBackground(new Color(0, 0, 102));
    }//GEN-LAST:event_BClassesMouseClicked

    private void BStudentsMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_BStudentsMouseClicked
        // TODO add your handling code here:
        refreshData();
        jTabbedPane1.setSelectedIndex(2);
        jSettings.setBackground(new Color(0, 0, 102));
        bSettings.setBackground(new Color(0, 0, 102));
        jAttendance.setBackground(new Color(0, 0, 102));
        bAttendance.setBackground(new Color(0, 0, 102));
        jDropOut.setBackground(new Color(0, 0, 102));
        bDropOut.setBackground(new Color(0, 0, 102));
        jTeachers.setBackground(new Color(0, 0, 102));
        BTeachers.setBackground(new Color(0, 0, 102));
        BSubject.setBackground(new Color(0, 0, 102));
        jSubjects.setBackground(new Color(0, 0, 102));
        BAssignTc.setBackground(new Color(0, 0, 102));
        jAssignTc.setBackground(new Color(0, 0, 102));
        BAssignSub.setBackground(new Color(0, 0, 102));
        jAssignSub.setBackground(new Color(0, 0, 102));
        jNotification.setBackground(new Color(0, 0, 102));
        BNotification.setBackground(new Color(0, 0, 102));
        BDashboard.setBackground(new Color(0, 0, 102));
        jDashboard.setBackground(new Color(0, 0, 102));
        BStudents.setBackground(new Color(0, 0, 153));
        jStudents.setBackground(new Color(0, 0, 153));
        BClasses.setBackground(new Color(0, 0, 102));
        jClasses.setBackground(new Color(0, 0, 102));
        BReports.setBackground(new Color(0, 0, 102));
        jReports.setBackground(new Color(0, 0, 102));
        BQrCodeGen.setBackground(new Color(0, 0, 102));
        jQrCodeGen.setBackground(new Color(0, 0, 102));
    }//GEN-LAST:event_BStudentsMouseClicked

    private void BDashboardMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_BDashboardMouseClicked
        // TODO add your handling code here:
        refreshData();
        jTabbedPane1.setSelectedIndex(0);
        jSettings.setBackground(new Color(0, 0, 102));
        bSettings.setBackground(new Color(0, 0, 102));
        jAttendance.setBackground(new Color(0, 0, 102));
        bAttendance.setBackground(new Color(0, 0, 102));
        jDropOut.setBackground(new Color(0, 0, 102));
        bDropOut.setBackground(new Color(0, 0, 102));
        jTeachers.setBackground(new Color(0, 0, 102));
        BTeachers.setBackground(new Color(0, 0, 102));
        BSubject.setBackground(new Color(0, 0, 102));
        jSubjects.setBackground(new Color(0, 0, 102));
        BAssignTc.setBackground(new Color(0, 0, 102));
        jAssignTc.setBackground(new Color(0, 0, 102));
        BAssignSub.setBackground(new Color(0, 0, 102));
        jAssignSub.setBackground(new Color(0, 0, 102));
        jNotification.setBackground(new Color(0, 0, 102));
        BNotification.setBackground(new Color(0, 0, 102));
        BDashboard.setBackground(new Color(0, 0, 153));
        jDashboard.setBackground(new Color(0, 0, 153));
        BStudents.setBackground(new Color(0, 0, 102));
        jStudents.setBackground(new Color(0, 0, 102));
        BClasses.setBackground(new Color(0, 0, 102));
        jClasses.setBackground(new Color(0, 0, 102));
        BReports.setBackground(new Color(0, 0, 102));
        jReports.setBackground(new Color(0, 0, 102));
        BQrCodeGen.setBackground(new Color(0, 0, 102));
        jQrCodeGen.setBackground(new Color(0, 0, 102));
    }//GEN-LAST:event_BDashboardMouseClicked

    private void jButton14ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton14ActionPerformed
        // TODO add your handling code here:
        try {

            JFileChooser dirChooser = new JFileChooser();
            dirChooser.setDialogTitle("Select Directory to Save QR Codes");
            dirChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
            int userSelection = dirChooser.showSaveDialog(null);

            if (userSelection != JFileChooser.APPROVE_OPTION) {
                return;
            }

            File selectedDir = dirChooser.getSelectedFile();
            String targetDirectory = selectedDir.getAbsolutePath() + File.separator;

            int rowCount = tableWithoutQR.getRowCount();
            if (rowCount == 0) {
                JOptionPane.showMessageDialog(null, "No students without QR codes.");
                return;
            }

            for (int i = 0; i < rowCount; i++) {
                String qrCodeData = tableWithoutQR.getValueAt(i, 1).toString().trim();

                if (qrCodeData.isEmpty()) {
                    continue;
                }

                String studentID = tableWithoutQR.getValueAt(i, 0).toString();

                String filePath = targetDirectory + qrCodeData.replaceAll("[^a-zA-Z0-9]", "_") + "_QR.png";

                String charset = "UTF-8";
                Map<EncodeHintType, ErrorCorrectionLevel> hintMap = new HashMap<>();
                hintMap.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.L);

                BitMatrix matrix = new MultiFormatWriter().encode(
                    new String(qrCodeData.getBytes(charset), charset),
                    BarcodeFormat.QR_CODE,
                    500,
                    500,
                    hintMap
                );

                Path path = new File(filePath).toPath();
                MatrixToImageWriter.writeToPath(matrix, "PNG", path);

                Connection con = Prototype.getConnection();
                PreparedStatement psUpdate = con.prepareStatement("UPDATE students SET qr_code_data = ? WHERE students_id = ?");
                psUpdate.setString(1, qrCodeData);
                psUpdate.setString(2, studentID);
                psUpdate.executeUpdate();

                PreparedStatement psInsert = con.prepareStatement("INSERT INTO qrcode (qrcodedata, qrcodefilepath) VALUES (?, ?)");
                psInsert.setString(1, qrCodeData);
                psInsert.setString(2, filePath);
                psInsert.executeUpdate();
            }

            JOptionPane.showMessageDialog(null, "QR Codes generated and saved successfully for all students!");

            loadStudentsWithoutQR();
            loadStudentsWithQR();

        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(null, "Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }//GEN-LAST:event_jButton14ActionPerformed

    private void txtSearch8KeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtSearch8KeyTyped
        // TODO add your handling code here:
        DefaultTableModel ob =  (DefaultTableModel) tableWithQR.getModel();
        TableRowSorter<DefaultTableModel> obj = new TableRowSorter<> (ob);
        tableWithQR.setRowSorter(obj);
        obj.setRowFilter(RowFilter.regexFilter(txtSearch8.getText()));
    }//GEN-LAST:event_txtSearch8KeyTyped

    private void txtSearch8ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtSearch8ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtSearch8ActionPerformed

    private void lblQrDataKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_lblQrDataKeyTyped
        // TODO add your handling code here:
    }//GEN-LAST:event_lblQrDataKeyTyped

    private void tableWithoutQRMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tableWithoutQRMouseClicked
        // TODO add your handling code here:
        int selectedRow = tableWithoutQR.getSelectedRow();

        if (selectedRow != -1) {
            String studentName = tableWithoutQR.getValueAt(selectedRow, 1).toString(); // Column 1 = Student Name
            lblQrData.setText(studentName);
            qrCodeLabel.setIcon(null);
        }
    }//GEN-LAST:event_tableWithoutQRMouseClicked

    private void tableWithQRMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tableWithQRMouseClicked
        // TODO add your handling code here:
        int row = tableWithQR.getSelectedRow();
        if (row != -1) {

            String qrCodeData = tableWithQR.getValueAt(row, 4).toString();

            String studentName = tableWithQR.getValueAt(row, 1).toString();
            lblQrData.setText(studentName);

            try {

                Connection con = Prototype.getConnection();
                PreparedStatement ps = con.prepareStatement("SELECT qrcodefilepath FROM qrcode WHERE qrcodedata = ?");
                ps.setString(1, qrCodeData);
                ResultSet rs = ps.executeQuery();

                if (rs.next()) {
                    String filePath = rs.getString("qrcodefilepath");
                    File file = new File(filePath);

                    if (file.exists()) {

                        ImageIcon qrIcon = new ImageIcon(filePath);
                        Image image = qrIcon.getImage().getScaledInstance(200, 200, Image.SCALE_SMOOTH);
                        qrCodeLabel.setIcon(new ImageIcon(image));
                    } else {

                        int choice = JOptionPane.showConfirmDialog(null, "QR Code image not found. Do you want to regenerate it?", "Missing QR Image", JOptionPane.YES_NO_OPTION);
                        if (choice == JOptionPane.YES_OPTION) {

                            JFileChooser fileChooser = new JFileChooser();
                            fileChooser.setDialogTitle("Save Regenerated QR Code");
                            fileChooser.setSelectedFile(new File(qrCodeData + "_QR.png"));

                            int userSelection = fileChooser.showSaveDialog(null);
                            if (userSelection == JFileChooser.APPROVE_OPTION) {
                                File newFile = fileChooser.getSelectedFile();
                                String newFilePath = newFile.getAbsolutePath();

                                generateQRCodeImage(qrCodeData, newFilePath);

                                PreparedStatement update = con.prepareStatement("UPDATE qrcode SET qrcodefilepath = ? WHERE qrcodedata = ?");
                                update.setString(1, newFilePath);
                                update.setString(2, qrCodeData);
                                update.executeUpdate();

                                ImageIcon qrIcon = new ImageIcon(newFilePath);
                                Image image = qrIcon.getImage().getScaledInstance(200, 200, Image.SCALE_SMOOTH);
                                qrCodeLabel.setIcon(new ImageIcon(image));

                                JOptionPane.showMessageDialog(null, "QR Code regenerated and saved successfully!");
                            } else {
                                JOptionPane.showMessageDialog(null, "Save cancelled. QR Code not regenerated.", "Info", JOptionPane.INFORMATION_MESSAGE);
                            }
                        } else {
                            JOptionPane.showMessageDialog(null, "No QR image available.", "Notice", JOptionPane.INFORMATION_MESSAGE);
                        }
                    }
                }

            } catch (Exception ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(null, "Error loading QR Code image: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }//GEN-LAST:event_tableWithQRMouseClicked

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jButton1ActionPerformed

    private void jButton1MouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jButton1MouseEntered
        // TODO add your handling code here:
    }//GEN-LAST:event_jButton1MouseEntered

    private void jButton1MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jButton1MouseClicked
        // TODO add your handling code here:
        try {
            String qrCodeData = lblQrData.getText().trim();

            if (qrCodeData.isEmpty()) {
                JOptionPane.showMessageDialog(null, "Please enter a name for the QR code!", "Warning", JOptionPane.WARNING_MESSAGE);
                return;
            }

            int selectedRow = tableWithoutQR.getSelectedRow();
            if (selectedRow == -1) {
                JOptionPane.showMessageDialog(null, "Please select a student from the 'No QR Code' table.");
                return;
            }

            String studentID = tableWithoutQR.getValueAt(selectedRow, 0).toString();

            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setDialogTitle("Save QR Code");
            fileChooser.setSelectedFile(new File(qrCodeData + "_QR.png"));

            int userSelection = fileChooser.showSaveDialog(null);
            if (userSelection != JFileChooser.APPROVE_OPTION) return;

            File file = fileChooser.getSelectedFile();
            String filePath = file.getAbsolutePath();

            String charset = "UTF-8";
            Map<EncodeHintType, ErrorCorrectionLevel> hintMap = new HashMap<>();
            hintMap.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.L);

            BitMatrix matrix = new MultiFormatWriter().encode(
                new String(qrCodeData.getBytes(charset), charset),
                BarcodeFormat.QR_CODE,
                500,
                500,
                hintMap
            );

            Path path = file.toPath();
            MatrixToImageWriter.writeToPath(matrix, "PNG", path);

            ImageIcon qrIcon = new ImageIcon(filePath);
            Image image = qrIcon.getImage().getScaledInstance(200, 200, Image.SCALE_SMOOTH);
            qrCodeLabel.setIcon(new ImageIcon(image));

            Connection con = Prototype.getConnection();
            PreparedStatement psUpdate = con.prepareStatement("UPDATE students SET qr_code_data = ? WHERE students_id = ?");
            psUpdate.setString(1, qrCodeData);
            psUpdate.setString(2, studentID);
            psUpdate.executeUpdate();

            PreparedStatement psInsert = con.prepareStatement("INSERT INTO qrcode (qrcodedata, qrcodefilepath) VALUES (?, ?)");
            psInsert.setString(1, qrCodeData);
            psInsert.setString(2, filePath);
            psInsert.executeUpdate();

            JOptionPane.showMessageDialog(null, "QR Code generated and saved successfully!");

            loadStudentsWithoutQR();
            loadStudentsWithQR();
            lblQrData.setText("");

        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(null, "Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }//GEN-LAST:event_jButton1MouseClicked

    private void txtSearch6KeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtSearch6KeyTyped
        // TODO add your handling code here:
        try {
           
            String sid = txtSearch6.getText().trim();

            
            con = Prototype.getConnection();

            
            if (sid.isEmpty()) {
                pst = con.prepareStatement("SELECT students_id, students_name, class_name, parent_contact_number, qr_code_data, status FROM inactive_students");
            } else {
               
                pst = con.prepareStatement("SELECT students_id, students_name, class_name, parent_contact_number, qr_code_data, status FROM inactive_students WHERE students_id LIKE ? OR students_name LIKE ? OR parent_contact_number LIKE ? OR class_name LIKE ? OR qr_code_data LIKE ?");
                pst.setString(1, "%" + sid + "%");
                pst.setString(2, "%" + sid + "%");
                pst.setString(3, "%" + sid + "%");
                pst.setString(4, "%" + sid + "%");
                pst.setString(5, "%" + sid + "%");
            }

            
            rs = pst.executeQuery();

            
            DefaultTableModel model = (DefaultTableModel) tableDropout.getModel();

            
            model.setRowCount(0);

            
            while (rs.next()) {
                
                String studentId = rs.getString("students_id");
                String studentName = rs.getString("students_name");
                String classId = rs.getString("class_name");
                String parentContactNumber = rs.getString("parent_contact_number");
                String qrCodeData = rs.getString("qr_code_data");
                String status = rs.getString("status");

                
                model.addRow(new Object[]{studentId, studentName, classId, parentContactNumber, qrCodeData, status});
            }
        } catch (SQLException ex) {
            Logger.getLogger(User.class.getName()).log(Level.SEVERE, null, ex);
        }     
    }//GEN-LAST:event_txtSearch6KeyTyped

    private void txtSearch6ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtSearch6ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtSearch6ActionPerformed

    private void jButton11ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton11ActionPerformed
        // TODO add your handling code here:
        int selectedRow = tableDropout.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(null, "Please select a student to restore.");
            return;
        }

        int students_id = Integer.parseInt(tableDropout.getValueAt(selectedRow, 0).toString());
        String students_name = tableDropout.getValueAt(selectedRow, 1).toString();
        String className = tableDropout.getValueAt(selectedRow, 2).toString();
        int classId = getClassIdForName(className);
        String parent_contact_number = tableDropout.getValueAt(selectedRow, 3).toString();
        String qr_code_data = tableDropout.getValueAt(selectedRow, 4).toString();

        int confirm = JOptionPane.showConfirmDialog(null,
            "Are you sure you want to active this student?",
            "Confirm Actived", JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            Connection con = null;
            PreparedStatement pstInsert = null;
            PreparedStatement pstDelete = null;

            try {
                con = Prototype.getConnection();
                con.setAutoCommit(false);

                String insertQuery = "INSERT INTO students (students_id, students_name, class_id, parent_contact_number, qr_code_data, status) "
                + "VALUES (?, ?, ?, ?, ?, 'Active')";
                pstInsert = con.prepareStatement(insertQuery);
                pstInsert.setInt(1, students_id);
                pstInsert.setString(2, students_name);
                pstInsert.setInt(3, classId);
                pstInsert.setString(4, parent_contact_number);
                pstInsert.setString(5, qr_code_data);
                pstInsert.executeUpdate();

                String deleteQuery = "DELETE FROM inactive_students WHERE students_id=?";
                pstDelete = con.prepareStatement(deleteQuery);
                pstDelete.setInt(1, students_id);
                pstDelete.executeUpdate();

                con.commit();

                JOptionPane.showMessageDialog(null, "Student actived successfully.");
                loadDropOutToTable();
                loadStudentsToTable();

            } catch (SQLException e) {
                if (con != null) {
                    try {
                        con.rollback();
                    } catch (SQLException ex) {
                        ex.printStackTrace();
                    }
                }
                e.printStackTrace();
                JOptionPane.showMessageDialog(null, "Error restoring student.");
            } finally {
                try {
                    if (pstInsert != null) {
                        pstInsert.close();
                    }
                    if (pstDelete != null) {
                        pstDelete.close();
                    }
                    if (con != null) {
                        con.setAutoCommit(true);
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }//GEN-LAST:event_jButton11ActionPerformed

    private void txtSearchKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtSearchKeyTyped
        // TODO add your handling code here:
        DefaultTableModel ob = (DefaultTableModel) tableAccount.getModel();
        TableRowSorter<DefaultTableModel> obj = new TableRowSorter<>(ob);
        tableAccount.setRowSorter(obj);
        obj.setRowFilter(RowFilter.regexFilter(txtSearch.getText()));
    }//GEN-LAST:event_txtSearchKeyTyped

    private void contactnumberActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_contactnumberActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_contactnumberActionPerformed

    private void mnameActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnameActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_mnameActionPerformed

    private void tableAccountMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tableAccountMouseClicked
        // TODO add your handling code here:
        int selectedRow = tableAccount.getSelectedRow();

        if (selectedRow >= 0) {
            int userId = Integer.parseInt(tableAccount.getValueAt(selectedRow, 0).toString());
            fetchUserData(userId);
        }
    }//GEN-LAST:event_tableAccountMouseClicked

    private void btnDeleteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnDeleteActionPerformed
        // TODO add your handling code here:
        int selectedRow = tableAccount.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(null, "Please select a user to delete.");
            return;
        }

        int userId = Integer.parseInt(tableAccount.getValueAt(selectedRow, 0).toString());

        int confirm = JOptionPane.showConfirmDialog(null, "Are you sure you want to delete this user?", "Confirm Delete", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            String query = "DELETE FROM login_table WHERE user_id=?";

            try {
                con = Prototype.getConnection();
                PreparedStatement pst = con.prepareStatement(query);
                pst.setInt(1, userId);

                int rowsDeleted = pst.executeUpdate();
                if (rowsDeleted > 0) {
                    JOptionPane.showMessageDialog(null, "User deleted successfully.");
                }
                loadUsersToTable();

            } catch (SQLException e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(null, "Error deleting user.");
            }
        }
    }//GEN-LAST:event_btnDeleteActionPerformed

    private void btnUpdateActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnUpdateActionPerformed
        // TODO add your handling code here:
        int selectedRow = tableAccount.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(null, "Please select a user to update.");
            return;
        }

        int userId = Integer.parseInt(tableAccount.getValueAt(selectedRow, 0).toString());

        String query = "UPDATE login_table SET firstname=?, middlename=?, lastname=?, gender=?, email=?, age=?, contact=?, username=?, password=?, role=? WHERE user_id=?";

        try {
            con = Prototype.getConnection();
            PreparedStatement pst = con.prepareStatement(query);

            pst.setString(1, fname.getText());
            pst.setString(2, mname.getText());
            pst.setString(3, lname.getText());
            pst.setString(4, gend.getText());
            pst.setString(5, em.getText());
            pst.setInt(6, Integer.parseInt(ages.getText()));
            pst.setString(7, contactnumber.getText());
            pst.setString(8, user.getText());
            pst.setString(9, pass.getText());
            pst.setString(10, jComboRole.getSelectedItem().toString());
            pst.setInt(11, userId);

            int rowsUpdated = pst.executeUpdate();
            if (rowsUpdated > 0) {
                JOptionPane.showMessageDialog(null, "User updated successfully.");
                loadUsersToTable();
            }

        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Error updating user.");
        }
    }//GEN-LAST:event_btnUpdateActionPerformed

    private void btnAddActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAddActionPerformed

        try {
            String firstname = fname.getText().trim();
            String middlename = mname.getText().trim();
            String lastname = lname.getText().trim();
            String gender = gend.getText().trim();
            String email = em.getText().trim();
            String age = ages.getText().trim();
            String contact = contactnumber.getText().trim();
            String username = user.getText().trim();
            String password = pass.getText().trim();
            String role = jComboRole.getSelectedItem().toString().trim();

            if (firstname.isEmpty() || lastname.isEmpty() || gender.isEmpty() || email.isEmpty() || age.isEmpty()
                || contact.isEmpty() || username.isEmpty() || password.isEmpty() || role.isEmpty()) {
                JOptionPane.showMessageDialog(rootPane, "No input! Please fill in all fields.");
                return;
            }

            con = Prototype.getConnection();

            String checkQuery = "SELECT COUNT(*) FROM login_table WHERE username = ? OR email = ? OR contact = ?";
            PreparedStatement checkStmt = con.prepareStatement(checkQuery);
            checkStmt.setString(1, username);
            checkStmt.setString(2, email);
            checkStmt.setString(3, contact);

            ResultSet rs = checkStmt.executeQuery();
            rs.next();
            int count = rs.getInt(1);

            if (count > 0) {
                JOptionPane.showMessageDialog(rootPane, "Username, email, or contact already exists! Please use a different one.");
                return;
            }

            pst = con.prepareStatement("INSERT INTO login_table (firstname, middlename, lastname, gender, email, age, contact, username, password, role) VALUES(?,?,?,?,?,?,?,?,?,?)");
            pst.setString(1, firstname);
            pst.setString(2, middlename);
            pst.setString(3, lastname);
            pst.setString(4, gender);
            pst.setString(5, email);
            pst.setString(6, age);
            pst.setString(7, contact);
            pst.setString(8, username);
            pst.setString(9, password);
            pst.setString(10, role);

            int k = pst.executeUpdate();

            if (k == 1) {
                JOptionPane.showMessageDialog(rootPane, "Record added successfully!");

                fname.setText("");
                mname.setText("");
                lname.setText("");
                gend.setText("");
                em.setText("");
                ages.setText("");
                contactnumber.setText("");
                user.setText("");
                pass.setText("");
                jComboRole.setSelectedIndex(0);
            } else {
                JOptionPane.showMessageDialog(rootPane, "Record failed");
            }

            loadUsersToTable();

        } catch (SQLException ex) {
            Logger.getLogger(User.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_btnAddActionPerformed

    private void bSendActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_bSendActionPerformed
        // TODO add your handling code here:
        String number = numberTextField.getText();
        String message = messageTextArea.getText();

        if (!number.matches("\\d{12}")) {
            JOptionPane.showMessageDialog(null, "Invalid number! Enter exactly 12 digits.", "Input Error", JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (message.isEmpty()) {
            JOptionPane.showMessageDialog(null, "Please enter a message!", "Input Error", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            sendSmsManual(number, message);
            loadSMSLogsToTable1();
        } catch (UnsupportedEncodingException ex) {
            ex.printStackTrace();
        }
    }//GEN-LAST:event_bSendActionPerformed

    private void numberTextFieldKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_numberTextFieldKeyTyped
        // TODO add your handling code here:
        char c = evt.getKeyChar();
        if (!Character.isDigit(c)) {
            Toolkit.getDefaultToolkit().beep();
            evt.consume();
        }
        if (numberTextField.getText().length() >= 12) {
            evt.consume();
        }
    }//GEN-LAST:event_numberTextFieldKeyTyped

    private void numberTextFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_numberTextFieldActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_numberTextFieldActionPerformed

    private void jButton12ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton12ActionPerformed
        // TODO add your handling code here:
        importFromCSV();
        totalstudents();
    }//GEN-LAST:event_jButton12ActionPerformed

    private void jButton9ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton9ActionPerformed
        // TODO add your handling code here:
        int selectedRow = tableStudent.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(null, "Please select a student to inactive.");
            return;
        }

        
        for (int i = 0; i <= 4; i++) {
            if (tableStudent.getValueAt(selectedRow, i) == null) {
                JOptionPane.showMessageDialog(null, "Selected row contains incomplete student data.");
                return;
            }
        }

        int students_id = Integer.parseInt(tableStudent.getValueAt(selectedRow, 0).toString());
        String students_name = tableStudent.getValueAt(selectedRow, 1).toString();
        String className = tableStudent.getValueAt(selectedRow, 2).toString(); 
        String parent_contact_number = tableStudent.getValueAt(selectedRow, 3).toString();
        String qr_code_data = tableStudent.getValueAt(selectedRow, 4).toString();

        int confirm = JOptionPane.showConfirmDialog(null, 
            "Are you sure you want to inactive this student?", 
            "Confirm Inactive", JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            Connection con = null;
            PreparedStatement pstInsert = null;
            PreparedStatement pstDelete = null;

            try {
                con = Prototype.getConnection();
                con.setAutoCommit(false); 

                String insertQuery = "INSERT INTO inactive_students (students_id, students_name, class_name, parent_contact_number, qr_code_data) " +
                                     "VALUES (?, ?, ?, ?, ?)";
                pstInsert = con.prepareStatement(insertQuery);
                pstInsert.setInt(1, students_id);
                pstInsert.setString(2, students_name);
                pstInsert.setString(3, className);
                pstInsert.setString(4, parent_contact_number);
                pstInsert.setString(5, qr_code_data);
                pstInsert.executeUpdate();

                String deleteQuery = "DELETE FROM students WHERE students_id=?";
                pstDelete = con.prepareStatement(deleteQuery);
                pstDelete.setInt(1, students_id);
                pstDelete.executeUpdate();

                con.commit(); 

                JOptionPane.showMessageDialog(null, "Student inactive successfully.");
                loadStudentsToTable(); 

            } catch (SQLException e) {
                if (con != null) {
                    try {
                        con.rollback(); 
                    } catch (SQLException ex) {
                        ex.printStackTrace();
                    }
                }
                e.printStackTrace();
                JOptionPane.showMessageDialog(null, "Error inactive student.");
            } finally {
                try {
                    if (pstInsert != null) pstInsert.close();
                    if (pstDelete != null) pstDelete.close();
                    if (con != null) con.setAutoCommit(true); 
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }

    }//GEN-LAST:event_jButton9ActionPerformed

    private void txtSearch1KeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtSearch1KeyTyped
        // TODO add your handling code here:
        try {
            
            String sid = txtSearch1.getText().trim();

            
            con = Prototype.getConnection();

           
            if (sid.isEmpty()) {
                pst = con.prepareStatement("SELECT s.students_id, s.students_name, c.class_name, s.parent_contact_number, s.qr_code_data, s.status FROM students s JOIN classes c ON s.class_id = c.class_id ");
            } else {
                
                pst = con.prepareStatement("SELECT s.students_id, s.students_name, c.class_name, s.parent_contact_number, s.qr_code_data, s.status FROM students s JOIN classes c ON s.class_id = c.class_id WHERE s.students_id LIKE ? OR s.students_name LIKE ? OR c.class_name LIKE ? OR s.qr_code_data LIKE ? OR s.status LIKE ?");
                pst.setString(1, "%" + sid + "%");
                pst.setString(2, "%" + sid + "%");
                pst.setString(3, "%" + sid + "%");
                pst.setString(4, "%" + sid + "%");
                pst.setString(5, "%" + sid + "%");
                
            }

           
            rs = pst.executeQuery();

            
            DefaultTableModel model = (DefaultTableModel) tableStudent.getModel();

            
            model.setRowCount(0);

            
            while (rs.next()) {
                
                String studentId = rs.getString("students_id");
                String studentName = rs.getString("students_name");
                String classId = rs.getString("class_name");
                String parentContactNumber = rs.getString("parent_contact_number");
                String qrCodeData = rs.getString("qr_code_data");
                String status = rs.getString("status");
                
                
                model.addRow(new Object[]{studentId, studentName, classId, parentContactNumber, qrCodeData, status});
            }
            
            updatestudentscount();
        } catch (SQLException ex) {
            Logger.getLogger(User.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_txtSearch1KeyTyped

    private void txtSearch1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtSearch1ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtSearch1ActionPerformed

    private void cmbClassIdActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmbClassIdActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_cmbClassIdActionPerformed

    private void txtNumberKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtNumberKeyTyped
        // TODO add your handling code here:
        char c = evt.getKeyChar();
        if (!Character.isDigit(c)) {
            Toolkit.getDefaultToolkit().beep();
            evt.consume();
        }
        if (txtNumber.getText().length() >= 12) {
            evt.consume();
        }
    }//GEN-LAST:event_txtNumberKeyTyped

    private void tableStudentMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tableStudentMouseClicked
        // TODO add your handling code here:
        int selectedRow = tableStudent.getSelectedRow();

        if (selectedRow >= 0) {
            int students_id = Integer.parseInt(tableStudent.getValueAt(selectedRow, 0).toString());
            fetchStudentsData(students_id);
        }
    }//GEN-LAST:event_tableStudentMouseClicked

    private void btnDelete1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnDelete1ActionPerformed
        // TODO add your handling code here:
        int selectedRow = tableStudent.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(null, "Please select a student to delete.");
            return;
        }

        int students_id = Integer.parseInt(tableStudent.getValueAt(selectedRow, 0).toString());

        int confirm = JOptionPane.showConfirmDialog(null, "Are you sure you want to delete this student?", "Confirm Delete", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            String query = "DELETE FROM students WHERE students_id=?";

            try {
                con = Prototype.getConnection();
                PreparedStatement pst = con.prepareStatement(query);
                pst.setInt(1, students_id);

                int rowsDeleted = pst.executeUpdate();
                if (rowsDeleted > 0) {
                    JOptionPane.showMessageDialog(null, "Student deleted successfully.");
                    totalstudents();
                }
                loadStudentsToTable();

            } catch (SQLException e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(null, "Error deleting student.");
            }
        }
    }//GEN-LAST:event_btnDelete1ActionPerformed

    private void btnUpdate1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnUpdate1ActionPerformed
        // TODO add your handling code here:
        int selectedRow = tableStudent.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(null, "Please select a student to update.");
            return;
        }

        int students_id = Integer.parseInt(tableStudent.getValueAt(selectedRow, 0).toString());

        String className = cmbClassId.getSelectedItem().toString();
        int classId = getClassIdForName(className);

        String query = "UPDATE students SET students_name=?, class_id=?, parent_contact_number=?, qr_code_data=? WHERE students_id=?";

        try {
            con = Prototype.getConnection();
            PreparedStatement pst = con.prepareStatement(query);

            pst.setString(1, txtFname.getText());
            pst.setInt(2, classId);
            pst.setString(3, txtNumber.getText());
            pst.setString(4, txtQrData.getText());
            pst.setInt(5, students_id);

            int rowsUpdated = pst.executeUpdate();
            if (rowsUpdated > 0) {
                JOptionPane.showMessageDialog(null, "Student updated successfully.");
                loadStudentsToTable();
            } else {
                JOptionPane.showMessageDialog(null, "Error updating student.");
            }

        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Error updating student.");
        }
    }//GEN-LAST:event_btnUpdate1ActionPerformed

    private void btnAdd1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAdd1ActionPerformed
        // TODO add your handling code here:
        try {
            String fullname = txtFname.getText();
            String className = cmbClassId.getSelectedItem().toString();
            String number = txtNumber.getText();

            int classId = getClassIdForName(className);

            if (!number.matches("\\d{12}")) {
                JOptionPane.showMessageDialog(null, "Invalid number! Enter exactly 12 digits.", "Input Error", JOptionPane.WARNING_MESSAGE);
                return;
            }

            if (fullname.isEmpty() || className.isEmpty() || number.isEmpty()) {
                JOptionPane.showMessageDialog(rootPane, "No input! Please fill in all fields.");
                return;
            }

            con = Prototype.getConnection();
            pst = con.prepareStatement("INSERT INTO students (students_name, class_id, parent_contact_number) VALUES(?,?,?)");
            pst.setString(1, fullname);
            pst.setInt(2, classId);
            pst.setString(3, number);

            int k = pst.executeUpdate();

            if (k == 1) {
                JOptionPane.showMessageDialog(rootPane, "Record successfully added.");
                txtFname.setText("");
                cmbClassId.setSelectedIndex(-1);
                txtNumber.setText("");
                totalstudents();
            } else {
                JOptionPane.showMessageDialog(rootPane, "Record failed.");
            }

            loadStudentsToTable();
        } catch (SQLException ex) {
            Logger.getLogger(User.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_btnAdd1ActionPerformed

    private void cboxclassActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cboxclassActionPerformed
        // TODO add your handling code here:
        try {
            Object selectedItem = cboxclass.getSelectedItem();

            if (selectedItem == null || selectedItem.toString().equals("Select Class")) {
                return;
            }

            con = Prototype.getConnection();
            String selectedClass = selectedItem.toString();

            String getClassIdQuery = "SELECT class_id FROM classes WHERE class_name = ?";
            PreparedStatement pst1 = con.prepareStatement(getClassIdQuery);
            pst1.setString(1, selectedClass);
            ResultSet rs1 = pst1.executeQuery();

            if (rs1.next()) {
                int classId = rs1.getInt("class_id");

                String queryStudents = "SELECT students_name FROM students WHERE class_id = ?";
                PreparedStatement pst2 = con.prepareStatement(queryStudents);
                pst2.setInt(1, classId);
                ResultSet rs2 = pst2.executeQuery();

                DefaultTableModel model = (DefaultTableModel) tableAttendance.getModel();
                model.setRowCount(0);

                String today = java.time.LocalDate.now().toString();

                while (rs2.next()) {
                    model.addRow(new Object[]{
                        rs2.getString("students_name"),
                        selectedClass,
                        today,
                        null,
                        null,
                        "Absent"
                    });
                }

                rs2.close();
                pst2.close();
            }

            rs1.close();
            pst1.close();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }//GEN-LAST:event_cboxclassActionPerformed

    private void jButton13ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton13ActionPerformed
        try {
            con = Prototype.getConnection();
            String insertQuery = "INSERT INTO attendance_records (students_name, class_"
            + "id, date, time_in, time_out, status) VALUES (?, ?, ?, ?, ?, ?)";
            PreparedStatement pst = con.prepareStatement(insertQuery);

            DefaultTableModel model = (DefaultTableModel) tableAttendance.getModel();

            for (int row = 0; row < model.getRowCount(); row++) {

                String studentName = (model.getValueAt(row, 0) != null) ? model.getValueAt(row, 0).toString() : "";
                String classID = (model.getValueAt(row, 1) != null) ? model.getValueAt(row, 1).toString() : "";
                String date = (model.getValueAt(row, 2) != null) ? model.getValueAt(row, 2).toString().trim() : "";
                String timeIn = (model.getValueAt(row, 3) != null) ? model.getValueAt(row, 3).toString().trim() : "";
                String timeOut = (model.getValueAt(row, 4) != null) ? model.getValueAt(row, 4).toString().trim() : "";
                String status = (model.getValueAt(row, 5) != null) ? model.getValueAt(row, 5).toString() : "";

                pst.setString(1, studentName);
                pst.setString(2, classID);

                if (date.isEmpty()) {
                    pst.setNull(3, java.sql.Types.DATE);
                } else {
                    pst.setString(3, date);
                }

                if (timeIn.isEmpty()) {
                    pst.setNull(4, java.sql.Types.TIME);
                } else {
                    pst.setString(4, timeIn);
                }

                if (timeOut.isEmpty()) {
                    pst.setNull(5, java.sql.Types.TIME);
                } else {
                    pst.setString(5, timeOut);
                }

                pst.setString(6, status);

                pst.addBatch();

            }
            pst.executeBatch();
            loadAttendanceData();
            pst.close();
            lblStatus.setText("Attendance saved successfully!");

        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }//GEN-LAST:event_jButton13ActionPerformed

    private void btnTimeOutActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnTimeOutActionPerformed
        try {
            Connection con = Prototype.getConnection();
            java.util.Date date = new java.util.Date();

            SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm:ss");
            String formattedTimeOut = timeFormat.format(date);

            if (tableAttendance.getRowCount() == 0) {
                lblStatus.setText("No records to time out!");
                return;
            }

            for (int i = 0; i < tableAttendance.getRowCount(); i++) {
                Object studentObj = tableAttendance.getValueAt(i, 0);
                Object classObj = tableAttendance.getValueAt(i, 1);
                Object dateObj = tableAttendance.getValueAt(i, 2);
                Object timeInObj = tableAttendance.getValueAt(i, 3);

                if (studentObj == null || classObj == null || dateObj == null || timeInObj == null) {
                    continue;
                }

                String studentName = studentObj.toString();
                String classID = classObj.toString();

                // 🔧 Fix casting error here by converting the string to java.sql.Date
                java.sql.Date sqlDate = java.sql.Date.valueOf(dateObj.toString());

                PreparedStatement pst = con.prepareStatement(
                    "UPDATE attendance_records SET time_out = ? WHERE students_name = ? AND class_id = ? AND date = ? AND time_in IS NOT NULL"
                );

                pst.setString(1, formattedTimeOut);
                pst.setString(2, studentName);
                pst.setString(3, classID);
                pst.setDate(4, sqlDate);

                int updatedRows = pst.executeUpdate();
                pst.close();

                PreparedStatement getParentPst = con.prepareStatement(
                    "SELECT parent_contact_number FROM students WHERE students_name = ?"
                );
                getParentPst.setString(1, studentName);
                ResultSet rs = getParentPst.executeQuery();

                if (rs.next()) {
                    String parentNumber = rs.getString("parent_contact_number");

                    if (parentNumber != null && !parentNumber.isEmpty()) {
                        sendSmsTimeOut(parentNumber, studentName);
                    }
                }
                rs.close();
                getParentPst.close();
            }

            for (int i = 0; i < tableAttendance.getRowCount(); i++) {
                if (tableAttendance.getValueAt(i, 3) != null) {
                    tableAttendance.setValueAt(formattedTimeOut, i, 4);
                }
            }

            lblStatus.setText("Time out updated successfully!");

        } catch (SQLException | UnsupportedEncodingException ex) {
            Logger.getLogger(User.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_btnTimeOutActionPerformed

    private void TxtQrcodeKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_TxtQrcodeKeyTyped
        // TODO add your handling code here:
        try {
            markStudentPresent(TxtQrcode.getText().trim());
        } catch (UnsupportedEncodingException ex) {
            Logger.getLogger(User.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_TxtQrcodeKeyTyped

    private void TxtQrcodeKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_TxtQrcodeKeyReleased
        // TODO add your handling code here:
    }//GEN-LAST:event_TxtQrcodeKeyReleased

    private void TxtQrcodeKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_TxtQrcodeKeyPressed

    }//GEN-LAST:event_TxtQrcodeKeyPressed

    private void TxtQrcodeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_TxtQrcodeActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_TxtQrcodeActionPerformed

    private void lblTotalStudentsMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lblTotalStudentsMouseClicked
        // TODO add your handling code here:
        jTabbedPane1.setSelectedIndex(2);
    }//GEN-LAST:event_lblTotalStudentsMouseClicked

    private void lblTotalTeachersMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lblTotalTeachersMouseClicked
        // TODO add your handling code here:
        jTabbedPane1.setSelectedIndex(7);
    }//GEN-LAST:event_lblTotalTeachersMouseClicked

    private void lblInactiveMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lblInactiveMouseClicked
        // TODO add your handling code here:
        jTabbedPane1.setSelectedIndex(11);
    }//GEN-LAST:event_lblInactiveMouseClicked

    private void LOGOUTMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_LOGOUTMouseClicked
        // TODO add your handling code here:
    }//GEN-LAST:event_LOGOUTMouseClicked

    private void jButton4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton4ActionPerformed
        // TODO add your handling code here:
        generateExcelSummaryFromJTable(tableReport);
        //        try {
            //
            //            JFileChooser fileChooser = new JFileChooser();
            //            fileChooser.setDialogTitle("Select Folder to Save PDF");
            //            fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
            //
            //            int userSelection = fileChooser.showSaveDialog(null);
            //
            //            if (userSelection == JFileChooser.APPROVE_OPTION) {
                //                File selectedFolder = fileChooser.getSelectedFile();
                //                String filePath = selectedFolder.getAbsolutePath() + File.separator + "Attendance_Report.pdf";
                //
                //
                //                Document document = new Document(PageSize.A4);
                //                PdfWriter.getInstance(document, new FileOutputStream(filePath));
                //                document.open();
                //
                //
                //                Paragraph title = new Paragraph("Attendance Report",
                    //                    FontFactory.getFont(FontFactory.HELVETICA_BOLD, 16, Font.BOLD));
                //                title.setAlignment(Element.ALIGN_CENTER);
                //                title.setSpacingAfter(20);
                //                document.add(title);
                //
                //
                //                float[] columnWidths = {3f, 2f, 3f, 2f, 2f, 2f};
                //                PdfPTable table = new PdfPTable(columnWidths);
                //                table.setWidthPercentage(100);
                //                table.setSpacingBefore(10f);
                //
                //
                //                for (int i = 0; i < tableReport.getColumnCount(); i++) {
                    //                    PdfPCell header = new PdfPCell(new Phrase(tableReport.getColumnName(i),
                        //                    FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12)));
                //                    header.setHorizontalAlignment(Element.ALIGN_CENTER);
                //                    header.setPadding(5);
                //                    table.addCell(header);
                //            }
            //
            //
            //            for (int i = 0; i < tableReport.getRowCount(); i++) {
                //                for (int j = 0; j < tableReport.getColumnCount(); j++) {
                    //                    Object cellValue = tableReport.getValueAt(i, j);
                    //                    String cellText = (cellValue == null) ? "" : cellValue.toString();
                    //
                    //                PdfPCell cell = new PdfPCell(new Phrase(cellText,
                        //                FontFactory.getFont(FontFactory.HELVETICA, 11)));
                //                cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                //                cell.setPadding(5);
                //                table.addCell(cell);
                //            }
            //        }
        //
        //        document.add(table);
        //        document.close();
        //
        //        JOptionPane.showMessageDialog(null, "PDF Exported Successfully!\nSaved at: " + filePath,
            //            "Success", JOptionPane.INFORMATION_MESSAGE);
        //        }
        //        } catch (Exception e) {
        //            e.printStackTrace();
        //            JOptionPane.showMessageDialog(null, "Error exporting PDF: " + e.getMessage(),
            //                "Error", JOptionPane.ERROR_MESSAGE);
        //        }
    }//GEN-LAST:event_jButton4ActionPerformed

    private void jButton7ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton7ActionPerformed
        // TODO add your handling code here:
        DefaultTableModel model = (DefaultTableModel) tableReport.getModel();
        model.setRowCount(0);

        String query = "SELECT * FROM attendance_records WHERE 1=1";
        List<Object> parameters = new ArrayList<>();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

        if (dateChooserFromDate.getDate() != null && dateChooserEndDate.getDate() != null) {
            String fromDate = sdf.format(dateChooserFromDate.getDate());
            String endDate = sdf.format(dateChooserEndDate.getDate());

            query += " AND date BETWEEN ? AND ?";
            parameters.add(fromDate);
            parameters.add(endDate);
        }

        if (!txtStudentName.getText().trim().isEmpty()) {
            query += " AND students_name LIKE ?";
            parameters.add("%" + txtStudentName.getText().trim() + "%");
        }

        if (cboxClassReports.getSelectedItem() != null
            && !cboxClassReports.getSelectedItem().toString().equals("Select All Classes")) {
            query += " AND class_id = ?";
            parameters.add(cboxClassReports.getSelectedItem().toString());
        }

        if (!txtStatus.getText().trim().isEmpty()) {
            query += " AND status = ?";
            parameters.add(txtStatus.getText().trim());
        }

        try {
            con = Prototype.getConnection();
            PreparedStatement pst = con.prepareStatement(query);

            for (int i = 0; i < parameters.size(); i++) {
                pst.setObject(i + 1, parameters.get(i));
            }

            ResultSet rs = pst.executeQuery();

            while (rs.next()) {
                String studentName = rs.getString("students_name");
                String classId = rs.getString("class_id");
                String date = rs.getString("date");
                String timeIn = rs.getString("time_in");
                String timeOut = rs.getString("time_out");
                String status = rs.getString("status");

                model.addRow(new Object[]{studentName, classId, date, timeIn, timeOut, status});
            }

            if (model.getRowCount() == 0) {
                JOptionPane.showMessageDialog(this, "No records found.", "Search Result", JOptionPane.INFORMATION_MESSAGE);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }//GEN-LAST:event_jButton7ActionPerformed

    private void cboxClassReportsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cboxClassReportsActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_cboxClassReportsActionPerformed

    private void tableClassesMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tableClassesMouseClicked
        // TODO add your handling code here:
        int selectedRow = tableClasses.getSelectedRow();

        if (selectedRow >= 0) {
            int class_id = Integer.parseInt(tableClasses.getValueAt(selectedRow, 0).toString());
            fetchClassesData(class_id);
        }
    }//GEN-LAST:event_tableClassesMouseClicked

    private void tableClassesMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tableClassesMouseEntered
        // TODO add your handling code here:
    }//GEN-LAST:event_tableClassesMouseEntered

    private void txtSearch4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtSearch4ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtSearch4ActionPerformed

    private void txtSearch4KeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtSearch4KeyTyped
        // TODO add your handling code here:
        DefaultTableModel ob =  (DefaultTableModel) tableClasses.getModel();
        TableRowSorter<DefaultTableModel> obj = new TableRowSorter<> (ob);
        tableClasses.setRowSorter(obj);
        obj.setRowFilter(RowFilter.regexFilter(txtSearch4.getText()));
    }//GEN-LAST:event_txtSearch4KeyTyped

    private void btnAdd4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAdd4ActionPerformed
        // TODO add your handling code here:
        try {
            
            String grade = comboGrade.getSelectedItem().toString();      
            String section = txtSection.getText().trim().toUpperCase();  
            String schoolYear = txtSchoolYear.getText().trim();          

            
            if (grade.isEmpty() || section.isEmpty() || schoolYear.isEmpty()) {
                JOptionPane.showMessageDialog(rootPane, "No input! Please fill in all fields.");
                return;
            }

            
            String fullname = "GRADE " + grade + " - " + section + " - SY. " + schoolYear;

            con = Prototype.getConnection();
            pst = con.prepareStatement("INSERT INTO classes (class_name) VALUES(?)");
            pst.setString(1, fullname);

            int k = pst.executeUpdate();

            if (k == 1) {
                JOptionPane.showMessageDialog(rootPane, "Record successfully added.");
                comboGrade.setSelectedIndex(0); 
                txtSection.setText("");
                txtSchoolYear.setText("");
            } else {
                JOptionPane.showMessageDialog(rootPane, "Record failed.");
            }

            loadClassesToTable();

        } catch (SQLException ex) {
            Logger.getLogger(User.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_btnAdd4ActionPerformed

    private void btnUpdate4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnUpdate4ActionPerformed
        // TODO add your handling code here:
        int selectedRow = tableClasses.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(null, "Please select a class to update.");
            return;
        }

        int class_id = Integer.parseInt(tableClasses.getValueAt(selectedRow, 0).toString());

        String grade = comboGrade.getSelectedItem().toString();
        String section = txtSection.getText().trim().toUpperCase();
        String schoolYear = txtSchoolYear.getText().trim();

        if (grade.isEmpty() || section.isEmpty() || schoolYear.isEmpty()) {
            JOptionPane.showMessageDialog(null, "No input! Please fill in all fields.");
            return;
        }

        String className = "GRADE " + grade + " - " + section + " - SY. " + schoolYear;

        String query = "UPDATE classes SET class_name=? WHERE class_id=?";

        try {
            con = Prototype.getConnection();
            PreparedStatement pst = con.prepareStatement(query);

            pst.setString(1, className);
            pst.setInt(2, class_id);

            int rowsUpdated = pst.executeUpdate();
            if (rowsUpdated > 0) {
                JOptionPane.showMessageDialog(null, "Class updated successfully.");
                comboGrade.setSelectedIndex(0);
                txtSection.setText("");
                txtSchoolYear.setText("");
                loadClassesToTable();
            }

        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Error updating class.");
        }
    }//GEN-LAST:event_btnUpdate4ActionPerformed

    private void btnDelete4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnDelete4ActionPerformed
        // TODO add your handling code here:
        int selectedRow = tableClasses.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(null, "Please select a classes to delete.");
            return;
        }

        int class_id = Integer.parseInt(tableClasses.getValueAt(selectedRow, 0).toString());

        int confirm = JOptionPane.showConfirmDialog(null, "Are you sure you want to delete this classes?", "Confirm Delete", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            String query = "DELETE FROM classes WHERE class_id=?";

            try {
                con = Prototype.getConnection();
                PreparedStatement pst = con.prepareStatement(query);
                pst.setInt(1, class_id);

                int rowsDeleted = pst.executeUpdate();
                if (rowsDeleted > 0) {
                    JOptionPane.showMessageDialog(null, "Classes deleted successfully.");
                }
                loadClassesToTable();

            } catch (SQLException e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(null, "Delete first CLASS_ID in Students Information!");
            }
        }
    }//GEN-LAST:event_btnDelete4ActionPerformed

    private void cboxNoQrActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cboxNoQrActionPerformed
        // TODO add your handling code here:
        DefaultTableModel model = (DefaultTableModel) tableWithoutQR.getModel();
        model.setRowCount(0);

        try {
            con = Prototype.getConnection();

            Object selectedObj = cboxNoQr.getSelectedItem();
            if (selectedObj == null) {
                return;
            }

            String selectedClassName = selectedObj.toString();

            String query;
            PreparedStatement pst;

            if (selectedClassName.equals("Select All Classes")) {
                query = "SELECT s.students_id, s.students_name, c.class_name, s.parent_contact_number " +
                        "FROM students s " +
                        "JOIN classes c ON s.class_id = c.class_id " +
                        "WHERE s.qr_code_data IS NULL";
                pst = con.prepareStatement(query);
            } else {
                
                String classIdQuery = "SELECT class_id FROM classes WHERE class_name = ?";
                PreparedStatement classIdStmt = con.prepareStatement(classIdQuery);
                classIdStmt.setString(1, selectedClassName);
                ResultSet classIdResult = classIdStmt.executeQuery();

                if (!classIdResult.next()) {
                    return;
                }

                int classId = classIdResult.getInt("class_id");

                query = "SELECT s.students_id, s.students_name, c.class_name, s.parent_contact_number " +
                        "FROM students s " +
                        "JOIN classes c ON s.class_id = c.class_id " +
                        "WHERE s.qr_code_data IS NULL AND s.class_id = ?";
                pst = con.prepareStatement(query);
                pst.setInt(1, classId);
            }

            ResultSet rs = pst.executeQuery();

            while (rs.next()) {
                String id = rs.getString("students_id");
                String name = rs.getString("students_name");
                String className = rs.getString("class_name");
                String parentContact = rs.getString("parent_contact_number");

                model.addRow(new Object[]{id, name, className, parentContact});
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

    }//GEN-LAST:event_cboxNoQrActionPerformed

    private void tableTeachersMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tableTeachersMouseClicked
        // TODO add your handling code here:
        int selectedRow = tableTeachers.getSelectedRow();

        if (selectedRow >= 0) {
            int teacher_id = Integer.parseInt(tableTeachers.getValueAt(selectedRow, 0).toString());
            fetchTeachersData(teacher_id);
        }
    }//GEN-LAST:event_tableTeachersMouseClicked

    private void tableTeachersMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tableTeachersMouseEntered
        // TODO add your handling code here:
    }//GEN-LAST:event_tableTeachersMouseEntered

    private void txtSearch2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtSearch2ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtSearch2ActionPerformed

    private void txtSearch2KeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtSearch2KeyTyped
        // TODO add your handling code here:
        DefaultTableModel ob =  (DefaultTableModel) tableTeachers.getModel();
        TableRowSorter<DefaultTableModel> obj = new TableRowSorter<> (ob);
        tableTeachers.setRowSorter(obj);
        obj.setRowFilter(RowFilter.regexFilter(txtSearch2.getText()));
    }//GEN-LAST:event_txtSearch2KeyTyped

    private void btnAdd2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAdd2ActionPerformed
        // TODO add your handling code here:
        try {
            String fullname = Tcfname.getText();

            con = Prototype.getConnection();
            pst = con.prepareStatement("INSERT INTO teachers (teacher_name) VALUES(?)");
            pst.setString(1,fullname);

            if (fullname.isEmpty()) {

                JOptionPane.showMessageDialog(rootPane, "No input! Please fill in all fields.");
                return;
            }

            int k = pst.executeUpdate();

            if (k==1){
                JOptionPane.showMessageDialog(rootPane,"Record successfully");
                Tcfname.setText("");

            }else{
                JOptionPane.showMessageDialog(rootPane,"Record failed");
            }

            loadTeachersToTable();
        } catch (SQLException ex) {
            Logger.getLogger(User.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_btnAdd2ActionPerformed

    private void btnUpdate2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnUpdate2ActionPerformed
        // TODO add your handling code here:
        int selectedRow = tableTeachers.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(null, "Please select a teachers to update.");
            return;
        }

        int teacher_id = Integer.parseInt(tableTeachers.getValueAt(selectedRow, 0).toString());

        String query = "UPDATE teachers SET teacher_name=? WHERE teacher_id=?";

        try {
            con = Prototype.getConnection();
            PreparedStatement pst = con.prepareStatement(query);

            pst.setString(1, Tcfname.getText());
            pst.setInt(2, teacher_id);

            int rowsUpdated = pst.executeUpdate();
            if (rowsUpdated > 0) {
                JOptionPane.showMessageDialog(null, "Updated successfully.");
                loadTeachersToTable();
            }

        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Error updating.");
        }
    }//GEN-LAST:event_btnUpdate2ActionPerformed

    private void btnDelete2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnDelete2ActionPerformed
        // TODO add your handling code here:
        int selectedRow = tableTeachers.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(null, "Please select a teacher to delete.");
            return;
        }

        int teacher_id = Integer.parseInt(tableTeachers.getValueAt(selectedRow, 0).toString());

        int confirm = JOptionPane.showConfirmDialog(null, "Are you sure you want to delete this teacher?", "Confirm Delete", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            String query = "DELETE FROM teachers WHERE teacher_id=?";

            try {
                con = Prototype.getConnection();
                PreparedStatement pst = con.prepareStatement(query);
                pst.setInt(1, teacher_id);

                int rowsDeleted = pst.executeUpdate();
                if (rowsDeleted > 0) {
                    JOptionPane.showMessageDialog(null, "Teacher deleted successfully.");
                }
                loadTeachersToTable();

            } catch (SQLException e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(null, "Error deleting teacher.");
            }
        }
    }//GEN-LAST:event_btnDelete2ActionPerformed

    private void tableSubjectMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tableSubjectMouseClicked
        // TODO add your handling code here:
        int selectedRow = tableSubject.getSelectedRow();

        if (selectedRow >= 0) {
            int subject_id = Integer.parseInt(tableSubject.getValueAt(selectedRow, 0).toString());
            fetchSubjectData(subject_id);
        }
    }//GEN-LAST:event_tableSubjectMouseClicked

    private void tableSubjectMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tableSubjectMouseEntered
        // TODO add your handling code here:
    }//GEN-LAST:event_tableSubjectMouseEntered

    private void btnAdd3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAdd3ActionPerformed
        // TODO add your handling code here:
        try {
            String subjectname = Subjectname.getText();

            con = Prototype.getConnection();
            pst = con.prepareStatement("INSERT INTO subject (subject_name) VALUES(?)");
            pst.setString(1,subjectname);

            if (subjectname.isEmpty()) {

                JOptionPane.showMessageDialog(rootPane, "No input! Please fill in all fields.");
                return;
            }

            int k = pst.executeUpdate();

            if (k==1){
                JOptionPane.showMessageDialog(rootPane,"Record successfully");
                Subjectname.setText("");
            }else{
                JOptionPane.showMessageDialog(rootPane,"Record failed");
            }

            loadSubjectToTable();
        } catch (SQLException ex) {
            Logger.getLogger(User.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_btnAdd3ActionPerformed

    private void btnUpdate3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnUpdate3ActionPerformed
        // TODO add your handling code here:
        int selectedRow = tableSubject.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(null, "Please select a subject to update.");
            return;
        }

        int students_id = Integer.parseInt(tableSubject.getValueAt(selectedRow, 0).toString());

        String query = "UPDATE subject SET subject_name=? WHERE subject_id=?";

        try {
            con = Prototype.getConnection();
            PreparedStatement pst = con.prepareStatement(query);

            pst.setString(1, Subjectname.getText());
            pst.setInt(2, students_id);

            int rowsUpdated = pst.executeUpdate();
            if (rowsUpdated > 0) {
                JOptionPane.showMessageDialog(null, "Subject updated successfully.");
                loadSubjectToTable();
            }

        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Error updating subject.");
        }
    }//GEN-LAST:event_btnUpdate3ActionPerformed

    private void btnDelete3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnDelete3ActionPerformed
        // TODO add your handling code here:
        int selectedRow = tableSubject.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(null, "Please select a subject to delete.");
            return;
        }

        int subject_id = Integer.parseInt(tableSubject.getValueAt(selectedRow, 0).toString());

        int confirm = JOptionPane.showConfirmDialog(null, "Are you sure you want to delete this subject?", "Confirm Delete", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            String query = "DELETE FROM subject WHERE subject_id=?";

            try {
                con = Prototype.getConnection();
                PreparedStatement pst = con.prepareStatement(query);
                pst.setInt(1, subject_id);

                int rowsDeleted = pst.executeUpdate();
                if (rowsDeleted > 0) {
                    JOptionPane.showMessageDialog(null, "Subject deleted successfully.");
                }
                loadSubjectToTable();

            } catch (SQLException e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(null, "Error deleting subject.");
            }
        }
    }//GEN-LAST:event_btnDelete3ActionPerformed

    private void txtSearch7ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtSearch7ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtSearch7ActionPerformed

    private void txtSearch7KeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtSearch7KeyTyped
        // TODO add your handling code here:
        DefaultTableModel ob =  (DefaultTableModel) tableSubject.getModel();
        TableRowSorter<DefaultTableModel> obj = new TableRowSorter<> (ob);
        tableSubject.setRowSorter(obj);
        obj.setRowFilter(RowFilter.regexFilter(txtSearch7.getText()));
    }//GEN-LAST:event_txtSearch7KeyTyped

    private void tableAssignTeacherMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tableAssignTeacherMouseClicked
        // TODO add your handling code here:
        int selectedRow = tableAssignTeacher.getSelectedRow();

        if (selectedRow != -1) {

            String className = tableAssignTeacher.getValueAt(selectedRow, 1).toString();
            String teacherName = tableAssignTeacher.getValueAt(selectedRow, 2).toString();

            cmbClass1.setSelectedItem(className);
            cmbsubject1.setSelectedItem(teacherName);
        }
    }//GEN-LAST:event_tableAssignTeacherMouseClicked

    private void cmbClass1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmbClass1ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_cmbClass1ActionPerformed

    private void cmbsubject1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmbsubject1ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_cmbsubject1ActionPerformed

    private void jButton6ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton6ActionPerformed
        // TODO add your handling code here:
        assignClassToTeacher();
    }//GEN-LAST:event_jButton6ActionPerformed

    private void jButton8ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton8ActionPerformed
        // TODO add your handling code here:
        int selectedRow = tableAssignTeacher.getSelectedRow();

        if (selectedRow != -1) {
            int id = (int) tableAssignTeacher.getValueAt(selectedRow, 0);
            deleteAssignedClassTeacher(id);
        } else {
            JOptionPane.showMessageDialog(null, "Please select an assignment to delete!");
        }
    }//GEN-LAST:event_jButton8ActionPerformed

    private void jButton10ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton10ActionPerformed
        // TODO add your handling code here:
        int selectedRow = tableAssignTeacher.getSelectedRow();

        if (selectedRow != -1) {
            int id = (int) tableAssignTeacher.getValueAt(selectedRow, 0);
            updateAssignedClassTeacher(id);
        } else {
            JOptionPane.showMessageDialog(null, "Please select an assignment to update!");
        }
    }//GEN-LAST:event_jButton10ActionPerformed

    private void txtSearch3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtSearch3ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtSearch3ActionPerformed

    private void txtSearch3KeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtSearch3KeyTyped
        // TODO add your handling code here:
        DefaultTableModel ob =  (DefaultTableModel) tableAssignTeacher.getModel();
        TableRowSorter<DefaultTableModel> obj = new TableRowSorter<> (ob);
        tableAssignTeacher.setRowSorter(obj);
        obj.setRowFilter(RowFilter.regexFilter(txtSearch3.getText()));
    }//GEN-LAST:event_txtSearch3KeyTyped

    private void tableAssignSubMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tableAssignSubMouseClicked
        // TODO add your handling code here:
        int selectedRow = tableAssignSub.getSelectedRow();

        if (selectedRow != -1) {

            String className = tableAssignSub.getValueAt(selectedRow, 1).toString();
            String subjectName = tableAssignSub.getValueAt(selectedRow, 2).toString();

            cmbClass.setSelectedItem(className);
            cmbSubject.setSelectedItem(subjectName);
        }
    }//GEN-LAST:event_tableAssignSubMouseClicked

    private void cmbClassActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmbClassActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_cmbClassActionPerformed

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
        // TODO add your handling code here:
        assignClassToSubject();
    }//GEN-LAST:event_jButton2ActionPerformed

    private void jButton3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton3ActionPerformed
        // TODO add your handling code here:
        int selectedRow = tableAssignSub.getSelectedRow();
        if (selectedRow != -1) {
            int id = (int) tableAssignSub.getValueAt(selectedRow, 0);
            deleteAssignedClassSubject(id);
        } else {
            JOptionPane.showMessageDialog(null, "Please select a record to delete.");
        }
    }//GEN-LAST:event_jButton3ActionPerformed

    private void jButton5ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton5ActionPerformed
        // TODO add your handling code here:
        int selectedRow = tableAssignSub.getSelectedRow();
        if (selectedRow != -1) {
            int id = (int) tableAssignSub.getValueAt(selectedRow, 0);
            updateAssignedClassSubject(id);
        } else {
            JOptionPane.showMessageDialog(null, "Please select a record to update.");
        }
    }//GEN-LAST:event_jButton5ActionPerformed

    private void txtSearch5ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtSearch5ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtSearch5ActionPerformed

    private void txtSearch5KeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtSearch5KeyTyped
        // TODO add your handling code here:
        DefaultTableModel ob =  (DefaultTableModel) tableAssignSub.getModel();
        TableRowSorter<DefaultTableModel> obj = new TableRowSorter<> (ob);
        tableAssignSub.setRowSorter(obj);
        obj.setRowFilter(RowFilter.regexFilter(txtSearch5.getText()));
    }//GEN-LAST:event_txtSearch5KeyTyped

    private void bSettingsMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_bSettingsMouseClicked
        // TODO add your handling code here:
        Settings ad = new Settings();
        ad.setVisible(true);
        
        refreshData();
        jSettings.setBackground(new Color(0, 0, 153));
        bSettings.setBackground(new Color(0, 0, 153));
        jAttendance.setBackground(new Color(0, 0, 102));
        bAttendance.setBackground(new Color(0, 0, 102));
        jDropOut.setBackground(new Color(0, 0, 102));
        bDropOut.setBackground(new Color(0, 0, 102));
        jTeachers.setBackground(new Color(0, 0, 102));
        BTeachers.setBackground(new Color(0, 0, 102));
        BSubject.setBackground(new Color(0, 0, 102));
        jSubjects.setBackground(new Color(0, 0, 102));
        BAssignTc.setBackground(new Color(0, 0, 102));
        jAssignTc.setBackground(new Color(0, 0, 102));
        BAssignSub.setBackground(new Color(0, 0, 102));
        jAssignSub.setBackground(new Color(0, 0, 102));
        jNotification.setBackground(new Color(0, 0, 102));
        BNotification.setBackground(new Color(0, 0, 102));
        BDashboard.setBackground(new Color(0, 0, 102));
        jDashboard.setBackground(new Color(0, 0, 102));
        BStudents.setBackground(new Color(0, 0, 102));
        jStudents.setBackground(new Color(0, 0, 102));
        BClasses.setBackground(new Color(0, 0, 102));
        jClasses.setBackground(new Color(0, 0, 102));
        BReports.setBackground(new Color(0, 0, 102));
        jReports.setBackground(new Color(0, 0, 102));
        BQrCodeGen.setBackground(new Color(0, 0, 102));
        jQrCodeGen.setBackground(new Color(0, 0, 102));
    }//GEN-LAST:event_bSettingsMouseClicked

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(User.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(User.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(User.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(User.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new User().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel AccountHeader;
    private javax.swing.JLabel BAssignSub;
    private javax.swing.JLabel BAssignTc;
    private javax.swing.JLabel BClasses;
    private javax.swing.JLabel BDashboard;
    private javax.swing.JLabel BLogout;
    private javax.swing.JLabel BNotification;
    private javax.swing.JLabel BQrCodeGen;
    private javax.swing.JLabel BReports;
    private javax.swing.JLabel BStudents;
    private javax.swing.JLabel BSubject;
    private javax.swing.JLabel BTeachers;
    private javax.swing.JPanel Header;
    private javax.swing.JPanel HeaderPNG;
    private javax.swing.JPanel LOGOUT;
    private javax.swing.JPanel Main;
    private javax.swing.JPanel MainPanel;
    private javax.swing.JPanel Navbar;
    private javax.swing.JPanel PClassses;
    private javax.swing.JPanel PInactive;
    private javax.swing.JPanel PStudent;
    private javax.swing.JTextField Subjectname;
    private javax.swing.JTextField Tcfname;
    private javax.swing.JPanel TermsAndCondition;
    private javax.swing.JTextField TxtQrcode;
    private javax.swing.JTextField ages;
    private javax.swing.JLabel bAttendance;
    private javax.swing.JLabel bDropOut;
    private javax.swing.JButton bSend;
    private javax.swing.JLabel bSettings;
    private javax.swing.JButton btnAdd;
    private javax.swing.JButton btnAdd1;
    private javax.swing.JButton btnAdd2;
    private javax.swing.JButton btnAdd3;
    private javax.swing.JButton btnAdd4;
    private javax.swing.JButton btnDelete;
    private javax.swing.JButton btnDelete1;
    private javax.swing.JButton btnDelete2;
    private javax.swing.JButton btnDelete3;
    private javax.swing.JButton btnDelete4;
    private javax.swing.JButton btnTimeOut;
    private javax.swing.JButton btnUpdate;
    private javax.swing.JButton btnUpdate1;
    private javax.swing.JButton btnUpdate2;
    private javax.swing.JButton btnUpdate3;
    private javax.swing.JButton btnUpdate4;
    private javax.swing.JComboBox<String> cboxClassReports;
    private javax.swing.JComboBox<String> cboxNoQr;
    private javax.swing.JComboBox<String> cboxclass;
    private javax.swing.JComboBox<String> cmbClass;
    private javax.swing.JComboBox<String> cmbClass1;
    private javax.swing.JComboBox<String> cmbClassId;
    private javax.swing.JComboBox<String> cmbSubject;
    private javax.swing.JComboBox<String> cmbsubject1;
    private javax.swing.JComboBox<String> comboGrade;
    private javax.swing.JTextField contactnumber;
    private com.toedter.calendar.JDateChooser dateChooserEndDate;
    private com.toedter.calendar.JDateChooser dateChooserFromDate;
    private javax.swing.JTextField em;
    private javax.swing.Box.Filler filler1;
    private javax.swing.Box.Filler filler10;
    private javax.swing.Box.Filler filler11;
    private javax.swing.Box.Filler filler12;
    private javax.swing.Box.Filler filler13;
    private javax.swing.Box.Filler filler14;
    private javax.swing.Box.Filler filler15;
    private javax.swing.Box.Filler filler16;
    private javax.swing.Box.Filler filler17;
    private javax.swing.Box.Filler filler18;
    private javax.swing.Box.Filler filler2;
    private javax.swing.Box.Filler filler3;
    private javax.swing.Box.Filler filler4;
    private javax.swing.Box.Filler filler5;
    private javax.swing.Box.Filler filler6;
    private javax.swing.Box.Filler filler7;
    private javax.swing.Box.Filler filler8;
    private javax.swing.Box.Filler filler9;
    private javax.swing.JTextField fname;
    private javax.swing.JTextField gend;
    private javax.swing.JPanel iAssignSub;
    private javax.swing.JPanel iAssignTc;
    private javax.swing.JPanel iAttendance;
    private javax.swing.JPanel iClasses;
    private javax.swing.JPanel iDashboard;
    private javax.swing.JPanel iDropout;
    private javax.swing.JPanel iNotification;
    private javax.swing.JPanel iQrCodeGen;
    private javax.swing.JPanel iReports;
    private javax.swing.JPanel iSettings;
    private javax.swing.JPanel iStudents;
    private javax.swing.JPanel iSubjects;
    private javax.swing.JPanel iTeachers;
    private javax.swing.JPanel iUsers;
    private javax.swing.JLabel jAge;
    private javax.swing.JPanel jAssignSub;
    private javax.swing.JPanel jAssignTc;
    private javax.swing.JPanel jAttendance;
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton10;
    private javax.swing.JButton jButton11;
    private javax.swing.JButton jButton12;
    private javax.swing.JButton jButton13;
    private javax.swing.JButton jButton14;
    private javax.swing.JButton jButton2;
    private javax.swing.JButton jButton3;
    private javax.swing.JButton jButton4;
    private javax.swing.JButton jButton5;
    private javax.swing.JButton jButton6;
    private javax.swing.JButton jButton7;
    private javax.swing.JButton jButton8;
    private javax.swing.JButton jButton9;
    private javax.swing.JPanel jClasses;
    private javax.swing.JComboBox<String> jComboRole;
    private javax.swing.JLabel jContact;
    private javax.swing.JPanel jDashboard;
    private javax.swing.JPanel jDropOut;
    private javax.swing.JLabel jFirstname;
    private javax.swing.JLabel jFirstname1;
    private javax.swing.JLabel jGender;
    private javax.swing.JLabel jGender1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel17;
    private javax.swing.JLabel jLabel18;
    private javax.swing.JLabel jLabel19;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel20;
    private javax.swing.JLabel jLabel21;
    private javax.swing.JLabel jLabel22;
    private javax.swing.JLabel jLabel23;
    private javax.swing.JLabel jLabel24;
    private javax.swing.JLabel jLabel25;
    private javax.swing.JLabel jLabel26;
    private javax.swing.JLabel jLabel27;
    private javax.swing.JLabel jLabel28;
    private javax.swing.JLabel jLabel29;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel30;
    private javax.swing.JLabel jLabel31;
    private javax.swing.JLabel jLabel32;
    private javax.swing.JLabel jLabel33;
    private javax.swing.JLabel jLabel34;
    private javax.swing.JLabel jLabel35;
    private javax.swing.JLabel jLabel36;
    private javax.swing.JLabel jLabel37;
    private javax.swing.JLabel jLabel38;
    private javax.swing.JLabel jLabel39;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel40;
    private javax.swing.JLabel jLabel41;
    private javax.swing.JLabel jLabel42;
    private javax.swing.JLabel jLabel43;
    private javax.swing.JLabel jLabel44;
    private javax.swing.JLabel jLabel45;
    private javax.swing.JLabel jLabel46;
    private javax.swing.JLabel jLabel47;
    private javax.swing.JLabel jLabel48;
    private javax.swing.JLabel jLabel49;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel50;
    private javax.swing.JLabel jLabel51;
    private javax.swing.JLabel jLabel52;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JLabel jLastname;
    private javax.swing.JLabel jLastname1;
    private javax.swing.JPanel jLogout;
    private javax.swing.JLabel jMiddlename;
    private javax.swing.JLabel jMiddlename1;
    private javax.swing.JPanel jNotification;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JPanel jPanel9;
    private javax.swing.JLabel jPassword;
    private javax.swing.JPanel jQrCodeGen;
    private javax.swing.JPanel jReports;
    private javax.swing.JLabel jRole;
    private javax.swing.JScrollPane jScrollPane10;
    private javax.swing.JScrollPane jScrollPane12;
    private javax.swing.JScrollPane jScrollPane13;
    private javax.swing.JScrollPane jScrollPane14;
    private javax.swing.JScrollPane jScrollPane15;
    private javax.swing.JScrollPane jScrollPane16;
    private javax.swing.JScrollPane jScrollPane17;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JScrollPane jScrollPane5;
    private javax.swing.JScrollPane jScrollPane6;
    private javax.swing.JScrollPane jScrollPane7;
    private javax.swing.JScrollPane jScrollPane8;
    private javax.swing.JScrollPane jScrollPane9;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JSeparator jSeparator2;
    private javax.swing.JPanel jSettings;
    private javax.swing.JPanel jStudents;
    private javax.swing.JPanel jSubjects;
    private javax.swing.JTabbedPane jTabbedPane1;
    private javax.swing.JPanel jTeachers;
    private javax.swing.JLabel jUsername;
    private javax.swing.JLabel lblDateToday;
    private javax.swing.JLabel lblInactive;
    private javax.swing.JLabel lblQrData;
    private javax.swing.JLabel lblStatus;
    private javax.swing.JLabel lblStudentCount;
    private javax.swing.JLabel lblTimeNow;
    private javax.swing.JLabel lblTotalStudents;
    private javax.swing.JLabel lblTotalTeachers;
    private javax.swing.JTextField lname;
    private javax.swing.JTextArea messageTextArea;
    private javax.swing.JTextField mname;
    private javax.swing.JTextField numberTextField;
    private javax.swing.JTextField pass;
    private javax.swing.JLabel qrCodeLabel;
    private javax.swing.JTable tableAccount;
    private javax.swing.JTable tableAssignSub;
    private javax.swing.JTable tableAssignTeacher;
    private javax.swing.JTable tableAttendance;
    private javax.swing.JTable tableClasses;
    private javax.swing.JTable tableDropout;
    private javax.swing.JTable tableReport;
    private javax.swing.JTable tableSms;
    private javax.swing.JTable tableSms1;
    private javax.swing.JTable tableStudent;
    private javax.swing.JTable tableSubject;
    private javax.swing.JTable tableTeachers;
    private javax.swing.JTable tableWithQR;
    private javax.swing.JTable tableWithoutQR;
    private javax.swing.JTextField txtFname;
    private javax.swing.JTextField txtNumber;
    private javax.swing.JLabel txtQrData;
    private javax.swing.JTextField txtSchoolYear;
    private javax.swing.JTextField txtSearch;
    private javax.swing.JTextField txtSearch1;
    private javax.swing.JTextField txtSearch2;
    private javax.swing.JTextField txtSearch3;
    private javax.swing.JTextField txtSearch4;
    private javax.swing.JTextField txtSearch5;
    private javax.swing.JTextField txtSearch6;
    private javax.swing.JTextField txtSearch7;
    private javax.swing.JTextField txtSearch8;
    private javax.swing.JTextField txtSection;
    private javax.swing.JTextField txtStatus;
    private javax.swing.JTextField txtStudentName;
    private javax.swing.JTextField user;
    // End of variables declaration//GEN-END:variables
}
