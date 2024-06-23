package store.ggun.admin.notification.domain;

import lombok.*;
import org.springframework.stereotype.Component;

@Data
@Component
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder
public class NoticeDto {
    private String id;
    private String message;
    private String userId; // 임직원 사용자
    private String response;
    private String adminId; // 관리자
    private String status;
}
