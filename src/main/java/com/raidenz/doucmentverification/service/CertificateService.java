package com.raidenz.doucmentverification.service;

import com.raidenz.doucmentverification.dto.CertificateCreateRequest;
import com.raidenz.doucmentverification.dto.CertificateResponse;
import com.raidenz.doucmentverification.dto.CertificateVerifyResponse;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface CertificateService {

    CertificateResponse generateCertificate(CertificateCreateRequest request) throws IOException;
    CertificateVerifyResponse verifyByUploadedFile(MultipartFile file);
    CertificateResponse findByCode(String code);

}
