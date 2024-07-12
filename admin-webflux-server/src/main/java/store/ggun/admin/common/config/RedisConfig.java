//package store.ggun.admin.common.config;
//
//import lombok.RequiredArgsConstructor;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.data.redis.connection.ReactiveRedisConnectionFactory;
//import org.springframework.data.redis.connection.RedisConnectionFactory;
//import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
//import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
//import org.springframework.data.redis.core.ReactiveStringRedisTemplate;
//import org.springframework.data.redis.core.RedisTemplate;
//import org.springframework.data.redis.serializer.StringRedisSerializer;
//
//@Configuration
//@RequiredArgsConstructor
//public class RedisConfig {
//
////    @Value("${spring.data.redis.host}")
////    private String redisHost;
////
////    @Value("${spring.data.redis.port}")
////    private int redisPort;
////
////    @Value("${spring.data.redis.password}")
////    private String redisPassword;
//
//    @Bean
//    public RedisConnectionFactory redisConnectionFactory() {
//        RedisStandaloneConfiguration redisStandaloneConfiguration = new RedisStandaloneConfiguration();
////        redisStandaloneConfiguration.setHostName(redisHost);
////        redisStandaloneConfiguration.setPort(redisPort);
////        if (!redisPassword.isEmpty()) {
////            redisStandaloneConfiguration.setPassword(redisPassword);
////        }
//        return new LettuceConnectionFactory(redisStandaloneConfiguration);
//    }
//
//    @Bean
//    public RedisTemplate<String, Object> redisTemplate() {
//        RedisTemplate<String, Object> redisTemplate = new RedisTemplate<>();
//        redisTemplate.setConnectionFactory(redisConnectionFactory());
//        redisTemplate.setKeySerializer(new StringRedisSerializer());
//        redisTemplate.setValueSerializer(new StringRedisSerializer());
//        return redisTemplate;
//    }
//
//    @Bean
//    public ReactiveRedisConnectionFactory reactiveRedisConnectionFactory() {
//        RedisStandaloneConfiguration redisStandaloneConfiguration = new RedisStandaloneConfiguration();
////        redisStandaloneConfiguration.setHostName(redisHost);
////        redisStandaloneConfiguration.setPort(redisPort);
////        if (!redisPassword.isEmpty()) {
////            redisStandaloneConfiguration.setPassword(redisPassword);
////        }
//        return new LettuceConnectionFactory(redisStandaloneConfiguration);
//    }
//
//    @Bean
//    public ReactiveStringRedisTemplate reactiveStringRedisTemplate() {
//        return new ReactiveStringRedisTemplate(reactiveRedisConnectionFactory());
//    }
//}
