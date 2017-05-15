package com.sdsu.deepak.campusjobs.model;

import java.io.Serializable;

/**
 * Model to store experiecne details of students
 * Created by Deepak on 4/28/2017.
 */

public class Experience implements Serializable{
        public String title;
        public String startDate;
        public String endDate;
        public String companyName;
        public boolean isCurrentEmployment;
    }

