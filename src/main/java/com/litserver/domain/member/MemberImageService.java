package com.litserver.domain.member;

import com.litserver.domain.member.dto.SignDto;
import com.litserver.global.exception.runtime.image.ImageProcessException;
import com.litserver.global.util.ImageUtil;
import com.litserver.global.util.S3Util;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

import static com.litserver.global.exception.ExceptionCode.IMAGE_UPLOAD_FAILURE;

@RequiredArgsConstructor
@Service
public class MemberImageService {
    private final S3Util s3Util;
    private final ImageUtil imageUtil;
    private final ProfileImageRepository profileImageRepository;
    public List<ProfileImage> addProfileImagesInS3(List<MultipartFile> imageFileList, Member member, List<Integer> imageOrder) {
        List<ProfileImage> profileImages = new ArrayList<>();
        for(int i = 0; i < imageFileList.size(); i++) {
            // 이미지를 WebP로 변환
            var createdImageFile = imageUtil.convertImageToWebp(imageFileList.get(i), member.getEmail(), member.getNickname());
            // 업로드 요청
            var putRequest = s3Util.createPutObjectRequest(createdImageFile);
            // 업로드 요청 실행
            String profileImageUrl = s3Util.executePutRequest(putRequest);
            System.out.println(profileImages.size());
            System.out.println(imageOrder.get(i));
            profileImages.add(new ProfileImage(member, profileImageUrl, imageOrder.size() == 0 ?profileImages.size() :imageOrder.get(i)));
        }
        return profileImages;
    }
    void deleteImageFileInS3(List<String> profileImageUrlList) {
        for(String profileImageUrl : profileImageUrlList){
            // 기존 이미지 삭제 요청
            var deleteRequest = s3Util.createDeleteRequest(profileImageUrl);
            s3Util.executeDeleteRequest(deleteRequest);
        }
    }
    @Transactional
    public long resetProfileImage(long memberId) {
        for(ProfileImage profileImage : profileImageRepository.findAllByMemberId(memberId)){
            var deleteRequest = s3Util.createDeleteRequest(profileImage.getProfileImageUrl());
            s3Util.executeDeleteRequest(deleteRequest);
        }
        return memberId;
    }


    public void countImage(SignDto signDto) {
        if(signDto.getImageFileList() == null) throw new ImageProcessException(IMAGE_UPLOAD_FAILURE, "추출할 이미지가 없습니다.");
        if(signDto.getImageFileList().size() > 5) throw new ImageProcessException(IMAGE_UPLOAD_FAILURE, "5개 이하의 이미지만 업로드 가능합니다.");
    }
}
