import com.google.gson.internal.LinkedTreeMap;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Vector;

public class CourseWin extends JFrame {
    private JLabel label1;
    private JLabel label2;
    private JLabel label3;
    private JLabel label4;
    private JLabel label5;

    private LinkedTreeMap taskData;
    private LinkedTreeMap testData;
    private LinkedTreeMap attenceData;
    private ArrayList taskList;
    private ArrayList testList;
    private ArrayList attenceList;
    CourseWin(Course course){
        setTitle(course.name+"-详细");
        URL iconURL = getClass().getResource("img/th.png");
        ImageIcon imageIcon = new ImageIcon(iconURL);
        setIconImage(imageIcon.getImage());
        setSize(1000, 600);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null); // 居中显示
        setResizable(false);

        taskData = API.getCourseTask(course.id);
        taskList = (ArrayList) taskData.get("list");
        testData = API.getTest(course.id);
        testList = (ArrayList) testData.get("list");
        attenceData = API.getAttence(course.id);
        attenceList = (ArrayList) attenceData.get("data");

        JPanel northPanel = new JPanel();
        northPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 9999, 10));

        label1 = new JLabel();
        label2 = new JLabel();
        label3 = new JLabel();
        label4 = new JLabel();
        label5 = new JLabel();
//        JLabel label_pad1 = new JLabel();
//        JLabel label_pad2 = new JLabel();
//        JLabel label_pad3 = new JLabel();
//        JLabel label_pad4 = new JLabel();
//        label_pad1.setText("                                         ");
//        label_pad2.setText("                                         ");
//        label_pad3.setText("                                         ");
//        label_pad4.setText("                                         ");
//        label_pad1.setVisible(false);
//        label_pad2.setVisible(false);
//        label_pad3.setVisible(false);
//        label_pad4.setVisible(false);

        label1.setText(course.name+"-"+course.code);
        label2.setText("教师："+course.teachername+"\n");
        label3.setText("学期："+course.semester+(course.term.equals("1")?"-上":"-下"));
        label4.setText("班级："+course.classname);
        label5.setText("总人数："+course.total+"    已选课人数："+course.selectStudentCount);

        label1.setFont(new Font(null, Font.BOLD, 16));
        label2.setFont(new Font(null, Font.BOLD, 14));
        label3.setFont(new Font(null, Font.BOLD, 14));
        label4.setFont(new Font(null, Font.BOLD, 14));
        label5.setFont(new Font(null, Font.BOLD, 14));

        JTabbedPane jTabbedPane = new JTabbedPane();
        jTabbedPane.setPreferredSize(new Dimension(600, 300));

        //作业页面
        Vector<String> columns = new Vector<>();
        columns.addElement("序号");
        columns.addElement("作业名称");
        columns.addElement("创建时间");
        columns.addElement("更新时间");
        columns.addElement("结束时间");
        columns.addElement("作业状态");
        columns.addElement("剩余时间");
        columns.addElement("是否查重");
        columns.addElement("查重率");
        columns.addElement("成绩");
        Vector<Vector<Object>> data = new Vector<>();
        int index = 1;
        for(Object i : taskList){
            LinkedTreeMap item = (LinkedTreeMap) i;
            Vector<Object> v = new Vector<>();
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

            v.addElement(index++);
            v.addElement(item.get("title"));
            Long createtime = Long.parseLong((String) item.get("createtime"))*1000;
            Long updatetime = Long.parseLong((String) item.get("updatetime"))*1000;
            Long endtime = Long.parseLong((String) item.get("endtime"))*1000;
            v.addElement(format.format(createtime));
            v.addElement(format.format(updatetime));
            v.addElement(format.format(endtime));
            double state = (double)item.get("mstatus");
            if(state == 0){
                v.addElement("未提交");
            }else if(state == 1){
                v.addElement("已提交");
            }else if(state == 2 || state == 4){
                v.addElement("已批改");
            }else if(state == 3){
                v.addElement("被打回");
            }else{
                v.addElement("未知");
            }
            Long remainder = endtime-System.currentTimeMillis();
            Long days = remainder/1000/60/60/24;
            Long hours = (remainder-days*24*60*60*1000)/1000/60/60;
            Long mins = (remainder-days*24*60*60*1000-hours*60*60*1000)/1000/60;
            Long secs = (remainder-days*24*60*60*1000-hours*60*60*1000-mins*60*1000)/1000;
            if(secs<0){
                v.addElement("已结束");
            }else v.addElement("%s天 %s小时 %s分钟 %s秒".formatted(days, hours, mins, secs));

            v.addElement((((String)item.get("checkrepeat")).equals("1")?"是":"否"));
            v.addElement(item.get("warmingcheckrate"));
            if(!item.get("grade").equals("")){
                float grade = Float.parseFloat((String) item.get("grade"));
                v.addElement("%s/%s".formatted(grade, item.get("fullscore")));
            }else{
                v.addElement("未知/%s".formatted(item.get("fullscore")));
            }

            data.addElement(v);
        }
        DefaultTableModel tableModel = new DefaultTableModel();
        tableModel.setDataVector(data, columns);
        JTable jTable = new JTable(tableModel){
            @Override
            public boolean isCellEditable(int row, int column){
                return false;
            }
        };
        jTable.getColumnModel().getColumn(0).setMaxWidth(30);
        jTable.getColumnModel().getColumn(5).setMaxWidth(100);
        jTable.getColumnModel().getColumn(7).setMaxWidth(100);
        jTable.getColumnModel().getColumn(8).setMaxWidth(50);
        jTable.getColumnModel().getColumn(9).setMaxWidth(100);
        JScrollPane jScrollPane = new JScrollPane(jTable);
        jScrollPane.setPreferredSize(new Dimension(600, 300));

        jTabbedPane.add("作业", jScrollPane);

        //测试页面
        Vector<String> columns1 = new Vector<>();
        columns1.addElement("序号");
        columns1.addElement("测试名称");
        columns1.addElement("开始时间");
        columns1.addElement("结束时间");
        columns1.addElement("测试状态");
        columns1.addElement("剩余时间");
        columns1.addElement("限时");
        columns1.addElement("成绩");
        Vector<Vector<Object>> data1 = new Vector<>();
        index = 1;
        for(Object i : testList){
            LinkedTreeMap item = (LinkedTreeMap) i;
            Vector<Object> v = new Vector<>();
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

            v.addElement(index++);
            v.addElement(item.get("title"));
            String total_score = (String) item.get("total_score");
            String score = (String) item.get("score");
            Long begintime = Long.parseLong((String) item.get("begintime"))*1000;
            Long endtime = Long.parseLong((String) item.get("endtime"))*1000;
            v.addElement(format.format(begintime));
            v.addElement(format.format(endtime));
            double state = (double)item.get("submit_state");
            if(state == 1){
                v.addElement("未参与");
            }else if(state == 2){
                v.addElement("未提交");
            }else if(state == 3){
                v.addElement("被打回");
            }else if(state == 4 || state == 5){
                v.addElement("已提交");
            }else if(state > 5){
                v.addElement("已批改");
            }else{
                v.addElement("未知");
            }

            Long remainder = endtime-System.currentTimeMillis();
            Long days = remainder/1000/60/60/24;
            Long hours = (remainder-days*24*60*60*1000)/1000/60/60;
            Long mins = (remainder-days*24*60*60*1000-hours*60*60*1000)/1000/60;
            Long secs = (remainder-days*24*60*60*1000-hours*60*60*1000-mins*60*1000)/1000;
            if(secs<0){
                v.addElement("已结束");
            }else v.addElement("%s天 %s小时 %s分钟 %s秒".formatted(days, hours, mins, secs));
            int timeLength = Integer.parseInt((String) item.get("timelength"));
            int days_tl = timeLength/60/60/24;
            int hours_tl = (timeLength-days_tl*24*60*60)/1000/60/60;
            int mins_tl = (timeLength-days_tl*24*60*60-hours_tl*60*60)/60;
            int secs_tl = (timeLength-days_tl*24*60*60-hours_tl*60*60-mins_tl*60);
            v.addElement("%s天 %s小时 %s分钟 %s秒".formatted(days_tl, hours_tl, mins_tl, secs_tl));
            v.addElement("%s/%s".formatted(score, total_score));
            data1.addElement(v);
        }
        DefaultTableModel tableModel1 = new DefaultTableModel();
        tableModel1.setDataVector(data1, columns1);
        JTable jTable1 = new JTable(tableModel1){
            @Override
            public boolean isCellEditable(int row, int column){
                return false;
            }
        };
        JScrollPane jScrollPane1 = new JScrollPane(jTable1);
        jScrollPane1.setPreferredSize(new Dimension(600, 300));
        jTabbedPane.add("测试", jScrollPane1);

        //考勤页面
        Vector<String> columns2 = new Vector<>();
        columns2.addElement("序号");
        columns2.addElement("考勤时间");
        columns2.addElement("考勤名称");
        columns2.addElement("考勤类型");
        columns2.addElement("签到时间");
        columns2.addElement("结束时间");
        columns2.addElement("考勤结果");
        columns2.addElement("限时");
        columns2.addElement("ip");
        Vector<Vector<Object>> data2 = new Vector<>();
        index = 1;
        for(Object i : attenceList){
            LinkedTreeMap item = (LinkedTreeMap) i;
            Vector<Object> v = new Vector<>();
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            v.add(index++);
            Long createtime;
            Long signTime;
            Long overtime;
            try{
                createtime = Long.parseLong((String) item.get("createtime"))*1000;
                v.addElement(format.format(createtime));
            }catch (Exception e){
                createtime = 0L;
                v.addElement("未知");
            }
            v.addElement(item.get("title"));
            int type = Integer.parseInt((String) item.get("type"));
            if(type == 1){
                v.addElement("数字考勤签到");
            }else if(type == 2){
                v.addElement("签到");
            }else if(type == 3){
                v.addElement("二维码签到");
            }else{
                v.addElement("未知");
            }
            try{
                signTime = Long.parseLong((String) item.get("signTime"))*1000;
                v.addElement(format.format(signTime));
            }catch (Exception e){
                signTime = 0L;
                v.addElement("未知");
            }
            try{
                overtime = Long.parseLong((String) item.get("overtime"))*1000;
                v.addElement(format.format(overtime));
            }catch (Exception e){
                overtime = 0L;
                v.addElement("未知");
            }
            int state = Integer.parseInt((String) item.get("state"));
            if(state == 0){
                v.addElement("出勤");
            } else if (state == 1) {
                v.addElement("旷课");
            } else if (state == 2) {
                v.addElement("迟到");
            } else if (state == 7) {
                v.addElement("早退");
            } else if (state == 3) {
                v.addElement("请假");
            } else if (state == 4) {
                v.addElement("事假");
            } else if (state == 5) {
                v.addElement("病假");
            } else if (state == 6) {
                v.addElement("公假");
            }else{
                v.addElement("未知");
            }
            int timeLength = Integer.parseInt((String)item.get("duration"));
            int mins_tl = timeLength/60;
            int secs_tl = timeLength-mins_tl*60;
            v.addElement("%s分钟 %s秒".formatted(mins_tl, secs_tl));
            v.addElement(item.get("ip"));
            data2.addElement(v);
        }
        DefaultTableModel tableModel2 = new DefaultTableModel();
        tableModel2.setDataVector(data2, columns2);
        JTable jTable2 = new JTable(tableModel2){
            @Override
            public boolean isCellEditable(int row, int column){
                return false;
            }
        };

        jTable2.getColumnModel().getColumn(0).setMaxWidth(30);
        jTable2.getColumnModel().getColumn(6).setMaxWidth(60);

        JScrollPane jScrollPane2 = new JScrollPane(jTable2);
        jScrollPane2.setPreferredSize(new Dimension(600, 300));
        jTabbedPane.add("考勤", jScrollPane2);

        northPanel.add(label1);
        //northPanel.add(label_pad1);
        northPanel.add(label2);
        //northPanel.add(label_pad2);
        northPanel.add(label3);
        //northPanel.add(label_pad3);
        northPanel.add(label4);
        //northPanel.add(label_pad4);
        northPanel.add(label5);
        northPanel.setPreferredSize(new Dimension(600, 200));
        add(northPanel, BorderLayout.NORTH);

        add(jTabbedPane, BorderLayout.CENTER);
    }
}
