package com.litserver.domain.member;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import java.util.List;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@ToString
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

    public ProfileImage(Member member, String profileImageUrl, List<Integer> imageOrder, int i) {
        this.memberId = member;
        this.profileImageUrl = profileImageUrl;
        this.imageOrder = imageOrder == null? 1 + i: imageOrder.get(i);
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
