package com.benefitflow.member_service.api;

import com.benefitflow.member_service.service.MemberService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@RestController
@RequestMapping("/api")
public class MemberController {

    private final MemberService memberService;

    public MemberController(MemberService memberService) {
        this.memberService = memberService;
    }

    @PostMapping("/members")
    public ResponseEntity<MemberResponse> createMember(@RequestBody CreateMemberRequest request) {
        return ResponseEntity.created(URI.create("/api/members/" + request.memberId())).body(memberService.create(request));
    }

    @GetMapping("/members/{memberId}")
    public ResponseEntity<MemberResponse> getMember(@PathVariable String memberId) {
        return ResponseEntity.ok(memberService.getByMemberId(memberId));
    }
}
