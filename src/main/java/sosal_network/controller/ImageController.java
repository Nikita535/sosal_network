package sosal_network.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import sosal_network.entity.Image;
import sosal_network.entity.User;
import sosal_network.repository.ImageRepository;

import java.io.ByteArrayInputStream;

@RestController
@RequiredArgsConstructor
public class ImageController {
    private final ImageRepository imageRepository;

    @GetMapping("/image/{id}")
    private ResponseEntity<?> getImageById(@PathVariable Long id){
//        Image image = imageRepository.findImageByUser_UsernameAndIsPreview(username,true);
        Image image =imageRepository.findImageById(id);
        return ResponseEntity.ok()
                .header("fileName",image.getOriginalFileName())
                .contentType(MediaType.valueOf(image.getContentType()))
                .contentLength(image.getSize())
                .body(new InputStreamResource(new ByteArrayInputStream(image.getBytes())));
    }

}
