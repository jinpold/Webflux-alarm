package store.ggun.admin.notification.repository;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import store.ggun.admin.notification.domain.NotificationModel;

@Repository
public interface NotificationRepository extends ReactiveMongoRepository<NotificationModel, String> {
    Flux<NotificationModel> findByUserId (String userId);
}
