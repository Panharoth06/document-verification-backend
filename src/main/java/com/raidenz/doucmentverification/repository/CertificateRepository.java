package com.raidenz.doucmentverification.repository;

import com.raidenz.doucmentverification.domain.Certificate;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CertificateRepository extends JpaRepository<Certificate, Long> {

    Optional<Certificate> findCertificateByHashValue(String hashValue);

}
