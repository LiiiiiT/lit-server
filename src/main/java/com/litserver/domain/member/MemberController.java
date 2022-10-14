package com.litserver.domain.member;


import com.litserver.domain.auth.dto.TokenRequestDto;
import com.litserver.domain.member.dto.LoginDto;
import com.litserver.domain.member.dto.MemberInfoUpdateDto;
import com.litserver.domain.member.dto.SignDto;
import com.litserver.global.common.ResponseHandler;
import com.litserver.global.exception.runtime.UnAuthorizedException;
import com.litserver.global.util.SecurityUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RequiredArgsConstructor
@RestController
@RequestMapping(("/api/members"))
public class MemberController {
    private final MemberService memberService;

    // 회원 가입
    @PostMapping("/auth/signup")
    public ResponseEntity<Object> signUp(@RequestBody @Valid SignDto signDto) {
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
    public ResponseEntity<Object> getMemberInfo(@RequestParam(value = "m", defaultValue = "0") long targetId) {
        long currentMemberId = SecurityUtil.getCurrentMemberId();
        if (targetId == 0L) {
            targetId = currentMemberId;
        }
        return ResponseHandler.ok(memberService.getMemberInfo(currentMemberId, targetId));
    }

    // 회원 정보 수정
    @PatchMapping("/info")
    public ResponseEntity<Object> updateMemberInfo(
            @RequestParam(value = "m", defaultValue = "0") long targetId, MemberInfoUpdateDto memberInfoUpdateDto) {
        long currentMemberId = SecurityUtil.getCurrentMemberId();
//        guestService.guestCheck(currentMemberId);
        if (targetId == 0L) {
            targetId = currentMemberId;
        }
        return ResponseHandler.ok(memberService.updateMemberInfo(currentMemberId, targetId, memberInfoUpdateDto));
    }

    // 로그아웃
    @GetMapping("/logout")
    public ResponseEntity<Object> logout() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return ResponseHandler.ok(memberService.logout(authentication.getName()));
    }

    // 회원 탈퇴
    @DeleteMapping("/delete")
    public ResponseEntity<Object> deleteAccount(@RequestParam(value = "m") long targetId) {
        long currentMemberId = SecurityUtil.getCurrentMemberId();
//        guestService.guestCheck(currentMemberId);
        if (currentMemberId != targetId) {
            throw new UnAuthorizedException("본인의 memberId가 아닙니다.");
        }
        return ResponseHandler.ok(memberService.deleteAccount(targetId));
    }

    // 프로필 사진 초기화
    @GetMapping("/reset")
    public ResponseEntity<Object> resetProfileImage() {
        long currentMemberId = SecurityUtil.getCurrentMemberId();
//        guestService.guestCheck(currentMemberId);
        return ResponseHandler.ok(memberService.resetProfileImage(currentMemberId));
    }
}
