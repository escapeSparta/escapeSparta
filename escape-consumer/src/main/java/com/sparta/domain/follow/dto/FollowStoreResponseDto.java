package com.sparta.domain.follow.dto;

import com.sparta.domain.store.entity.Store;
import lombok.Getter;

@Getter
public class FollowStoreResponseDto {

  private final Long storeId;
  private final String name;
  private final String address;
  private final String phoneNumber;
  private final String storeImage;

  public FollowStoreResponseDto(Store store) {
    this.storeId = store.getId();
    this.name = store.getName();
    this.address = store.getAddress();
    this.phoneNumber = store.getPhoneNumber();
    this.storeImage = store.getStoreImage();
  }
}
