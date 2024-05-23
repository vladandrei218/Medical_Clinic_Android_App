// Request.java
package com.example.medicalclinic;

public class Request {
    private String emailTo;
    private String emailFrom;
    private String status;
    private String role;

    public Request() {
        // Public no-argument constructor needed for Firestore
    }

    public Request(String emailTo, String emailFrom, String status) {
        this.emailTo = emailTo;
        this.emailFrom = emailFrom;
        this.status = status;
    }

    public String getEmailTo() {
        return emailTo;
    }

    public void setEmailTo(String emailTo) {
        this.emailTo = emailTo;
    }

    public String getEmailFrom() {
        return emailFrom;
    }

    public void setEmailFrom(String emailFrom) {
        this.emailFrom = emailFrom;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
    public String getRole() {
        return role;
    }
    public void setRole(String role) {
        this.role = role;
    }
}
