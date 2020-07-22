package utils;
111
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * User      : Yao Ma
 * Email     : yaoma@58ganji.com
 * Date      : 2018/5/21.
 * Purpose   : com.bj58.fang.detail.web.http
 */
@Slf4j
public abstract class AbstractHttp {
    private final static String REQUEST_GET = "GET";
    private final static String REQUEST_POST = "POST";
    private final static int TIME_OUT = 3000;

    static String post(Map<String, Object> param, String remoteUrl, boolean needEncode) {

        try {
            String paramStr = getParamStr(param);
            URL url = new URL(remoteUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
            connection.setRequestMethod(REQUEST_POST);
            connection.setConnectTimeout(TIME_OUT);
            connection.setDoInput(true);
            connection.setDoOutput(true);
            connection.connect();
            OutputStream os = connection.getOutputStream();
            os.write(paramStr.getBytes("utf-8"));
            os.flush();
            os.close();
            if (connection.getResponseCode() == 200) {
                String ret = convertInputStream2String(connection.getInputStream());
                if (needEncode) {
                    ret = ret.replaceAll("%(?![0-9a-fA-F]{2})", "%25");
                    return URLDecoder.decode(ret, "utf-8");
                }
                return ret;
            }
        } catch (Exception e) {
            e.printStackTrace();
//            log.info("url:{}", remoteUrl);

        }
        return null;
    }

    static String post(JSONObject param, String remoteUrl, boolean needEncode) {
        try {
            URL url = new URL(remoteUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
            connection.setRequestMethod(REQUEST_POST);
            connection.setConnectTimeout(TIME_OUT);
            connection.setDoInput(true);
            connection.setDoOutput(true);
            connection.connect();
            OutputStream os = connection.getOutputStream();
            os.write(param.toString().getBytes("utf-8"));
            os.flush();
            os.close();
            if (connection.getResponseCode() == 200) {
                String ret = convertInputStream2String(connection.getInputStream());
                if (needEncode) {
                    ret = ret.replaceAll("%(?![0-9a-fA-F]{2})", "%25");
                    return URLDecoder.decode(ret, "utf-8");
                }
                return ret;
            }
        } catch (Exception e) {
            e.printStackTrace();
//            log.info("url:{}", remoteUrl);
        }
        return null;
    }

    public static String get(Map<String, Object> param, String remoteUrl, boolean needEncode) {
        try {
            String paramStr = getParamStr(param);
            if (needEncode) {
                paramStr = URLEncoder.encode(paramStr, "utf-8");
            }
            String urlStr = remoteUrl + (paramStr.isEmpty() ? "" : ("?" + paramStr));
            URL url = new URL(urlStr);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod(REQUEST_GET);
            connection.setConnectTimeout(TIME_OUT);
            connection.setDoInput(true);
            connection.setDoOutput(true);
            connection.setRequestProperty("Cookie","aliyungf_tc=AQAAALueBQSG4QQA+oh/fN/iPS7T4Drx; account_key=a6b209aa76ecb1b5931d3a29b10eaf50; c=aCmJQe9e-1594105267580-b8e7086e1fa47-1763210076; _fmdata=B5P%2F%2B12k%2FFM6gb01ur2OGXJtPWuXNQ3IXlUoubWTjQxSJ7SwV%2BbMEGRpUYOEeKZc6uSVJIYHDCcq8oLTEbWyeTvXZzVXMkH%2BL270eas%2Bwos%3D; _xid=ECiMcTNr0qC7mioK0XP8%2BLvy20SIGAo6NWqy%2Fwkb%2BzDQrNH6r52VrVitwK1w1VPaXiayiq56sNNe%2BZ4j22HchA%3D%3D; session_key=NPHH8mWqs1rLT1jLLiQabgQpJLf3tA8oAP1GkvzFus1usHf14FGYvoPyldutHZT6O23RcoslzL0TS2bkF64oeZ-0002; PUSS=6bVu4DqCkBC0PG5YfpFEzvvwAppDM94uFQgM09SYitWkBc7xnH1jqAoqvZxQmeQmQWZn37zF5zOgbuyjUhCPCdr3RE5yXCAzxwfjXfKQPOki3u8q8w0PrDrQDmvG1LoJ; Hm_lvt_c623214f9752cb51e7f85c21b8e170bc=1594105242,1594110083,1594272683,1594360930; Hm_lpvt_c623214f9752cb51e7f85c21b8e170bc=1594360930; zdaw_zrb_zdaw=%7B%22distinct_id%22%3A%20%221732812a013170-03cde89756bf0f-31617402-1ea000-1732812a01421d%22%2C%22%24device_id%22%3A%20%221732812a013170-03cde89756bf0f-31617402-1ea000-1732812a01421d%22%2C%22%24initial_referrer%22%3A%20%22https%3A%2F%2Fwww.baidu.com%2Flink%3Furl%3D8dH-Pm9NaBC8ubmOqZ7sG5CwTkRaeK8MK_XVfW3LDVvEIXsh4dIkVNycWPGlb00I%26wd%3D%26eqid%3Dd40a7d14000990df000000025f041d53%22%2C%22%24initial_referring_domain%22%3A%20%22www.baidu.com%22%7D");            connection.connect();

            if (connection.getResponseCode() == 200) {
                String ret = convertInputStream2String(connection.getInputStream());
                if (needEncode) {
                    return URLDecoder.decode(ret, "utf-8");
                }
                return ret;
            }
        } catch (Exception e) {
            e.printStackTrace();
            log.info("url:{}", remoteUrl);
        }
        return null;
    }

    private static String convertInputStream2String(InputStream inputStream) throws IOException {
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"));
        StringBuilder jsonResult = new StringBuilder();
        String temp = null;
        while ((temp = bufferedReader.readLine()) != null) {
            jsonResult.append(temp);
        }
        return jsonResult.toString();
    }

    /**
     * 将一个map中的值用‘&’链接起来
     *
     * @param param map
     * @return query
     */
    public static String getParamStr(Map<String, Object> param) {
        List<String> list = new LinkedList<>();
        try {
            for (String key : param.keySet()) {
                list.add(key + "=" + URLEncoder.encode(String.valueOf(param.get(key)), "utf-8"));
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return join(list, "&");
    }

    public static String stripTags(String text) {
        Matcher matcher = Pattern.compile("(<.*?>)").matcher(text);
        while (matcher.find()) {
            String what = matcher.group(1);
            text = text.replaceFirst(what, "");
        }
        return text;
    }

    /**
     * 合并一个集合
     *
     * @param list 可迭代对象
     * @param str  间隔符
     * @param <T>  任意类型
     * @return 合并后的字符串
     */
    public static <T> String join(Iterable<T> list, String str) {
        StringBuilder dest = new StringBuilder();
        for (T t : list) {
            dest.append(t.toString());
            dest.append(str);
        }
        if (!str.isEmpty() && dest.length() > 0) {
            dest.deleteCharAt(dest.length() - 1);
        }
        return dest.toString();
    }
}
