package store.ggun.admin.notification.controller;

import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import store.ggun.admin.notification.domain.NoticeModel;
import store.ggun.admin.notification.service.NoticeService;


@Slf4j
@CrossOrigin(origins = "*", allowedHeaders = "*")
@RestController
@RequiredArgsConstructor
@ApiResponses(value = {
        @ApiResponse(responseCode = "400", description = "Invalid ID supplied"),
        @ApiResponse(responseCode = "404", description = "Customer not found")})
@RequestMapping(path = "/api/notifications")
public class NoticeController {
    private final NoticeService noticeService;

    @PostMapping("/subscribe")
    public Mono<NoticeModel> createNoticeModel(@RequestBody NoticeModel notification) {
        return noticeService.createNoticeModel(notification);
    }

    @PostMapping("/{id}/respond")
    public Mono<NoticeModel> respondToNoticeModel(@PathVariable("id") String id, @RequestParam("status") String status) {
        return noticeService.updateNoticeModelStatus(id, status);
    }

    @GetMapping(value = "/admin", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<NoticeModel> subscribeToAdminNoticeModels() {
        return noticeService.getAdminNoticeModels();
    }

    @GetMapping(value = "/user/{userId}", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<NoticeModel> subscribeToUserNoticeModels(@PathVariable("userId") String userId) {
        return noticeService.getUserNoticeModels(userId);
    }

    @GetMapping("/admin/{adminId}")
    public Flux<NoticeModel> getNotificationsByAdminId(@PathVariable("adminId") String lawyerId) {
        return noticeService.getNotificationsByAdminId(lawyerId);
    }

    @DeleteMapping("/{id}")
    public Mono<Void> deleteNotification(@PathVariable("id") String id) {
        return noticeService.deleteNotification(id);
    }
}
