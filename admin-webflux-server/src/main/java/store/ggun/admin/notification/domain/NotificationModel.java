package store.ggun.admin.notification.domain;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Document(collection = "notices")
@Builder
@AllArgsConstructor
@ToString(exclude = "id")
@NoArgsConstructor
public class NotificationModel {
    @Id
    String id;
    String message; // user message
    String userId; // 임직원 사용자
    String response; // admin response
    String adminId; // 관리자
    String status; // 상태(공지 or 답변)
}
