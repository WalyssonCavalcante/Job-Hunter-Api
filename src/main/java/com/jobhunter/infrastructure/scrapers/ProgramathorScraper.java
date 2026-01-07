package com.jobhunter.infrastructure.scrapers;

import com.jobhunter.domain.models.JobOpportunity;
import com.jobhunter.domain.ports.JobScraper;
import com.microsoft.playwright.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class ProgramathorScraper implements JobScraper {

    private static final Logger logger = LoggerFactory.getLogger(ProgramathorScraper.class);
    private static final String SEARCH_URL = "https://programathor.com.br/jobs?term=%s";

    @Override
    public List<JobOpportunity> scrape(String technology) {
        List<JobOpportunity> jobs = new ArrayList<>();
        logger.info("Starting Programathor scraping for: {}", technology);

        try (Playwright playwright = Playwright.create()) {
            Browser browser = playwright.chromium().launch(new BrowserType.LaunchOptions()
                    .setHeadless(true));

            BrowserContext context = browser.newContext();
            Page page = context.newPage();

            String url = String.format(SEARCH_URL, technology);
            page.navigate(url);

            List<String> jobLinks = new ArrayList<>();
            List<ElementHandle> linkElements = page.querySelectorAll(".cell-list > a");

            for (ElementHandle link : linkElements) {
                String href = link.getAttribute("href");
                if (href != null && !href.isEmpty()) {
                    jobLinks.add("https://programathor.com.br" + href);
                }
            }

            logger.info("Found {} potential jobs. Visiting details for accuracy...", jobLinks.size());

            for (String link : jobLinks) {
                try {
                    page.navigate(link);

                    String title = "Unknown Title";
                    if (page.isVisible("h1")) {
                        title = page.locator("h1").first().innerText().trim();
                    }

                    String company = "Confidential";

                    Locator companyLocator = page.locator(".wrapper-content-job-show h2 a").first();

                    if (companyLocator.count() == 0) {
                        companyLocator = page.locator(".wrapper-content-job-show h2").first();
                    }

                    if (companyLocator.count() > 0) {
                        company = companyLocator.innerText().trim();
                    }

                    if (!title.equalsIgnoreCase("Unknown Title")) {
                        jobs.add(new JobOpportunity(title, company, link));
                        logger.info("Scraped: {} at {}", title, company);
                    }

                } catch (Exception e) {
                    logger.error("Error scraping specific job details: " + link, e);
                }
            }

        } catch (Exception e) {
            logger.error("Critical error in Programathor scraper", e);
        }

        return jobs;
    }
}