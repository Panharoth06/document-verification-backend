package com.raidenz.doucmentverification.service;

import com.raidenz.doucmentverification.dto.CertificateCreateRequest;
import com.raidenz.doucmentverification.dto.CertificateResponse;

public interface CertificateService {

    CertificateResponse generateCertificate(CertificateCreateRequest request);

}
