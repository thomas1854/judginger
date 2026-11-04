package project.judginger.dto;

import java.util.UUID;

public class SubmissionResponse
{
    private UUID submissionId;
    private String verdict;
    private String test;
    private Integer time;
    private Integer memory;

    public SubmissionResponse() {
    }

    public SubmissionResponse(UUID submissionId, String verdict, String test, Integer time, Integer memory) {
        this.submissionId = submissionId;
        this.verdict = verdict;
        this.test = test;
        this.time = time;
        this.memory = memory;
    }

    public UUID getSubmissionId() {
        return submissionId;
    }

    public void setSubmissionId(UUID submissionId) {
        this.submissionId = submissionId;
    }

    public String getTest() {
        return test;
    }

    public void setTest(String test) {
        this.test = test;
    }

    public String getVerdict() {
        return verdict;
    }

    public void setVerdict(String verdict) {
        this.verdict = verdict;
    }

    public Integer getTime() {
        return time;
    }

    public void setTime(Integer time) {
        this.time = time;
    }

    public Integer getMemory() {
        return memory;
    }

    public void setMemory(Integer memory) {
        this.memory = memory;
    }
}
