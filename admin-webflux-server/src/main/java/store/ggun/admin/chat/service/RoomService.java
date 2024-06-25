package store.ggun.admin.chat.service;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import store.ggun.admin.chat.domain.dto.ChatDto;
import store.ggun.admin.chat.domain.dto.RoomDto;
import store.ggun.admin.chat.domain.model.ChatModel;
import store.ggun.admin.chat.domain.model.RoomModel;
import store.ggun.admin.common.service.CommandService;
import store.ggun.admin.common.service.QueryService;



@Service
public interface RoomService extends CommandService<RoomModel, RoomDto>, QueryService<RoomModel, RoomDto> {
    Mono<ChatDto> saveChat(ChatDto chatDTO);
    Mono<ChatModel> findChatById(String id);
    Flux<ChatModel> findChatsByRoomId(String roomId);
    Flux<ServerSentEvent<ChatDto>> subscribeByRoomId(String roomId);
    Mono<Integer> countConnection();
}