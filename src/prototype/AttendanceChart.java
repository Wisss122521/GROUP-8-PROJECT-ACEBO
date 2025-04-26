/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package prototype;

/**
 *
 * @author Wisss
 */
import java.awt.Color;
import java.awt.Font;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.block.BlockBorder;
import org.jfree.chart.plot.PiePlot;
import org.jfree.chart.title.LegendTitle;
import org.jfree.data.general.DefaultPieDataset;

public class AttendanceChart {

    public static ChartPanel getPieChartPanel() {
        DefaultPieDataset dataset = new DefaultPieDataset();
        
        try {
            
            Connection con = Prototype.getConnection();
            
            String query = "SELECT status, COUNT(*) as count FROM attendance_records GROUP BY status";
            PreparedStatement stmt = con.prepareStatement(query);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                String status = rs.getString("status");
                int count = rs.getInt("count");
                dataset.setValue(status, count);
            }

            rs.close();
            stmt.close();
            
        } catch (Exception e) {
            e.printStackTrace();
            dataset.setValue("Error", 1); 
        }
        
        
        JFreeChart chart = ChartFactory.createPieChart("ATTENDANCE", dataset, true, true, false);
        Font titleFont = new Font("Tahoma", Font.BOLD, 24); 
        chart.getTitle().setFont(titleFont);
        chart.getTitle().setMargin(5, 5, 10, 5);

        PiePlot plot = (PiePlot) chart.getPlot();
        plot.setLabelShadowPaint(null); 
        plot.setLabelBackgroundPaint(Color.WHITE); 
        plot.setLabelOutlinePaint(null);
        plot.setLabelFont(new Font("Tahoma", Font.BOLD, 12));
        plot.setBackgroundPaint(Color.WHITE);
        plot.setOutlineVisible(true);
        chart.setBackgroundPaint(Color.WHITE); 
        
        LegendTitle legend = chart.getLegend();
        legend.setItemFont(new Font("Tahoma", Font.BOLD, 18)); 
        chart.getLegend().setFrame(BlockBorder.NONE);
        
        return new ChartPanel(chart);
        
    }
}
