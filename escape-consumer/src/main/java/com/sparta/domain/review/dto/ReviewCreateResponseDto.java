package com.sparta.domain.review.dto;

import com.sparta.domain.review.entity.Review;
import lombok.Getter;

@Getter
public class ReviewCreateResponseDto {
    private Long reviewId;
    private String title;
    private String contents;
    private Double rating;

    public ReviewCreateResponseDto(Review review){
        this.reviewId = review.getId();
        this.title = review.getTitle();
        this.contents = review.getContents();
        this.rating = review.getRating();
    }
}
