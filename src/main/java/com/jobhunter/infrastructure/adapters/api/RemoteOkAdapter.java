package com.jobhunter.infrastructure.adapters.api;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.jobhunter.domain.model.Job;
import com.jobhunter.domain.ports.outgoing.JobProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Adapter para RemoteOK.com.
 * Fonte: API JSON Oficial.
 * Vantagem: Extremamente rápido e estável.
 */
@Component
public class RemoteOkAdapter implements JobProvider {

    private static final Logger logger = LoggerFactory.getLogger(RemoteOkAdapter.class);
    private static final String API_URL = "https://remoteok.com/api?tag=%s";

    private final RestTemplate restTemplate;

    public RemoteOkAdapter() {
        this.restTemplate = new RestTemplate();
    }

    @Override
    public List<Job> fetchJobs(String technology) {
        List<Job> jobs = new ArrayList<>();
        // RemoteOK usa tags em minúsculo (ex: java, spring-boot)
        String url = String.format(API_URL, technology.toLowerCase());

        try {
            // TRUQUE: RemoteOK bloqueia requisições sem User-Agent definido
            HttpHeaders headers = new HttpHeaders();
            headers.add("User-Agent", "JobHunter-App/1.0");
            HttpEntity<String> entity = new HttpEntity<>(headers);

            ResponseEntity<RemoteOkJobDTO[]> response = restTemplate.exchange(
                    url, HttpMethod.GET, entity, RemoteOkJobDTO[].class
            );

            if (response.getBody() != null) {
                for (RemoteOkJobDTO dto : response.getBody()) {
                    // O primeiro item do array do RemoteOK é sempre um disclaimer legal, ignoramos se não tiver empresa
                    if (dto.company() == null || dto.company().isEmpty()) continue;

                    jobs.add(new Job(
                            dto.position(),
                            dto.company(),
                            dto.url(),
                            "RemoteOK (API)",
                            LocalDateTime.now()
                    ));
                }
            }
            logger.info("RemoteOK returned {} jobs", jobs.size());

        } catch (Exception e) {
            logger.error("Error fetching RemoteOK: {}", e.getMessage());
        }

        return jobs;
    }

    // DTO Interno (Record) para mapear o JSON deles
    @JsonIgnoreProperties(ignoreUnknown = true)
    record RemoteOkJobDTO(String position, String company, String url) {}
}