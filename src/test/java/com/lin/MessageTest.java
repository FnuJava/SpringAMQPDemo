package com.lin;

import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.lin.consumer.UserInfo;
import com.lin.producer.MessageProducer;
import com.sun.jmx.snmp.UserAcl;

/**
 * 功能概要：
 * 
 * @author linbingwen
 * @since  2016年1月15日 
 */
public class MessageTest  {
	
	private Logger logger = LoggerFactory.getLogger(MessageTest.class);
	
	private ApplicationContext context = null;
	
	@Before
	public void setUp() throws Exception {
	    context = new ClassPathXmlApplicationContext("application.xml");
	}

	@Test
	public void should_send_a_amq_message() throws Exception {
       MessageProducer messageProducer = (MessageProducer) context.getBean("messageProducer");
      
       int a = Integer.MAX_VALUE;
       messageProducer.createQueueListener();
       while (a > 0) {
    	   UserInfo userInfo  =new UserInfo();
    	   userInfo.setUserId(a);
    	   userInfo.setUserName("hehheda");
    	   messageProducer.sendMessage(userInfo);
    	   try {
    		   //暂停一下，好让消息消费者去取消息打印出来
               Thread.sleep(10000);
           } catch (InterruptedException e) {
               e.printStackTrace(); 
           }
    
	   }
	}
}
