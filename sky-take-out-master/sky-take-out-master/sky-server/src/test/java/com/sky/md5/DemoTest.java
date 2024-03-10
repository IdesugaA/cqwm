package com.sky.md5;

import org.junit.jupiter.api.Test;
import org.springframework.util.DigestUtils;

/**
 * @Description DemoTest
 * @Author songyu
 * @Date 2023-09-21
 */
public class DemoTest {

    @Test
    public void test(){
        //spring框架提供工具类生成md5加密字符串
        String md5 = DigestUtils.md5DigestAsHex("123456itheima".getBytes());
        System.out.println(md5);
    }
}
