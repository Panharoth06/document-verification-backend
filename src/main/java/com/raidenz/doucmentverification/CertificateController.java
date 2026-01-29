package com.raidenz.doucmentverification;

import com.raidenz.doucmentverification.dto.CertificateCreateRequest;
import com.raidenz.doucmentverification.dto.CertificateResponse;
import com.raidenz.doucmentverification.dto.CertificateValidateRequest;
import com.raidenz.doucmentverification.dto.CertificateValidateResponse;
import com.raidenz.doucmentverification.service.CertificateService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

@RestController
@RequiredArgsConstructor
public class CertificateController {

    private final CertificateService certificateService;

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/generate")
    public ResponseEntity<?> generateCertificate(@RequestBody CertificateCreateRequest certificateCreateRequest) throws IOException {
        CertificateResponse cert = certificateService.generateCertificate(certificateCreateRequest);
        byte[] pdfBytes = Files.readAllBytes(Paths.get(cert.pdfPath()));

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + cert.code() + ".pdf")
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdfBytes);
    }
    @PostMapping("/validate")
    public ResponseEntity<?> validateCertificate(@RequestBody CertificateValidateRequest request) {
        CertificateValidateResponse response =
                certificateService.validateByHash(request.hashValue());
        return ResponseEntity.ok(response);
    }

}
