package com.raidenz.doucmentverification.service;

import com.raidenz.doucmentverification.dto.CertificateCreateRequest;
import com.raidenz.doucmentverification.dto.CertificateResponse;
import com.raidenz.doucmentverification.dto.CertificateValidateResponse;

import java.io.IOException;

public interface CertificateService {

    CertificateResponse generateCertificate(CertificateCreateRequest request) throws IOException;
    CertificateValidateResponse validateByHash(String hashValue);

}
