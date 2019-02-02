package bean;

/**
 * 用户信息bean
 */
public class User {

    /**
     * 唯一标识的id
     */
    private int id;
    /**
     * 学号或者教师号
     */
    private String accountNumber;
    /**
     * 密码
     */
    private String password;
    /**
     * 姓名
     */
    private String name;
    /**
     * 身份
     */
    private String identity;
    /**
     * 学院
     */
    private String department;
    /**
     * 性别
     */
    private String sex;



    public User(){}

    public User(int id, String accountNumber, String password, String name, String identity, String department, String sex) {
        this.id = id;
        this.accountNumber = accountNumber;
        this.password = password;
        this.name = name;
        this.identity = identity;
        this.department = department;
        this.sex = sex;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getIdentity() {
        return identity;
    }

    public void setIdentity(String identity) {
        this.identity = identity;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }
}
