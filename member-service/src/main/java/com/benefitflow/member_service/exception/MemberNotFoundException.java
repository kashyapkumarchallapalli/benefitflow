package com.benefitflow.member_service.exception;

public class MemberNotFoundException extends RuntimeException {

    public MemberNotFoundException(String memberId) {
        super("Member not found with memberId: " + memberId);
    }
}
