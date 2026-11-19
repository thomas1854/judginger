package project.judginger.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler
{
    @ResponseStatus(HttpStatus.CONFLICT)
    @ExceptionHandler(ProblemKeyExistsException.class)
    public Map<String, String> handleProblemKeyExistsException(ProblemKeyExistsException problemKeyExistsException)
    {
        return Map.of("code", "problem-key-exists", "message", problemKeyExistsException.getMessage());
    }

    @ResponseStatus(HttpStatus.UNPROCESSABLE_CONTENT)
    @ExceptionHandler(ProblemBuildException.class)
    public Map<String, String> handleProblemBuildException(ProblemBuildException problemBuildException)
    {
        return Map.of("code", "invalid-problem-file", "message", problemBuildException.getMessage());
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(InvalidFileTypeException.class)
    public Map<String, String> handleInvalidFileTypeException(InvalidFileTypeException invalidFileTypeException)
    {
        return Map.of("code", "invalid-file-type", "message", invalidFileTypeException.getMessage());
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(InvalidFileException.class)
    public Map<String, String> handleInvalidFileException(InvalidFileException invalidFileException)
    {
        return Map.of("code", "invalid-file", "message", invalidFileException.getMessage());
    }

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(ResourceNotFoundException.class)
    public Map<String, String> handleResourceNotFoundException(ResourceNotFoundException resourceNotFoundException)
    {
        return Map.of("code", "resource-not-found", "message", resourceNotFoundException.getMessage());
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Map<String, String> handleMethodArgumentNotValidException(MethodArgumentNotValidException exception) {
        Map<String, String> errors = new HashMap<>();
        errors.put("code", "argument-not-valid");
        exception.getBindingResult().getFieldErrors().forEach((error) -> {
            errors.put(error.getField(), error.getDefaultMessage());
        });
        return errors;
    }


    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(FileSizeLimitExceededException.class)
    public Map<String, String> handleFileSizeLimitExceededException(FileSizeLimitExceededException fileSizeLimitExceededException)
    {
        return Map.of("code", "file-size-limit-exceeded", "message", fileSizeLimitExceededException.getMessage());
    }
}
