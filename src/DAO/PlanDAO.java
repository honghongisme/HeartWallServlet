package DAO;

import bean.PlanNail;
import bean.PlanPullNail;
import util.DateHelper;

import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.util.ArrayList;

public class PlanDAO extends BaseDAO{


    public PlanDAO() {
        createTables();
    }

    /************************************** create tables *********************************************/

    private void createTables() {
        createPlanNailTable();
        createPlanPullNailTable();
        createPlanWeekTable();
        createPlanMonthTable();
    }

    private void createPlanNailTable() {
        String sql = "create table if not exists plan_nail(id int, x int, y int, mDate datetime, record text) charset utf8mb4 collate utf8mb4_bin";
        try {
            preparedStatement = connection.prepareStatement(sql);
            preparedStatement.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void createPlanPullNailTable() {
        String sql = "create table if not exists plan_pull_nail(id int, firstDate datetime, firstRecord text, lastDate datetime, lastRecord text) charset utf8mb4 collate utf8mb4_bin";
        try {
            preparedStatement = connection.prepareStatement(sql);
            preparedStatement.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void createPlanWeekTable() {
        String sql = "create table if not exists plan_week(id int, mDate char(14), nailNum int, pullNum int) charset utf8mb4 collate utf8mb4_bin";
        try {
            preparedStatement = connection.prepareStatement(sql);
            preparedStatement.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void createPlanMonthTable() {
        String sql = "create table if not exists plan_month(id int, mDate char(7), nailNum int, pullNum int) charset utf8mb4 collate utf8mb4_bin";
        try {
            preparedStatement = connection.prepareStatement(sql);
            preparedStatement.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /************************************insert and update plan nail ************************************/

    public void updatePlanNailOnBag(PlanPullNail nail) {
        insertPlanPullNail(nail);
        deletePlanNailOnBag(nail);
    }

    public void deletePlanNailOnBag(PlanPullNail nail) {
        String sql = "delete from plan_nail where id = ? and mDate = ?";
        try {
            preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setInt(1, nail.getId());
            preparedStatement.setString(2, nail.getFirstDate());
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void insertPlanNail(PlanNail nail) {
        String sql = "insert into plan_nail values(?, ?, ?, ?, ?)";
        try {
            preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setInt(1, nail.getId());
            preparedStatement.setInt(2, nail.getX());
            preparedStatement.setInt(3, nail.getY());
            preparedStatement.setTimestamp(4, Timestamp.valueOf(nail.getDate()));
            preparedStatement.setString(5, nail.getRecord());
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     *
     * @param planNail
     * @param planPullNail
     */
    public void updatePlanNail(PlanNail planNail, PlanPullNail planPullNail) {

        String sql = "select mDate, record from plan_nail where id = ? and x = ? and y = ? limit 1";
        try {
            preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setInt(1, planNail.getId());
            preparedStatement.setInt(2, planNail.getX());
            preparedStatement.setInt(3, planNail.getY());
            resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                System.out.println("------------------date1 = " + DateFormat.getDateTimeInstance().format(resultSet.getTimestamp("mDate")));
                planPullNail.setFirstDate(resultSet.getString("mDate"));
                planPullNail.setFirstRecord(resultSet.getString("record"));
            }
            insertPlanPullNail(planPullNail);
            deletePlanNail(planNail);
        } catch (SQLException s) {
            s.printStackTrace();
        }
    }

    /**
     *
     * @param nail
     */
    public void deletePlanNail(PlanNail nail) {
        String sql = "delete from plan_nail where id = ? and x = ? and y = ?";
        try {
            preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setInt(1, nail.getId());
            preparedStatement.setInt(2, nail.getX());
            preparedStatement.setInt(3, nail.getY());
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     *
     * @param nail
     */
    public void insertPlanPullNail(PlanPullNail nail) {
        String sql = "insert into plan_pull_nail values(?, ?, ?, ?, ?)";
        try {
            preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setInt(1, nail.getId());
            preparedStatement.setTimestamp(2, Timestamp.valueOf(nail.getFirstDate()));
            preparedStatement.setString(3, nail.getFirstRecord());
            preparedStatement.setTimestamp(4, Timestamp.valueOf(nail.getLastDate()));
            preparedStatement.setString(5, nail.getLastRecord());
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**************************** update date info *******************************************/

    /**
     * 更新日期信息
     * @param id
     * @param tableName mood_week / mood_month
     * @param updateCol 更新的列
     * @param date
     */
    public void updatePlanDate(int id, String tableName, int updateCol, String date) {
        insertPlanDate(id, tableName);
        String sql = null;
        if (updateCol == 1) {
            sql = "update " + tableName + " set nailNum = nailNum + 1 where id = ? and mDate = ?";
        }else if (updateCol == 2){
            sql = "update " + tableName + " set pullNum = pullNum + 1 where id = ? and mDate = ?";
        }
        try {
            preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setInt(1, id);
            preparedStatement.setString(2, date);
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    /**
     * 新增空的日期行记录
     * @param id
     * @param tableName
     */
    private void insertPlanDate(int id, String tableName) {
        ArrayList<String> list = "plan_week".equals(tableName) ? DateHelper.getCurrentWeeksDate("yyyy-MM-dd") : DateHelper.getCurrentMonthsDate("yyyy");
        String sql = "select count(*) as countNum from " + tableName + " where mDate = ? and id = ? limit 1";
        try {
            preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1, list.get(0));
            preparedStatement.setInt(2, id);
            resultSet = preparedStatement.executeQuery();
            int num = 0;
            while (resultSet.next()) {
                num = resultSet.getInt("countNum");
            }
            if (num == 0) {
                sql = "insert into " + tableName + " values(?, ?, ?, ?)";
                preparedStatement = connection.prepareStatement(sql);
                for (String aList : list) {
                    preparedStatement.setInt(1, id);
                    preparedStatement.setString(2, aList);
                    preparedStatement.setInt(3, 0);
                    preparedStatement.setInt(4, 0);
                    preparedStatement.addBatch();
                }
                preparedStatement.executeBatch();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
