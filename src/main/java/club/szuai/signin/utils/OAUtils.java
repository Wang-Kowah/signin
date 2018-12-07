package club.szuai.signin.utils;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class OAUtils {
    private static CloseableHttpClient httpClient;
    private static Map<String, String> cookieMap;

    /**
     * 登录OA
     */
    public static boolean loginOA(String un, String pw) {
        cookieMap = new HashMap<>();
        String url = "https://authserver.szu.edu.cn/authserver/login";

        try {
            //第一次
            httpClient = HttpClients.createDefault();
            HttpGet httpGet = new HttpGet(url);
            CloseableHttpResponse response = httpClient.execute(httpGet);
            getCookies(response.getAllHeaders());
            HttpEntity entity = response.getEntity();
            String responseBody = EntityUtils.toString(entity);

            int start = responseBody.indexOf("name=\"lt\" value=\"") + 17;
            int end = responseBody.indexOf("\"", start);
            String lt = responseBody.substring(start, end);
            start = responseBody.indexOf("execution\" value=\"") + 18;
            end = responseBody.indexOf("\"", start);
            String execution = responseBody.substring(start, end);

            //第二次
            HttpPost httpPost = new HttpPost(url + ";JSESSIONID_auth=" + cookieMap.get("JSESSIONID_auth"));
            httpPost.setHeader("Cookie", "route=" + cookieMap.get("route")
                    + "; JSESSIONID_auth=" + cookieMap.get("JSESSIONID_auth")
                    + "; org.springframework.controller.servlet.i18n.CookieLocaleResolver.LOCALE=zh_CN");
            //组装表单
            List<NameValuePair> pairs = new ArrayList<>();
            pairs.add(new BasicNameValuePair("username", un));
            pairs.add(new BasicNameValuePair("password", pw));
            pairs.add(new BasicNameValuePair("lt", lt));
            pairs.add(new BasicNameValuePair("dllt", "userNamePasswordLogin"));
            pairs.add(new BasicNameValuePair("execution", execution));
            pairs.add(new BasicNameValuePair("_eventId", "submit"));
            pairs.add(new BasicNameValuePair("rmShown", "1"));
            httpPost.setEntity(new UrlEncodedFormEntity(pairs, "UTF-8"));

            response = httpClient.execute(httpPost);
            getCookies(response.getAllHeaders());
            //  302即可确定登录成功，无需继续请求
            if (response.getStatusLine().getStatusCode() == 302) {
                return true;
            }

            //第三次
//            httpGet = new HttpGet("https://authserver.szu.edu.cn/authserver/index.do");
//            httpGet.setHeader("Cookie", "CASTGC=" + cookieMap.get("CASTGC")
//                    + "; route=" + cookieMap.get("route")
//                    + "; JSESSIONID_auth=" + cookieMap.get("JSESSIONID_auth")
//                    + "; org.springframework.controller.servlet.i18n.CookieLocaleResolver.LOCALE=zh_CN");
//            response = httpClient.execute(httpGet);
//            entity = response.getEntity();
//            responseBody = EntityUtils.toString(entity);
//            EntityUtils.consume(entity);
//            if (responseBody.contains("个人资料")){
//                return true;
//            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 保存cookie
     */
    private static void getCookies(Header[] headers) {
        for (Header header : headers) {
//            System.out.println(header.getName() + ":" + header.getValue());
            if (header.getName().equalsIgnoreCase("Set-Cookie")) {
                String[] cookies = header.getValue().split(";");
                for (String cookiePair : cookies) {
                    if (cookiePair.contains("=")) {
                        String[] cookie = cookiePair.split("=");
                        if (cookie.length == 2) {
                            cookieMap.put(cookie[0], cookie[1]);
                        }
                    }
                }
            }
        }
    }

    /**
     * 爬取校卡照片（需登录OA）
     *
     * @param dirPath 存放的文件夹
     * @param start   开始学号
     * @param end     结束学号
     * @param pause   爬取间隔(建议不低于5)
     */
    public static void getIDPhotos(String dirPath, int start, int end, int pause) {
        try {
            for (int id = start; id <= end; id++) {
                HttpGet httpGet = new HttpGet("http://ehall.szu.edu.cn/jwapp/sys/jwpubapp/showImageBydsForZPGL.do?XH=" + id + "&ZPLX=XJZP");
                CloseableHttpResponse response = httpClient.execute(httpGet);
                File dir = new File(dirPath);
                dir.mkdirs();
                File file = new File(dirPath + "\\" + id + ".jpg");
                FileOutputStream fileOutputStream = new FileOutputStream(file);
                response.getEntity().writeTo(fileOutputStream);
                fileOutputStream.close();

                TimeUnit.SECONDS.sleep(pause);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 获取姓名（需登录OA）
     */
    public static String getName() {
        String username = "";
        try {
            HttpGet httpGet = new HttpGet("https://authserver.szu.edu.cn/authserver/index.do");
            httpGet.setHeader("Cookie", "CASTGC=" + cookieMap.get("CASTGC")
                    + "; route=" + cookieMap.get("route")
                    + "; JSESSIONID_auth=" + cookieMap.get("JSESSIONID_auth")
                    + "; org.springframework.controller.servlet.i18n.CookieLocaleResolver.LOCALE=zh_CN");
            CloseableHttpResponse response = httpClient.execute(httpGet);
            HttpEntity entity = response.getEntity();
            String responseBody = EntityUtils.toString(entity);
            EntityUtils.consume(entity);
            Document doc = Jsoup.parse(responseBody);
            Element element = doc.getElementsByClass("auth_username").first().selectFirst("span");
            username = element.text();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return username;
    }

    /**
     * 获取生日（需登录OA）
     */
    public static long getBirthday() {
        long timestamp = 0;
        try {
            HttpGet httpGet = new HttpGet("https://authserver.szu.edu.cn/authserver/userAttributesEdit.do");
            httpGet.setHeader("Cookie", "CASTGC=" + cookieMap.get("CASTGC")
                    + "; route=" + cookieMap.get("route")
                    + "; JSESSIONID_auth=" + cookieMap.get("JSESSIONID_auth")
                    + "; org.springframework.controller.servlet.i18n.CookieLocaleResolver.LOCALE=zh_CN");
            CloseableHttpResponse response = httpClient.execute(httpGet);
            HttpEntity entity = response.getEntity();
            String responseBody = EntityUtils.toString(entity);
            EntityUtils.consume(entity);
            Document doc = Jsoup.parse(responseBody);
            Element element = doc.getElementById("birthday");
            String birthStr = element.attr("value");
            timestamp = DateUtils.getTimestamp(birthStr, "yyyy-MM-dd");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return timestamp;
    }

    /**
     * 根据姓名获取学号
     */
    public static int getIDByName(String name) {
        int id = 0;

        return id;
    }

}