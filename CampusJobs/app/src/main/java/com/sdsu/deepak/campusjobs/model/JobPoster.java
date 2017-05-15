package com.sdsu.deepak.campusjobs.model;

import java.io.Serializable;

/**
 * Used to store Job Poster details
 * Created by Deepak on 5/2/2017.
 */

public class JobPoster implements Serializable{
    public String jobId;
    public String postedOn;
    public String jobTitle;

    public JobPoster(String id,String postdate, String  title){
        this.jobId = id;
        this.postedOn =postdate;
        this.jobTitle = title;
    }

    public JobPoster(){
    }
}
