package com.litserver.domain.member;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProfileImageRepository extends JpaRepository<ProfileImage, Long> {

//    List<ProfileImage> findAllById(List<Integer> profileImageId);

    List<ProfileImage> findAllByMemberId(long memberId);
}
