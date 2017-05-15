package com.sdsu.deepak.campusjobs.model;

import java.io.Serializable;

/**
 * Model to store education details of students
 * Created by Deepak on 4/28/2017.
 */

public class Education implements Serializable {
    public String schoolName;
    public String startDate;
    public String endDate;
    public String major;
    public String degree;
    public String gpa;
    public boolean inProgress;
    public Education(){
    }
}