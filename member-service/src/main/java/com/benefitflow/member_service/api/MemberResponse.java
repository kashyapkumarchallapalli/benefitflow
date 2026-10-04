package com.benefitflow.member_service.api;

public record MemberResponse(
        String memberId,
        String fullName,
        String email,
        String dateOfBirth,
        String status,
        String createdAt
) {
}
