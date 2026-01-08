package com.jobhunter.infrastructure.adapters.api;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.jobhunter.domain.model.Job;
import com.jobhunter.domain.ports.outgoing.JobProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Component
public class GreenhouseAdapter implements JobProvider {

    private static final String API_URL_TEMPLATE = "https://boards-api.greenhouse.io/v1/boards/%s/jobs";

    // ARQUITETURA LIMPA: Injeção de Configuração
    // O Spring vai ler a lista do application.properties automaticamente.
    @Value("#{'${jobhunter.greenhouse.boards}'.split(',')}")
    private List<String> targetBoards;

    private final RestTemplate restTemplate;

    public GreenhouseAdapter() {
        this.restTemplate = new RestTemplate();
    }

    @Override
    public List<Job> fetchJobs(String technology) {
        List<Job> foundJobs = new ArrayList<>();

        // Agora iteramos sobre a lista dinâmica injetada
        for (String board : targetBoards) {
            // Trim() é importante para remover espaços acidentais no properties (ex: "nubank, airbnb")
            String cleanBoard = board.trim();

            try {
                String url = String.format(API_URL_TEMPLATE, cleanBoard);
                GreenhouseResponse response = restTemplate.getForObject(url, GreenhouseResponse.class);

                if (response != null && response.jobs() != null) {
                    response.jobs().stream()
                            .filter(dto -> matchesTechnology(dto, technology))
                            .map(dto -> toDomain(dto, cleanBoard))
                            .forEach(foundJobs::add);
                }

            } catch (Exception e) {
                // Log de erro melhorado para sabermos qual empresa falhou
                System.err.println("⚠️  Skipping board '" + cleanBoard + "': " + e.getMessage());
            }
        }
        return foundJobs;
    }

    // ... (restante dos métodos auxiliares e records mantêm-se iguais) ...

    private boolean matchesTechnology(JobDTO job, String tech) {
        return job.title() != null &&
                job.title().toLowerCase().contains(tech.toLowerCase());
    }

    private Job toDomain(JobDTO dto, String boardName) {
        return new Job(
                dto.title(),
                boardName.toUpperCase(), // Estética: Nome da empresa em Caps
                dto.url(),
                "Greenhouse API",
                LocalDateTime.now()
        );
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    record GreenhouseResponse(List<JobDTO> jobs) {}

    @JsonIgnoreProperties(ignoreUnknown = true)
    record JobDTO(String title, @JsonProperty("absolute_url") String url) {}
}