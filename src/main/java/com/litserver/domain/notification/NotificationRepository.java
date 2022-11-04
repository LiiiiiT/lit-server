package com.litserver.domain.notification;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NotificationRepository extends JpaRepository<Notification, Long> {

    List<Notification> findAllBySenderMemberIdAndReceiverMemberId(long senderMemberId, long receiverMemberId);

    Notification findBySenderMemberId(long senderMemberId);

    List<Notification> findAllByReceiverMemberId(long receiverMemberId);

    List<Notification> findAllBySenderMemberIdAndReceiverMemberIdAndMessageType(long senderMemberId, long receiverMemberId, MessageType type);
}