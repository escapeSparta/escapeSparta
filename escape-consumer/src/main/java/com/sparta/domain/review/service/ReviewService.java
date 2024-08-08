package com.sparta.domain.review.service;

import com.sparta.domain.reaction.entity.Reaction;
import com.sparta.domain.reaction.entity.ReactionType;
import com.sparta.domain.reaction.repository.ReactionRepository;
import com.sparta.domain.reservation.entity.Reservation;
import com.sparta.domain.reservation.repository.ReservationRepository;
import com.sparta.domain.review.dto.*;
import com.sparta.domain.review.entity.Review;
import com.sparta.domain.review.repository.ReviewRepository;
import com.sparta.domain.user.entity.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final ReservationRepository reservationRepository;
    private final ReactionRepository reactionRepository;


    /**
     * 테마 리뷰 작성
     * @param reviewCreateRequestDto 작성할 리뷰의 데이터 값
     * @param user 로그인 유저
     * @return 작성한 리뷰 반환
     */
    @Transactional
    public ReviewCreateResponseDto createReview(ReviewCreateRequestDto reviewCreateRequestDto, User user) {
        Reservation reservation = reservationRepository.findByIdAndUserOrElseThrow(reviewCreateRequestDto.getReservationId(), user);

        reviewRepository.checkIfAlreadyReview(user, reservation);

        Review review = Review.builder()
                .rating(reviewCreateRequestDto.getRating())
                .title(reviewCreateRequestDto.getTitle())
                .contents(reviewCreateRequestDto.getContents())
                .user(user)
                .theme(reservation.getTheme())
                .reservation(reservation)
                .build();

        return new ReviewCreateResponseDto(reviewRepository.save(review));

    }

    /**
     * 테마 리뷰 수정
     * @param reviewUpdateRequestDto 수정할 리뷰의 데이터 값
     * @param user 로그인 유저
     * @return 수정한 리뷰 반환
     */
    @Transactional
    public ReviewUpdateResponseDto updateReview(Long reviewId, ReviewUpdateRequestDto reviewUpdateRequestDto, User user) {
        Review review = reviewRepository.findByIdAndUserOrElse(reviewId, user);
        review.update(reviewUpdateRequestDto.getTitle(), reviewUpdateRequestDto.getContents(), reviewUpdateRequestDto.getRating());
        return new ReviewUpdateResponseDto(review);
    }

    /**
     * 리뷰 조회
     * @param reviewId 조회할 리뷰 id
     * @return 리뷰
     */
    public ReviewResponseDto getReview(Long reviewId) {
        Review review = reviewRepository.findByReview(reviewId);
        return new ReviewResponseDto(review);
    }

    /**
     * 테마 리뷰 삭제
     * @param reviewId 수정할 리뷰의 id
     * @param user 로그인 유저
     */
    @Transactional
    public void deleteReview(Long reviewId, User user) {
        Review review = reviewRepository.findByIdAndUserOrElse(reviewId, user);
        reviewRepository.delete(review);
    }


    /**
     * 리액션 등록, 수정, 삭제
     * @param reviewId 리액션할 리뷰 id
     * @param reactionType 리액션
     * @param user 로그인 유저
     * @return 등록 true, 취소 false 반환
     */
    @Transactional
    public ReactionResponseDto createReaction(Long reviewId, ReactionType reactionType, User user) {
        Review review = reviewRepository.findByIdOrElse(reviewId);
        Reaction findReaction = reactionRepository.findByReviewAndUser(review, user).orElse(null);

        if(findReaction == null){
            Reaction reaction = Reaction.builder()
                    .reactionType(reactionType)
                    .user(user)
                    .review(review)
                    .build();
            reactionRepository.save(reaction);
            return new ReactionResponseDto(reactionType, true);
        }
        else{
            return changeOrDeleteReaction(findReaction, reactionType);
        }
    }

    /**
     * 내가 등록한 리뷰 전체 보기
     * @param user 로그인 유저
     * @return 등록한 리뷰
     */
    public List<ReviewResponseDto> getMyReviews(User user) {
        List<Review> reviewList = reviewRepository.findByMyReviews(user);
        return reviewList.stream().map(ReviewResponseDto::new).toList();
    }

    private ReactionResponseDto changeOrDeleteReaction(Reaction findReaction, ReactionType reactionType) {
        if (findReaction.getReactionType() != reactionType) {
            findReaction.update(reactionType);
            return new ReactionResponseDto(reactionType, true);
        } else {
            reactionRepository.delete(findReaction);
            return new ReactionResponseDto(null, false);
        }
    }

}
