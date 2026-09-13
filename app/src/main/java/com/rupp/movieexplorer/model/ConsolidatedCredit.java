package com.rupp.movieexplorer.model;

import java.util.List;

public class ConsolidatedCredit {
    private PersonDetails personDetails;
    private List<String> jobs;

   public ConsolidatedCredit(PersonDetails personDetails, List<String> jobs){
       this.personDetails = personDetails;
       this.jobs = jobs;
   }

    public List<String> getJobs() {
        return jobs;
    }

    public String getFormattedJobs() {
        return String.join("/", jobs) ;
    }

    public PersonDetails getPersonDetails() {
        return personDetails;
    }

}
