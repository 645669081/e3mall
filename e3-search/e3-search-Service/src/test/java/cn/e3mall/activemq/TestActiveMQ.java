package cn.e3mall.activemq;

import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class TestActiveMQ {
	
	@Test
	public void testQueueConsumer() throws Exception {
		ApplicationContext ac=new ClassPathXmlApplicationContext("classpath:spring/applicationContext-mq.xml");
		
		//等待
		System.in.read();

	}
}
