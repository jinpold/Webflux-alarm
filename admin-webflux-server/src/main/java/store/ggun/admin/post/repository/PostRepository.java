package store.ggun.admin.post.repository;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import store.ggun.admin.notification.domain.NotificationModel;
import store.ggun.admin.post.domain.PostModel;

@Repository
public interface PostRepository extends ReactiveMongoRepository<PostModel, String> {
//    Flux<PostModel> findByPostId (String postId);
}
