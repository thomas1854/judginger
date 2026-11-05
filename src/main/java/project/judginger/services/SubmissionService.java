package project.judginger.services;

import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import project.judginger.dto.SubmissionRequest;
import project.judginger.dto.SubmissionResponse;
import project.judginger.exception.FileSizeLimitExceededException;
import project.judginger.exception.InvalidFileTypeException;
import project.judginger.exception.ResourceNotFoundException;
import project.judginger.model.Submission;
import project.judginger.repository.ProblemRepository;
import project.judginger.repository.SubmissionRepository;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;

@Service
public class SubmissionService {
    private final SubmissionRepository submissionRepository;
    private final ProblemRepository problemRepository;
    private static final int MAX_SUBMISSION_SIZE = 2 * 1024 * 1024; // 2 MB


    public SubmissionService(SubmissionRepository submissionRepository, ProblemRepository problemRepository) {
        this.submissionRepository = submissionRepository;
        this.problemRepository = problemRepository;
    }

    private String run(UUID submissionId, String problemId, Integer timeLimit, Integer memoryLimit) throws IOException, InterruptedException {
        ProcessBuilder processBuilder = new ProcessBuilder("bash" , "run-script.sh", submissionId.toString(), problemId, timeLimit.toString(), memoryLimit.toString());
        processBuilder.directory(new File("/home/thomas/thomas/judginger/judging"));
        processBuilder.redirectErrorStream(true);
        Process process = processBuilder.start();

        String output = new String(process.getInputStream().readAllBytes());

        int exitCode = process.waitFor();

        ProcessBuilder cleanUpProcessBuilder = new ProcessBuilder("rm" , "-rf", "/home/thomas/thomas/judginger/judging/submissions/" + submissionId);
        cleanUpProcessBuilder.directory(new File("/home/thomas/thomas/judginger/judging"));
        cleanUpProcessBuilder.redirectErrorStream(true);
        cleanUpProcessBuilder.start();
        return output;
    }

    private void buildSubmissionDirectory(UUID uuid, MultipartFile submissionFile) throws IOException {
        Path uploadPath = Paths.get("/home/thomas/thomas/judginger/judging/submissions/" + uuid);
        if (!Files.exists(uploadPath))
            Files.createDirectory(uploadPath);

        Path filePath = uploadPath.resolve(uuid + ".cpp");
        submissionFile.transferTo(filePath.toFile());
    }

    public SubmissionResponse submit(SubmissionRequest submissionRequest, String problemId, MultipartFile submissionFile) throws IOException, InterruptedException {
        if (!problemRepository.existsByProblemKey(problemId))
            throw new ResourceNotFoundException("problem key " + problemId + " is not found");

        if (!"cpp".equalsIgnoreCase(StringUtils.getFilenameExtension(submissionFile.getOriginalFilename())))
            throw new InvalidFileTypeException("Submitted solution file must have the .cpp extension.");

        if (submissionFile.getSize() > MAX_SUBMISSION_SIZE)
            throw new FileSizeLimitExceededException("Submission file size must not exceed 2 MB.");

        UUID submissionId = UUID.randomUUID();

        buildSubmissionDirectory(submissionId, submissionFile);

        String output = run(submissionId, problemId, submissionRequest.time_limit(), submissionRequest.memory_limit());

        SubmissionResponse submissionResponse = new SubmissionResponse();
        submissionResponse.setSubmissionId(submissionId);

        String[] stats = output.split("-");

        if (stats.length == 2)
        {
            submissionResponse.setVerdict(stats[0]);
            submissionResponse.setTest(stats[1].trim());
        }
        else if (stats.length == 3)
        {
            submissionResponse.setVerdict(stats[0].trim());
            submissionResponse.setTime((int) (Double.parseDouble(stats[1].trim()) * 1000));
            submissionResponse.setMemory(Integer.parseInt(stats[2].trim()));
        }
        else
        {
            submissionResponse.setVerdict(stats[0].trim());
            submissionResponse.setTest(stats[1].trim());
            submissionResponse.setTime((int) (Double.parseDouble(stats[2].trim()) * 1000));
            submissionResponse.setMemory(Integer.parseInt(stats[3].trim()));
        }

        Submission submission = new Submission();
        submission.setExecutionTime(submissionResponse.getTime());
        submission.setExecutionMemory(submissionResponse.getMemory());
        submission.setMemoryLimit(submissionRequest.memory_limit());
        submission.setTimeLimit(submissionRequest.time_limit());
        submission.setVerdict(submissionResponse.getVerdict());
        submission.setTest(submissionResponse.getTest());
        submission.setProblemKey(problemId);

        submissionRepository.save(submission);
        return submissionResponse;
    }

    public List<Submission> getAllSubmissions() {
        return submissionRepository.findAll();
    }
}
