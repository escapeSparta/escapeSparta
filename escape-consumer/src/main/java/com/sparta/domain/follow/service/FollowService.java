package com.sparta.domain.follow.service;

import com.sparta.domain.follow.dto.FollowStoreResponseDto;
import com.sparta.domain.follow.entity.Follow;
import com.sparta.domain.follow.repository.FollowRepository;
import com.sparta.domain.store.entity.Store;
import com.sparta.domain.store.repository.StoreRepository;
import com.sparta.domain.user.entity.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class FollowService {

    private final FollowRepository followRepository;
    private final StoreRepository storeRepository;

    /**
     * 방탈출 카페 팔로우
     * @param storeId 팔로우할 카페 id
     * @param user 로그인 유저
     */
    @Transactional
    public void followStore(Long storeId, User user) {
        Store store = storeRepository.findByActiveStore(storeId);
        followRepository.checkIfAlreadyFollowed(user, store);

        Follow follow = Follow.builder()
                .user(user)
                .store(store)
                .build();

        followRepository.save(follow);
    }

    /**
     * 방탈출 카페 언팔로우
     * @param storeId 언팔로우할 카페 id
     * @param user 로그인 유저
     */
    @Transactional
    public void unfollowStore(Long storeId, User user) {
        Store store = storeRepository.findByActiveStore(storeId);
        Follow follow = followRepository.getFollowOrThrow(user, store);
        followRepository.delete(follow);
    }

    /**
     * 팔로우한 카페 조회
     * @param user 로그인 유저
     * @return 팔로우한 카페 리턴
     */
    @Transactional(readOnly = true)
    public List<FollowStoreResponseDto> getFollowStores(User user) {
        List<Follow> follow = followRepository.findByGetStores(user);

        return follow.stream().map(f -> new FollowStoreResponseDto(f.getStore())).toList();
    }
}
