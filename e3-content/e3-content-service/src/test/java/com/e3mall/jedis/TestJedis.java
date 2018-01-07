package com.e3mall.jedis;

import java.util.HashSet;
import java.util.Set;

import org.junit.Test;

import redis.clients.jedis.HostAndPort;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisCluster;
import redis.clients.jedis.JedisPool;

public class TestJedis {
	
	
	/**
	 * 集群版测试
	 */
	@Test
	public void testJedisCluster(){
		Set<HostAndPort> nodes=new HashSet<HostAndPort>();
		
		nodes.add(new HostAndPort("192.168.25.128", 7001));
		nodes.add(new HostAndPort("192.168.25.128", 7002));
		nodes.add(new HostAndPort("192.168.25.128", 7003));
		nodes.add(new HostAndPort("192.168.25.128", 7004));
		nodes.add(new HostAndPort("192.168.25.128", 7005));
		nodes.add(new HostAndPort("192.168.25.128", 7006));
		
		JedisCluster cluster=new JedisCluster(nodes);
		
		cluster.set("cluster-01", "001");
		String string = cluster.get("cluster-01");
		System.out.println(string);
		
		cluster.close();
	}
	
	@Test
	public void testJedis(){
		Jedis jedis=new Jedis("192.168.25.128",6379);
		
		jedis.set("key02", "hehe");
		String string = jedis.get("key02");
		System.out.println(string);
		jedis.close();
		
	}
	
	@Test
	public void testJedisPool(){
		JedisPool pool=new JedisPool("192.168.25.128", 6379);
		
		Jedis jedis = pool.getResource();
		jedis.set("key03", "55555");
		String string = jedis.get("key03");
		System.out.println(string);
		jedis.close();
		pool.close();
	}
}
