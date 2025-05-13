package com.jmu.shikou.view;

import com.jmu.shikou.config.Config;

import javax.swing.*;
import java.awt.*;
import java.net.URL;

public class UserInfoWin extends JFrame {
    UserInfoWin(){
        setTitle("用户信息");
        URL iconURL = getClass().getClassLoader().getResource("img/th.png");
        ImageIcon imageIcon = new ImageIcon(iconURL);
        setIconImage(imageIcon.getImage());
        setSize(600, 300);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null); // 居中显示
        setResizable(false);

        Font font = new Font(null, Font.BOLD, 14);

        JLabel name = new JLabel();
        JLabel account = new JLabel();
        JLabel school = new JLabel();
        JLabel stno = new JLabel();
        JLabel department = new JLabel();
        JLabel mobile = new JLabel();
        JLabel token = new JLabel();

        name.setFont(font);
        account.setFont(font);
        school.setFont(font);
        stno.setFont(font);
        department.setFont(font);
        mobile.setFont(font);
        token.setFont(font);

        name.setText("用户名："+ Config.user.name);
        account.setText("账号："+Config.user.account);
        school.setText("学校："+Config.user.school);
        stno.setText("学号："+Config.user.stno);
        department.setText("学院（部门）："+Config.user.department);
        mobile.setText("手机号：(+86)"+Config.user.mobile);
        token.setText("Token："+Config.user.token);

        FlowLayout fl = new FlowLayout(FlowLayout.CENTER, 9999, 10);
        setLayout(fl);

        add(name);
        add(account);
        add(school);
        add(stno);
        add(department);
        add(mobile);
        add(token);
    }
}
