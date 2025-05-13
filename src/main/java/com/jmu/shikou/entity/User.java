package com.jmu.shikou.entity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Vector;

public class User {
    public String name;
    public String account;
    public String school;
    public String stno;
    public String department;
    public String mobile;
    public String token;
    public HashMap<String, String> curSemester;
    public Vector<HashMap<String, String>> semesterList;
    public ArrayList courseList;
    public User(String name, String account, String school, String stno, String department, String mobile, String token) {
        this.name = name;
        this.account = account;
        this.school = school;
        this.stno = stno;
        this.department = department;
        this.mobile = mobile;
        this.token = token;
        curSemester = new HashMap<>();
        semesterList = new Vector<>();
    }
}
