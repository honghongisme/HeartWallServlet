package DAO;

import bean.CommentItem;
import bean.MoodBadComment;
import util.JDBCManager;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class BaseDAO {

    protected Connection connection = null;
    protected PreparedStatement preparedStatement = null;
    protected ResultSet resultSet = null;

    BaseDAO() {
        //获得数据库的连接对象
        connection = JDBCManager.getConnection();
    }

    /**
     * 关闭数据库
     */
    public void closeDB() {
        JDBCManager.closeAll(connection, preparedStatement, resultSet);
    }

    /**
     * 是否存在某表
     * @param tableName
     * @return
     */
    public boolean isExistTable(String tableName) {
        String sql = "SELECT count(*) as countNum FROM information_schema.TABLES WHERE table_name = ?";
        boolean result = true;
        try {
            preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1, tableName);
            resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                if (resultSet.getInt("countNum") == 0) {
                    result = false;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return result;
    }
}
