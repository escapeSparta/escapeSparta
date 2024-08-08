package com.sparta.domain.follow.controller;

import com.sparta.domain.follow.dto.FollowStoreResponseDto;
import com.sparta.domain.follow.service.FollowService;
import com.sparta.global.response.ResponseMessage;
import com.sparta.global.security.UserDetailsImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/follow")
public class FollowController {

    private final FollowService followService;

    /**
     * 방탈출 카페 팔로우
     * @param storeId 팔로우할 카페 id
     * @param userDetails 로그인 유저
     * @return status.code, message
     */
    @PostMapping("/stores/{storeId}")
    public ResponseEntity<ResponseMessage<Void>> followStore(
            @PathVariable Long storeId,
            @AuthenticationPrincipal UserDetailsImpl userDetails){

        followService.followStore(storeId, userDetails.getUser());

        ResponseMessage<Void> responseMessage = ResponseMessage.<Void>builder()
                .statusCode(HttpStatus.OK.value())
                .message("방탈출 카페를 팔로우 하였습니다.")
                .data(null)
                .build();

        return ResponseEntity.status(HttpStatus.OK).body(responseMessage);
    }

    /**
     * 방탈출 카페 언팔로우
     * @param storeId 언팔로우할 카페 id
     * @param userDetails 로그인 유저
     * @return status.code, message
     */
    @DeleteMapping("/stores/{storeId}")
    public ResponseEntity<ResponseMessage<Void>> unfollowStore(
            @PathVariable Long storeId,
            @AuthenticationPrincipal UserDetailsImpl userDetails){

        followService.unfollowStore(storeId, userDetails.getUser());

        ResponseMessage<Void> responseMessage = ResponseMessage.<Void>builder()
                .statusCode(HttpStatus.OK.value())
                .message("방탈출 카페를 팔로우 취소 하였습니다.")
                .data(null)
                .build();

        return ResponseEntity.status(HttpStatus.OK).body(responseMessage);
    }

    @GetMapping("/stores")
    public ResponseEntity<ResponseMessage<List<FollowStoreResponseDto>>> getFollowStores(
            @AuthenticationPrincipal UserDetailsImpl userDetails){

        List<FollowStoreResponseDto> responseDtoList = followService.getFollowStores(userDetails.getUser());

        ResponseMessage<List<FollowStoreResponseDto>> responseMessage = ResponseMessage
                .<List<FollowStoreResponseDto>>builder()
                .statusCode(HttpStatus.OK.value())
                .message("팔로우한 방탈출 카페를 조회했습니다.")
                .data(responseDtoList)
                .build();

        return ResponseEntity.status(HttpStatus.OK).body(responseMessage);
    }
}
