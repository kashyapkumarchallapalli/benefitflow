package com.benefitflow.member_service.api;

import java.time.LocalDate;

public record CreateMemberRequest(
    String memberId,
    String fullName,
    String email,
    LocalDate dateOfBirth
) {

}