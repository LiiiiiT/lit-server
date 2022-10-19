package com.litserver.domain.friend.dto;

import com.litserver.domain.friend.FriendState;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.experimental.FieldDefaults;

@Getter
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class FriendResponseDto {
    private long toMemberId;
    private String toMemberNickname;
    private long fromMemberId;
    private String fromMemberNickname;
    private FriendState friendState;

    @Builder
    public FriendResponseDto(long toMemberId, String toMemberNickname, long fromMemberId, String fromMemberNickname, FriendState friendState) {
        this.toMemberId = toMemberId;
        this.toMemberNickname = toMemberNickname;
        this.fromMemberId = fromMemberId;
        this.fromMemberNickname = fromMemberNickname;
        this.friendState = friendState;
    }
}