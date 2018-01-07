package com.e3mall.jedis;

import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import cn.e3mall.jedis.JedisClient;
import cn.e3mall.jedis.JedisClientCluster;
import cn.e3mall.jedis.JedisClientPool;

public class TestJedisSpring {
	
	@Test
	public void testJedisClientPool(){
		ApplicationContext ac=new ClassPathXmlApplicationContext("classpath:spring/applicationContext-redis.xml");
		
		JedisClient jedisClient = ac.getBean(JedisClient.class);
		
		jedisClient.set("key01", "haha");
		String string = jedisClient.get("key01");
		System.out.println(string);
		
	
	}
	
	
}
