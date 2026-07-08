package com.chatnotify.config;

import com.chatnotify.message.ChatMessageSubscriber;
import com.chatnotify.notification.NotificationSubscriber;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;

@Configuration
public class RedisConfig {

    public static final String CHAT_CHANNEL = "chat:messages";
    public static final String NOTIFICATION_CHANNEL = "chat:notifications";

    @Bean
    public StringRedisTemplate redisTemplate(RedisConnectionFactory connectionFactory) {
        return new StringRedisTemplate(connectionFactory);
    }

    @Bean
    public RedisMessageListenerContainer redisMessageListenerContainer(
            RedisConnectionFactory connectionFactory,
            ChatMessageSubscriber chatMessageSubscriber,
            NotificationSubscriber notificationSubscriber) {
        RedisMessageListenerContainer container = new RedisMessageListenerContainer();
        container.setConnectionFactory(connectionFactory);
        container.addMessageListener(chatMessageSubscriber, new ChannelTopic(CHAT_CHANNEL));
        container.addMessageListener(notificationSubscriber, new ChannelTopic(NOTIFICATION_CHANNEL));
        return container;
    }
}
