package com.raidenz.doucmentverification.service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import com.microsoft.playwright.*;
import com.microsoft.playwright.options.LoadState;
import com.raidenz.doucmentverification.domain.Certificate;
import com.raidenz.doucmentverification.dto.CertificateCreateRequest;
import com.raidenz.doucmentverification.dto.CertificateResponse;
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

    @Value("${FRONTEND_BASE_URL}")
    private static String FRONTEND_BASE_URL;

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

        byte[] pdfBytes = renderPdfFromUrl(cert.getId());

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

    private byte[] renderPdfFromUrl(Long certificateId) {
        String url = FRONTEND_BASE_URL + "/certificate/print/" + certificateId;

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
