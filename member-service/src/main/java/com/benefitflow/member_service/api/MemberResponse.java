package com.benefitflow.member_service.api;

import java.time.Instant;

public record MemberResponse(
        String memberId,
        String fullName,
        String email,
        String dateOfBirth,
        String status,
        Instant createdAt
) {
}
