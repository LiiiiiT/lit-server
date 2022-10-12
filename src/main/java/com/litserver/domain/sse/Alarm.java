package com.litserver.domain.sse;

import com.litserver.domain.member.Member;
import com.litserver.global.common.BaseTimeEntity;
import lombok.*;

import javax.persistence.*;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Entity
@Table(name = "alarm")
@Builder
public class Alarm extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private MessageType messageType;

    @Column(nullable = false)
    private String content;

    // 발송자
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sender_member_id", nullable = false)
    private Member senderMember;

    // 작성자 PK (읽기전용으로만 사용할 것)
    @Column(name = "sender_member_id", updatable = false, insertable = false)
    private Long senderMemberId;

    // 수신자
    @Column(nullable = false)
    private long receiverMemberId;
}
