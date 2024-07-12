package store.ggun.admin.post.repository;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;
import store.ggun.admin.post.domain.PostModel;

@Repository
public interface PostRepository extends ReactiveMongoRepository<PostModel, String> {
}
