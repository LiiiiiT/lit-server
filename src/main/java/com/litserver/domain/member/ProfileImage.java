package com.litserver.domain.member;

import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

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
    private Member member;

    @Column
    @NotBlank
    private String profileImageUrl;

    @Column
    @NotNull
    private int imageOrder;

    public ProfileImage(Member member, String profileImageUrl, int imageOrder) {
        this.member = member;
        this.profileImageUrl = profileImageUrl;
        this.imageOrder = imageOrder;
    }

    public Integer setImageOrder(Long imageOrderId, int imageOrder){
        System.out.println("imageOrderId=="+imageOrderId);
        System.out.println("imageOrder=="+imageOrder);
        if(imageOrderId == null){
            this.imageOrder = 0;
            return imageOrder;
        }else{
            this.imageOrder = imageOrder;
            return null;
        }
    }
}
