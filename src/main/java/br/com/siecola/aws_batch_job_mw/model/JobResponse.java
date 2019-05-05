package br.com.siecola.aws_batch_job_mw.model;

import br.com.siecola.aws_batch_job_mw.enums.JobStatus;
import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class JobResponse {

    private String jobId;
    private String url;
    private long expiresInSeconds;
    private int numberOfRegisters;
    private JobStatus status;

    public String getJobId() {
        return jobId;
    }

    public void setJobId(String jobId) {
        this.jobId = jobId;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public long getExpiresInSeconds() {
        return expiresInSeconds;
    }

    public void setExpiresInSeconds(long expiresInSeconds) {
        this.expiresInSeconds = expiresInSeconds;
    }

    public int getNumberOfRegisters() {
        return numberOfRegisters;
    }

    public void setNumberOfRegisters(int numberOfRegisters) {
        this.numberOfRegisters = numberOfRegisters;
    }

    public JobStatus getStatus() {
        return status;
    }

    public void setStatus(JobStatus status) {
        this.status = status;
    }
}