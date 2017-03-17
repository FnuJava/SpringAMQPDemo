package com.lin.producer;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.AcknowledgeMode;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.ChannelAwareMessageListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.core.RabbitTemplate.ConfirmCallback;
import org.springframework.amqp.rabbit.core.RabbitTemplate.ReturnCallback;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.amqp.rabbit.support.CorrelationData;
import org.springframework.stereotype.Service;

import com.lin.consumer.UserInfo;
import com.rabbitmq.client.Channel;


/**
 * 功能概要：消息产生,提交到队列中去
 * 
 * @author linbingwen
 * @since  2016年1月15日 
 */
@Service
public class MessageProducer implements ConfirmCallback,ReturnCallback{
	
	private Logger logger = LoggerFactory.getLogger(MessageProducer.class);

	@Resource
	private RabbitTemplate  amqpTemplate;
	
	@Resource
	private ConnectionFactory connectionFactory;

	public void sendMessage(Object message){
	  logger.info("to send message:{}",message);
	  amqpTemplate.setConfirmCallback(this); //rabbitTemplate如果为单例的话，那回调就是最后设置的内容  
	  amqpTemplate.setMandatory(true);//ReturnCallBack使用时需要通过RabbitTemplate 的setMandatory方法设置变量mandatoryExpression的值，该值可以是一个表达式或一个Boolean值。当为TRUE时，如果消息无法发送到指定的消息队列那么ReturnCallBack回调方法会被调用
	  amqpTemplate.convertAndSend("queueTestKey",message);
	}
	
	/**  
     * 回调  
     */  
    @Override  
    public void confirm(CorrelationData correlationData, boolean ack, String cause) {  
        System.out.println(" 回调id:" + correlationData);  
        if (ack) {  
            System.out.println("消息成功消费");  
        } else {  
            System.out.println("消息消费失败:" + cause);  
        }  
    }  
    
	/**  
     * 回调  
     */  
    @Override
    public void returnedMessage(Message message, int replyCode, String replyText,
			String exchange, String routingKey){
    		System.err.println("text: " + replyText + " code: " + replyCode + " exchange: " + exchange + " routingKey :" + routingKey);
    }
	
	//代码监听方法
	public void createQueueListener(){
		
	  SimpleMessageListenerContainer container = new SimpleMessageListenerContainer(connectionFactory);  
      container.setQueueNames("queueTest1");;  
      container.setExposeListenerChannel(true);  
      container.setMaxConcurrentConsumers(1);  
      container.setConcurrentConsumers(1);  
      container.setAcknowledgeMode(AcknowledgeMode.MANUAL); //设置确认模式手工确认  
      container.setMessageListener(new ChannelAwareMessageListener() {  
          @Override  
          public void onMessage(Message message, Channel channel) throws Exception {  
              byte[] body = message.getBody();  
              System.err.println("receive msg : " + new String(body)); 
              channel.basicAck(message.getMessageProperties().getDeliveryTag(), false); //确认消息成功消费  
          }  
      });  
      container.start();//启动监听。。。。。
	}
}
