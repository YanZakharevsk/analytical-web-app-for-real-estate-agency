package com.hoxsik.project.real_estate_agency.services;

import com.hoxsik.project.real_estate_agency.dto.response.Response;
import com.hoxsik.project.real_estate_agency.jpa.entities.Document;
import com.hoxsik.project.real_estate_agency.jpa.entities.Estate;
import com.hoxsik.project.real_estate_agency.jpa.repositories.DocumentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class DocumentService {
    private final DocumentRepository documentRepository;
    private final EstateService estateService;

    @Value("${app.documents-path}")
    private String path;

    @Transactional
    public Response addDocument(MultipartFile file, Long id) {

        Optional<Estate> estate = estateService.getByID(id);

        if (estate.isEmpty())
            return new Response(false, HttpStatus.NOT_FOUND, "No estate of the provided ID found");

        Optional<Document> document = createDocument(estate.get(), file);

        if (document.isEmpty())
            return new Response(false, HttpStatus.BAD_REQUEST, "Failed to upload the document");

        documentRepository.save(document.get());

        return new Response(true, HttpStatus.CREATED, "Successfully added the document");
    }

    public Optional<Document> getDocumentInfo(Long id) {
        return documentRepository.findByEstateID(id);
    }

    public Optional<InputStream> getDocumentData(Long id) {
        Optional<Document> document = documentRepository.findByEstateID(id);

        if (document.isEmpty())
            return Optional.empty();

        try { return Optional.of(new FileInputStream(document.get().getFilePath() + document.get().getFilename())); }
        catch (IOException e) { return Optional.empty(); }
    }

    private Optional<Document> createDocument(Estate estate, MultipartFile file) {
        File directory = new File(path + estate.getId());

        if (!directory.exists())
            directory.mkdirs();

        String filePath = path + estate.getId() + "/";

        try { file.transferTo(new File(filePath + file.getOriginalFilename())); }
        catch (IOException e) { return Optional.empty(); }

        Document document = new Document();

        document.setFilePath(filePath);
        document.setFileSize(file.getSize());
        document.setContentType(file.getContentType());
        document.setEstate(estate);
        document.setFilename(file.getOriginalFilename());

        return Optional.of(document);
    }
}
