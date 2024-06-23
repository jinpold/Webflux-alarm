package store.ggun.admin.notification.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Sinks;
import store.ggun.admin.notification.domain.NoticeModel;
import store.ggun.admin.notification.repository.NoticeRepository;

import java.time.Duration;

@Service
@Slf4j
@RequiredArgsConstructor
public class NoticeService {

    private final NoticeRepository noticeRepository;
    private final Sinks.Many<NoticeModel> adminSink = Sinks.many().multicast().onBackpressureBuffer();
    private final Sinks.Many<NoticeModel> userSink = Sinks.many().multicast().onBackpressureBuffer();


    public Mono<NoticeModel> createNoticeModel(NoticeModel notice) {
        return noticeRepository.save(notice)
                .doOnSuccess(savedNoticeModel ->{
                    log.info("NoticeModel created: {}", savedNoticeModel);
                    adminSink.tryEmitNext(savedNoticeModel);
                });
    }

    public Mono<NoticeModel> updateNoticeModelStatus(String id, String status) {
        return noticeRepository.findById(id)
                .flatMap(notice -> {
                    notice.setStatus(status);
                    return noticeRepository.save(notice)
                            .doOnSuccess(updatedNoticeModel -> userSink.tryEmitNext(updatedNoticeModel));
                });
    }

    public Flux<NoticeModel> getAdminNoticeModels() {
        return adminSink.asFlux();
    }

    public Flux<NoticeModel> getUserNoticeModels(String userId) {
        return userSink.asFlux().filter(notice -> notice.getUserId().equals(userId));
    }

    public Flux<NoticeModel> getNotificationsByAdminId(String adminId) {
        return noticeRepository.findAll().filter(notification -> notification.getAdminId().equals(adminId));
    }

    public Mono<Void> deleteNotification(String id) {
        return noticeRepository.deleteById(id);
    }
}
