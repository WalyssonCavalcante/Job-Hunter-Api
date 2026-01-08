package com.jobhunter.infrastructure.adapters.web;

import com.jobhunter.domain.model.Job;
import com.jobhunter.domain.ports.outgoing.JobProvider;
import com.microsoft.playwright.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Component
public class WeWorkRemotelyAdapter implements JobProvider {

    private static final Logger logger = LoggerFactory.getLogger(WeWorkRemotelyAdapter.class);
    private static final String SEARCH_URL = "https://weworkremotely.com/remote-jobs/search?term=%s";

    @Override
    public List<Job> fetchJobs(String technology) {
        List<Job> jobs = new ArrayList<>();
        logger.info("🕷️ Scraping WeWorkRemotely for: {}", technology);

        try (Playwright playwright = Playwright.create()) {
            Browser browser = playwright.chromium().launch(new BrowserType.LaunchOptions().setHeadless(true));
            Page page = browser.newPage();

            page.navigate(String.format(SEARCH_URL, technology));

            // O container de vagas são seções <section class="jobs">
            // Dentro tem <ul> e <li>
            List<ElementHandle> jobItems = page.querySelectorAll("section.jobs li.feature");

            for (ElementHandle item : jobItems) {
                try {
                    // WWR tem links que apontam para o site deles, e de lá redirecionam
                    ElementHandle linkEl = item.querySelector("a");

                    if (linkEl != null) {
                        // Extração de dados
                        String title = item.querySelector(".title").innerText();
                        String company = item.querySelector(".company").innerText();
                        String relativeLink = linkEl.getAttribute("href");
                        String fullUrl = "https://weworkremotely.com" + relativeLink;

                        jobs.add(new Job(
                                title,
                                company,
                                fullUrl,
                                "WeWorkRemotely",
                                LocalDateTime.now()
                        ));
                    }
                } catch (Exception e) {
                }
            }
            logger.info("WWR found {} jobs", jobs.size());

        } catch (Exception e) {
            logger.error("Error scraping WWR: {}", e.getMessage());
        }

        return jobs;
    }
}