package project.judginger.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import project.judginger.model.Problem;

import java.util.Optional;

@Repository
public interface ProblemRepository extends JpaRepository<Problem, Long> {
    Optional<Problem> findByProblemKey(String problemKey);
    boolean existsByProblemKey(String problemKey);
}
