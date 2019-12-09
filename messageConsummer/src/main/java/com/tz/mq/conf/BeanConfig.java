package com.tz.mq.conf;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jms.annotation.EnableJms;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.jms.config.DefaultJmsListenerContainerFactory;
import org.springframework.jms.config.JmsListenerContainerFactory;
import org.springframework.messaging.handler.annotation.SendTo;

import javax.jms.ConnectionFactory;


/**
 * @Description:
 * @author: zhongxp
 * @Date: 12/9/2019 1:51 PM
 */
@Configuration
@EnableJms
public class BeanConfig {

    //接收queue类型消息
    //destination对应配置类中ActiveMQQueue("springboot.queue")设置的名字
    @JmsListener(destination = "ActiveMQQueue")
    // SendTo 会将此方法返回的数据, 写入到 OutQueue 中去.
    @SendTo("SQueue")
    public String handleMessage(String name) {
        System.out.println("成功接受Name" + name);
        return "成功接受队列消息Name" + name;
    }
    //接收topic类型消息
    //destination对应配置类中ActiveMQTopic("springboot.topic")设置的名字
    //containerFactory对应配置类中注册JmsListenerContainerFactory的bean名称
    @JmsListener(destination="springboot.topic", containerFactory = "jmsTopicListenerContainerFactory")
    public void ListenTopic(String msg){
        System.out.println("接收到topic消息：" + msg);
    }

    @Bean
    public JmsListenerContainerFactory jmsTopicListenerContainerFactory(ConnectionFactory connectionFactory){
        DefaultJmsListenerContainerFactory factory = new DefaultJmsListenerContainerFactory();
        factory.setConnectionFactory(connectionFactory);
        //这里必须设置为true，false则表示是queue类型
        factory.setPubSubDomain(true);
        return factory;
    }

}
