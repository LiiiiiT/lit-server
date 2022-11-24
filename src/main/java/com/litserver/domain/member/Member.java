package com.litserver.domain.member;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.litserver.domain.friend.Friend;
import com.litserver.domain.member.dto.SignDto;
import com.litserver.domain.notification.Notification;
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
@ToString(exclude = "profileImages")
public class Member extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "member_id")
    private Long id;
    @NotBlank
    @Email
    @Column(unique = true)
    private String email;
    @NotBlank
    @JsonIgnore
    private String password;
    @NotBlank
    private String nickname;

    private char gender;
    private double latitude;
    private double longitude;

    private String profile;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "member",
            cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<ProfileImage> profileImages = new ArrayList<>();

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
    private List<Notification> notifications = new ArrayList<>();

    @Enumerated(EnumType.STRING)
    private MemberState memberState;

    public Member (SignDto signDto, BCryptPasswordEncoder bCryptPasswordEncoder) {
        this.email = signDto.getEmail();
        this.password = bCryptPasswordEncoder.encode(signDto.getPassword());
        this.nickname = signDto ==null ? signDto.getEmail().split("@")[0] : signDto.getNickname();
//        this.profile = signDto.getProfile();
        this.memberState = MemberState.WAIT;
    }
}

