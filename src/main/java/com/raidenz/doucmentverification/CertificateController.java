package com.raidenz.doucmentverification;

import com.raidenz.doucmentverification.dto.CertificateCreateRequest;
import com.raidenz.doucmentverification.dto.CertificateResponse;
import com.raidenz.doucmentverification.service.CertificateService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;


@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/certificates")
public class CertificateController {

    private final CertificateService certificateService;

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    public CertificateResponse create(@Valid @RequestBody CertificateCreateRequest request) throws IOException {
        return certificateService.generateCertificate(request);
    }

    @GetMapping("/{code}/pdf")
    public ResponseEntity<byte[]> download(@PathVariable String code) throws IOException {
        CertificateResponse cert = certificateService.findByCode(code);
        byte[] pdf = Files.readAllBytes(Paths.get(cert.pdfPath()));

        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_PDF)
                .header(
                        HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=" + code + ".pdf"
                )
                .body(pdf);
    }

    @GetMapping("/code/{code}")
    public CertificateResponse getCertificateByCode(@PathVariable String code) {
        return certificateService.findByCode(code);
    }

    @PostMapping("/verify/upload")
    public boolean verifyByUpload(@RequestParam("file") MultipartFile file) {
        return certificateService.verifyByUploadedFile(file);
    }

}
