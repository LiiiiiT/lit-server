package com.litserver.domain.member;

import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@ToString(exclude = "memberId")
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
    @NotNull
    private int imageOrder;

    public ProfileImage(Member member, String profileImageUrl, int imageOrder) {
        this.memberId = member;
        this.profileImageUrl = profileImageUrl;
        this.imageOrder = imageOrder;
    }

    public Integer setImageOrder(Long imageOrderId, int imageOrder){
        if(imageOrderId == null){
            this.imageOrder = imageOrder;
            return null;
        }else{
            this.imageOrder = 0;
            return imageOrder;
        }
    }
}
