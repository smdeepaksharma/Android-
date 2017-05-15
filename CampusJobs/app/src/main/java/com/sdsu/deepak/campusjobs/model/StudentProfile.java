package com.sdsu.deepak.campusjobs.model;

import com.sdsu.deepak.campusjobs.EditExperienceDetails;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

/**
 * Model class used to store student profile data
 * Created by Deepak on 4/24/2017.
 */

public class StudentProfile implements Serializable{

    private String studentId;
    private String name;
    private String emailId;
    private String contactNumber;
    private ArrayList<String> skills;
    private HashMap<String,Education> educationDetails;
    private HashMap<String,Experience> employmentDetails;

    public StudentProfile(){
        // required empty
    }

    public StudentProfile(String name, String emailId, String phone){
        this.setName(name);
        this.setContactNumber(phone);
        this.setEmailId(emailId);
    }

    public String getStudentId() {
        return studentId;
    }

    public void setStudentId(String studentId) {
        this.studentId = studentId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmailId() {
        return emailId;
    }

    public void setEmailId(String emailId) {
        this.emailId = emailId;
    }

    public String getContactNumber() {
        return contactNumber;
    }

    private void setContactNumber(String contactNumber) {
        this.contactNumber = contactNumber;
    }

    public ArrayList<String> getSkills() {
        return skills;
    }

    public void setSkills(ArrayList<String> skills) {
        this.skills = skills;
    }

    public void setEducationDetails(HashMap<String, Education> educationDetails) {
        this.educationDetails = educationDetails;
    }

    public HashMap<String, Education> getEducationDetails() {
        return educationDetails;
    }

    public HashMap<String, Experience> getEmploymentDetails() {
        return employmentDetails;
    }

    public void setEmploymentDetails(HashMap<String, Experience> employmentDetails) {
        this.employmentDetails = employmentDetails;
    }

}
