package com.tz.mq.conf;

import org.apache.activemq.command.ActiveMQQueue;
import org.apache.activemq.command.ActiveMQTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jms.annotation.EnableJms;
import org.springframework.jms.config.DefaultJmsListenerContainerFactory;
import org.springframework.jms.config.JmsListenerContainerFactory;
import org.springframework.jms.core.JmsMessagingTemplate;

import javax.jms.ConnectionFactory;
import javax.jms.Queue;
import javax.jms.Topic;


/**
 * @Description:
 * @author: zhongxp
 * @Date: 12/9/2019 1:51 PM
 */
@Configuration
@EnableJms
public class BeanConfig {

    @Bean
    public Queue queue() {
        return new ActiveMQQueue("ActiveMQQueue");
    }
    @Bean
    public Topic topic() {
        return new ActiveMQTopic("springboot.topic") ;
    }

}
