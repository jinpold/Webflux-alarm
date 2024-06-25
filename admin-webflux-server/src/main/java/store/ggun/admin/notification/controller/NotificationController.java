package store.ggun.admin.notification.controller;

import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import store.ggun.admin.notification.domain.NotificationModel;
import store.ggun.admin.notification.service.NotificationService;


@Slf4j
@CrossOrigin(origins = "*", allowedHeaders = "*")
@RestController
@RequiredArgsConstructor
@ApiResponses(value = {
        @ApiResponse(responseCode = "400", description = "Invalid ID supplied"),
        @ApiResponse(responseCode = "404", description = "Customer not found")})
@RequestMapping(path = "/api/notifications")
public class NotificationController {
    private final NotificationService notificationService;

    @PostMapping("/subscribe")
    public Mono<NotificationModel> createNoticeModel(@RequestBody NotificationModel notification) {
        return notificationService.createNoticeModel(notification);
    }

    @PostMapping("/{id}/respond")
    public Mono<NotificationModel> respondToNoticeModel(@PathVariable("id") String id, @RequestParam("status") String status) {
        return notificationService.updateNoticeModelStatus(id, status);
    }

    @GetMapping(value = "/admin", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<NotificationModel> subscribeToAdminNoticeModels() {
        return notificationService.getAdminNoticeModels();
    }

    @GetMapping(value = "/user/{userId}", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<NotificationModel> subscribeToUserNoticeModels(@PathVariable("userId") String userId) {
        return notificationService.getUserNoticeModels(userId);
    }

    @GetMapping("/admin/{adminId}")
    public Flux<NotificationModel> getNotificationsByAdminId(@PathVariable("adminId") String lawyerId) {
        return notificationService.getNotificationsByAdminId(lawyerId);
    }

    @DeleteMapping("/{id}")
    public Mono<Void> deleteNotification(@PathVariable("id") String id) {
        return notificationService.deleteNotification(id);
    }
    //---------------------------------------------------------------------------


}
