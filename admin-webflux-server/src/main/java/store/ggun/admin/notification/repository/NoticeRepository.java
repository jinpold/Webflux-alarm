package store.ggun.admin.notification.repository;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import store.ggun.admin.notification.domain.NoticeModel;

@Repository
public interface NoticeRepository extends ReactiveMongoRepository<NoticeModel, String> {
    Flux<NoticeModel> findByUserId (String userId);
}
