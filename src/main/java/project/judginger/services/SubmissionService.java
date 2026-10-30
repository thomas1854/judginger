package project.judginger.services;

import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import project.judginger.dto.SubmissionRequest;
import project.judginger.dto.SubmissionResponse;
import project.judginger.exception.InvalidFileTypeException;
import project.judginger.model.Submission;
import project.judginger.repository.SubmissionRepository;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@Service
public class SubmissionService {
    private final SubmissionRepository submissionRepository;

    public SubmissionService(SubmissionRepository submissionRepository) {
        this.submissionRepository = submissionRepository;
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
        UUID submissionId = UUID.randomUUID();

        if (!"cpp".equalsIgnoreCase(StringUtils.getFilenameExtension(submissionFile.getOriginalFilename())))
            throw new InvalidFileTypeException("Submitted solution file must have the .cpp extension.");

        buildSubmissionDirectory(submissionId, submissionFile);

        String output = run(submissionId, problemId, submissionRequest.timeLimit(), submissionRequest.memoryLimit());

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
            submissionResponse.setTime(stats[1].trim());
            submissionResponse.setMemory(stats[2].trim());
        }
        else
        {
            submissionResponse.setVerdict(stats[0].trim());
            submissionResponse.setTest(stats[1].trim());
            submissionResponse.setTime(stats[2].trim());
            submissionResponse.setMemory(stats[3].trim());
        }

        Submission submission = new Submission();
        submission.setExecutionTime(submissionResponse.getTime());
        submission.setExecutionMemory(submissionResponse.getMemory());
        submission.setMemoryLimit(submissionRequest.memoryLimit());
        submission.setTimeLimit(submissionRequest.timeLimit());
        submission.setVerdict(submissionResponse.getVerdict());
        submission.setTest(submissionResponse.getTest());


        submissionRepository.save(submission);
        return submissionResponse;
    }
}
