package project.judginger.controller;

import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import project.judginger.dto.SubmissionRequest;
import project.judginger.dto.SubmissionResponse;
import project.judginger.services.SubmissionService;

import java.io.IOException;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/judge")
public class SubmissionController {
    private final SubmissionService submissionService;

    public SubmissionController(SubmissionService submissionService) {
        this.submissionService = submissionService;
    }


    @PostMapping("/problems/{problemId}/submissions")
    SubmissionResponse submit(@PathVariable String problemId, @RequestPart("submission") SubmissionRequest submissionRequest, @RequestPart("file") MultipartFile submissionFile) throws IOException, InterruptedException {
        return submissionService.submit(submissionRequest, problemId, submissionFile);
    }
}
