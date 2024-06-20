package store.ggun.admin.chat.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Map;

import jakarta.annotation.PreDestroy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Sinks;
import store.ggun.admin.chat.domain.dto.ChatDTO;
import store.ggun.admin.chat.domain.dto.RoomDTO;
import store.ggun.admin.chat.domain.exception.ChatException;
import store.ggun.admin.chat.domain.model.ChatModel;
import store.ggun.admin.chat.domain.model.RoomModel;
import store.ggun.admin.chat.repository.ChatRepository;
import store.ggun.admin.chat.repository.RoomRepository;

@Slf4j
@Service
@RequiredArgsConstructor
public class RoomServiceImpl implements RoomService {
    private final RoomRepository roomRepository;
    private final ChatRepository chatRepository;
    private final Map<String, Sinks.Many<ServerSentEvent<ChatDTO>>> chatSinks;

    @PreDestroy
    public void close() {
        chatSinks.values().forEach(Sinks.Many::tryEmitComplete);
    }

    @Override
    public Mono<RoomModel> save(RoomDTO dto) {
        return roomRepository.save(RoomModel.builder()
                .title(dto.getTitle())
                .members(dto == null ? new ArrayList<>() : dto.getMembers())
                .build());
    }

    @Override
    public Mono<ChatDTO> saveChat(ChatDTO chatDTO) {
        if (chatDTO == null || chatDTO.getRoomId() == null || chatDTO.getSenderId() == null) {
            return Mono.error(new IllegalArgumentException("ChatDTO, roomId, and senderId must not be null"));
        }

        log.info("saveChat called with roomId: {}, senderId: {}", chatDTO.getRoomId(), chatDTO.getSenderId());

        return roomRepository.findById(chatDTO.getRoomId())
                .switchIfEmpty(Mono.error(new ChatException("Room not found")))
                .filter(room -> {
                    boolean isMember = room.getMembers().contains(chatDTO.getSenderId());
                    log.info("Room members: {}, isMember: {}", room.getMembers(), isMember);
                    return isMember;
                })
                .flatMap(room -> chatRepository.save(ChatModel.builder()
                        .roomId(chatDTO.getRoomId())
                        .message(chatDTO.getMessage())
                        .senderId(chatDTO.getSenderId())
                        .senderName(chatDTO.getSenderName())
                        .createdAt(LocalDateTime.now())
                        .build()))
                .map(chat -> ChatDTO.builder()
                        .id(chat.getId())
                        .roomId(chat.getRoomId())
                        .senderId(chat.getSenderId())
                        .senderName(chat.getSenderName())
                        .message(chat.getMessage())
                        .createdAt(chat.getCreatedAt())
                        .build())
                .doOnSuccess(chat -> {
                    Sinks.Many<ServerSentEvent<ChatDTO>> sink = chatSinks.get(chat.getRoomId());
                    if (sink != null) {
                        sink.tryEmitNext(ServerSentEvent.builder(chat).build());
                    }
                });
    }

    @Override
    public Mono<RoomModel> update(RoomDTO dto) {
        return roomRepository.existsById(dto.getId())
                .flatMap(exists -> roomRepository.save(RoomModel.builder()
                        .id(dto.getId())
                        .title(dto.getTitle())
                        .members(dto.getMembers())
                        .build()));
    }

    @Override
    public Mono<Boolean> delete(String id) {
        return roomRepository.existsById(id)
                .filter(exists -> exists)
                .flatMap(exists -> roomRepository.deleteById(id).thenReturn(exists));
    }

    @Override
    public Mono<RoomModel> findById(String id) {
        return roomRepository.findById(id);
    }

    @Override
    public Mono<ChatModel> findChatById(String id) {
        return chatRepository.findById(id);
    }

    @Override
    public Flux<ChatModel> findChatsByRoomId(String roomId) {
        return roomRepository.existsById(roomId)
                .filter(exists -> exists)
                .flatMapMany(exists -> chatRepository.findByRoomId(roomId));
    }

    @Override
    public Flux<RoomModel> findAll() {
        return roomRepository.findAll();
    }

    @Override
    public Mono<Long> count() {
        return roomRepository.count();
    }

    @Override
    public Flux<ServerSentEvent<ChatDTO>> subscribeByRoomId(String roomId) {
        return chatSinks.computeIfAbsent(roomId, id -> {
                    Sinks.Many<ServerSentEvent<ChatDTO>> sink = Sinks.many().replay().all();
                    chatRepository.findByRoomId(roomId)
                            .flatMap(chat -> Flux.just(
                                    ServerSentEvent.builder(
                                                    ChatDTO.builder()
                                                            .id(chat.getId())
                                                            .roomId(chat.getRoomId())
                                                            .senderId(chat.getSenderId())
                                                            .senderName(chat.getSenderName())
                                                            .message(chat.getMessage())
                                                            .createdAt(chat.getCreatedAt())
                                                            .build())
                                            .build()))
                            .subscribe(sink::tryEmitNext);
                    return sink;
                })
                .asFlux()
                .doOnCancel(() -> {
                    log.info("Unsubscribed room {}", roomId);
                })
                .doOnError(error -> {
                    log.error("Error in room {}", roomId, error);
                    chatSinks.get(roomId).tryEmitError(new ChatException(error.getMessage()));
                })
                .doOnComplete(() -> {
                    log.info("Complete room {}", roomId);
                    chatSinks.get(roomId).tryEmitComplete();
                    chatSinks.remove(roomId);
                });
    }

    @Override
    public Mono<Integer> countConnection() {
        return Mono.just(chatSinks.size());
    }
}