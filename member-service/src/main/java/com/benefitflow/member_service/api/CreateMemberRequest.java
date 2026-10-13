package com.benefitflow.member_service.api;

import jakarta.validation.constraints.*;

import java.time.LocalDate;

public record CreateMemberRequest(
    @NotBlank
    @Pattern(regexp = "^M[0-9]+$", message = "must be M followed by digits")
    @Size(max = 20, message = "must be at most 20 characters")
    String memberId,
    @NotBlank
    @Size(max = 100)
    String fullName,
    @NotBlank
    @Email
    String email,
    @Past
    @NotNull
    LocalDate dateOfBirth
) {

}