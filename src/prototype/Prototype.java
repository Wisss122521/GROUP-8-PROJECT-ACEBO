    /*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */
package prototype;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
/**
 *
 * @author Wisss
 */
public class Prototype {
    static Connection con;
    
    public static Connection getConnection() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            con = DriverManager.getConnection("jdbc:mysql://sql309.infinityfree.com:3306/if0_38840252_studentattendanceif0_38840252_studentattendance","if0_38840252","uHpBU8l1NF5");           
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(Prototype.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SQLException ex) {
            Logger.getLogger(Prototype.class.getName()).log(Level.SEVERE, null, ex);
        }
        return con;
    }   
}
