package com.litserver.domain.member;

import com.litserver.domain.member.dto.TestDto;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class ProfileImage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "profileImage_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(nullable = false)
    private Member memberId;

    @Column
    @NotBlank
    private String profileImageUrl;

    public ProfileImage(Member member, String profileImageUrl) {
        this.memberId = member;
        this.profileImageUrl = profileImageUrl;
    }
}
