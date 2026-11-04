package project.judginger.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler
{
    @ResponseStatus(HttpStatus.CONFLICT)
    @ExceptionHandler(ProblemKeyExistsException.class)
    public Map<String, String> handleProblemKeyExistsException(ProblemKeyExistsException problemKeyExistsException)
    {
        return Map.of("error", problemKeyExistsException.getMessage());
    }

    @ResponseStatus(HttpStatus.UNPROCESSABLE_CONTENT)
    @ExceptionHandler(ProblemBuildException.class)
    public Map<String, String> handleProblemBuildException(ProblemBuildException problemBuildException)
    {
        return Map.of("error", problemBuildException.getMessage());
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(InvalidFileTypeException.class)
    public Map<String, String> handleProblemBuildException(InvalidFileTypeException invalidFileTypeException)
    {
        return Map.of("error", invalidFileTypeException.getMessage());
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(InvalidFileException.class)
    public Map<String, String> handleProblemBuildException(InvalidFileException invalidFileException)
    {
        return Map.of("error", invalidFileException.getMessage());
    }

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(ResourceNotFoundException.class)
    public Map<String, String> handleResourceNotFoundException(ResourceNotFoundException resourceNotFoundException)
    {
        return Map.of("error", resourceNotFoundException.getMessage());
    }
}
