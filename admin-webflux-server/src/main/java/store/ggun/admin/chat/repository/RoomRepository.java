package store.ggun.admin.chat.repository;


import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;
import store.ggun.admin.chat.domain.model.RoomModel;


@Repository
public interface RoomRepository extends ReactiveMongoRepository<RoomModel, String> {

}
