package com.hoxsik.courseproject.real_estate_agency.services;

import com.hoxsik.courseproject.real_estate_agency.dto.response.Response;
import com.hoxsik.courseproject.real_estate_agency.jpa.entities.Estate;
import com.hoxsik.courseproject.real_estate_agency.jpa.entities.Photo;
import com.hoxsik.courseproject.real_estate_agency.jpa.repositories.PhotoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PhotoService {
    private final PhotoRepository photoRepository;
    private final EstateService estateService;
    private final ZipperService zipperService;

    @Value("${app.photos-path}")
    private String path;

    @Transactional
    public Response uploadPhoto(MultipartFile file, Long id) {
        Optional<Estate> estate = estateService.getByID(id);

        if (estate.isEmpty())
            return new Response(false, HttpStatus.NOT_FOUND, "No estate of the provided ID found");

        Optional<Photo> photo = createPhoto(file, estate.get());

        if (photo.isEmpty())
            return new Response(false, HttpStatus.BAD_REQUEST, "Failed to upload the file");

        photoRepository.save(photo.get());

        return new Response(true, HttpStatus.CREATED, "Successfully uploaded the photo");
    }

    public Optional<ByteArrayOutputStream> getPhotosByEstateID(Long id) {
        Optional<Estate> estate = estateService.getByID(id);

        if (estate.isEmpty())
            return Optional.empty();

        Optional<List<Photo>> photos = photoRepository.findByEstate(estate.get());

        if (photos.isEmpty())
            return Optional.empty();

        try { return Optional.of(zipperService.zipFolder(photos.get().get(0).getFilePath())); }
        catch (IOException e) { return Optional.empty(); }
    }

    private Optional<Photo> createPhoto(MultipartFile file, Estate estate) {

        File directory = new File(path + estate.getId());

        if (!directory.exists())
            directory.mkdirs();

        String filePath = path + estate.getId() + "/";

        try { file.transferTo(new File(filePath + file.getOriginalFilename())); }
        catch (IOException e) { return Optional.empty(); }

        Photo photo = new Photo();

        photo.setFilename(file.getOriginalFilename());
        photo.setFileSize(file.getSize());
        photo.setFilePath(filePath);
        photo.setEstate(estate);
        photo.setContentType(file.getContentType());

        return Optional.of(photo);
    }


}
