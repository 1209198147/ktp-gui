package com.jmu.shikou.view;

import com.google.gson.internal.LinkedTreeMap;
import com.jmu.shikou.ktpApi.API;
import com.jmu.shikou.config.Config;
import com.jmu.shikou.entity.Course;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.net.URL;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.prefs.BackingStoreException;

public class MainWin extends JFrame {
    private JLabel state;
    private JButton save;
    private JButton quit;
    private JButton query;
    private JButton getUserInfo;
    private Object source;
    MainWin(Object source) {
        this.source = source;
        System.out.println("主页面source来源:"+source);
        if(source instanceof JFrame){
            ((JFrame) source).dispose();
        }
        setTitle("课堂派客服端 状态：%s\t".formatted(Config.isLogin?"已登录":"未登录"));
        URL iconURL = getClass().getClassLoader().getResource("img/th.png");
        ImageIcon imageIcon = new ImageIcon(iconURL);
        setIconImage(imageIcon.getImage());
        setSize(800, 800);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null); // 居中显示
        setResizable(false);

        try{
            Image img = ImageIO.read(new File("C:\\Users\\Admin\\Desktop\\Java作业\\java课设\\img\\favicon.ico"));
            setIconImage(img);
        }catch (IOException e){
            System.out.println("[ERROR] "+e.getMessage());
        }


        API.getUserBasinInfo();
        if(Config.user == null){
            JOptionPane.showMessageDialog(MainWin.this, "登录失效！");
            dispose();
            try{
                LoginForm loginForm = new LoginForm(this);
                loginForm.setVisible(true);
            }catch (Exception e){
                System.out.println("[ERROR] "+e.getMessage());
                System.out.println(e.getStackTrace());
            }

        }
        setTitle("课堂派客服端\t状态：已登录\t[你好，来自"+Config.user.name+"]\t学校："+Config.user.school+"\t学号："+Config.user.stno);
        API.getSemesterList();
        //com.jmu.czh.ktpApi.API.setSemesterCourseList(com.jmu.czh.config.Config.user.curSemester);

        initUi();
    }

    private void initUi(){
        BorderLayout bl = new BorderLayout();
        setLayout(bl);

        FlowLayout fl = new FlowLayout();
        JPanel northPanel = new JPanel();
        northPanel.setLayout(fl);

        getUserInfo = new JButton();
        getUserInfo.setText("用户信息");
        getUserInfo.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        UserInfoWin userInfoWin = new UserInfoWin();
                        userInfoWin.setVisible(true);
                    }
                }).start();
            }
        });

        save = new JButton();
        save.setText("保存Token并自动登录");
        save.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        LoginForm.prefs.put("autoLogin", "true");
                        LoginForm.prefs.put("token", Config.user.token);
                        File tokenFile = new File(Config.tokenSavePath);
                        if(!tokenFile.exists()){
                            tokenFile.mkdirs();
                        }
                        System.out.println(tokenFile.getPath()+ File.separator +Config.user.account+".txt");
                        try (BufferedWriter writer = new BufferedWriter(new FileWriter(tokenFile.getPath()+ File.separator +Config.user.name+".txt"))) {
                            writer.write(Config.user.token);
                            JOptionPane.showMessageDialog(MainWin.this, "保存成功，保存在token.txt中！");
                        } catch (IOException e) {
                            JOptionPane.showMessageDialog(MainWin.this, "保存失败！");
                            System.out.println("[ERROR] " + e.getMessage());
                        }
                    }
                }).start();
            }
        });
        quit = new JButton();
        quit.setText("清除自动登录");
        quit.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        String username = LoginForm.prefs.get("username", "");
                        String password = LoginForm.prefs.get("password", "");
                        try {
                            LoginForm.prefs.clear();
                        } catch (BackingStoreException ex) {
                            throw new RuntimeException(ex);
                        }
                        LoginForm.prefs.put("username", username);
                        LoginForm.prefs.put("password", password);
                    }
                }).start();
            }
        });
        query = new JButton();
        query.setText("查询未交作业");
        query.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        TaskWin taskWin = new TaskWin();
                        taskWin.setVisible(true);
                    }
                }).start();
            }
        });
        northPanel.add(getUserInfo);
        northPanel.add(save);
        northPanel.add(quit);
        northPanel.add(query);

        state = new JLabel();
        state.setFont(new Font(null, Font.BOLD, 16));
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                while (true){
                    DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("HH:mm:ss");
                    state.setText(" 现在时间是"+LocalDate.now()+" "+ LocalTime.now().format(dateTimeFormatter));
                }
            }
        });
        t.start();

        state.setPreferredSize(new Dimension(0, 40));

        JTabbedPane jTabbedPane = new JTabbedPane();
        for(HashMap<String, String> map : Config.user.semesterList) {
            JList<Course> courseJList = new JList<>() {
                @Override
                public int locationToIndex(Point location) {
                    int index = super.locationToIndex(location);
                    if (index != -1 && !getCellBounds(index, index).contains(location)) {
                        return -1;
                    } else return index;
                }
            };
            courseJList.setBorder(BorderFactory.createTitledBorder("课程列表"));
            courseJList.addMouseListener(new MouseAdapter() {
                @Override
                public void mousePressed(MouseEvent e) {
                    if (courseJList.locationToIndex(e.getPoint()) == -1) {
                        courseJList.clearSelection();
                        return;
                    }
                    //System.out.println(courseJList.getSelectedValue());
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            CourseWin courseWin = new CourseWin(courseJList.getSelectedValue());
                            courseWin.setVisible(true);
                        }
                    }).start();
                }
            });
            //        DefaultListModel<com.jmu.czh.entity.Course> model = new DefaultListModel<>();
            JScrollPane jScrollPane = new JScrollPane(courseJList);
            //
            //        if(!com.jmu.czh.config.Config.user.courseList.isEmpty()){
            //
            //            for(Object i: com.jmu.czh.config.Config.user.courseList){
            //                LinkedTreeMap item = (LinkedTreeMap) i;
            //                model.addElement(new com.jmu.czh.entity.Course((String)item.get("id"), (String)item.get("uid"), (String)item.get("coursename"), (String)item.get("code"), (String)item.get("classname"), (String)item.get("semester"), (String)item.get("term"), (String)item.get("username"), (String)item.get("total"), (String)item.get("selectStudentCount")));
            //            }
            //        }
            //        courseJList.setFont(new Font(null, Font.BOLD, 14));
            //        courseJList.setModel(model);
            //        courseJList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
            initJList(courseJList, API.getSemesterCourseList(map));
            jTabbedPane.add(map.get("semester")+map.get("termTxt"), jScrollPane);
        }

        add(northPanel, BorderLayout.NORTH);
        add(jTabbedPane, BorderLayout.CENTER);
        add(state, BorderLayout.SOUTH);
    }

    private static void initJList(JList jList, ArrayList list){
        DefaultListModel<Course> model = new DefaultListModel<>();
        if(!list.isEmpty()){
            for(Object i: list){
                LinkedTreeMap item = (LinkedTreeMap) i;
                String selectStudentCount = null;
                if(item.get("selectStudentCount") instanceof Double){
                    double t = (double)item.get("selectStudentCount");
                    selectStudentCount = Double.toString(t);
                }else{
                    selectStudentCount = (String) item.get("selectStudentCount");
                }
                model.addElement(new Course((String)item.get("id"), (String)item.get("uid"), (String)item.get("coursename"), (String)item.get("code"), (String)item.get("classname"), (String)item.get("semester"), (String)item.get("term"), (String)item.get("username"), (String) item.get("total"), selectStudentCount));
            }
        }
        jList.setFont(new Font(null, Font.BOLD, 14));
        jList.setModel(model);
        jList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    }
}
