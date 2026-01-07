package com.jobhunter.domain.ports;

import com.jobhunter.domain.models.JobOpportunity;
import java.util.List;

/**
 * Port (Interface) for Job Scraping operations.
 * Application logic relies on this interface, not on concrete implementations (Google, LinkedIn, etc.).
 */
public interface JobScraper {

    /**
     * Searches for job opportunities based on a technology keyword.
     * @param technology The tech stack to search for (e.g., "Java", "Python")
     * @return A list of found job opportunities
     */
    List<JobOpportunity> scrape(String technology);
}