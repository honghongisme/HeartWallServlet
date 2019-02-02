package servlet;

import DAO.PlanDAO;
import bean.PlanNail;
import bean.PlanPullNail;
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

@WebServlet(name = "PlanServlet")
public class PlanServlet extends HttpServlet {

    /**
     * 钉下操作
     */
    private static final String INSERT_NAIL = "1";
    /**
     * 拔出操作
     */
    private static final String DELETE_NAIL = "2";
    private static final String DELETE_NAIL_ON_BAG = "3";

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("text/html;charset=utf-8");
        request.setCharacterEncoding("utf-8");
        response.setCharacterEncoding("utf-8");

        String operationType = request.getParameter("OperationType");
        PlanDAO dao = new PlanDAO();
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("Result", "success");
        Gson gson = new Gson();
        if (INSERT_NAIL.equals(operationType)) {
            String planNailJsonStr = request.getParameter("Nail");
            PlanNail nail = gson.fromJson(planNailJsonStr, PlanNail.class);
            dao.insertPlanNail(nail);
            dao.updatePlanDate(nail.getId(), "plan_week", 1, DateHelper.getWeekDateStr(nail.getDate()));
            dao.updatePlanDate(nail.getId(), "plan_month", 1, DateHelper.getMonthDateStr(nail.getDate()));
        }else if (DELETE_NAIL.equals(operationType)) {
            String nailJsonStr = request.getParameter("Nail");
            String pullNailJsonStr = request.getParameter("PullNail");
            PlanNail planNail= gson.fromJson(nailJsonStr, PlanNail.class);
            PlanPullNail planPullNail = gson.fromJson(pullNailJsonStr, PlanPullNail.class);
            dao.updatePlanNail(planNail, planPullNail);
            dao.updatePlanDate(planNail.getId(), "plan_week", 2, DateHelper.getWeekDateStr(planPullNail.getLastDate()));
            dao.updatePlanDate(planNail.getId(), "plan_month", 2, DateHelper.getMonthDateStr(planPullNail.getLastDate()));
        } else if (DELETE_NAIL_ON_BAG.equals(operationType)) {
            String nailJsonStr = request.getParameter("Nail");
            PlanPullNail nail = gson.fromJson(nailJsonStr, PlanPullNail.class);
            dao.updatePlanNailOnBag(nail);
            dao.updatePlanDate(nail.getId(), "plan_week", 2, DateHelper.getWeekDateStr(nail.getLastDate()));
            dao.updatePlanDate(nail.getId(), "plan_month", 2, DateHelper.getMonthDateStr(nail.getLastDate()));
        }

        dao.closeDB();

        try (PrintWriter out = response.getWriter()){
            out.write(jsonObject.toString());
            out.flush();
        }
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

    }
}
