package project.judginger.model;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "problems")
public class Problem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long problemId;
    @Column(unique = true)
    private String problemKey;
    private String problemDescription;
    private final LocalDateTime createdAt;
    private Integer timeLimit;
    private Integer memoryLimit;

    public Problem()
    {
        this.createdAt = LocalDateTime.now();
    }

    public Problem(Long problemId, String problemKey, String problemDescription, Integer timeLimit, Integer memoryLimit) {
        this.problemId = problemId;
        this.problemKey = problemKey;
        this.problemDescription = problemDescription;
        this.createdAt = LocalDateTime.now();
        this.timeLimit = timeLimit;
        this.memoryLimit = memoryLimit;
    }

    public Long getProblemId() {
        return problemId;
    }

    public void setProblemId(Long problemId) {
        this.problemId = problemId;
    }

    public String getProblemKey() {
        return problemKey;
    }

    public void setProblemKey(String problemKey) {
        this.problemKey = problemKey;
    }

    public String getProblemDescription() {
        return problemDescription;
    }

    public void setProblemDescription(String problemDescription) {
        this.problemDescription = problemDescription;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public Integer getTimeLimit() {
        return timeLimit;
    }

    public void setTimeLimit(Integer timeLimit) {
        this.timeLimit = timeLimit;
    }

    public Integer getMemoryLimit() {
        return memoryLimit;
    }

    public void setMemoryLimit(Integer memoryLimit) {
        this.memoryLimit = memoryLimit;
    }
}
