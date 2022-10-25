package com.litserver.domain.member;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.litserver.domain.auth.dto.TokenRequestDto;
import com.litserver.domain.member.dto.LoginDto;
import com.litserver.domain.member.dto.ProfileUpdateDto;
import com.litserver.domain.member.dto.SignDto;
import com.litserver.global.common.ResponseHandler;
import com.litserver.global.util.SecurityUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RequiredArgsConstructor
@RestController
@RequestMapping(("/api/members"))
public class MemberController {
    private final MemberService memberService;
    private final MemberImageService memberImageService;

    // 회원 가입
    @PostMapping("/auth/signup")
    public ResponseEntity<Object> signUp(@Valid SignDto signDto) {
        memberImageService.countImage(signDto);
        return ResponseHandler.ok(memberService.signUp(signDto));
    }
    // 로그인
    @PostMapping("/auth/login")
    public ResponseEntity<Object> login(@RequestBody @Valid LoginDto loginDto) {
        return ResponseHandler.ok(memberService.login(loginDto));
    }

    // 토큰 재발급
    @PostMapping("/refresh")
    public ResponseEntity<Object> refreshToken(@RequestBody TokenRequestDto tokenRequestDto) {
        return ResponseHandler.ok(memberService.refreshToken(tokenRequestDto));
    }

    // 회원 정보 가져오기(기본값: 현재 로그인한 사용자의 정보 반환)
    @GetMapping("/info")
    public ResponseEntity<Object> getMemberInfo() {
        return ResponseHandler.ok(memberService.getMemberInfo());
    }

    // 회원 정보 수정
    @PatchMapping("/info")
    public ResponseEntity<Object> updateMemberInfo(ProfileUpdateDto profileUpdateDto) {

        return ResponseHandler.ok(memberService.updateMemberInfo(profileUpdateDto));
    }
    // 로그아웃
    @GetMapping("/logout")
    public ResponseEntity<Object> logout() {
        return ResponseHandler.ok(memberService.logout(SecurityContextHolder.getContext().getAuthentication().getName()));
    }

    // 회원 탈퇴
    @DeleteMapping("/delete")
    public ResponseEntity<Object> deleteAccount() {
        return ResponseHandler.ok(memberService.deleteAccount());
    }

    // 프로필 사진 초기화
    @GetMapping("/reset")
    public ResponseEntity<Object> resetProfileImage() {
        return ResponseHandler.ok(memberImageService.resetProfileImage(SecurityUtil.getCurrentMemberId()));
    }
}
