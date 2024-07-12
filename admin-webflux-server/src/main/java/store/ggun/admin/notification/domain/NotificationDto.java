package store.ggun.admin.notification.domain;

import lombok.*;
import org.springframework.stereotype.Component;

@Data
@Component
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder
public class NotificationDto {
    private String id;
    private String message;
    private String adminId; // 임직원 사용자
    private String response; // admin response
    private String hrAdminId; // 인사 관리자
    private String status; // 상태(공지 or 답변)
}
