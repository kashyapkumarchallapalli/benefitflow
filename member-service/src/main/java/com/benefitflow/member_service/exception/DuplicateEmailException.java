package com.benefitflow.member_service.exception;

public class DuplicateEmailException extends RuntimeException{
    public DuplicateEmailException(String email) {
        super("Member with email " + email + " already exists.");
    }
}
