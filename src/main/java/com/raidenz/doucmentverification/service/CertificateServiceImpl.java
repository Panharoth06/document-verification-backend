package com.raidenz.doucmentverification.service;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import com.microsoft.playwright.*;
import com.microsoft.playwright.options.LoadState;
import com.raidenz.doucmentverification.domain.Certificate;
import com.raidenz.doucmentverification.dto.CertificateCreateRequest;
import com.raidenz.doucmentverification.dto.CertificateResponse;
import com.raidenz.doucmentverification.exception.customException.ResourceNotFoundException;
import com.raidenz.doucmentverification.repository.CertificateRepository;
import com.raidenz.doucmentverification.util.HashUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CertificateServiceImpl implements CertificateService{

    private final CertificateRepository certificateRepository;
    private final HashUtil hashUtil;

    @Value("${frontend.base-url}")
    private String frontEndUrl;

    @Override
    public CertificateResponse generateCertificate(CertificateCreateRequest request) throws IOException {

        Certificate cert = new Certificate();
        cert.setCourseName(request.courseName());
        cert.setOwner(request.ownerName());
        cert.setOfferedBy(request.offeredBy());
        cert.setCoveredTopics(request.coveredTopics());
        cert.setIssueDate(LocalDateTime.now());
        cert.setCode(UUID.randomUUID().toString());

        certificateRepository.save(cert);

        byte[] pdfBytes = renderPdfFromUrl(cert);

        String pdfHash = hashUtil.calculatePdfHash(pdfBytes);
        cert.setHashValue(pdfHash);

        String pdfPath = savePdfToDisk(cert.getCode(), pdfBytes);
        cert.setPdfPath(pdfPath);

        certificateRepository.save(cert);
        return CertificateResponse.builder()
                .courseName(cert.getCourseName())
                .ownerName(cert.getOwner())
                .offeredBy(cert.getOfferedBy())
                .coveredTopics(cert.getCoveredTopics())
                .issueDate(cert.getIssueDate())
                .pdfPath(cert.getPdfPath())
                .code(cert.getCode())
                .build();
    }

    @Override
    public CertificateResponse findByCode(String code) {
        Certificate cert = certificateRepository.findCertificateByCode(code).orElseThrow(
                () -> new ResourceNotFoundException("Certificate with code: " + code + " not found")
        );

        return CertificateResponse.builder()
                .courseName(cert.getCourseName())
                .ownerName(cert.getOwner())
                .offeredBy(cert.getOfferedBy())
                .coveredTopics(cert.getCoveredTopics())
                .issueDate(cert.getIssueDate())
                .pdfPath(cert.getPdfPath())
                .code(cert.getCode())
                .build();
    }

    private byte[] renderPdfFromUrl(Certificate cert) throws UnsupportedEncodingException {
        // example in CertificateServiceImpl
        String url = frontEndUrl + "/certificate/print/" + cert.getCode() +
                "?owner=" + URLEncoder.encode(cert.getOwner(), StandardCharsets.UTF_8) +
                "&course=" + URLEncoder.encode(cert.getCourseName(), StandardCharsets.UTF_8) +
                "&offeredBy=" + URLEncoder.encode(cert.getOfferedBy(), StandardCharsets.UTF_8) +
                "&topics=" + URLEncoder.encode(String.join(",", cert.getCoveredTopics()), "UTF-8") +
                "&date=" + URLEncoder.encode(cert.getIssueDate().toString(), StandardCharsets.UTF_8);


        try (Playwright playwright = Playwright.create()) {
            Browser browser = playwright.chromium().launch(new BrowserType.LaunchOptions().setHeadless(true));
            BrowserContext context = browser.newContext(new Browser.NewContextOptions().setViewportSize(1123, 794));
            Page page = context.newPage();

            page.navigate(url);
            page.waitForLoadState(LoadState.NETWORKIDLE);

            return page.pdf(new Page.PdfOptions()
                    .setPrintBackground(true)
                    .setLandscape(true)
                    .setWidth("1123px")
                    .setHeight("794px")
            );

        } catch (Exception e) {
            throw new RuntimeException("PDF generation failed", e);
        }
    }

    private String savePdfToDisk(String code, byte[] pdfBytes) throws IOException {
        String path = "./certificates/" + code + ".pdf";
        Files.createDirectories(Paths.get("./certificates/"));
        Files.write(Paths.get(path), pdfBytes);
        return path;
    }
}
