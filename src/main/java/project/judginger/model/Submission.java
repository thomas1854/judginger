package project.judginger.model;

import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "submissions")
public class Submission {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long submissionId;
    private Integer memoryLimit;    // Memory Limit in MB
    private Integer timeLimit;      // Time Limit in seconds
    private String verdict;         // Final verdict of the submission
    private String test;            // The wrong test
    private String executionTime;   // Execution time in seconds
    private String executionMemory; // Execution memory in kilobytes
    final private LocalDateTime submittedAt;

    public Submission() {
        this.submittedAt = LocalDateTime.now();
    }

    public Submission(Long submissionId, Integer memoryLimit, Integer timeLimit, String verdict, String test, String executionTime, String executionMemory) {
        this.submissionId = submissionId;
        this.memoryLimit = memoryLimit;
        this.timeLimit = timeLimit;
        this.verdict = verdict;
        this.test = test;
        this.executionTime = executionTime;
        this.executionMemory = executionMemory;
        this.submittedAt = LocalDateTime.now();
    }

    public Long getSubmissionId() {
        return submissionId;
    }

    public void setSubmissionId(Long submissionId) {
        this.submissionId = submissionId;
    }

    public Integer getMemoryLimit() {
        return memoryLimit;
    }

    public void setMemoryLimit(Integer memoryLimit) {
        this.memoryLimit = memoryLimit;
    }

    public Integer getTimeLimit() {
        return timeLimit;
    }

    public void setTimeLimit(Integer timeLimit) {
        this.timeLimit = timeLimit;
    }

    public String getVerdict() {
        return verdict;
    }

    public void setVerdict(String verdict) {
        this.verdict = verdict;
    }

    public String getTest() {
        return test;
    }

    public void setTest(String test) {
        this.test = test;
    }

    public String getExecutionTime() {
        return executionTime;
    }

    public void setExecutionTime(String executionTime) {
        this.executionTime = executionTime;
    }

    public String getExecutionMemory() {
        return executionMemory;
    }

    public void setExecutionMemory(String executionMemory) {
        this.executionMemory = executionMemory;
    }

    public LocalDateTime getSubmittedAt() {
        return submittedAt;
    }
}