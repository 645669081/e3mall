package cn.e3mall.activemq;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageListener;
import javax.jms.MessageProducer;
import javax.jms.Queue;
import javax.jms.Session;
import javax.jms.TextMessage;
import javax.jms.Topic;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.command.ActiveMQTextMessage;
import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;

public class TestActiveMQ {
	
	@Test
	public void testQueueProducer() throws Exception {
//		第一步：创建ConnectionFactory对象，需要指定服务端ip及端口号。
		ConnectionFactory factory=new ActiveMQConnectionFactory("tcp://192.168.25.128:61616");
//		第二步：使用ConnectionFactory对象创建一个Connection对象。
		Connection connection = factory.createConnection();
//		第三步：开启连接，调用Connection对象的start方法。
		connection.start();
//		第四步：使用Connection对象创建一个Session对象。
		//第一个参数：是否开启事务。true：开启事务，第二个参数忽略。
		//第二个参数：当第一个参数为false时，才有意义。消息的应答模式。1、自动应答2、手动应答。一般是自动应答。
		Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
//		第五步：使用Session对象创建一个Destination对象（topic、queue），此处创建一个Queue对象。
		Queue queue = session.createQueue("test-queue");
//		第六步：使用Session对象创建一个Producer对象,并指定要放入的队列
		MessageProducer messageProducer = session.createProducer(queue);
//		第七步：创建一个Message对象，创建一个TextMessage对象。
		/*TextMessage message=new ActiveMQTextMessage();
		message.setText("hello activemq");*/
		
		TextMessage textMessage = session.createTextMessage("hello activeMq,this is my first test");
//		第八步：使用Producer对象发送消息。
		messageProducer.send(textMessage);
//		第九步：关闭资源。
		messageProducer.close();
		session.close();
		connection.close();
	}

	
	@Test
	public void testQueueConsumer() throws Exception {
//		第一步：创建一个ConnectionFactory对象。
		ConnectionFactory factory=new ActiveMQConnectionFactory("tcp://192.168.25.128:61616");
//		第二步：从ConnectionFactory对象中获得一个Connection对象。
		Connection connection = factory.createConnection();
//		第三步：开启连接。调用Connection对象的start方法。
		connection.start();
//		第四步：使用Connection对象创建一个Session对象。
		Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
//		第五步：使用Session对象创建一个Destination对象。和发送端保持一致queue，并且队列的名称一致。
		Queue queue = session.createQueue("test-queue");
//		第六步：使用Session对象创建一个Consumer对象。
		MessageConsumer consumer = session.createConsumer(queue);
//		第七步：接收消息,设置一个接听，保持队列中有消息就可以消费
		consumer.setMessageListener(new MessageListener() {
			
			@Override
			public void onMessage(Message message) {
				//转换为和你存放是一样的消息数据类型
				TextMessage textMessage=(TextMessage) message;
				
				try {
					String text = textMessage.getText();
//					第八步：打印消息。
					System.out.println(text);
				} catch (JMSException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
			}
		});
		//系统等待接收消息,用这句等待用户输入来实现线程一直在监听
		System.in.read();
		
//		第九步：关闭资源
		consumer.close();
		session.close();
		connection.close();
	}
	
	
	@Test
	public void testTopicProducer() throws Exception {
//		第一步：创建ConnectionFactory对象，需要指定服务端ip及端口号。
		ConnectionFactory factory=new ActiveMQConnectionFactory("tcp://192.168.25.128:61616");
//		第二步：使用ConnectionFactory对象创建一个Connection对象。
		Connection connection = factory.createConnection();
//		第三步：开启连接，调用Connection对象的start方法。
		connection.start();
//		第四步：使用Connection对象创建一个Session对象。
		Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
//		第五步：使用Session对象创建一个Destination对象（topic、queue），此处创建一个Topic对象。
		//创建一个主题对象进行发布
		Topic topic = session.createTopic("test-topic");
//		第六步：使用Session对象创建一个Producer对象。
		MessageProducer producer = session.createProducer(topic);
//		第七步：创建一个Message对象，创建一个TextMessage对象。
		/*TextMessage  message=new ActiveMQTextMessage();
		message.setText("hello activeMq,this is my topic test");*/
		
		TextMessage message = session.createTextMessage("hello activeMq,this is my topic test");
//		第八步：使用Producer对象发送消息。
		producer.send(message);
//		第九步：关闭资源。
		producer.close();
		session.close();
		connection.close();

	}

	
	@Test
	public void testTopicConsumer() throws Exception {
//		第一步：创建一个ConnectionFactory对象。
		ConnectionFactory factory=new ActiveMQConnectionFactory("tcp://192.168.25.128:61616");
//		第二步：从ConnectionFactory对象中获得一个Connection对象。
		Connection connection = factory.createConnection();
//		第三步：开启连接。调用Connection对象的start方法。
		connection.start();
//		第四步：使用Connection对象创建一个Session对象。
		Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
//		第五步：使用Session对象创建一个Destination对象。和发送端保持一致topic，并且话题的名称一致。
		Topic topic = session.createTopic("test-topic");
//		第六步：使用Session对象创建一个Consumer对象。
		MessageConsumer consumer = session.createConsumer(topic);
//		第七步：接收消息。
		consumer.setMessageListener(new MessageListener() {
			
			@Override
			public void onMessage(Message message) {
				TextMessage string=(TextMessage) message;
				
				try {
					String text = string.getText();
					//		第八步：打印消息。
					System.out.println(text);
				} catch (JMSException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});
		
		
		System.out.println("topic的消费端03。。。。。");
		// 等待键盘输入
		System.in.read();

//		第九步：关闭资源
		consumer.close();
		session.close();
		connection.close();

	}
	
	
	/**
	 * spring下测试发送消息
	 * @throws Exception
	 */
	@Test
	public void testSpringActiveMq() throws Exception {
		ApplicationContext ac=new ClassPathXmlApplicationContext("classpath:spring/applicationContext-mq.xml");
		
		//获取spring提供的消息模板对象
		JmsTemplate template = ac.getBean(JmsTemplate.class);
		
		//获取队列对象
		Destination destination = (Destination) ac.getBean("queueDestination");
		
		//使用模板发送消息
		template.send(destination, new MessageCreator() {
			
			@Override
			public Message createMessage(Session session) throws JMSException {
				
				TextMessage textMessage = session.createTextMessage("spring activemq queue message");
				
				return textMessage;
			}
		});
	}
	
}
