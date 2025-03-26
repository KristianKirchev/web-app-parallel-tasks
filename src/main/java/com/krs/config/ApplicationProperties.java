package com.krs.config;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

@Data
public class ApplicationProperties {

    @Positive
    @NotNull
    private Integer maxThreads;
}
