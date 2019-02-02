package servlet;

import DAO.MoodDAO;
import bean.*;
import com.google.gson.Gson;
import net.sf.json.JSONObject;
import util.DateHelper;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;

@WebServlet(name = "MoodServlet")
public class MoodServlet extends HttpServlet {

    /**
     * 钉下 bad nail
     */
    private static final String INSERT_BAD_NAIL = "1";
    /**
     * 拔出 bad nail
     */
    private static final String UPDATE_BAD_NAIL = "2";
    /**
     * 钉下 good nail
     */
    private static final String INSERT_GOOD_NAIL = "3";
    /**
     * 拔出 good nail
     */
    private static final String UPDATE_GOOD_NAIL = "4";
    /**
     * 查询一条指定记录的comment list
     */
    private static final String SELECT_ONE_COMMENT_LIST = "5";
    /**
     * 随机查询多条bad nail信息
     */
    private static final String SELECT_SOME_RANDOM_NAIL = "6";
    /**
     * 插入一条评论
     */
    private static final String INSERT_ONE_COMMENT = "7";



    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("text/html;charset=utf-8");
        request.setCharacterEncoding("utf-8");
        response.setCharacterEncoding("utf-8");

        String operationType = request.getParameter("OperationType");
        MoodDAO dao = new MoodDAO();
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("Result", "success");
        Gson gson = new Gson();
        if (INSERT_BAD_NAIL.equals(operationType)) {
            String badNailJson = request.getParameter("IBadNail");
            String crackJson = request.getParameter("Crack");
            MoodBadNail nail = (MoodBadNail) gson.fromJson(badNailJson, MoodBadNail.class);
            if (!("".equals(crackJson))) {
                Crack crack = gson.fromJson(crackJson, Crack.class);
                dao.insertCrackInfo(crack);
            }
            dao.insertMoodBadNail(nail);
            dao.updateMoodDate(nail.getId(), "mood_week", 1, DateHelper.getWeekDateStr(nail.getFirstDate()));
            dao.updateMoodDate(nail.getId(), "mood_month", 1, DateHelper.getMonthDateStr(nail.getFirstDate()));
        }else if (UPDATE_BAD_NAIL.equals(operationType)) {
            String badNailInfo = request.getParameter("UBadNail");
            MoodBadNail nail = gson.fromJson(badNailInfo, MoodBadNail.class);
            dao.updateMoodBadNail(nail.getId(), nail.getX(), nail.getY(), nail.getLastDate());
            dao.updateMoodDate(nail.getId(), "mood_week", 2, DateHelper.getWeekDateStr(nail.getLastDate()));
            dao.updateMoodDate(nail.getId(), "mood_month", 2, DateHelper.getMonthDateStr(nail.getLastDate()));
        }else if (SELECT_ONE_COMMENT_LIST.equals(operationType)) {
            String badNail = request.getParameter("SBadNail");
            MoodBadNail nail = gson.fromJson(badNail, MoodBadNail.class);
            ArrayList<CommentItem> list = dao.getCommentItemList(dao.getCommentList(nail.getId(), nail.getX(), nail.getY()));
            jsonObject.put("commentItem", gson.toJson(list));
        }else if (SELECT_SOME_RANDOM_NAIL.equals(operationType)) {
            ArrayList<CommentItem> list = new ArrayList<>();
            ArrayList<Integer> commentTagList = new ArrayList<>();
            dao.getRandomItem(list, commentTagList);
            System.out.println("--------------------list.size = " + list.size());
            jsonObject.put("random", gson.toJson(list));
            jsonObject.put("commentTag", gson.toJson(commentTagList));
        }else if (INSERT_GOOD_NAIL.equals(operationType)) {
            String goodNailInfo = request.getParameter("IGoodNail");
            MoodGoodNail nail = gson.fromJson(goodNailInfo, MoodGoodNail.class);
            dao.insertMoodGoodNail(nail);
            dao.updateMoodDate(nail.getId(), "mood_week", 3, DateHelper.getWeekDateStr(nail.getFirstDate()));
            dao.updateMoodDate(nail.getId(), "mood_month", 3, DateHelper.getMonthDateStr(nail.getFirstDate()));
        }else if (UPDATE_GOOD_NAIL.equals(operationType)) {
            String goodNailInfo = request.getParameter("UGoodNail");
            MoodGoodNail nail = gson.fromJson(goodNailInfo, MoodGoodNail.class);
            dao.updateMoodGoodNail(nail);
            dao.updateMoodDate(nail.getId(), "mood_week", 4, DateHelper.getWeekDateStr(nail.getLastDate()));
            dao.updateMoodDate(nail.getId(), "mood_month", 4, DateHelper.getMonthDateStr(nail.getLastDate()));
        } else if (INSERT_ONE_COMMENT.equals(operationType)) {
            String commentJsonStr = request.getParameter("Comment");
            MoodBadComment comment = gson.fromJson(commentJsonStr, MoodBadComment.class);
            dao.insertOneComment(comment);
        }

        dao.closeDB();

        try (PrintWriter out = response.getWriter()){
            out.write(jsonObject.toString());
            out.flush();
        }

    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doPost(request, response);
    }
}
