package com.jobhunter.domain.ports.outgoing;

import com.jobhunter.domain.model.Job;
import java.util.List;

/**
 * Output Port (SPI - Service Provider Interface).
 * Defines the contract for fetching jobs from external sources.
 * Infrastructure adapters must implement this interface.
 */
public interface JobProvider {
    List<Job> fetchJobs(String technology);
}