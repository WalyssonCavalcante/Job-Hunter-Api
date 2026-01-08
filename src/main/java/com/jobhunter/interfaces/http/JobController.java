package com.jobhunter.interfaces.http;

import com.jobhunter.application.usecases.SearchJobsUseCase;
import com.jobhunter.domain.model.Job;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;

@RestController
@RequestMapping("/api/jobs")
public class JobController {

    private final SearchJobsUseCase searchJobsUseCase;

    public JobController(SearchJobsUseCase searchJobsUseCase) {
        this.searchJobsUseCase = searchJobsUseCase;
    }

    @GetMapping
    public ResponseEntity<List<Job>> getJobs(@RequestParam String tech) {
        List<Job> jobs = searchJobsUseCase.execute(tech);
        return ResponseEntity.ok(jobs);
    }
}