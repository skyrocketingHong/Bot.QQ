package ninja.skyrocketing.bot.fuyao.util;

import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONException;
import cn.hutool.json.JSONObject;

import java.io.*;
import java.net.URL;
import java.nio.charset.StandardCharsets;

/**
 * @Author skyrocketing Hong
 * @Date 2021-03-05 16:36:51
 */

public class HttpUtil {
    private static String ReadAll(Reader rd) throws IOException {
        StringBuilder sb = new StringBuilder();
        int cp;
        while ((cp = rd.read()) != -1) {
            sb.append((char) cp);
        }
        return sb.toString();
    }

    public static JSONObject ReadJsonFromURL(String url) throws IOException, JSONException {
        try (InputStream is = new URL(url).openStream()) {
            BufferedReader rd = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8));
            String jsonText = ReadAll(rd);
            return new JSONObject(jsonText);
        }
    }

    public static JSONArray ReadJsonArrayFromUrl(String url) throws IOException, JSONException {
        try (InputStream is = new URL(url).openStream()) {
            BufferedReader rd = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8));
            String jsonText = ReadAll(rd);
            return new JSONArray(jsonText);
        }
    }

    public static String CHNCharterReplace(String str) throws UnsupportedEncodingException {
        return java.net.URLEncoder.encode(str, StandardCharsets.UTF_8);
    }

    public static String SpaceReplace(String str) {
        return str.replaceAll("\\s", "+");
    }

    public static String CHNCharterAndSpaceReplace(String str) throws UnsupportedEncodingException {
        return CHNCharterReplace(SpaceReplace(str));
    }
}