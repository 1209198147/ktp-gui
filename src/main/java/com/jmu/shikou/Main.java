package com.jmu.shikou;

import com.jmu.shikou.view.LoginForm;

public class Main {
    public static void main(String[] args) {
        try {
            LoginForm loginForm = new LoginForm("com.jmu.czh.Main");
            loginForm.setVisible(true);
        }catch (Exception e){
            System.out.println("[ERROR] 发生错误！");
            System.out.println(e.getMessage());
            System.out.println(e.getStackTrace());
        }
    }
}
