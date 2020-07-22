package utils;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
public class CalcUtil extends AbstractHttp {
    public static final String remoteUrl="https://www.zhenrongbaop2p.com/user/usercreditassemble";
    public static final String remoteDetailUrl="https://www.zhenrongbaop2p.com/user/GetPortfolioInfo";
    public static List<Map<String,String>> list=new ArrayList<>();
    public static int total=0;
    public static void getAll(final int currentPage) throws Exception{
        final int curpage=currentPage==0?1:currentPage;
        String res=get(new HashMap<String, Object>(){{
            put("current_page",curpage);
            put("serial_id","66207373");
        }},remoteUrl,false);
        if(res==null){
            System.err.println("获取标的请求结果为空:"+currentPage);
            return ;
        }
        JSONObject jsonObject=JSONObject.parseObject(res);
//        double sum = 0;
        if(currentPage==0){
            total=jsonObject.getJSONObject("data").getInteger("page_count");
        }else {
            JSONArray array = jsonObject.getJSONObject("data").getJSONArray("user_credit");
            for (Object json : array) {
                Double amount = ((JSONObject) json).getDouble("amount");

                String serialId = ((JSONObject) json).getString("credit_serial_id");
                Thread.sleep(100);
                Map<String,String>detailInfo=getDetail(serialId);
                System.out.println(detailInfo);
                list.add(detailInfo);
//                sum += amount;
            }
        }


    }

    public static Map<String,String>  getDetail(String creditSerialId){
        String res=get(new HashMap<String,Object>(){{
            put("serial_id","66207373");
            put("credit_serial_id",creditSerialId);
        }},remoteDetailUrl,false);
        if(res==null){
            System.err.println("获取标的详情请求结果为空:"+creditSerialId);
            return null;
        }

        Map<String,String> map=new HashMap<>();
        JSONObject resJson=  JSONObject.parseObject(res);
        String data=resJson.getString("data");

        JSONObject jsonres=JSONObject.parseObject(data);
        JSONObject project=jsonres.getJSONObject("project");
        map.put("标号",creditSerialId);
        map.put("借款金额",project.getString("amount"));
        map.put("借款期限",project.getString("timeTerm"));
        map.put("借款用途",project.getString("usage"));
        map.put("还款方式",project.getString("paybackType"));
        map.put("年化利率",project.getString("yearlyRate")+"%");
        map.put("还款来源",project.getString("paybackSource"));
        map.put("募集开始时间",project.getString("valueDate"));
        JSONObject person=jsonres.getJSONObject("person");
        map.put("姓名",person.getString("name"));
        map.put("证件号码",person.getString("idCard"));
        map.put("年龄",person.getString("gender"));
        return map;

    }

    public static void main(String[] args) throws Exception{

         final BufferedWriter bufferedWriter=new BufferedWriter(new FileWriter(new File("/Users/lizixuan08/Documents/zrb.txt")));
        double sum=0;
        getAll(0);

        for(int i=1;i<=total;i++){
            getAll(i);

        }

        list.forEach(o->{
            StringBuilder sb=new StringBuilder();
            o.forEach((k,v)->{
                sb.append(k).append(": ").append(v).append("\t");
            });
            try {
                bufferedWriter.write(sb.toString());
                bufferedWriter.newLine();
            } catch (IOException e) {
                e.printStackTrace();
            }
           });
        bufferedWriter.close();

    }
}

