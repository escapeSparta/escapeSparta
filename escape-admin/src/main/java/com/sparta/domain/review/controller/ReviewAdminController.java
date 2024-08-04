package com.sparta.domain.review.controller;

import com.sparta.domain.review.service.ReviewAdminService;
import com.sparta.global.response.ResponseMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/admin")
public class ReviewAdminController {

    private final ReviewAdminService reviewService;

    /**
     * TODO : 리뷰 강제 삭제
     * @param reviewId
     * @author SEMI
     */
    @DeleteMapping("/reviews/{reviewId}")
    public ResponseEntity<ResponseMessage<Void>> deleteReview(@PathVariable("reviewId")Long reviewId) {

        reviewService.deleteReview(reviewId);

        ResponseMessage<Void> responseMessage = ResponseMessage.<Void>builder()
                .statusCode(HttpStatus.OK.value())
                .build();

        return ResponseEntity.status(HttpStatus.OK).body(responseMessage);
    }
}
