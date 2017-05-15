package com.sdsu.deepak.campusjobs.model;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Job Listing class
 * Created by Deepak on 4/23/2017.
 */

public class JobListingModel implements Serializable{

    private String jobId;
    private String jobTitle;
    private String jobTitleSortVersion;
    private String department;
    private String departmentSortVersion;
    private String jobdescription;
    private String skillsRequired;
    private String pay;
    private String contactPersonName;
    private String contactPersonEmail;
    private String contactPersonPhone;

    public String getJobId() {
        return jobId;
    }

    public void setJobId(String jobId) {
        this.jobId = jobId;
    }

    public JobListingModel() {
        // Required Empty Constructor
    }

    public String getJobTitle() {
        return jobTitle;
    }

    public void setJobTitle(String jobTitle) {
        this.jobTitle = jobTitle;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public String getPay() {
        return pay;
    }

    public void setPay(String pay) {
        this.pay = pay;
    }

    public String getContactPersonName() {
        return contactPersonName;
    }

    public void setContactPersonName(String contactPersonName) {
        this.contactPersonName = contactPersonName;
    }

    public String getContactPersonPhone() {
        return contactPersonPhone;
    }

    public void setContactPersonPhone(String contactPersonPhone) {
        this.contactPersonPhone = contactPersonPhone;
    }

    public String getJobdescription() {
        return jobdescription;
    }

    public void setJobdescription(String jobdescription) {
        this.jobdescription = jobdescription;
    }

    public String getSkillsRequired() {
        return skillsRequired;
    }

    public void setSkillsRequired(String skillsRequired) {
        this.skillsRequired = skillsRequired;
    }

    public String getContactPersonEmail() {
        return contactPersonEmail;
    }

    public void setContactPersonEmail(String contactPersonEmail) {
        this.contactPersonEmail = contactPersonEmail;
    }

    public String getJobTitleSortVersion() {
        return jobTitleSortVersion;
    }

    public void setJobTitleSortVersion(String jobTitleSortVersion) {
        this.jobTitleSortVersion = jobTitleSortVersion;
    }

    public String getDepartmentSortVersion() {
        return departmentSortVersion;
    }

    public void setDepartmentSortVersion(String departmentSortVersion) {
        this.departmentSortVersion = departmentSortVersion;
    }
}
