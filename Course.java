public class Course {
    public String id;
    public String uid;
    public String name;
    public String code;
    public String classname;
    public String semester;
    public String term;
    public String teachername;
    public String total;
    public String selectStudentCount;

    public Course(String id, String uid, String name, String code, String classname, String semester, String term, String teachername, String total, String selectStudentCount) {
        this.id = id;
        this.uid = uid;
        this.name = name;
        this.code = code;
        this.classname = classname;
        this.semester = semester;
        this.term = term;
        this.teachername = teachername;
        this.total = total;
        this.selectStudentCount = selectStudentCount;
    }

    @Override
    public String toString() {
        return "%s code:%s    教师:%s    学期:%s    班级:%s".formatted(name, code, teachername, (term.equals("1")?semester+"-上":semester+"-下"), classname);
    }
}
