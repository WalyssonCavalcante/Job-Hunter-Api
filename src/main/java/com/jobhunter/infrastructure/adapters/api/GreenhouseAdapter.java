package com.jobhunter.infrastructure.adapters.api;

import com.jobhunter.domain.model.Job;
import com.jobhunter.domain.ports.outgoing.JobProvider;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Adapter implementation for Greenhouse.
 * This class knows about Spring (@Component) and HTTP (RestTemplate).
 */
@Component
public class GreenhouseAdapter implements JobProvider {

    // Simulating an external call logic
    @Override
    public List<Job> fetchJobs(String technology) {
        // In a real scenario, use RestTemplate/WebClient here to call Greenhouse API
        // For now, returning dummy data to prove the architecture works

        System.out.println("Fetching from Greenhouse API for: " + technology);

        return List.of(
                new Job(
                        "Backend Engineer (" + technology + ")",
                        "Nubank",
                        "https://nubank.greenhouse.io/jobs/123",
                        "Greenhouse API",
                        LocalDateTime.now()
                )
        );
    }
}