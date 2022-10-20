package com.litserver.domain.member;

import com.litserver.domain.member.dto.SignDto;
import com.litserver.global.util.ImageUtil;
import com.litserver.global.util.S3Util;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
@Service
public class MemberImageService {
    private final S3Util s3Util;
    private final ImageUtil imageUtil;
    private final ProfileImageRepository profileImageRepository;
    public List<ProfileImage> addProfileImagesInS3(SignDto signDto, Member member, List<Integer> imageOrderList) {
        List<ProfileImage> profileImages = new ArrayList<>();
        for(int i=0; i<signDto.getImageFileList().size(); i++) {
            // 이미지를 WebP로 변환
            var createdImageFile = imageUtil.convertImageToWebp(signDto.getImageFileList().get(i), signDto.getEmail(), signDto.getNickname());
            // 업로드 요청
            var putRequest = s3Util.createPutObjectRequest(createdImageFile);
            // 업로드 요청 실행
            String profileImageUrl = s3Util.executePutRequest(putRequest);
            profileImages.add(new ProfileImage(member, profileImageUrl, imageOrderList.size()==0?profileImages.size():imageOrderList.get(i)));
        }
        return profileImages;
    }

    @Transactional
    public long resetProfileImage(long memberId) {
        for(ProfileImage profileImage : profileImageRepository.findAllByMemberId(memberId)){
            var deleteRequest = s3Util.createDeleteRequest(profileImage.getProfileImageUrl());
            s3Util.executeDeleteRequest(deleteRequest);
        }
        return memberId;
    }
}
