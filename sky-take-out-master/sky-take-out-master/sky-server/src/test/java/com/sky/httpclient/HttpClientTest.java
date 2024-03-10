package com.sky.httpclient;

import com.alibaba.fastjson.JSONObject;
import com.sky.utils.HttpClientUtil;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * @Description HttpClientTest
 * @Author songyu
 * @Date 2023-09-27
 */
public class HttpClientTest {

    //目标：远程调用 http://localhost:8080/user/shop/status 服务地址获取相应数据
    @Test
    public void testGET() throws Exception {

        //1.创建HttpClient
        CloseableHttpClient httpClient = HttpClients.createDefault();

        //2.创建请求对象
        HttpGet httpGet = new HttpGet("http://localhost:8080/user/shop/status");

        //3.执行请求
        CloseableHttpResponse response = httpClient.execute(httpGet);

        //4.获取相应数据
        int statusCode = response.getStatusLine().getStatusCode(); //相应状态码，200代表通信正确
        HttpEntity entity = response.getEntity(); //响应体
        String json = EntityUtils.toString(entity);
        System.out.println(json);

        //5.释放资源
        response.close();
        httpClient.close();

    }

    //目标：远程调用 https://api.map.baidu.com/geocoding/v3/?address=广州市天河区珠吉路58号&output=json&ak=svtU5Y6kI0ur0yiqVrS0LXAd4bfo4NM4 服务地址获取相应数据
    @Test
    public void testGET2() throws Exception {

        //定义url
        String url = "https://api.map.baidu.com/geocoding/v3/";
        //定义请求参数数据
        Map<String,String> parmsMap = new HashMap<>();
        parmsMap.put("address","广州市天河区珠吉路58号");
        parmsMap.put("output","json");
        parmsMap.put("ak","svtU5Y6kI0ur0yiqVrS0LXAd4bfo4NM4");

        //执行远程调用，并返回json字符串
        String json = HttpClientUtil.doGet(url, parmsMap);

        //打印
        System.out.println(json);
    }


    /**
     * 测试通过httpclient发送POST方式的请求
     */
    @Test
    public void testPOST() throws Exception{
        // 创建httpclient对象
        CloseableHttpClient httpClient = HttpClients.createDefault();

        //创建请求对象
        HttpPost httpPost = new HttpPost("http://localhost:8080/admin/employee/login");

        JSONObject jsonObject = new JSONObject(); //阿里巴巴fastjson提供的工具对象存储数据
        jsonObject.put("username","admin");
        jsonObject.put("password","123456");

        //jsonObject.toString() 将Map转换为json字符串
        StringEntity entity = new StringEntity(jsonObject.toString()); //将json封装到字符串请求体对象中
        //指定请求编码方式
        entity.setContentEncoding("utf-8");  //设置支持中文的码表
        //数据格式
        entity.setContentType("application/json"); //设置请求体数据格式：json
        httpPost.setEntity(entity); //将请求体对象封装到post请求对象中

        //发送请求
        CloseableHttpResponse response = httpClient.execute(httpPost);

        //解析返回结果
        int statusCode = response.getStatusLine().getStatusCode();
        System.out.println("响应码为：" + statusCode);

        HttpEntity entity1 = response.getEntity();
        String body = EntityUtils.toString(entity1);
        System.out.println("响应数据为：" + body);

        //关闭资源
        response.close();
        httpClient.close();
    }
}
