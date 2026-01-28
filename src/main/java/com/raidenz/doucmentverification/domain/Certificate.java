package com.raidenz.doucmentverification.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.Set;

@Entity
@Table(name = "certificates")
@Getter
@Setter
@NoArgsConstructor
public class Certificate {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 100, nullable = false)
    private String owner;

    private LocalDateTime issueDate;

    @Column(length = 100, nullable = false)
    private String courseName;

    @Column(length = 100, nullable = false)
    private String offeredBy;

    @Column(nullable = false)
    private String pdfPath;

    @Column(nullable = false)
    private String hashValue;

    private Set<String> coveredTopics;
}
