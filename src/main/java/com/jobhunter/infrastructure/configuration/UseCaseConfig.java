package com.jobhunter.infrastructure.configuration;

import com.jobhunter.application.usecases.SearchJobsUseCase;
import com.jobhunter.domain.ports.outgoing.JobProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import java.util.List;

@Configuration
public class UseCaseConfig {

    @Bean
    public SearchJobsUseCase searchJobsUseCase(List<JobProvider> providers) {
        return new SearchJobsUseCase(providers);
    }
}