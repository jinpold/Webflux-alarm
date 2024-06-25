package store.ggun.admin.common.config;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;

import lombok.RequiredArgsConstructor;
import store.ggun.admin.chat.domain.model.ChatModel;
import store.ggun.admin.chat.domain.model.RoomModel;
import store.ggun.admin.user.domain.RoleModel;
import store.ggun.admin.user.domain.UserModel;

@Configuration
@RequiredArgsConstructor
public class FluxChatMongoConfig {
    private final ReactiveMongoTemplate mongoTemplate;

    @Bean
    public CommandLineRunner commandLineRunner() {
        return args -> {
            mongoTemplate.getCollectionNames()
                    .flatMap(collectionName -> mongoTemplate.dropCollection(collectionName))
                    .log()
                    .collectList()
                    .flatMapMany(i -> mongoTemplate.insertAll(
                            List.of(
                                    UserModel.builder()
                                            .email("admin1@admin")
                                            .password("admin")
                                            .firstName("soo")
                                            .lastName("jeGal")
                                            .profile("test url1")
                                            .roles(List.of(RoleModel.SUPER_ADMIN, RoleModel.ADMIN, RoleModel.USER))
                                            .build(),
                                    UserModel.builder()
                                            .email("admin2@admin")
                                            .password("admin2")
                                            .firstName("jin")
                                            .lastName("heo")
                                            .profile("test url2")
                                            .roles(List.of(RoleModel.SUPER_ADMIN, RoleModel.ADMIN, RoleModel.USER))
                                            .build()
                            )
                    ))
                    .collectList()
                    .flatMap(users -> {
                        UserModel user1 = users.get(0);
                        UserModel user2 = users.get(1);// 사용자 id 고유값을 member로 넣어줌
                        return mongoTemplate.insert(
                                RoomModel.builder()
                                        .id("1")
                                        .title("test room")
                                        .members(List.of(user1.getUserId(), user2.getUserId()))
                                        .build()
                        );
                    })
                    .flatMap(room -> {
                        // chats 컬렉션에 ChatModel 데이터 추가
                        return mongoTemplate.insert(
                                ChatModel.builder()
                                        .roomId(room.getId())
                                        .senderId("admin2@admin")
                                        .senderName("jin heo")
                                        .message("Welcome to the test room!")
                                        .createdAt(LocalDateTime.now())
                                        .build()
                        );
                    })
                    .subscribe();

            System.out.println("MongoDB Initiated!");
        };
    }
}
