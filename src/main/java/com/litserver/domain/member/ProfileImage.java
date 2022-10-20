package com.litserver.domain.member;

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

    @Column
    @NotBlank
    private int imageOrder;

    public ProfileImage(Member member, String profileImageUrl, int imageOrder) {
        this.memberId = member;
        this.profileImageUrl = profileImageUrl;
        this.imageOrder = imageOrder;
    }
}
