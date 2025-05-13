package com.jmu.shikou.config;

import com.jmu.shikou.entity.User;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.util.Properties;

public class Config {
    private static Properties config = new Properties();

    static {
        InputStream resourceAsStream = Config.class.getClassLoader().getResourceAsStream("config.properties");
        try {
            config.load(resourceAsStream);
        } catch (IOException e) {
            System.out.println("读取配置文件失败，使用默认配置！");
        }
        System.out.println("读取成功");
        Field[] fields = Config.class.getFields();
        for(Field field:fields){
            if(config.get(field.getName())!=null){
                try {
                    Class<?> fieldType = field.getType();
                    String value = (String) config.get(field.getName());
                    if (fieldType == boolean.class) {
                        field.set(null, Boolean.parseBoolean(value));
                    } else if (fieldType == int.class) {
                        field.set(null, Integer.parseInt(value));
                    } else if (fieldType == double.class) {
                        field.set(null, Double.parseDouble(value));
                    }else{
                        field.set(null, value);
                    }
                    System.out.println(field.getName()+": "+field.get(null));

                } catch (Exception e) {
                    System.out.println(field.getName()+"配置失败！");
                    System.out.println(e.getMessage());
                }
            }
        }
    }

    public static User user;
    public static boolean isLogin = false;
    public static String tokenSavePath = "D:/ktp/token/";
}
