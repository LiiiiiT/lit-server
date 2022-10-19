package com.litserver.domain.member;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.litserver.domain.friend.Friend;
import com.litserver.domain.member.dto.SignDto;
import com.litserver.domain.member.dto.TestDto;
import com.litserver.domain.sse.Alarm;
import com.litserver.global.common.BaseTimeEntity;
import lombok.*;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class Member extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "member_id")
    private Long id;
    @NotBlank
    @Email
    @Column(unique = true)
    private String email;
    @Column
    @NotBlank
    // @Size(min = 4, max = 20)
    private String nickname;
    @Column
    @NotBlank
    @JsonIgnore
    private String password;
    @Column
    @NotBlank
    private String profileImageUrl;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "memberId",
            cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<ProfileImage> profileImages = new ArrayList<>();

    @Column
    @NotBlank
    @JsonIgnore
    private final String authority = "ROLE_USER";

    // 친구들
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "fromMember",
            cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<Friend> fromMembers = new ArrayList<>();

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "toMember",
            cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<Friend> toMembers = new ArrayList<>();
    private int friendCount;

    // 알림
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "senderMember",
            cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<Alarm> alarms = new ArrayList<>();

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private MemberState memberState;

    public Member (SignDto signDto, BCryptPasswordEncoder bCryptPasswordEncoder, String DEFAULT_PROFILE_IMAGE_URL) {
        this.email = signDto.getEmail();
        this.password = bCryptPasswordEncoder.encode(signDto.getPassword());
        this.nickname = signDto.getEmail().split("@")[0];
        this.profileImageUrl = DEFAULT_PROFILE_IMAGE_URL;
        this.memberState = MemberState.WAIT;
    }
    public Member (TestDto testDto, BCryptPasswordEncoder bCryptPasswordEncoder) {
        this.email = testDto.getEmail();
        this.password = bCryptPasswordEncoder.encode(testDto.getPassword());
        this.nickname = testDto==null ? testDto.getEmail().split("@")[0] : testDto.getNickname();
        this.profileImageUrl = "test";
        this.memberState = MemberState.WAIT;
    }

    public void updateInfo(String nickname, String profileImageUrl) {
        if (nickname != null) {
            this.nickname = nickname;
        }
        if (profileImageUrl != null) {
            this.profileImageUrl = profileImageUrl;
        }
    }

}

