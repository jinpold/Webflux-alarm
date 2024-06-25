package store.ggun.admin.notification.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Sinks;
import store.ggun.admin.notification.domain.NotificationModel;
import store.ggun.admin.notification.repository.NotificationRepository;

@Service
@Slf4j
@RequiredArgsConstructor
public class NotificationService {


    private final NotificationRepository notificationRepository;
    private final Sinks.Many<NotificationModel> adminSink = Sinks.many().multicast().onBackpressureBuffer();
    private final Sinks.Many<NotificationModel> userSink = Sinks.many().multicast().onBackpressureBuffer();
    private final Sinks.Many<NotificationModel> sink = Sinks.many().multicast().onBackpressureBuffer();


    public Mono<NotificationModel> createNoticeModel(NotificationModel notice) {
        return notificationRepository.save(notice)
                .doOnSuccess(savedNotificationModel ->{
                    log.info("NoticeModel created: {}", savedNotificationModel);
                    adminSink.tryEmitNext(savedNotificationModel);
                });
    }

    public Mono<NotificationModel> updateNoticeModelStatus(String id, String status) {
        return notificationRepository.findById(id)
                .flatMap(notice -> {
                    notice.setStatus(status);
                    return notificationRepository.save(notice)
                            .doOnSuccess(updatedNotificationModel -> userSink.tryEmitNext(updatedNotificationModel));
                });
    }

    public Flux<NotificationModel> getAdminNoticeModels() {
        return adminSink.asFlux();
    }

    public Flux<NotificationModel> getUserNoticeModels(String userId) {
        return userSink.asFlux().filter(notice -> notice.getUserId().equals(userId));
    }

    public Flux<NotificationModel> getNotificationsByAdminId(String adminId) {
        return notificationRepository.findAll().filter(notification -> notification.getAdminId().equals(adminId));
    }

    public Mono<Void> deleteNotification(String id) {
        return notificationRepository.deleteById(id);
    }

    public void sendNotification(NotificationModel notificationModel) {
        sink.tryEmitNext(notificationModel);
    }

    public Flux<NotificationModel> getNotifications() {
        return sink.asFlux();
    }


}
