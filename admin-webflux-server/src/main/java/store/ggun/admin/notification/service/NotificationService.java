package store.ggun.admin.notification.service;

import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Sinks;
import store.ggun.admin.notification.domain.NotificationModel;
import store.ggun.admin.notification.repository.NotificationRepository;

@Service
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final Sinks.Many<NotificationModel> sink;

    public NotificationService(NotificationRepository notificationRepository) {
        this.notificationRepository = notificationRepository;
        this.sink = Sinks.many().multicast().onBackpressureBuffer();
    }

    public Mono<Void> sendNotification(NotificationModel notification) {
        return notificationRepository.save(notification)
                .doOnSuccess(sink::tryEmitNext)
                .then();
    }

    public Flux<NotificationModel> getAdminNoticeModels() {
        return sink.asFlux().filter(notification -> "admin".equals(notification.getAdminId()));
    }

    public Flux<NotificationModel> getUserNoticeModels(String adminId) {
        return sink.asFlux().filter(notification -> adminId.equals(notification.getAdminId()));
    }
}
