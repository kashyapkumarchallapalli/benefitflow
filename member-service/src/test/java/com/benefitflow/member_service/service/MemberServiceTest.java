package com.benefitflow.member_service.service;

import com.benefitflow.member_service.api.CreateMemberRequest;
import com.benefitflow.member_service.api.MemberResponse;
import com.benefitflow.member_service.domain.Member;
import com.benefitflow.member_service.domain.MemberStatus;
import com.benefitflow.member_service.exception.DuplicateEmailException;
import com.benefitflow.member_service.exception.DuplicateMemberException;
import com.benefitflow.member_service.exception.MemberNotFoundException;
import com.benefitflow.member_service.repository.MemberRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;

import java.time.LocalDate;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)          // Mockito only, no Spring context
class MemberServiceTest {

    @Mock
    MemberRepository memberRepository;        // fake repository we control

    MemberService memberService;

    CreateMemberRequest request = new CreateMemberRequest(
            "M101", "Kashyap", "k@example.com", LocalDate.of(1998, 1, 17));

    @BeforeEach
    void setUp() {
        memberService = new MemberService(memberRepository);   // constructor injection pays off
    }

    @Test
    void create_newMember_savesAndReturnsActiveMember() {
        // Arrange: no duplicates; save returns whatever it's given
        when(memberRepository.existsByMemberId("M101")).thenReturn(false);
        when(memberRepository.existsByEmail("k@example.com")).thenReturn(false);
        when(memberRepository.saveAndFlush(any(Member.class))).thenAnswer(inv -> inv.getArgument(0));

        // Act
        MemberResponse response = memberService.create(request);

        // Assert
        assertThat(response.memberId()).isEqualTo("M101");
        assertThat(response.status()).isEqualTo(MemberStatus.ACTIVE.name());
        verify(memberRepository).saveAndFlush(any(Member.class));
    }

    @Test
    void create_memberIdExists_throwsDuplicateMember_andNeverSaves() {
        when(memberRepository.existsByMemberId("M101")).thenReturn(true);

        assertThatThrownBy(() -> memberService.create(request))
                .isInstanceOf(DuplicateMemberException.class);

        verify(memberRepository, never()).saveAndFlush(any());
    }

    @Test
    void create_emailExists_throwsDuplicateEmail_andNeverSaves() {
        when(memberRepository.existsByEmail("k@example.com")).thenReturn(true);

        assertThatThrownBy(() -> memberService.create(request))
                .isInstanceOf(DuplicateEmailException.class);

        verify(memberRepository, never()).saveAndFlush(any());
    }

    @Test
    void create_duplicateMemberId_throwsDuplicateMemberException() {

        when(memberRepository.existsByMemberId("M101")).thenReturn(false);
        when(memberRepository.existsByEmail("k@example.com")).thenReturn(false);
        when(memberRepository.saveAndFlush(any(Member.class))).thenAnswer(inv -> {
            throw new DataIntegrityViolationException("could not execute statement",new RuntimeException("duplicate key value violates unique constraint \"uq_members_member_id\""));
        });

        // Assert
        assertThatThrownBy(() -> memberService.create(request))
                .isInstanceOf(DuplicateMemberException.class);
    }

    @Test
    void create_duplicateEmail_throwsDuplicateEmailException() {

        when(memberRepository.existsByMemberId("M101")).thenReturn(false);
        when(memberRepository.existsByEmail("k@example.com")).thenReturn(false);
        when(memberRepository.saveAndFlush(any(Member.class))).thenAnswer(inv -> {
            throw new DataIntegrityViolationException("could not execute statement",new RuntimeException("duplicate key value violates unique constraint \"uq_members_email\""));
        });

        // Assert
        assertThatThrownBy(() -> memberService.create(request))
                .isInstanceOf(DuplicateEmailException.class);
    }

    @Test
    void getMemberById_returnsExistingMember() {

        Member member = Member.create("M101", "Kashyap", "k@example.com", LocalDate.of(1998, 1, 17));
        // Arrange
        when(memberRepository.findByMemberId("M101")).thenReturn(Optional.of(member));

        // Act
        MemberResponse response = memberService.getByMemberId("M101");

        // Assert
        assertThat(response.memberId()).isEqualTo("M101");
    }

    @Test
    void getMemberById_returnsNotFound() {

        // Arrange
        when(memberRepository.findByMemberId("M101")).thenReturn(Optional.empty());

        // Act
        assertThatThrownBy(() -> memberService.getByMemberId("M101"))
                .isInstanceOf(MemberNotFoundException.class);
    }

    @Test
    void createMember_throwsDataIntegrityViolationException() {
        when(memberRepository.existsByMemberId("M101")).thenReturn(false);
        when(memberRepository.existsByEmail("k@example.com")).thenReturn(false);
        var ex = new DataIntegrityViolationException("could not execute statement",
                new RuntimeException("new row violates check constraint \"ck_members_status\""));
        when(memberRepository.saveAndFlush(any(Member.class))).thenThrow(ex);

        assertThatThrownBy(() -> memberService.create(request)).isSameAs(ex);
    }
}