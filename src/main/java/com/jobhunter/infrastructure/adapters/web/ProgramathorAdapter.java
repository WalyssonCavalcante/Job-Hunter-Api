package com.jobhunter.infrastructure.adapters.web;

import com.jobhunter.domain.model.Job;
import com.jobhunter.domain.ports.outgoing.JobProvider;
import com.microsoft.playwright.*;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Component
public class ProgramathorAdapter implements JobProvider {

    private static final String SEARCH_URL = "https://programathor.com.br/jobs?term=%s";

    @Override
    public List<Job> fetchJobs(String technology) {
        List<Job> jobs = new ArrayList<>();
        System.out.println("🕷️ Starting scraping on Programathor for: " + technology);

        try (Playwright playwright = Playwright.create()) {
            Browser browser = playwright.chromium().launch(new BrowserType.LaunchOptions().setHeadless(true));
            Page page = browser.newPage();

            String url = String.format(SEARCH_URL, technology);
            page.navigate(url);

            List<ElementHandle> elements = page.querySelectorAll(".cell-list > a");

            for (ElementHandle element : elements) {
                try {
                    String rawText = element.innerText();
                    String[] parts = rawText.split("\n");

                    String title = parts.length > 0 ? parts[0] : "Unknown Title";
                    String company = parts.length > 1 ? parts[1] : "Programathor Job";

                    String relativeLink = element.getAttribute("href");
                    String fullUrl = "https://programathor.com.br" + relativeLink;

                    jobs.add(new Job(
                            title,
                            company,
                            fullUrl,
                            "Programathor (Web)",
                            LocalDateTime.now()
                    ));

                } catch (Exception e) {
                }
            }
        } catch (Exception e) {
            System.err.println("Error scraping Programathor: " + e.getMessage());
        }

        return jobs;
    }
}