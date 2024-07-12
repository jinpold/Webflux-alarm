package store.ggun.admin.notification.controller;

import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import store.ggun.admin.notification.domain.NotificationModel;
import store.ggun.admin.notification.service.NotificationService;

@Slf4j
@CrossOrigin(origins = "*", allowedHeaders = "*")
@RestController
@RequiredArgsConstructor
@ApiResponses(value = {
        @ApiResponse(responseCode = "400", description = "Invalid ID supplied"),
        @ApiResponse(responseCode = "404", description = "Customer not found")})
@RequestMapping(path = "/notifications")
public class NotificationController {
    private final NotificationService notificationService;

    @GetMapping(value = "/admin", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<NotificationModel> subscribeToAdminNoticeModels() {
        // 이 메서드는 관리자에게 실시간으로 모든 알림을 스트리밍합니다.
        // TEXT_EVENT_STREAM_VALUE로 설정된 미디어 타입 덕분에 SSE 스트림이 사용됩니다.
        return notificationService.getAdminNoticeModels();
    }

    @GetMapping(value = "/admin/{adminId}", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<NotificationModel> subscribeToUserNoticeModels(@PathVariable("adminId") String adminId) {
        // 메서드는 특정 사용자에게 실시간으로 알림을 스트리밍합니다. URL 경로에서 adminId를 받아 해당 사용자에게만 알림을 필터링합니다.
        return notificationService.getUserNoticeModels(adminId);
    }
}
