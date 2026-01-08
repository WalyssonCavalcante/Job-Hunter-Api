package com.jobhunter.domain.model;

import java.time.LocalDateTime;

/**
 * Core Domain Entity.
 * Represents a job opportunity in its purest form.
 */
public record Job(
        String title,
        String company,
        String url,
        String source, // e.g., "Greenhouse", "LinkedIn"
        LocalDateTime foundAt
) {}