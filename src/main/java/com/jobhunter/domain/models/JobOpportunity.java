package com.jobhunter.domain.models;

public class JobOpportunity {
    private String title;
    private String company;
    private String url;

    public JobOpportunity(String title, String company, String url) {
        this.title = title;
        this.company = company;
        this.url = url;
    }

    // Getters
    public String getTitle() { return title; }
    public String getCompany() { return company; }
    public String getUrl() { return url; }

    @Override
    public String toString() {
        return "JobOpportunity{title='" + title + "', company='" + company + "'}";
    }
}