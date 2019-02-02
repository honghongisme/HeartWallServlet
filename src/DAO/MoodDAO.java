package DAO;


import bean.*;
import util.DateHelper;

import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;

public class MoodDAO extends BaseDAO {

    public MoodDAO() {
        createTables();
    }

    /****************************************** create tables *********************************************/

    private void createTables() {
        createMoodBadNailTables();
        createCommentTable();
        createMoodWeekTable();
        createMoodMonthTable();
        createCrackTable();
        createMoodGoodNailTable();
    }

    private void createMoodBadNailTables() {
        String sql = "create table if not exists mood_bad_nail(id int, x int, y int, firstDate datetime, lastDate datetime, record text , state int, visibility int, anonymous int, commentTag int auto_increment unique) charset utf8mb4 collate utf8mb4_bin";
        try {
            preparedStatement = connection.prepareStatement(sql);
            preparedStatement.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void createCommentTable() {
        String sql = "create table if not exists comment(id int, commentTag int, record text, mDate datetime, anonymous int) charset utf8mb4 collate utf8mb4_bin";
        try {
            preparedStatement = connection.prepareStatement(sql);
            preparedStatement.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void createMoodWeekTable() {
        String sql = "create table if not exists mood_week(id int, mDate char(14), badNailNum int, badPullNum int, goodNailNum int, goodPullNum int) charset utf8mb4 collate utf8mb4_bin";
        try {
            preparedStatement = connection.prepareStatement(sql);
            preparedStatement.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void createMoodMonthTable() {
        String sql = "create table if not exists mood_month(id int, mDate char(7), badNailNum int, badPullNum int, goodNailNum int, goodPullNum int) charset utf8mb4 collate utf8mb4_bin";
        try {
            preparedStatement = connection.prepareStatement(sql);
            preparedStatement.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void createCrackTable() {
        String sql = "create table if not exists crack(id int, x int, y int, mDate datetime, num int, power int, resId int) charset utf8mb4 collate utf8mb4_bin";
        try {
            preparedStatement = connection.prepareStatement(sql);
            preparedStatement.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void createMoodGoodNailTable() {
        String sql = "create table if not exists mood_good_nail(id int, x int, y int, firstDate datetime, lastDate datetime, record text, state int, visibility int, anonymous int) charset utf8mb4 collate utf8mb4_bin";
        try {
            preparedStatement = connection.prepareStatement(sql);
            preparedStatement.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**************************************get comment list of mood bad nail*******************************************/

    /**
     * 获取指定id和位置的comment tag
     * @param id
     * @param x
     * @param y
     * @return
     */
    private int getCommentTag(int id, int x, int y) {
        String sql = "select commentTag from mood_bad_nail where id = ? and x = ? and y = ? and state = ? limit 1";
        int commentTag = 0;
        try {
            preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setInt(1, id);
            preparedStatement.setInt(2, x);
            preparedStatement.setInt(3, y);
            preparedStatement.setInt(4, 0);
            resultSet = preparedStatement.executeQuery();
            System.out.println("  -------------- start");
            System.out.println("  -------------- id = " + id + " ; x = " + x + " ; y = " + y);
            while (resultSet.next()) {
                commentTag = resultSet.getInt("commentTag");
                System.out.println("  -------------- commentTag = " + commentTag);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return commentTag;
    }

    /**
     * get comment list
     * @param id
     * @param x
     * @param y
     * @return
     */
    public ArrayList<MoodBadComment> getCommentList(int id, int x, int y) {
        ArrayList<MoodBadComment> commentArrayList = new ArrayList<>();
        if (!isExistTable("comment")) return commentArrayList;
        String sql = "select id, record, mDate, anonymous from comment where commentTag = ?";
        MoodBadComment comment;
        int commentorId, anonymous;
        String record, date;
        int commentTag = getCommentTag(id, x, y);
        try {
            preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setInt(1, commentTag);
            resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                commentorId = resultSet.getInt("id");
                record = resultSet.getString("record");
                date = resultSet.getString("mDate");
                anonymous = resultSet.getInt("anonymous");
                comment = new MoodBadComment(commentorId, commentTag, date, record, anonymous);
                commentArrayList.add(comment);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return commentArrayList;
    }

    /**
     *
     * @param moodBadComments
     * @return
     */
    public ArrayList<CommentItem> getCommentItemList(ArrayList<MoodBadComment> moodBadComments) {
        ArrayList<CommentItem> commentItems = new ArrayList<>();
        CommentItem commentItem;
        for (MoodBadComment moodBadComment : moodBadComments) {
            commentItem = new CommentItem();
            commentItem.setDate(DateHelper.getDateDistance(moodBadComment.getDate()));
            commentItem.setContent(moodBadComment.getRecord());
            if (moodBadComment.getIsAnonymous() == 1) {
                commentItem.setUserName("匿名用户");
            } else {
                commentItem.setUserName(getUserNameById(moodBadComment.getId()));
            }
            commentItems.add(commentItem);
        }
        return commentItems;
    }

    /**
     * get username by user id
     * @param id
     * @return
     */
    private String getUserNameById(int id) {
        String sql = "select userName from tjut_user where id = ? limit 1";
        String name = null;
        try {
            preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setInt(1, id);
            resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                name = resultSet.getString("userName");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return name;
    }

    /*************************************** insert and update mood good type nail ******************************************/

    /**
     *
     * @param nail
     */
    public void insertMoodGoodNail(MoodGoodNail nail) {
        String sql = "insert into mood_good_nail(id, x, y, firstDate, record, state, visibility, anonymous) values(?, ?, ?, ?, ?, ?, ?, ?)";
        try {
            preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setInt(1, nail.getId());
            preparedStatement.setInt(2, nail.getX());
            preparedStatement.setInt(3, nail.getY());
            preparedStatement.setTimestamp(4, Timestamp.valueOf(nail.getFirstDate()));
            preparedStatement.setString(5, nail.getRecord());
            preparedStatement.setInt(6, 0);
            preparedStatement.setInt(7, nail.getVisibility());
            preparedStatement.setInt(8, nail.getAnonymous());
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     *
     * @param nail
     */
    public void updateMoodGoodNail(MoodGoodNail nail) {
        System.out.println("------updateMoodGoodNail");
        String sql = "update mood_good_nail set lastDate = ?, state = ? where id = ? and x = ? and y = ? and state = ?";
        try {
            preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setTimestamp(1, Timestamp.valueOf(nail.getLastDate()));
            preparedStatement.setInt(2, 1);
            preparedStatement.setInt(3, nail.getId());
            preparedStatement.setInt(4, nail.getX());
            preparedStatement.setInt(5, nail.getY());
            preparedStatement.setInt(6, 0);
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /************************************* insert and update mood bad type nail ****************************************/

    /**
     *
     * @param nail
     */
    public void insertMoodBadNail(MoodBadNail nail) {
        String sql = "insert into mood_bad_nail(id, x, y, firstDate, record, state, visibility, anonymous) values(?, ?, ?, ?, ?, ?, ?, ?)";
        try {
            preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setInt(1, nail.getId());
            preparedStatement.setInt(2, nail.getX());
            preparedStatement.setInt(3, nail.getY());
            preparedStatement.setTimestamp(4, Timestamp.valueOf(nail.getFirstDate()));
            preparedStatement.setString(5, nail.getRecord());
            preparedStatement.setInt(6, 0);
            preparedStatement.setInt(7, nail.getVisibility());
            preparedStatement.setInt(8, nail.getAnonymous());
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     *
     * @param id
     * @param x
     * @param y
     * @param lastDate
     */
    public void updateMoodBadNail(int id, int x, int y, String lastDate) {
        String sql = "update mood_bad_nail set lastDate = ?, state = ? where id = ? and x = ? and y = ? and state = ?";
        try {
            preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setTimestamp(1, Timestamp.valueOf(lastDate));
            preparedStatement.setInt(2, 1);
            preparedStatement.setInt(3, id);
            preparedStatement.setInt(4, x);
            preparedStatement.setInt(5, y);
            preparedStatement.setInt(6, 0);
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /************************************** insert crack info *********************************************/

    public void insertCrackInfo(Crack crack) {
        String sql = "insert into crack values(?, ?, ?, ?, ?, ?, ?)";
        try {
            preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setInt(1, crack.getId());
            preparedStatement.setInt(2, crack.getX());
            preparedStatement.setInt(3, crack.getY());
            preparedStatement.setTimestamp(4, Timestamp.valueOf(crack.getDate()));
            preparedStatement.setInt(5, crack.getNum());
            preparedStatement.setInt(6, crack.getPower());
            preparedStatement.setInt(7, crack.getResId());
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /************************************** insert and update mood date info **********************************************/

    /**
     * 更新日期信息
     * @param id
     * @param tableName mood_week / mood_month
     * @param updateCol 更新的列
     * @param date
     */
    public void updateMoodDate(int id, String tableName, int updateCol, String date) {
        insertMoodDate(id, tableName);
        String sql = null;
        if (updateCol == 1) {
            sql = "update " + tableName + " set badNailNum = badNailNum + 1 where id = ? and mDate = ?";
        }else if (updateCol == 2){
            sql = "update " + tableName + " set badPullNum = badPullNum + 1 where id = ? and mDate = ?";
        }else if (updateCol == 3){
            sql = "update " + tableName + " set goodNailNum = goodNailNum + 1 where id = ? and mDate = ?";
        } else if (updateCol == 4) {
            sql = "update " + tableName + " set goodPullNum = goodPullNum + 1 where id = ? and mDate = ?";
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
    private void insertMoodDate(int id, String tableName) {
        ArrayList<String> list = "mood_week".equals(tableName) ? DateHelper.getCurrentWeeksDate("yyyy-MM-dd") : DateHelper.getCurrentMonthsDate("yyyy");
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
                sql = "insert into " + tableName + " values(?, ?, ?, ?, ?, ?)";
                preparedStatement = connection.prepareStatement(sql);
                for (String aList : list) {
                    preparedStatement.setInt(1, id);
                    preparedStatement.setString(2, aList);
                    preparedStatement.setInt(3, 0);
                    preparedStatement.setInt(4, 0);
                    preparedStatement.setInt(5, 0);
                    preparedStatement.setInt(6, 0);
                    preparedStatement.addBatch();
                }
                preparedStatement.executeBatch();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /******************************** randomly select 6(3+3) nail content ******************************************/

    /**
     * 获取好坏交叉的钉子内容
     * @param items
     * @param commentTagList
     */
    public void getRandomItem(ArrayList<CommentItem> items, ArrayList<Integer> commentTagList) {
        ArrayList<MoodBadNail> badNails = getRandomBadNailList();
        ArrayList<MoodGoodNail> goodNails = getRandomGoodNailList();
        MoodBadNail badNail;
        MoodGoodNail goodNail;
        CommentItem commentItem;
        for (MoodBadNail nail: badNails
             ) {
            commentItem = new CommentItem();
            commentItem.setContent(nail.getRecord());
            commentItem.setDate(DateHelper.getDateDistance(nail.getFirstDate()));
            if (nail.getAnonymous() == 1) {
                commentItem.setUserName("匿名用户");
            } else {
                commentItem.setUserName(getUserNameById(nail.getId()));
            }
            items.add(commentItem);
            commentTagList.add(nail.getCommentTag());
        }
        for (MoodGoodNail nail: goodNails
                ) {
            commentItem = new CommentItem();
            commentItem.setContent(nail.getRecord());
            commentItem.setDate(DateHelper.getDateDistance(nail.getFirstDate()));
            if (nail.getAnonymous() == 1) {
                commentItem.setUserName("匿名用户");
            } else {
                commentItem.setUserName(getUserNameById(nail.getId()));
            }
            items.add(commentItem);
            commentTagList.add(0);
        }
      /*  if (badNails.size() > goodNails.size()) {
            for (int i = 0, j = 0; i < goodNails.size() * 2; i+=2, j++) {
                badNail = badNails.get(j);
                commentItem = new CommentItem();
                commentItem.setContent(badNail.getRecord());
                commentItem.setDate(DateHelper.getDateDistance(badNail.getFirstDate()));
                if (badNail.getAnonymous() == 1) {
                    commentItem.setUserName("匿名用户");
                } else {
                    commentItem.setUserName(getUserNameById(badNail.getId()));
                }
                items.add(i, commentItem);
            }
            for (int i = 1, j = 0; i < (goodNails.size() * 2 + 1); i+=2, j++) {
                goodNail = goodNails.get(j);
                commentItem = new CommentItem();
                commentItem.setContent(goodNail.getRecord());
                commentItem.setDate(DateHelper.getDateDistance(goodNail.getFirstDate()));
                if (goodNail.getAnonymous() == 1) {
                    commentItem.setUserName("匿名用户");
                } else {
                    commentItem.setUserName(getUserNameById(goodNail.getId()));
                }
                items.add(i, commentItem);
            }
            int j = badNails.size() - goodNails.size();
            for (int i = goodNails.size(); j < 0; i++, j--) {
                badNail = badNails.get(i);
                commentItem = new CommentItem();
                commentItem.setContent(badNail.getRecord());
                commentItem.setDate(DateHelper.getDateDistance(badNail.getFirstDate()));
                if (badNail.getAnonymous() == 1) {
                    commentItem.setUserName("匿名用户");
                } else {
                    commentItem.setUserName(getUserNameById(badNail.getId()));
                }
                items.add(commentItem);
            }
        } else {
            for (int i = 0, j = 0; i < badNails.size() * 2; i+=2, j++) {
                badNail = badNails.get(j);
                commentItem = new CommentItem();
                commentItem.setContent(badNail.getRecord());
                commentItem.setDate(DateHelper.getDateDistance(badNail.getFirstDate()));
                if (badNail.getAnonymous() == 1) {
                    commentItem.setUserName("匿名用户");
                } else {
                    commentItem.setUserName(getUserNameById(badNail.getId()));
                }
                items.add(i, commentItem);
            }
            for (int i = 1, j = 0; i < (badNails.size() * 2 + 1); i+=2, j++) {
                goodNail = goodNails.get(j);
                commentItem = new CommentItem();
                commentItem.setContent(goodNail.getRecord());
                commentItem.setDate(DateHelper.getDateDistance(goodNail.getFirstDate()));
                if (goodNail.getAnonymous() == 1) {
                    commentItem.setUserName("匿名用户");
                } else {
                    commentItem.setUserName(getUserNameById(goodNail.getId()));
                }
                items.add(i, commentItem);
            }
            int j = goodNails.size() - badNails.size();
            for (int i = goodNails.size(); j < 0; i++, j--) {
                goodNail = goodNails.get(j);
                commentItem = new CommentItem();
                commentItem.setContent(goodNail.getRecord());
                commentItem.setDate(DateHelper.getDateDistance(goodNail.getFirstDate()));
                if (goodNail.getAnonymous() == 1) {
                    commentItem.setUserName("匿名用户");
                } else {
                    commentItem.setUserName(getUserNameById(goodNail.getId()));
                }
                items.add(commentItem);
            }
        }*/
    }

    /**
     * get 3 random bad nail list
     * @return
     */

    public ArrayList<MoodBadNail> getRandomBadNailList() {
      //  String sql = "SELECT * FROM mood_bad_nail WHERE id >= (SELECT floor( RAND() * ((SELECT MAX(id) FROM mood_bad_nail - (SELECT MIN(id) FROM mood_bad_nail))  + (SELECT MIN(id) FROM mood_bad_nail))) ORDER BY id LIMIT 1;" +
        //        "SELECT * FROM mood_bad_nail AS t1 JOIN (SELECT ROUND(RAND() * ((SELECT MAX(id) FROM mood_bad_nail)-(SELECT MIN(id) FROM mood_bad_nail))+(SELECT MIN(id) FROM mood_bad_nail)) AS id) AS t2 WHERE t1.id >= t2.id ORDER BY t1.id LIMIT 1;";
    //    String sql = "SELECT id, firstDate, record, anonymous FROM mood_bad_nail WHERE state = 0 and visibility = 1 and id >= ((SELECT MAX(id) FROM mood_bad_nail)-(SELECT MIN(id) FROM mood_bad_nail)) * RAND() + (SELECT MIN(id) FROM mood_bad_nail) LIMIT 3";
  //      String sql = "SELECT id, firstDate, record, anonymous FROM mood_bad_nail WHERE state = 0 and visibility = 1 LIMIT 0,3";
   //     String sql = "select id, firstDate, record, anonymous, commentTag from mood_bad_nail state = 0 and visibility = 1" +
   //             " and id >= ((select max(id) from mood_bad_nail) - (select min(id) from mood_bad_nail)) * rand() + " +
   //             " (select min(id) from mood_bad_nail) limit 3";
        String sql = "SELECT id, firstDate, record, anonymous, commentTag FROM mood_bad_nail ORDER BY rand() LIMIT 3";
        ArrayList<MoodBadNail> nailArrayList = new ArrayList<>();
        MoodBadNail nail;
        int id, anonymous, commentTag;
        String firstDate, record;
        try {
            preparedStatement = connection.prepareStatement(sql);
            resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                id = resultSet.getInt("id");
                anonymous = resultSet.getInt("anonymous");
                firstDate = resultSet.getString("firstDate");
                record = resultSet.getString("record");
                commentTag = resultSet.getInt("commentTag");
                nail = new MoodBadNail(id, 0, 0, firstDate, null, record, 0, 1, anonymous, commentTag);
                nailArrayList.add(nail);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return nailArrayList;
    }

    /**
     * get 3 random good nail list
     * @return
     */
    public ArrayList<MoodGoodNail> getRandomGoodNailList() {
 //       String sql = "SELECT id, firstDate, record, anonymous FROM mood_good_nail WHERE state = 0 and visibility = 1 and id >= ((SELECT MAX(id) FROM mood_good_nail)-(SELECT MIN(id) FROM mood_good_nail)) * RAND() + (SELECT MIN(id) FROM mood_good_nail) LIMIT 3";
      //  String sql = "SELECT id, firstDate, record, anonymous FROM mood_good_nail WHERE state = 0 and visibility = 1 LIMIT 0,3";
   /*     String sql = "select id, firstDate, record, anonymous from mood_good_nail state = 0 and visibility = 1" +
                " and id >= ((select max(id) from mood_good_nail) - (select min(id) from mood_good_nail)) * rand() + " +
                " (select min(id) from mood_good_nail) limit 3";*/
        String sql = "SELECT id, firstDate, record, anonymous FROM mood_good_nail ORDER BY rand() LIMIT 3";
        ArrayList<MoodGoodNail> nailArrayList = new ArrayList<>();
        MoodGoodNail nail;
        int id, anonymous;
        String firstDate, record;
        try {
            preparedStatement = connection.prepareStatement(sql);
            resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                id = resultSet.getInt("id");
                firstDate = resultSet.getString("firstDate");
                record = resultSet.getString("record");
                anonymous = resultSet.getInt("anonymous");
                nail = new MoodGoodNail(id, 0, 0, firstDate, null, record,0,  1, anonymous);
                nailArrayList.add(nail);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return nailArrayList;
    }

    /************************************** insert one comment ***************************************/
    public void insertOneComment(MoodBadComment comment) {
        String sql = "insert into comment values(?, ?, ?, ?, ?)";
        try {
            preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setInt(1, comment.getId());
            preparedStatement.setInt(2, comment.getTag());
            preparedStatement.setString(3, comment.getRecord());
            preparedStatement.setTimestamp(4, Timestamp.valueOf(comment.getDate()));
            preparedStatement.setInt(5, comment.getIsAnonymous());
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

}
