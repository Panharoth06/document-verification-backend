package com.raidenz.doucmentverification.service;

import com.raidenz.doucmentverification.dto.CertificateCreateRequest;
import com.raidenz.doucmentverification.dto.CertificateResponse;

import java.io.IOException;

public interface CertificateService {

    CertificateResponse generateCertificate(CertificateCreateRequest request) throws IOException;

}
