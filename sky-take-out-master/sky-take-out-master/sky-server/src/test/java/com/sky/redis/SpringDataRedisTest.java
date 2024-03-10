package com.sky.redis;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.connection.DataType;
import org.springframework.data.redis.core.*;

import javax.annotation.Resource;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * @Description SpringDataRedisTest
 * @Author songyu
 * @Date 2023-09-26
 */
@SpringBootTest
public class SpringDataRedisTest {

    //注入RedisTemplate （redis模板对象，提供了很多模板方法，方法就是通用的方法）
    //          方法里面可以切换客户端技术，默认lettuce客户端技术
    @Resource
    private RedisTemplate redisTemplate;

    @Test
    public void test(){

        //目标：操作字符串类型数据，写入与读取缓存数据

        //1.获取字符串操作类
        ValueOperations valueOperations = redisTemplate.opsForValue();

        //2.写入数据 itcast=黑马程序员
        valueOperations.set("itcast","黑马程序员");

        //3.读取数据
        Object itcast = valueOperations.get("itcast");
        System.out.println(itcast);

    }

    /**
     * 操作字符串类型的数据
     */
    @Test
    public void testString(){
        // set get setex setnx

        //命令：set key value
        redisTemplate.opsForValue().set("name","小明");
        //命令：get key
        String city = (String) redisTemplate.opsForValue().get("name");
        System.out.println(city);
        //命令：setex key 过期秒数 value
        redisTemplate.opsForValue().set("code","1234",3, TimeUnit.MINUTES);
        //命令：setnx key value
        redisTemplate.opsForValue().setIfAbsent("lock","1");
        redisTemplate.opsForValue().setIfAbsent("lock","2");
    }

    /**
     * 操作哈希类型的数据
     */
    @Test
    public void testHash(){
        //hset hget hdel hkeys hvals
        HashOperations hashOperations = redisTemplate.opsForHash();

        //命令：hset key field value
        hashOperations.put("100","name","tom");
        hashOperations.put("100","age","20");

        //命令：hget key field
        String name = (String) hashOperations.get("100", "name");
        System.out.println(name);

        //命令：hkeys key
        Set keys = hashOperations.keys("100");
        System.out.println(keys);

        //命令：hvals key
        List values = hashOperations.values("100");
        System.out.println(values);

        //命令：hdel key field
        hashOperations.delete("100","age");
    }

    /**
     * 操作列表类型的数据
     */
    @Test
    public void testList(){
        //lpush lrange rpop llen
        ListOperations listOperations = redisTemplate.opsForList();

        //命令：lpush key value1 value2 ...
        listOperations.leftPushAll("mylist","a","b","c");
        listOperations.leftPush("mylist","d");
        //listOperations.rightPush()

        //命令：lrange key 0 -1
        List mylist = listOperations.range("mylist", 0, -1);
        System.out.println(mylist);

        //命令：rpop key
        listOperations.rightPop("mylist");
        //listOperations.leftPop()

        //命令：llen key
        Long size = listOperations.size("mylist");
        System.out.println(size);
    }

    /**
     * 操作集合类型的数据
     */
    @Test
    public void testSet(){
        //sadd smembers scard sinter sunion srem
        SetOperations setOperations = redisTemplate.opsForSet();

        //命令：sadd key value1 value2 ...
        setOperations.add("set1","a","b","c","d");
        setOperations.add("set2","a","b","x","y");

        //命令：smembers key
        Set members = setOperations.members("set1");
        System.out.println(members);

        //命令：scard key
        Long size = setOperations.size("set1");
        System.out.println(size);
        //命令：sinter key1 key2
        Set intersect = setOperations.intersect("set1", "set2");
        System.out.println(intersect);

        //命令：sunion key1 key2
        Set union = setOperations.union("set1", "set2");
        System.out.println(union);
        //命令：srem value1 value2 ...
        setOperations.remove("set1","a","b");
    }

    /**
     * 操作有序集合类型的数据
     */
    @Test
    public void testZset(){
        //zadd zrange zincrby zrem
        ZSetOperations zSetOperations = redisTemplate.opsForZSet();

        //命令：zadd key score1 memmber1 score2 member2 ...
        zSetOperations.add("zset1","a",10);
        zSetOperations.add("zset1","b",12);
        zSetOperations.add("zset1","c",9);

        //命令：zrange key 0 -1 withscores
        Set zset1 = zSetOperations.range("zset1", 0, -1);
        //zSetOperations.reverseRange() 降序查询
        System.out.println(zset1);

        //命令：zincrby key 增加分数 member
        zSetOperations.incrementScore("zset1","c",10);

        //命令：zrem key member1 member2 ...
        zSetOperations.remove("zset1","a","b");
    }

    /**
     * 通用命令操作
     */
    @Test
    public void testCommon(){
        //keys exists type del

        //命令：keys *
        Set keys = redisTemplate.keys("*");
        System.out.println(keys);
        //命令：exists key
        Boolean name = redisTemplate.hasKey("name");
        Boolean set1 = redisTemplate.hasKey("set1");

        for (Object key : keys) {
            //命令：type key
            DataType type = redisTemplate.type(key);
            System.out.println(type.name());
        }
        //命令：del key1 key2 ...
        redisTemplate.delete("mylist");
    }
}
