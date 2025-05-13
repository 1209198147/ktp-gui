package com.jmu.shikou.view;

import com.jmu.shikou.ktpApi.API;
import com.jmu.shikou.config.Config;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.net.URL;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;

public class LoginForm extends JFrame {
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JCheckBox rememberCheckBox;
    public static Preferences prefs;

    private Object source;

    public LoginForm(Object source) throws Exception{
        this.source = source;
        System.out.println("登录source来源："+source);
        System.out.println("登录");
        // 设置窗口属性  
        setTitle("登录");
        URL iconURL = getClass().getClassLoader().getResource("img/th.png");
        ImageIcon imageIcon = new ImageIcon(iconURL);
        setIconImage(imageIcon.getImage());
        setSize(400, 200);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null); // 居中显示  
        setResizable(false);

        // 创建面板并添加到窗口中心  
        //JPanel panel = new JPanel();
        //panel.setLayout(new GridLayout(3, 2));
        FlowLayout flayout1 = new FlowLayout(FlowLayout.CENTER, 10, 10);
        setLayout(flayout1);

        // 添加用户名和密码字段  
        add(new JLabel("账号:"));
        usernameField = new JTextField();
        Dimension dim1 = new Dimension(300, 30);
        usernameField.setPreferredSize(dim1);
        add(usernameField);

        add(new JLabel("密码:"));
        passwordField = new JPasswordField();
        passwordField.setPreferredSize(dim1);
        add(passwordField);

        // 添加记住密码复选框  
        rememberCheckBox = new JCheckBox("记住我");
        add(rememberCheckBox);

        // 添加登录按钮  
        JButton loginButton = new JButton("登录");
        add(loginButton);

        // 初始化偏好设置（用于保存密码）  
        prefs = Preferences.userRoot().node(this.getClass().getName());

        //自动补全记住的密码
        usernameField.setText(prefs.get("username", ""));
        String password = prefs.get("password", "");

        // 添加监听器以处理登录事件和记住密码选项  
        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String username = usernameField.getText();
                String password = passwordField.getText();
                String storedPassword = prefs.get("password", "");
                boolean remember = rememberCheckBox.isSelected();
                boolean loggedIn = false; // 假设登录检查很复杂，这里只是简单模拟
                API.login(username, password);

                if (API.hasToken()) {
                    loggedIn = true; // 简单模拟验证，实际应用中需要更复杂的验证机制
                    if(rememberCheckBox.isSelected()){
                        prefs.put("password", password);
                    }else{
                        try {
                            prefs.clear();
                        } catch (BackingStoreException ex) {
                            throw new RuntimeException(ex);
                        }
                    }
                    prefs.put("username", username);
                    try {
                        prefs.flush();
                    } catch (BackingStoreException ex) {
                        throw new RuntimeException(ex);
                    }
                } else { // 如果以上条件都不满足，显示错误消息（可选）并重置密码字段（可选）
                    JOptionPane.showMessageDialog(LoginForm.this, "错误的账号或密码！"); // 可选：显示错误消息对话框（可选）
                    passwordField.setText(""); // 可选：重置密码字段（可选）
                }
                if (loggedIn) { // 如果登录成功，可以执行登录成功后的操作（例如打开另一个窗口或弹出成功消息）
                    loginButton.setEnabled(false);
                    JOptionPane.showMessageDialog(LoginForm.this, "登录成功！"); // 可选：显示成功消息对话框（可选）
                    Config.isLogin = true;
                    dispose();
                    MainWin mainWin = new MainWin(this);
                    mainWin.setVisible(true);
                } else { // 如果登录失败，可以执行登录失败后的操作（例如禁用登录按钮或显示错误消息）
                    JOptionPane.showMessageDialog(LoginForm.this, "登录失败！");
                }
            }
        });

        addWindowFocusListener(new WindowFocusListener() {
            @Override
            public void windowGainedFocus(WindowEvent e) {
                System.out.println("自动登录！");
                if(prefs.get("autoLogin", "false").equals("true") && !prefs.get("token", "").isEmpty()){
                    Config.isLogin = true;
                    API.setToken(prefs.get("token", ""));
                    dispose();
                    MainWin mainWin = new MainWin(this);
                    mainWin.setVisible(true);
                }else {
                    if(!password.isEmpty()){
                        passwordField.setText(password);
                        rememberCheckBox.setSelected(true);
                    }
                }
            }

            @Override
            public void windowLostFocus(WindowEvent e) {

            }
        });


    }
}