package com.jobhunter.application.usecases;

import com.jobhunter.domain.model.Job;
import com.jobhunter.domain.ports.outgoing.JobProvider;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Application Business Logic.
 * Orchestrates the search across multiple providers.
 * Pure Java - No Spring annotations here ensures easier unit testing.
 */
public class SearchJobsUseCase {

    private final List<JobProvider> providers;

    // Dependency Injection via Constructor (Pure Java)
    public SearchJobsUseCase(List<JobProvider> providers) {
        this.providers = providers;
    }

    public List<Job> execute(String technology) {
        if (technology.isBlank()) {
            throw new IllegalArgumentException("Technology cannot be empty");
        }

        // Parallel processing could be added here later for performance
        return providers.stream()
                .map(provider -> {
                    try {
                        return provider.fetchJobs(technology);
                    } catch (Exception e) {
                        // Resilience: If one provider fails, we log and continue
                        // (In a real app, pass a Logger via constructor)
                        System.err.println("Provider failed: " + e.getMessage());
                        return List.<Job>of();
                    }
                })
                .flatMap(List::stream)
                .collect(Collectors.toList());
    }
}