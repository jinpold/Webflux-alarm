package store.ggun.admin.chat.controller;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.web.bind.annotation.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import store.ggun.admin.chat.domain.dto.ChatDTO;
import store.ggun.admin.chat.domain.dto.RoomDTO;
import store.ggun.admin.chat.domain.exception.ChatException;
import store.ggun.admin.chat.domain.model.RoomModel;
import store.ggun.admin.chat.service.RoomService;

@Slf4j
@CrossOrigin(origins = "*", allowedHeaders = "*")
@RestController
@RequestMapping("/api/chats")
@RequiredArgsConstructor
public class ChatController {
    private final RoomService roomService;

    @GetMapping("/checkServer")
    public Mono<String> getMethodName() {
        log.info("Check server status");
        return roomService.countConnection()
                .flatMap(count -> Mono.just("Server is running. Total connection: " + count));
    }

    @PostMapping("/save")
    public Mono<RoomModel> saveRoom(@RequestBody RoomDTO dto) {
        log.info("Save room");
        return roomService.save(dto);
    }


    @GetMapping("/recieve/{roomId}")
    public Flux<ServerSentEvent<ChatDTO>> subscribeByRoomId(@PathVariable String roomId) {
        log.info("subscribe chat by room id {}", roomId);
        return roomService.subscribeByRoomId(roomId)
                .switchIfEmpty(Flux.error(new ChatException("Room not found")));
    }

    @PostMapping("/send")
    public Mono<ChatDTO> sendChat(@RequestBody ChatDTO chatDTO) {
        log.info("Send chat {}", chatDTO.toString());
        return roomService.saveChat(chatDTO)
                .switchIfEmpty(Mono.error(new ChatException("Room not found")));
    }
}