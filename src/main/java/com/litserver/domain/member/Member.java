package com.litserver.domain.member;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.litserver.domain.friend.Friend;
import com.litserver.domain.sse.Alarm;
import com.litserver.global.common.BaseTimeEntity;
import lombok.*;

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
    @Column
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
    public void updateInfo(String nickname, String profileImageUrl) {
        if (nickname != null) {
            this.nickname = nickname;
        }
        if (profileImageUrl != null) {
            this.profileImageUrl = profileImageUrl;
        }
    }
}

