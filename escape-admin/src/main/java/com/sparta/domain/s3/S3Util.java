package com.sparta.domain.s3;

import com.sparta.global.exception.customException.S3Exception;
import com.sparta.global.exception.errorCode.S3ErrorCode;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@Component
public class S3Util {

    private static final String STORE_DIR = "store";
    private static final String THEME_DIR = "theme";

    /**
     * 파일 존재 여부 검사
     */
    public static boolean doesFileExist(MultipartFile multipartFile) {
        return multipartFile != null && !multipartFile.isEmpty();
    }

    /**
     * 파일 확장자 검사
     */
    public static String getValidateImageExtension(String fileName) {
        List<String> validExtensionList = Arrays.asList("jpg", "jpeg", "png", "webp");

        int extensionIndex = fileName.lastIndexOf(".");

        String extension = fileName.substring(extensionIndex + 1).toLowerCase();

        if(!validExtensionList.contains(extension)) {
            throw new S3Exception(S3ErrorCode.INVALID_EXTENSION);
        }

        return extension;
    }

    /**
     * 방탈출 카페 이미지 경로 생성
     * store/{storeId}
     */
    public static String createStoreImageDir(Long storeId) {
        return STORE_DIR + "/"
                + storeId + "/";
    }

    /**
     * 방탈출 테마 이미지 저장 경로 생성
     * store/{storeId}/theme/{themeId}
     */
    public static String createThemeImageDir(Long storeId, Long themeId) {
        return STORE_DIR + "/" + storeId + "/"
                + THEME_DIR + "/" + themeId + "/";
    }

    /**
     * 파일명 랜덤 생성
     */
    public static String createFileName(String extension) {
        return UUID.randomUUID().toString().concat("." + extension);
    }
}