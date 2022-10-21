package com.litserver.domain.member;

import com.litserver.domain.auth.CustomUserDetails;
import com.litserver.domain.auth.dto.TokenRequestDto;
import com.litserver.domain.auth.dto.TokenResponseDto;
import com.litserver.domain.friend.FriendRepository;
import com.litserver.domain.friend.FriendState;
import com.litserver.domain.member.dto.*;
import com.litserver.domain.member.exception.DuplicateUserInfoException;
import com.litserver.domain.sse.Alarm;
import com.litserver.domain.sse.AlarmRepository;
import com.litserver.global.exception.runtime.InvalidJwtException;
import com.litserver.global.exception.runtime.RefreshTokenNotFoundException;
import com.litserver.global.exception.runtime.UnAuthorizedException;
import com.litserver.global.jwt.JwtExceptionCode;
import com.litserver.global.jwt.JwtProvider;
import com.litserver.global.redis.RedisService;
import com.litserver.global.util.SecurityUtil;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;
import java.util.ArrayList;
import java.util.List;


@Slf4j
@RequiredArgsConstructor
@Service
public class MemberService {
    @Value("${default-images.profile}")
    private String DEFAULT_PROFILE_IMAGE_URL;
    private final MemberRepository memberRepository;
    private final FriendRepository friendRepository;
    private final ProfileImageRepository profileImageRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final AuthenticationManagerBuilder authenticationManagerBuilder;
    private final JwtProvider jwtProvider;
    private final RedisService redisService;
    private final RedisTemplate<String, String> redisTemplate;
    private final AlarmRepository alarmRepository;
    private final MemberImageService memberImageService;
    @Transactional
    public Member signUp(SignDto signDto) {
        checkEmail(signDto.getEmail());
        Member member = memberRepository.save(new Member(signDto, bCryptPasswordEncoder));
        if(signDto.getImageFileList().size()!=0){
            List<ProfileImage> profileImages = memberImageService.addProfileImagesInS3(signDto, member);
            profileImageRepository.saveAll(profileImages).size();
        }
        return member;
    }



    @Transactional
    public TokenResponseDto login(LoginDto loginDto) {
        // Login 화면에서 입력 받은 email/pw 를 기반으로 AuthenticationToken 생성
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(loginDto.getEmail(), loginDto.getPassword());
        // 실제로 검증 (사용자 비밀번호 체크) 이 이루어지는 부분
        // authenticate 메서드가 실행이 될 때 CustomUserDetailsService 에서 만들었던 loadUserByUsername 메서드가 실행됨
        Authentication authentication;
        try {
            authentication = authenticationManagerBuilder.getObject()
                    .authenticate(authenticationToken);
        } catch (AuthenticationException e) {
            throw new BadCredentialsException("아이디, 혹은 비밀번호가 잘못되었습니다.");
        }
        // 인증 정보를 사용해 JWT 토큰 생성
        TokenResponseDto tokenResponseDto = jwtProvider.createTokenDto((CustomUserDetails) authentication.getPrincipal());
        // RefreshToken 저장
        String refreshToken = tokenResponseDto.getRefreshToken();
        redisService.setDataWithExpiration("JWT:" + authentication.getName(), refreshToken, tokenResponseDto.getRefreshTokenLifetimeInMs());
        // 토큰 발급
        return tokenResponseDto;
    }
    @Transactional(readOnly = true)
    public MemberInfoResponseDto getMemberInfo() {
        Member member = findMember(SecurityUtil.getCurrentMemberId());
        return MemberInfoResponseDto.builder()
                .memberId(member.getId())
                .nickname(member.getNickname())
                .email(member.getEmail())
                .profile(member.getProfile())
                .build();
    }

    // TODO: 2022/10/19 사진 순서 만들기
    @Transactional
    public long updateMemberInfo(ProfileUpdateDto profileUpdateDto) {
        long currentMemberId = SecurityUtil.getCurrentMemberId();
        Member member = findMember(currentMemberId);
//        List<ProfileImage> ProfileImage = profileImageRepository.findAllById(profileUpdateDto.getProfileImageId());
//        int index = ProfileImage.size();
        List<ProfileImage> profileImages = new ArrayList<>();
//        for(MultipartFile multipartFile : profileUpdateDto.getImageFile()) {
//            // 새로운 이미지를 WebP로 변환
//            var createdImageFile = imageUtil.convertImageToWebp(multipartFile, member.getEmail(), profileUpdateDto.getNickname());
//            // 업로드 요청
//            var putRequest = s3Util.createPutObjectRequest(createdImageFile);
//            // 업로드, 삭제 요청 실행
//            String profileImageUrl = s3Util.executePutRequest(putRequest);
//            profileImages.add(new ProfileImage(member, profileImageUrl, index++));
//        }
//        for(ProfileImage profileImage : ProfileImage){
//            // 기존 이미지 삭제 요청
//            var deleteRequest = s3Util.createDeleteRequest(profileImage.getProfileImageUrl());
//            s3Util.executeDeleteRequest(deleteRequest);
//        }
//        member.updateInfo(profileUpdateDto.getNickname(), profileUpdateDto.getProfile());
        return currentMemberId;
    }

    @Transactional
    public TokenResponseDto refreshToken(TokenRequestDto tokenRequestDto) {
        // 1. Refresh Token 검증
        try {
            jwtProvider.validateToken(tokenRequestDto.getRefreshToken());
        } catch (SecurityException | MalformedJwtException e) {
            log.info(JwtExceptionCode.INVALID_SIGNATURE_TOKEN.getMessage());
            throw new InvalidJwtException(JwtExceptionCode.INVALID_SIGNATURE_TOKEN.getMessage());
        } catch (ExpiredJwtException e) {
            log.info(JwtExceptionCode.EXPIRED_TOKEN.getMessage());
            throw new InvalidJwtException(JwtExceptionCode.EXPIRED_TOKEN.getMessage());
        } catch (UnsupportedJwtException e) {
            log.info(JwtExceptionCode.UNSUPPORTED_TOKEN.getMessage());
            throw new InvalidJwtException(JwtExceptionCode.UNSUPPORTED_TOKEN.getMessage());
        } catch (IllegalArgumentException e) {
            log.info(JwtExceptionCode.WRONG_TOKEN.getMessage());
            throw new InvalidJwtException(JwtExceptionCode.WRONG_TOKEN.getMessage());
        } catch (Exception e) {
            log.info(JwtExceptionCode.UNKNOWN_ERROR.getMessage());
            throw new InvalidJwtException(JwtExceptionCode.UNKNOWN_ERROR.getMessage());
        }

        // 2. Access Token 에서 memberId(PK) 가져오기
        Authentication authentication = jwtProvider.getAuthentication(tokenRequestDto.getAccessToken());
        var userDetails = (CustomUserDetails) authentication.getPrincipal();

        // 3. (수정) Redis 저장소에서 토큰 가져오는것으로 대체
        String savedRefreshToken = redisTemplate.opsForValue().get("JWT:" + authentication.getName());
        if (savedRefreshToken == null) {
            throw new RefreshTokenNotFoundException("로그아웃 된 사용자입니다.");
        }

        // 4. Refresh Token 일치하는지 검사 (추가) 로그아웃 사용자 검증)
        if (!savedRefreshToken.equals(tokenRequestDto.getRefreshToken())) {
            throw new InvalidJwtException("토큰의 유저 정보가 일치하지 않습니다.");
        }

        // 5. Access Token 에서 가져온 memberId(PK)를 다시 새로운 토큰의 클레임에 넣고 토큰 생성
        TokenResponseDto refreshedTokenResponseDto = jwtProvider.createTokenDto(userDetails);

        // 6. db의 리프레쉬 토큰 정보 업데이트 -> Redis에 Refresh 업데이트
        redisService.setDataWithExpiration("JWT:" + authentication.getName(),
                refreshedTokenResponseDto.getRefreshToken(),
                refreshedTokenResponseDto.getRefreshTokenLifetimeInMs());

        // 토큰 발급
        return refreshedTokenResponseDto;
    }
    @Transactional
    public Boolean logout(String email) {
        return redisTemplate.delete("JWT:" + email);
    }
    @Transactional
    public String deleteAccount() {
        long memberId = SecurityUtil.getCurrentMemberId();
        Member member = findMember(memberId);
        memberRepository.delete(member);
        redisTemplate.delete("JWT:" + member.getEmail());
        // 받은 모든 알림 삭제
        List<Alarm> alarms = alarmRepository.findAllByReceiverMemberId(memberId);
        alarmRepository.deleteAll(alarms);
        return member.getEmail();
    }

    public Member findMember(long memberId) {
        return memberRepository.findById(memberId)
                .orElseThrow(() -> new EntityNotFoundException(Member.class.getPackageName()));
    }

    private void checkEmail(String email) {
        if (memberRepository.existsByEmail(email)) {
            throw new DuplicateUserInfoException("사용중인 이메일입니다. 로그인 해주세요.");
        }
    }

    // 자기 자신, 혹은 친구 관계인지 검증
    private void checkAuthorization(long currentMemberId, long targetId) {
        checkSelfAuthorization(currentMemberId, targetId);
        if (!isFriend(currentMemberId, targetId)) {
            throw new UnAuthorizedException("접근 권한이 없습니다.");
        }
    }

    // 요청한 memberId(targetId)와 현재 로그인 한 사용자의 memberId가 동일한지 검증
    private void checkSelfAuthorization(long currentMemberId, long targetId) {
        if (currentMemberId != targetId) {
            throw new UnAuthorizedException("접근 권한이 없습니다.");
        }
    }

    // 두 memberId가 서로 친구 관계인지 검증
    private boolean isFriend(long currentMemberId, long targetId) {
        return friendRepository.existsByFromMemberIdAndToMemberIdAndFriendState(
                targetId, currentMemberId, FriendState.FRIEND)
                || friendRepository.existsByFromMemberIdAndToMemberIdAndFriendState(
                currentMemberId, targetId, FriendState.FRIEND);
    }
}
