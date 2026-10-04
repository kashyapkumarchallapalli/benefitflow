package com.benefitflow.member_service.service;

import com.benefitflow.member_service.api.CreateMemberRequest;
import com.benefitflow.member_service.api.MemberResponse;
import com.benefitflow.member_service.domain.Member;
import com.benefitflow.member_service.exception.DuplicateEmailException;
import com.benefitflow.member_service.exception.DuplicateMemberException;
import com.benefitflow.member_service.exception.MemberNotFoundException;
import com.benefitflow.member_service.repository.MemberRepository;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

@Service
public class MemberService {

    private final MemberRepository memberRepository;

    public MemberService(MemberRepository memberRepository) {
        this.memberRepository = memberRepository;
    }

    public MemberResponse getByMemberId(String memberId) {
        return memberRepository.findByMemberId(memberId)
                .map(this::getMemberDetails)
                .orElseThrow(() -> new MemberNotFoundException(memberId));
    }

    @Transactional   // org.springframework.transaction.annotation.Transactional
    public MemberResponse create(CreateMemberRequest req) {
        if (memberRepository.existsByMemberId(req.memberId())) {          // fast path: friendly error for the common case
            throw new DuplicateMemberException(req.memberId());
        }
        if(memberRepository.existsByEmail(req.email())) {                // fast path: friendly error for the common case
            throw new DuplicateEmailException(req.email());
        }
        try {
            Member saved = memberRepository.saveAndFlush(
                    Member.create(req.memberId(), req.fullName(), req.email(), req.dateOfBirth()));
            return getMemberDetails(saved);
        } catch (DataIntegrityViolationException e) {                       // the race: DB constraint is the real guard
            throw new DuplicateMemberException(req.memberId());
        }
    }

    public MemberResponse getMemberDetails(Member member) {
        return new MemberResponse(
                member.getMemberId(),
                member.getFullName(),
                member.getEmail(),
                member.getDateOfBirth().toString(),
                member.getStatus().name(),
                member.getCreatedAt().toString()
        );
    }
}
