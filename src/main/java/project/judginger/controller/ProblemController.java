package project.judginger.controller;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import project.judginger.model.Problem;
import project.judginger.services.ProblemService;

import java.io.IOException;

@RestController
@RequestMapping("/problems")
public class ProblemController {
    private final ProblemService problemService;

    public ProblemController(ProblemService problemService) {
        this.problemService = problemService;
    }

    @PostMapping(value = "", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    Problem createProblem(@RequestPart("problem")Problem problem, @RequestPart("main-solution") MultipartFile mainSolution, @RequestPart("input-files") MultipartFile[] inputFiles) throws IOException, InterruptedException {
        return problemService.createProblem(problem, mainSolution, inputFiles);
    }
}
