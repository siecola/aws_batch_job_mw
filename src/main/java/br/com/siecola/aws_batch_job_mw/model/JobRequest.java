package br.com.siecola.aws_batch_job_mw.model;

import br.com.siecola.aws_batch_job_mw.enums.JobType;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import javax.validation.constraints.NotNull;

@JsonIgnoreProperties(ignoreUnknown = true)
public class JobRequest {

    @NotNull
    private JobType jobType;

    @NotNull
    private String username;

    public JobType getJobType() {
        return jobType;
    }

    public void setJobType(JobType jobType) {
        this.jobType = jobType;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}