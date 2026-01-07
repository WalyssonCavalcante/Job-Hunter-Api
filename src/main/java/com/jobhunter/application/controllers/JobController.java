package com.jobhunter.application.controllers;

import com.jobhunter.domain.models.JobOpportunity;
import com.jobhunter.domain.ports.JobScraper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * REST Controller responsible for exposing job scraping capabilities via HTTP.
 * Acts as a Driving Adapter in the hexagonal architecture.
 */
@RestController
@RequestMapping("/api/jobs")
public class JobController {

    private static final Logger logger = LoggerFactory.getLogger(JobController.class);

    // Dependency Injection of the Interface (Port), not the concrete class.
    // This allows us to switch between Google/LinkedIn implementations easily.
    private final JobScraper jobScraper;

    public JobController(JobScraper jobScraper) {
        this.jobScraper = jobScraper;
    }

    /**
     * Endpoint to search for jobs.
     * Usage: GET /api/jobs?tech=java
     *
     * @param tech The technology keyword to search for.
     * @return A list of JobOpportunity objects.
     */
    @GetMapping
    public ResponseEntity<List<JobOpportunity>> getJobs(@RequestParam String tech) {
        logger.info("REST Request received: search jobs for '{}'", tech);

        if (tech == null || tech.trim().isEmpty()) {
            logger.warn("Bad request: technology parameter is missing or empty.");
            return ResponseEntity.badRequest().build();
        }

        List<JobOpportunity> opportunities = jobScraper.scrape(tech);

        if (opportunities.isEmpty()) {
            logger.info("No jobs found for '{}'", tech);
            return ResponseEntity.noContent().build(); // Returns HTTP 204
        }

        return ResponseEntity.ok(opportunities); // Returns HTTP 200 with JSON
    }
}