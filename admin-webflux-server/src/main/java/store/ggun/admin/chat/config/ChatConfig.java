package store.ggun.admin.chat.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.codec.ServerSentEvent;
import reactor.core.publisher.Sinks;
import store.ggun.admin.chat.domain.dto.ChatDTO;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Configuration
public class ChatConfig {

    @Bean
    public Map<String, Sinks.Many<ServerSentEvent<ChatDTO>>> chatSinks() {
        return new ConcurrentHashMap<>();
    }
}
