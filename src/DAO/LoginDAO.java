package DAO;

import bean.*;

import java.sql.SQLException;
import java.util.ArrayList;

public class LoginDAO extends BaseDAO{

    public LoginDAO() {
        super();
    }

    /**
     * 建立用户表
     */
    public void createUserTable() {
        String sql = "create table if not exists tjut_user(id int auto_increment primary key, accountNumber char(8) unique not null, password char(15) not null, sex char(2) not null, department char(15) not null, userName char(10) not null, userIdentity char(10) not null) charset utf8mb4 collate utf8mb4_bin";
        try {
            preparedStatement = connection.prepareStatement(sql);
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * 插入一条用户数据
     * @param user
     */
    public void insertUser(User user) {
        String sql = "insert into tjut_user(accountNumber, password, sex, department, userName, userIdentity) values(? ,?, ?, ?, ?, ?)";
        try {
            preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1, user.getAccountNumber());
            preparedStatement.setString(2, user.getPassword());
            preparedStatement.setString(3, user.getSex());
            preparedStatement.setString(4, user.getDepartment());
            preparedStatement.setString(5, user.getName());
            preparedStatement.setString(6, user.getIdentity());
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * 查询是否存在该用户
     * @param accountNumber 账号
     * @param password 密码
     * @return
     */
    public boolean isExistUser(String accountNumber, String password) {
        if (!isExistTable("tjut_user")) {
            return false;
        }
        String sql = "select count(*) as countNumber from tjut_user where accountNumber = ? and password = ? limit 1";
        boolean result = false;
        try {
            preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1, accountNumber);
            preparedStatement.setString(2, password);
            resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                if (resultSet.getInt("countNumber") != 0) {
                    result = true;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * 获取用户的id
     * @param accountNumber
     * @return
     */
    public int getUserId(String accountNumber) {
        String sql = "select id from tjut_user where accountNumber = ? limit 1";
        int id = 0;
        try {
            preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1, accountNumber);
            resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                id = resultSet.getInt("id");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return id;
    }

    /**
     * 获取user信息
     * @param accountNumber 账号
     * @return user
     */
    public User getUser(String accountNumber, String password) {
        String sql = "select id, sex, department, userName, userIdentity from tjut_user where accountNumber = ? limit 1";
        int id = 0;
        String sex = null;
        String name = null;
        String identity = null;
        String department = null;
        try {
            preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1, accountNumber);
            resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                id = resultSet.getInt("id");
                sex = resultSet.getString("sex");
                department = resultSet.getString("department");
                name = resultSet.getString("userName");
                identity = resultSet.getString("userIdentity");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        if (id == 0) {
            return null;
        }else {
            User user = new User();
            user.setId(id);
            user.setAccountNumber(accountNumber);
            user.setPassword(password);
            user.setName(name);
            user.setIdentity(identity);
            user.setAccountNumber(accountNumber);
            user.setSex(sex);
            user.setDepartment(department);
            return user;
        }
    }

    /**
     * 获取planNail list
     * @param id 用户id
     * @return planNail list
     */
    public ArrayList<PlanNail> getPlanNailList(int id) {
        if (!isExistTable("plan_nail")) {
            return new ArrayList<PlanNail>();
        }
        String sql = "select * from plan_nail where id = ?";
        int x, y;
        String date, record;
        PlanNail nail = null;
        ArrayList<PlanNail> nailArrayList = new ArrayList<>();
        try {
            preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setInt(1, id);
            resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                x = resultSet.getInt("x");
                y = resultSet.getInt("y");
                date = resultSet.getString("mDate");
                record = resultSet.getString("record");
                nail = new PlanNail(id, x, y, date, record);
                nailArrayList.add(nail);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return nailArrayList;
    }

    /**
     * 获取planPullNail list
     * @param id
     * @return planPullNail list
     */
    public ArrayList<PlanPullNail> getPlanPullNailList(int id) {
        ArrayList<PlanPullNail> nailArrayList = new ArrayList<>();
        if (!isExistTable("plan_pull_nail")) {
            return nailArrayList;
        }
        String sql = "select * from plan_pull_nail where id = ?";
        String firstDate, firstRecord, lastDate, lastRecord;
        PlanPullNail nail = null;
        try {
            preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setInt(1, id);
            resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                firstDate = resultSet.getString("firstDate");
                firstRecord = resultSet.getString("firstRecord");
                lastDate = resultSet.getString("lastDate");
                lastRecord = resultSet.getString("lastRecord");
                nail = new PlanPullNail(id, firstDate, firstRecord, lastDate, lastRecord);
                nailArrayList.add(nail);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return nailArrayList;
    }

    /**
     * 获取planWeek list
     * @param id
     * @return
     */
    public ArrayList<PlanDate> getPlanWeekList(int id) {
        if (!isExistTable("plan_week")) {
            return new ArrayList<PlanDate>();
        }
        return getPlanDateList(id, "week");
    }

    /**
     * 获取planMonth list
     * @param id
     * @return
     */
    public ArrayList<PlanDate> getPlanMonthList(int id) {
        if (!isExistTable("plan_month")) {
            return new ArrayList<PlanDate>();
        }
        return getPlanDateList(id, "month");
    }

    /**
     * 获取plan日期info list
     * @param id
     * @param tag
     * @return
     */
    private ArrayList<PlanDate> getPlanDateList(int id, String tag) {
        String sql = "select * from plan_" + tag + " where id = ?";
        int nailNum, pullNum;
        String date;
        PlanDate planWeek;
        ArrayList<PlanDate>  planDateArrayList = new ArrayList<>();
        try {
            preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setInt(1, id);
            resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                nailNum = resultSet.getInt("nailNum");
                pullNum = resultSet.getInt("pullNum");
                date = resultSet.getString("mDate");
                planWeek = new PlanDate(id, date, nailNum, pullNum);
                planDateArrayList.add(planWeek);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return planDateArrayList;
    }

    /**
     * 获取good nail list
     * @param id
     * @return
     */
    public ArrayList<MoodGoodNail> getMoodGoodNailList(int id) {
        if (!isExistTable("mood_good_nail")) {
            return new ArrayList<MoodGoodNail>();
        }
        String sql = "select x, y, firstDate, record, state, visibility, anonymous from mood_good_nail where id = ?";
        int x, y, state, visibility, anonymous;
        String firstDate, record;
        MoodGoodNail nail;
        ArrayList<MoodGoodNail> nailArrayList = new ArrayList<>();
        try {
            preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setInt(1, id);
            resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                x = resultSet.getInt("x");
                y = resultSet.getInt("y");
                visibility = resultSet.getInt("visibility");
                state = resultSet.getInt("state");
                anonymous = resultSet.getInt("anonymous");
                firstDate = resultSet.getString("firstDate");
                record = resultSet.getString("record");
                nail = new MoodGoodNail(id, x, y, firstDate, null, record, state, visibility, anonymous);
                nailArrayList.add(nail);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return nailArrayList;
    }

    /**
     * 获取mood bad nail 在墙上的钉子
     * @param id
     * @return
     */
    public ArrayList<MoodBadNail> getMoodBadNail(int id) {
        if (!isExistTable("mood_bad_nail")) {
            return new ArrayList<MoodBadNail>();
        }
        String sql = "select x, y, firstDate, record, state, visibility, anonymous from mood_bad_nail where id = ?";
        int x, y, anonymous, visibility, state;
        String date, record;
        MoodBadNail nail;
        ArrayList<MoodBadNail> nailArrayList = new ArrayList<>();
        try {
            preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setInt(1, id);
            resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                x = resultSet.getInt("x");
                y = resultSet.getInt("y");
                date = resultSet.getString("firstDate");
                record = resultSet.getString("record");
                state = resultSet.getInt("state");
                visibility = resultSet.getInt("visibility");
                anonymous = resultSet.getInt("anonymous");
                nail = new MoodBadNail(id, x, y, date, null, record, state, visibility, anonymous, 0);
                nailArrayList.add(nail);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return nailArrayList;
    }

    /**
     * 获取情绪周信息list
     * @param id
     * @return
     */
    public ArrayList<MoodDate> getMoodWeekList(int id) {
        if (!isExistTable("mood_week")) {
            return new ArrayList<MoodDate>();
        }
        return getMoodDateList(id, "mood_week");
    }

    /**
     * 获取情绪月信息list
     * @param id
     * @return
     */
    public ArrayList<MoodDate> getMoodMonthList(int id) {
        if (!isExistTable("mood_month")) {
            return new ArrayList<MoodDate>();
        }
        return getMoodDateList(id, "mood_month");
    }

    /**
     *
     * @param id
     * @param tableName
     * @return
     */
    private ArrayList<MoodDate> getMoodDateList(int id, String tableName) {
        String sql = "select mDate, goodNailNum, goodPullNum, badNailNum, badPullNum from " + tableName + " where id = ?";
        String date;
        int goodNailNum, goodPullNum, badNailNum, badPullNum;
        MoodDate moodDate;
        ArrayList<MoodDate> moodDateArrayLis = new ArrayList<>();
        try {
            preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setInt(1, id);
            resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                date = resultSet.getString("mDate");
                goodNailNum = resultSet.getInt("goodNailNum");
                goodPullNum = resultSet.getInt("goodPullNum");
                badNailNum = resultSet.getInt("badNailNum");
                badPullNum = resultSet.getInt("badPullNum");
                moodDate = new MoodDate(id, date, goodNailNum, goodPullNum, badNailNum, badPullNum);
                moodDateArrayLis.add(moodDate);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return moodDateArrayLis;
    }

    /**
     * get crack list
     * @param id
     * @return
     */
    public ArrayList<Crack>  getCrackList(int id) {
        if (!isExistTable("crack")) {
            return new ArrayList<Crack>();
        }
        String sql = "select * from crack where id = ?";
        int x, y, num, power, resId;
        String date;
        ArrayList<Crack> crackArrayList = new ArrayList<>();
        Crack crack;
        try {
            preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setInt(1, id);
            resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                x = resultSet.getInt("x");
                y = resultSet.getInt("y");
                num = resultSet.getInt("num");
                power = resultSet.getInt("power");
                resId = resultSet.getInt("resId");
                date = resultSet.getString("mDate");
                crack = new Crack(id, x, y, date, num, power, resId);
                crackArrayList.add(crack);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return crackArrayList;
    }
}
