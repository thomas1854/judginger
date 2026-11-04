package project.judginger.services;

import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import project.judginger.exception.InvalidFileTypeException;
import project.judginger.exception.ProblemBuildException;
import project.judginger.exception.ProblemKeyExistsException;
import project.judginger.model.Problem;
import project.judginger.repository.ProblemRepository;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class ProblemService {
    private final ProblemRepository problemRepository;

    public ProblemService(ProblemRepository problemRepository) {
        this.problemRepository = problemRepository;
    }

    private void buildProblem(String problemKey, Integer timeLimit, Integer memoryLimit) throws IOException, InterruptedException {
        ProcessBuilder processBuilder = new ProcessBuilder("bash" , "build-problem.sh", problemKey, timeLimit.toString(), memoryLimit.toString());
        processBuilder.directory(new File("/home/thomas/thomas/judginger/judging"));
        processBuilder.redirectErrorStream(true);

        Process process = processBuilder.start();

        int exitCode = process.waitFor();

        if (exitCode != 0)
        {
            destroyProblemDirectory(problemKey);
            String verdict = new String(process.getInputStream().readAllBytes()).split("-")[0].trim();
            throw new ProblemBuildException("your main solution got " + verdict);
        }
    }

    private void buildProblemDirectory(String problemKey, MultipartFile mainSolutionFile, MultipartFile[] inputFiles) throws IOException {
        Path problemDirectory = Paths.get("/home/thomas/thomas/judginger/judging/problems/" + problemKey);
        if (!Files.exists(problemDirectory))
            Files.createDirectory(problemDirectory);

        Path mainSolutionFilePath = problemDirectory.resolve("main.cpp");
        mainSolutionFile.transferTo(mainSolutionFilePath.toFile());

        Path problemInputFilesDirectory = Paths.get("/home/thomas/thomas/judginger/judging/problems/" + problemKey + "/inputs");
        if (!Files.exists(problemInputFilesDirectory))
            Files.createDirectory(problemInputFilesDirectory);

        Integer testNumber = 1;
        for (MultipartFile inputFile : inputFiles)
        {
            Path inputFilePath = problemInputFilesDirectory.resolve(testNumber + ".in");
            inputFile.transferTo(inputFilePath.toFile());
            testNumber++;
        }
    }

    private void destroyProblemDirectory(String problemKey) throws IOException {
        ProcessBuilder processBuilder = new ProcessBuilder("rm" , "-rf", problemKey);
        processBuilder.directory(new File("/home/thomas/thomas/judginger/judging/problems"));
        processBuilder.redirectErrorStream(true);

        processBuilder.start();
    }

    public Problem createProblem(Problem problem, MultipartFile mainSolutionFile, MultipartFile[] inputFiles) throws IOException, InterruptedException {
        if (problemRepository.existsByProblemKey(problem.getProblemKey()))
            throw new ProblemKeyExistsException("Problem key already exists.");

        if (!"cpp".equalsIgnoreCase(StringUtils.getFilenameExtension(mainSolutionFile.getOriginalFilename())))
            throw new InvalidFileTypeException("Main solution file must have the .cpp extension.");

        for (MultipartFile inputFile : inputFiles)
        {
            if (inputFile.isEmpty())
                throw new InvalidFileTypeException("Input files must not be empty.");

            if (!"in".equalsIgnoreCase(StringUtils.getFilenameExtension(inputFile.getOriginalFilename())))
                throw new InvalidFileTypeException("Input files must have the .in extension.");
        }

        buildProblemDirectory(problem.getProblemKey(), mainSolutionFile, inputFiles);

        buildProblem(problem.getProblemKey(), problem.getTimeLimit(), problem.getMemoryLimit());

        return problemRepository.save(problem);
    }


    public List<Problem> getAllProblems()
    {
        return problemRepository.findAll();
    }
}
