package store.ggun.admin.chat.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import org.springframework.http.codec.ServerSentEvent;
import reactor.core.publisher.Sinks;
import store.ggun.admin.chat.domain.dto.ChatDto;

import java.util.HashMap;
import java.util.Map;


@Configuration
public class SenderConfig {
    @Bean
    public Map<String, Sinks.Many<ServerSentEvent<ChatDto>>> chatSenderSink() {
        return new HashMap<String, Sinks.Many<ServerSentEvent<ChatDto>>>();
    }
}
