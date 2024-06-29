package store.ggun.admin.file;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import org.springframework.core.io.buffer.DataBufferUtils;

import java.util.List;

@RestController
@RequestMapping("/file-record")
@Slf4j
@RequiredArgsConstructor
public class FileController {
    private final FileService fileService;

    @PostMapping(value = "/upload-files", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public Mono<List<FileDto>> uploadFiles(ServerWebExchange exchange) {
        return exchange.getMultipartData()
                .flatMapMany(multipart -> {
                    List<FilePart> fileParts = multipart.containsKey("files") ?
                            multipart.get("files").stream().map(part -> (FilePart) part).toList() :
                            List.of((FilePart) multipart.getFirst("file"));

                    return Flux.fromIterable(fileParts);
                })
                .flatMap(filePart -> DataBufferUtils.join(filePart.content())
                        .flatMap(dataBuffer -> {
                            byte[] bytes = new byte[dataBuffer.readableByteCount()];
                            dataBuffer.read(bytes);
                            DataBufferUtils.release(dataBuffer);
                            FileModel fileModel = FileModel.builder()
                                    .filename(filePart.filename())
                                    .contentType(filePart.headers().getContentType().toString())
                                    .data(bytes)
                                    .build();
                            return fileService.save(fileModel);
                        }))
                .map(this::toDto)
                .collectList();
    }

    @GetMapping("/files/{id}")
    public Mono<ResponseEntity<byte[]>> getFile(@PathVariable("id") String id) {
        return fileService.getFile(id)
                .map(fileModel -> ResponseEntity.ok()
                        .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + fileModel.getFilename() + "\"")
                        .contentType(MediaType.parseMediaType(fileModel.getContentType()))
                        .body(fileModel.getData()))
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @GetMapping("/files/download")
    public Flux<ResponseEntity<byte[]>> downloadFiles(@RequestParam List<String> ids) {
        return Flux.fromIterable(ids)
                .flatMap(fileService::getFile)
                .map(fileModel -> ResponseEntity.ok()
                        .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + fileModel.getFilename() + "\"")
                        .contentType(MediaType.parseMediaType(fileModel.getContentType()))
                        .body(fileModel.getData()));
    }

    private FileDto toDto(FileModel fileModel) {
        return FileDto.builder()
                .id(fileModel.getId())
                .filename(fileModel.getFilename())
                .contentType(fileModel.getContentType())
                .data(fileModel.getData())
                .build();
    }
}
