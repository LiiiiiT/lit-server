package com.litserver.domain.member;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProfileImageRepository extends JpaRepository<ProfileImage, Long> {

    List<ProfileImage> findAllByMember(Member member);

    void deleteAllByImageOrder(int imageOrder);

    int countByMember(Member member);
}
