package project.judginger.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record SubmissionRequest(
        @NotNull(message = "time limit value must be provided") @Max(value = 20) @Min(value = 1, message = "time limit value must be between 1 and 20 seconds")
                                Integer time_limit,
        @NotNull(message = "memory limit value must be provided") @Max(value = 1024) @Min(value = 256, message = "memory limit value must be between 256 and 1024 megabytes")
                                Integer memory_limit) {
}
