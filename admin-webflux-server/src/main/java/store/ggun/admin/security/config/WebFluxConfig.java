package store.ggun.admin.security.config;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.format.FormatterRegistry;
import org.springframework.format.datetime.standard.DateTimeFormatterRegistrar;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.web.reactive.config.WebFluxConfigurer;
import reactor.core.publisher.Sinks;
import store.ggun.admin.chat.domain.dto.ChatDTO;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


@Configuration
public class WebFluxConfig implements WebFluxConfigurer{
    @Override
    public void addFormatters( FormatterRegistry registry) {
        DateTimeFormatterRegistrar registrar = new DateTimeFormatterRegistrar();
        registrar.setUseIsoFormat(true);
        registrar.registerFormatters(registry);
    }
}