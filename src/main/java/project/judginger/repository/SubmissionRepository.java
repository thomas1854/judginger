package project.judginger.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import project.judginger.model.Submission;

public interface SubmissionRepository extends JpaRepository<Submission, Long> {
}
