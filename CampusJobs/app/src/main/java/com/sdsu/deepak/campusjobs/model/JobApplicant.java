package com.sdsu.deepak.campusjobs.model;


import java.io.Serializable;

/**
 * Model to store job applicant data
 * Created by Deepak on 5/1/2017.
 */

public class JobApplicant implements Serializable {

    public JobApplicant(){
    }
    public String userId;
    public String jobId;
    public String name;
    public String department;
    public String jobTitle;
    public String appliedOn;
}
