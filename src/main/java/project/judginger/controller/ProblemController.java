package project.judginger.controller;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import project.judginger.model.Problem;
import project.judginger.services.ProblemService;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@CrossOrigin(origins = "*")
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

    @GetMapping("")
    List<Problem> getAllProblems()
    {
        return problemService.getAllProblems();
    }

    @GetMapping("{problemKey}")
    Problem getProblem(@PathVariable String problemKey)
    {
        return problemService.getProblem(problemKey);
    }

    @DeleteMapping("{problemKey}")
    Map<String, String> deleteProblem(@PathVariable String problemKey) throws IOException {
         problemService.deleteProblem(problemKey);
         return Map.of("message", "Deleted problem " + problemKey + " successfully.");
    }
}
