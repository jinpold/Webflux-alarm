package store.ggun.admin.file;

import jakarta.mail.internet.MimeUtility;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import org.springframework.core.io.buffer.DataBufferUtils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

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
                    List<FilePart> fileParts = multipart.containsKey("file") ?
                            multipart.get("file").stream().map(part -> (FilePart) part).toList() :
                            List.of((FilePart) multipart.getFirst("file"));

                    log.info("Number of files received: {}", fileParts.size());

                    return Flux.fromIterable(fileParts);
                })
                .flatMap(filePart -> {
                    log.info("Processing file: {}", filePart.filename());
                    return DataBufferUtils.join(filePart.content())
                            .flatMap(dataBuffer -> {
                                byte[] bytes = new byte[dataBuffer.readableByteCount()];
                                dataBuffer.read(bytes);
                                DataBufferUtils.release(dataBuffer);
                                FileModel fileModel = FileModel.builder()
                                        .filename(filePart.filename())
                                        .contentType(filePart.headers().getContentType().toString())
                                        .data(bytes)
                                        .build();
                                return fileService.save(fileModel)
                                        .doOnSuccess(savedFile -> log.info("File saved: {}", savedFile.getFilename()))
                                        .doOnError(e -> log.error("Error saving file: {}", filePart.filename(), e));
                            })
                            .onErrorResume(e -> {
                                log.error("Error processing file: {}", filePart.filename(), e);
                                return Mono.empty(); // 파일 처리 중 오류가 발생하면 해당 파일 건너뜀
                            });
                })
                .map(this::toDto)
                .collectList()
                .doOnSuccess(fileDtos -> log.info("All files processed successfully"))
                .doOnError(e -> log.error("Error processing files", e));
    }



    @GetMapping("/files/{id}") // 파일 1개 다운로드
    public Mono<ResponseEntity<byte[]>> getFile(@PathVariable("id") String id) {
        return fileService.getFile(id)
                .map(fileModel -> {
                    String filename = fileModel.getFilename();
                    String encodedFilename = "";
                    try {
                        encodedFilename = MimeUtility.encodeText(filename, "UTF-8", "B");
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }
                    String contentDisposition = "attachment; filename=\"" + encodedFilename + "\"";

                    return ResponseEntity.ok()
                            .header(HttpHeaders.CONTENT_DISPOSITION, contentDisposition)
                            .contentType(MediaType.parseMediaType(fileModel.getContentType()))
                            .body(fileModel.getData());
                })
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @GetMapping("/files/download") // 알집으로 다운로드 (파일 2개 이상)
    public Mono<ResponseEntity<byte[]>> downloadFiles(@RequestParam List<String> ids) {
        return Flux.fromIterable(ids)
                .flatMap(fileService::getFile)
                .collectList()
                .map(fileModels -> {
                    try (ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                         ZipOutputStream zipOutputStream = new ZipOutputStream(byteArrayOutputStream)) {

                        for (FileModel fileModel : fileModels) {
                            ZipEntry zipEntry = new ZipEntry(fileModel.getFilename());
                            zipOutputStream.putNextEntry(zipEntry);
                            zipOutputStream.write(fileModel.getData());
                            zipOutputStream.closeEntry();
                        }
                        zipOutputStream.finish();
                        byte[] zipBytes = byteArrayOutputStream.toByteArray();

                        String encodedFilename = MimeUtility.encodeText("files.zip", "UTF-8", "B");
                        String contentDisposition = "attachment; filename=\"" + encodedFilename + "\"";

                        return ResponseEntity.ok()
                                .header(HttpHeaders.CONTENT_DISPOSITION, contentDisposition)
                                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                                .body(zipBytes);

                    } catch (IOException e) {
                        e.printStackTrace();
                        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
                    }
                });
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
