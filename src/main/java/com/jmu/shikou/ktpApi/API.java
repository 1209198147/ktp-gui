package com.jmu.shikou.ktpApi;

import java.util.ArrayList;
import java.util.HashMap;

import com.google.gson.*;
import com.google.gson.internal.LinkedTreeMap;
import com.jmu.shikou.config.Config;
import com.jmu.shikou.entity.User;
import com.jmu.shikou.utils.HttpRequest;

public class API {
    private static String token;
    public static void login(String username, String password){
        String payload = "{\n" +
                "        \"email\": \""+username+"\",\n" +
                "        \"password\": \""+password+"\",\n" +
                "        \"remember\": \"1\",\n" +
                "        \"code\": \"\",\n" +
                "        \"mobile\": \"\",\n" +
                "        \"type\": \"login\",\n" +
                "        \"reqtimestamp\":"+System.currentTimeMillis()+"\n" +
                "    }";
        //System.out.println(payload);
        try{
            String response = HttpRequest.Post("https://openapiv5.ketangpai.com//UserApi/login", payload);

            // 输出响应内容
            //System.out.println("Response: " + response.toString());
            HashMap data = new Gson().fromJson(response, HashMap.class);
            token = (String) ((LinkedTreeMap) data.get("data")).get("token");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void getUserBasinInfo(){
        if(token.isEmpty()) return;
        String payload = "{\"reqtimestamp\":"+System.currentTimeMillis()+"}";
        try{
            HashMap<String, Object> map = new HashMap<>();
            map.put("Token", token);
            String response = HttpRequest.Post("https://openapiv5.ketangpai.com//UserApi/getUserBasinInfo", map, payload);

            // 输出响应内容
            //System.out.println("Response: " + response.toString());
            HashMap data = new Gson().fromJson(response, HashMap.class);
            LinkedTreeMap ltm = (LinkedTreeMap) data.get("data");
            Config.user = new User((String) ltm.get("username"), (String)ltm.get("account"), (String)ltm.get("school"), (String)ltm.get("stno"), (String)ltm.get("department"), (String)ltm.get("mobile"), (String)ltm.get("token"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void getSemesterList(){
        if(token.isEmpty()) return;
        String payload = ("{\n" +
                "    \"isstudy\": \"1\",\n" +
                "    \"search\": \"\",\n" +
                "    \"reqtimestamp\": %s\n" +
                "}").formatted(System.currentTimeMillis());
        try{
            HashMap<String, Object> map = new HashMap<>();
            map.put("Token", token);
            String response = HttpRequest.Post("https://openapiv5.ketangpai.com/CourseApi/semesterList", map, payload);

            // 输出响应内容
            //System.out.println("Response: " + response.toString());
            HashMap data = new Gson().fromJson(response, HashMap.class);
            LinkedTreeMap ltm = (LinkedTreeMap) data.get("data");
            ArrayList<LinkedTreeMap> semesterList = (ArrayList)ltm.get("semester");
            for(LinkedTreeMap item :semesterList){
                HashMap<String, String> temp = new HashMap();
                for(Object key:item.keySet()){
                    temp.put(key.toString(), item.get(key).toString());
                }
                Config.user.semesterList.add(temp);
            }
            Config.user.curSemester = Config.user.semesterList.get(0);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void setSemesterCourseList(HashMap<String, String > semester){
        if(token.isEmpty()) return;
        String payload = ("{\n" +
                "    \"isstudy\": \"1\",\n" +
                "    \"search\": \"\",\n" +
                "    \"semester\": \"%s\",\n" +
                "    \"term\": \"%s\",\n" +
                "    \"reqtimestamp\": %s\n" +
                "}").formatted(semester.get("semester"), semester.get("term"), System.currentTimeMillis());
        try{
            HashMap<String, Object> map = new HashMap<>();
            map.put("Token", token);
            String response = HttpRequest.Post("https://openapiv5.ketangpai.com//CourseApi/semesterCourseList", map, payload);


            HashMap data = new Gson().fromJson(response, HashMap.class);
            ArrayList courseList = (ArrayList) data.get("data");
            Config.user.courseList = courseList;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static ArrayList getSemesterCourseList(HashMap<String, String > semester){
        if(token.isEmpty()) return null;
        String payload = ("{\n" +
                "    \"isstudy\": \"1\",\n" +
                "    \"search\": \"\",\n" +
                "    \"semester\": \"%s\",\n" +
                "    \"term\": \"%s\",\n" +
                "    \"reqtimestamp\": %s\n" +
                "}").formatted(semester.get("semester"), semester.get("term"), System.currentTimeMillis());
        try{
            HashMap<String, Object> map = new HashMap<>();
            map.put("Token", token);
            String response = HttpRequest.Post("https://openapiv5.ketangpai.com//CourseApi/semesterCourseList", map, payload);


            HashMap data = new Gson().fromJson(response, HashMap.class);
            ArrayList courseList = (ArrayList) data.get("data");
            return courseList;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static LinkedTreeMap getCourseTask(String courseid){
        if(token.isEmpty()) return null;
        String payload = ("{\n" +
                "    \"courseid\": \"%s\",\n" +
                "    \"contenttype\": 4,\n" +
                "    \"dirid\": 0,\n" +
                "    \"lessonlink\": [],\n" +
                "    \"sort\": [],\n" +
                "    \"page\": 1,\n" +
                "    \"limit\": 50,\n" +
                "    \"desc\": 3,\n" +
                "    \"courserole\": 0,\n" +
                "    \"vtr_type\": \"\",\n" +
                "    \"reqtimestamp\": %s\n" +
                "}").formatted(courseid, System.currentTimeMillis());
        try{
            HashMap<String, Object> map = new HashMap<>();
            map.put("Token", token);
            String response = HttpRequest.Post("https://openapiv5.ketangpai.com//FutureV2/CourseMeans/getCourseContent", map, payload);


            HashMap data = new Gson().fromJson(response, HashMap.class);
            return (LinkedTreeMap) data.get("data");
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static LinkedTreeMap getTest(String courseid){
        if(token.isEmpty()) return null;
        String payload = ("{\"courseid\":\"%s\"," +
                "\"contenttype\":6," +
                "\"dirid\":0," +
                "\"lessonlink\":[]," +
                "\"sort\":[]," +
                "\"page\":1," +
                "\"limit\":50," +
                "\"desc\":3," +
                "\"courserole\":0," +
                "\"vtr_type\":\"\"," +
                "\"reqtimestamp\":%s}").formatted(courseid, System.currentTimeMillis());
        try{
            HashMap<String, Object> map = new HashMap<>();
            map.put("Token", token);
            String response = HttpRequest.Post("https://openapiv5.ketangpai.com//FutureV2/CourseMeans/getCourseContent", map, payload);
            HashMap data = new Gson().fromJson(response, HashMap.class);
            return (LinkedTreeMap) data.get("data");
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static LinkedTreeMap getResources(String courseid){
        if(token.isEmpty()) return null;
        String payload = ("{\"courseid\":\"%s\"," +
                "\"contenttype\":[\"2\",\"8\"]," +
                "\"dirid\":0," +
                "\"lessonlink\":[]," +
                "\"sort\":[]," +
                "\"page\":1," +
                "\"limit\":50," +
                "\"desc\":3," +
                "\"courserole\":0," +
                "\"vtr_type\":\"\"," +
                "\"reqtimestamp\":%s}").formatted(courseid, System.currentTimeMillis());
        try{
            HashMap<String, Object> map = new HashMap<>();
            map.put("Token", token);
            String response = HttpRequest.Post("https://openapiv5.ketangpai.com//FutureV2/CourseMeans/getCourseContent", map, payload);
            HashMap data = new Gson().fromJson(response, HashMap.class);
            return (LinkedTreeMap) data.get("data");
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    static public LinkedTreeMap getAttence(String courseid){
        if(token.isEmpty()) return null;
        String payload = "{\"courseid\":\"%s\",\"reqtimestamp\":%s}".formatted(courseid, System.currentTimeMillis());
        try{
            HashMap<String, Object> map = new HashMap<>();
            map.put("Token", token);
            String response = HttpRequest.Post("https://openapiv5.ketangpai.com//SummaryApi/attence", map, payload);
            HashMap data = new Gson().fromJson(response, HashMap.class);
            return (LinkedTreeMap) data.get("data");
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static boolean hasToken(){
        return !token.isEmpty();
    }

    public static void setToken(String token){
        API.token = token;
    }
}
