package servlet;

import DAO.LoginDAO;
import bean.*;
import com.google.gson.Gson;
import net.sf.json.JSONObject;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class LoginServlet extends javax.servlet.http.HttpServlet {

    private LoginDAO dao;

    protected void doPost(javax.servlet.http.HttpServletRequest request, javax.servlet.http.HttpServletResponse response) throws javax.servlet.ServletException, IOException {
        // 设置响应内容类型
        response.setContentType("text/html;charset=utf-8");
        request.setCharacterEncoding("utf-8");
        response.setCharacterEncoding("utf-8");
        String accountNumber = request.getParameter("AccountNumber");
        String password = request.getParameter("Password");
        dao = new LoginDAO();

        JSONObject jsonObject = new JSONObject();
        Gson gson = new Gson();
        boolean result = dao.isExistUser(accountNumber, password);
        if (result) {
            // 用户已存在，返回基本信息
            User user = dao.getUser(accountNumber, password);
            System.out.println("" + user.getId() + " ; " + user.getAccountNumber() + " ; " + user.getPassword() + " ; " + user.getName() + " ; " + user.getSex() + " ; " + user.getIdentity() + " ; " + user.getDepartment());

            String userJson = gson.toJson(user);
            jsonObject.put("Result", "success");
            jsonObject.put("IsLogin", true);
            jsonObject.put("User", userJson);
            getSyncInformation(user.getId(), jsonObject);
        }else {
            // 用户不存在，登录信息门户，抓取数据
            User user = loginInformationPortal(accountNumber, password);
            if (user == null) {
                // 该学校没有此用户
                jsonObject.put("Result", "failed");
            }else {
                // 保存用户数据
                dao.createUserTable();
                dao.insertUser(user);
                user.setId(dao.getUserId(accountNumber));
                String userJson = gson.toJson(user);

                jsonObject.put("Result", "success");
                jsonObject.put("IsLogin", false);
                jsonObject.put("User", userJson);
            }
        }

        dao.closeDB();

        try (PrintWriter out = response.getWriter()) {
            out.write(jsonObject.toString());
            out.flush();
        }
    }

    protected void doGet(javax.servlet.http.HttpServletRequest request, javax.servlet.http.HttpServletResponse response) throws javax.servlet.ServletException, IOException {
        doPost(request, response);
    }

    /**
     * 登录信息门户
     * @param accountNumber
     * @param password
     */
    public User loginInformationPortal(String accountNumber, String password) {
        User user = null;
        String url = "http://authserver.tjut.edu.cn/authserver/login?service=http%3A%2F%2Fmy.tjut.edu.cn%2Flogin%3Fservice%3Dhttp%3A%2F%2Fmy.tjut.edu.cn%2Fnew%2Findex.html";
        try {
            // 爬取登录需要的data
            Connection.Response response = Jsoup.connect(url)
                    .method(Connection.Method.GET)
                    .execute();
            String html = response.body();
            System.out.println(html);
            Map<String, String> cookieMap = response.cookies();
            Document document = Jsoup.parse(html);
            Elements elements = document.select("form#casLoginForm > input");
            Map<String, String> data = new HashMap<>();
            data.put("username", accountNumber);
            data.put("password", password);
            for (int i = 0; i < elements.size(); i++) {
                data.put(elements.get(i).attr("name"), elements.get(i).attr("value"));
            }

            // 登录静态首页
            Connection.Response resp = Jsoup.connect(url)
                    .cookies(cookieMap)
                    .data(data)
                    .header("Host", "authserver.tjut.edu.cn")
                    .header("Referer", url)
                    .header("User-Agent", "Mozilla/5.0 (Windows NT 6.1; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/69.0.3497.100 Safari/537.36")
                    .header("Content-Type", "application/x-www-form-urlencoded")
                    .header("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8")
                    .header("Accept-Encoding", "gzip, deflate")
                    .header("Accept-Language", "zh-CN,zh;q=0.9")
                    .method(Connection.Method.POST)
                    .execute();
            String ht = resp.body();
            Document doc = Jsoup.parse(ht);
            try {
                doc.select("div#ampHasLogin > span").get(0).text();
            }catch (Exception e) {
                // 没抓取到节点则表示登录失败
                return null;
            }
            cookieMap = resp.cookies();

            //请求动态数据
            Connection.Response r = Jsoup.connect("http://my.tjut.edu.cn/jsonp/userDesktopInfo.json?type=&amp_jsonp_callback=jQuery2110644038523956654_1538146913054&_=1538146913056")
                    .method(Connection.Method.GET)
                    .header("Content-Type", "pplication/json;charset=UTF-8")
                    .cookies(cookieMap)
                    .ignoreContentType(true)
                    .header("User-Agent", "Mozilla/5.0 (Windows NT 6.1; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/69.0.3497.100 Safari/537.36")
                    .header("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8")
                    .header("Accept-Encoding", "gzip, deflate")
                    .header("Accept-Language", "zh-CN,zh;q=0.9")
                    .header("X-Requested-With", "XMLHttpRequest")
                    .header("Referer", "http://my.tjut.edu.cn/new/index.html")
                    .header("Host", "my.tjut.edu.cn")
                    .execute();
            System.out.println(r.body());
            String[] s = r.body().split("\"siteType\":\"0\",");
            String jsonStr = "{" + s[1].split("\\)")[0];
            System.out.println(jsonStr);
            JSONObject jsonObject = JSONObject.fromObject(jsonStr);

            // 构建user
            user = new User();
            user.setPassword(password);
            user.setAccountNumber(accountNumber);
            user.setIdentity(jsonObject.getString("userTypeName"));
            user.setName(jsonObject.getString("userName"));
            user.setDepartment(jsonObject.getString("userDepartment"));
            user.setSex(jsonObject.getString("userSex"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return user;
    }

    /**
     * 同步信息
     * @param id
     * @param jsonObject
     */
    private void getSyncInformation(int id, JSONObject jsonObject) {
        Gson gson = new Gson();
        ArrayList<PlanNail> list1 = dao.getPlanNailList(id);
        ArrayList<PlanPullNail> list2 = dao.getPlanPullNailList(id);
        ArrayList<PlanDate> list3 = dao.getPlanWeekList(id);
        ArrayList<PlanDate> list4 = dao.getPlanMonthList(id);
        ArrayList<MoodGoodNail> list5 = dao.getMoodGoodNailList(id);
        ArrayList<MoodBadNail> list6 = dao.getMoodBadNail(id);
        ArrayList<MoodDate> list7 = dao.getMoodWeekList(id);
        ArrayList<MoodDate> list8 = dao.getMoodMonthList(id);
        ArrayList<Crack> list9 = dao.getCrackList(id);

        jsonObject.put("planNail", gson.toJson(list1));
        jsonObject.put("planPullNail", gson.toJson(list2));
        jsonObject.put("planWeek", gson.toJson(list3));
        jsonObject.put("planMonth", gson.toJson(list4));
        jsonObject.put("moodGoodNail", gson.toJson(list5));
        jsonObject.put("moodBadNail", gson.toJson(list6));
        jsonObject.put("moodWeek", gson.toJson(list7));
        jsonObject.put("moodMonth", gson.toJson(list8));
        jsonObject.put("crack", gson.toJson(list9));
    }

}
