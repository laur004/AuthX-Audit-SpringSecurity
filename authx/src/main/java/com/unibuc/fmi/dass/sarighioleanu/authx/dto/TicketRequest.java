package com.unibuc.fmi.dass.sarighioleanu.authx.dto;

import com.unibuc.fmi.dass.sarighioleanu.authx.model.TicketSeverityLevel;

public class TicketRequest {

    private String title;
    private String description;
    private TicketSeverityLevel severityLevel;

    public TicketRequest(){}

    public TicketRequest(String title, String description, TicketSeverityLevel severityLevel) {
        this.title = title;
        this.description = description;
        this.severityLevel = severityLevel;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public TicketSeverityLevel getSeverityLevel() {
        return severityLevel;
    }

    public void setSeverityLevel(TicketSeverityLevel severityLevel) {
        this.severityLevel = severityLevel;
    }
}
