package com.benefitflow.member_service.exception;

public class DuplicateMemberException extends RuntimeException {
    public DuplicateMemberException(String memberId) {
        super("Member already exists with memberId: " + memberId);
    }
}
