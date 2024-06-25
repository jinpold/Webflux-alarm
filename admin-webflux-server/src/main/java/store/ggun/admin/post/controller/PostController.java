package store.ggun.admin.post.controller;

import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import store.ggun.admin.notification.domain.NotificationModel;
import store.ggun.admin.notification.service.NotificationService;
import store.ggun.admin.post.domain.PostModel;
import store.ggun.admin.post.service.PostService;


@Slf4j
@CrossOrigin(origins = "*", allowedHeaders = "*")
@RestController
@ApiResponses(value = {
        @ApiResponse(responseCode = "400", description = "Invalid ID supplied"),
        @ApiResponse(responseCode = "404", description = "Customer not found")})
@RequestMapping(path = "/board/posts")
public class PostController {

    private final PostService postService;
    private final NotificationService notificationService;

    public PostController(PostService postService, NotificationService notificationService) {
        this.postService = postService;
        this.notificationService = notificationService;
    }

    @PostMapping
    public Mono<PostModel> createPost(@RequestBody PostModel postModel) {
        notificationService.sendNotification(NotificationModel.builder()
                .id("2")
                .message("새 문의글이 등록되었습니다.")
                .userId("admin")
                .response(null)
                .adminId(null)
                .status("공지")
                .build());
        return postService.createPost(postModel);
    }

    @GetMapping("/list")
    public Flux<PostModel> getAllPosts() {
        return postService.getAllPosts();
    }

    @GetMapping("/find/{id}")
    public Mono<PostModel> getPostById(@PathVariable String id) {
        return postService.getPostById(id);
    }

    @DeleteMapping("/delete/{id}")
    public Mono<Void> deletePostById(@PathVariable String id) {
        return postService.deletePostById(id);
    }
}
