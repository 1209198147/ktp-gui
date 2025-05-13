package com.jmu.shikou.view;

import com.google.gson.internal.LinkedTreeMap;
import com.jmu.shikou.ktpApi.API;
import com.jmu.shikou.config.Config;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Vector;
import java.util.concurrent.CountDownLatch;

public class TaskWin extends JFrame {
    TaskWin(){
        setTitle("未交作业查询 -查询中...");
        URL iconURL = getClass().getClassLoader().getResource("img/th.png");
        ImageIcon imageIcon = new ImageIcon(iconURL);
        setIconImage(imageIcon.getImage());
        setSize(1000, 600);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null); // 居中显示
        setResizable(false);

        Vector<String> columns = new Vector<>();
        columns.addElement("学期");
        columns.addElement("课程名称");
        columns.addElement("作业名称");
        columns.addElement("创建时间");
        columns.addElement("更新时间");
        columns.addElement("结束时间");
        columns.addElement("作业状态");
        columns.addElement("剩余时间");

        DefaultTableModel tableModel = new DefaultTableModel();
        tableModel.setColumnIdentifiers(columns);
        new Thread(new Runnable() {
            @Override
            public void run() {
                Long startTime = System.currentTimeMillis();
                try {
                    getData(tableModel);
                }catch (Exception e){
                    System.out.println("[ERROR] "+e.getMessage());
                    System.out.println(e.getStackTrace());
                }

                setTitle("未交作业查询 -加载完成!    用时：%ss".formatted((System.currentTimeMillis()-startTime)*1.0/1000));
            }
        }).start();

        JTable jTable = new JTable(tableModel){
            @Override
            public boolean isCellEditable(int row, int column){
                return false;
            }
        };
        JScrollPane jScrollPane = new JScrollPane(jTable);
        add(jScrollPane);
    }

    public static void getData(DefaultTableModel tableModel) throws InterruptedException {
        final CountDownLatch outer_latch = new CountDownLatch(Config.user.semesterList.size());
        for(HashMap<String, String> map:Config.user.semesterList){
            new Thread(new Runnable() {
                @Override
                public void run() {
                    //获取学期课程
                    ArrayList list = API.getSemesterCourseList(map);
                    //倒计时锁存器-用来等待子线程完成任务
                    final CountDownLatch latch = new CountDownLatch(list.size());
                    if(!list.isEmpty()){
                        //获取课程作业
                        for(Object i: list) {
                            new Thread(new Runnable() {
                                @Override
                                public void run() {
                                    LinkedTreeMap item = (LinkedTreeMap) i;
                                    //System.out.println(item.get("coursename")+"开始查询");
                                    String courseId = (String)item.get("id");
                                    ArrayList taskList = (ArrayList) API.getCourseTask(courseId).get("list");
                                    Vector<Object> v = getTaskVector(taskList);
                                    if(v.isEmpty()){
                                        latch.countDown();
                                        return;
                                    }
                                    v.add(0, item.get("coursename"));
                                    v.add(0, map.get("semester")+map.get("termTxt"));
                                    System.out.println(v);
                                    tableModel.addRow(v);
                                    //System.out.println(item.get("coursename")+"查询完成");
                                    latch.countDown();
                                }
                            }).start();
                        }
                        //等待子线程
                        try {
                            latch.await();
                        } catch (InterruptedException e) {
                            throw new RuntimeException(e);
                        }
                    }
                    outer_latch.countDown();
                }
            }).start();
        }
        outer_latch.await();
    }
    public static Vector<Object> getTaskVector(ArrayList taskList){
        Vector<Object> v = new Vector<>();
        for(Object i : taskList) {
            LinkedTreeMap item = (LinkedTreeMap) i;
            double state = (double)item.get("mstatus");
            if(state == 0 || state==3){
                System.out.println(state);
                SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

                v.addElement(item.get("title"));
                Long createtime = Long.parseLong((String) item.get("createtime"))*1000;
                Long updatetime = Long.parseLong((String) item.get("updatetime"))*1000;
                Long endtime = Long.parseLong((String) item.get("endtime"))*1000;
                v.addElement(format.format(createtime));
                v.addElement(format.format(updatetime));
                v.addElement(format.format(endtime));

                if(state == 0){
                    v.addElement("未提交");
                }else{
                    v.addElement("被打回");
                }

                Long remainder = endtime-System.currentTimeMillis();
                Long days = remainder/1000/60/60/24;
                Long hours = (remainder-days*24*60*60*1000)/1000/60/60;
                Long mins = (remainder-days*24*60*60*1000-hours*60*60*1000)/1000/60;
                Long secs = (remainder-days*24*60*60*1000-hours*60*60*1000-mins*60*1000)/1000;
                if(secs<0){
                    v.addElement("已结束");
                }else v.addElement("%s天 %s小时 %s分钟 %s秒".formatted(days, hours, mins, secs));

            }else{
                continue;
            }
        }
        return v;
    }
}
