package com.example.myshop.member.controller;

import com.example.myshop.member.Address;
import com.example.myshop.member.Member;
import com.example.myshop.member.dto.LoginRequest;
import com.example.myshop.member.dto.LoginResponse;
import com.example.myshop.member.dto.MemberDto;
import com.example.myshop.member.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class MemberController {

    private final MemberService memberService;

    @PostMapping("/members")
    public MemberDto saveMember(@RequestBody MemberDto memberDto) {
        Member member = new Member();
        member.setEmail(memberDto.getEmail());
        member.setPassword(memberDto.getPassword()); // 비밀번호 설정
        Address address = new Address();
        address.setCity(memberDto.getCity());
        address.setStreet(memberDto.getStreet());
        address.setZipcode(memberDto.getZipcode());
        member.setAddress(address);
        memberService.join(member);
        return memberDto;
    }

    @GetMapping("/members")
    public List<MemberDto> list() {
        List<Member> members = memberService.findMembers();
        return members.stream()
                .map(m -> new MemberDto(m.getEmail(), null, m.getAddress().getCity(), m.getAddress().getStreet(), m.getAddress().getZipcode())) // 비밀번호는 노출하지 않음
                .collect(Collectors.toList());
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest loginRequest) {
        Member member = memberService.login(loginRequest.getEmail(), loginRequest.getPassword());
        if (member != null) {
            return ResponseEntity.ok(new LoginResponse(member.getId(), member.getEmail(), "Login successful"));
        } else {
            return ResponseEntity.status(401).body(new LoginResponse(null, null, "Invalid credentials"));
        }
    }
}