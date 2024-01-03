public class Main {
    public static void main(String[] args) {
        try {
            LoginForm loginForm = new LoginForm("Main");
            loginForm.setVisible(true);
        }catch (Exception e){
            System.out.println("[ERROR] 发生错误！");
            System.out.println(e.getMessage());
            System.out.println(e.getStackTrace());
        }
    }
}
