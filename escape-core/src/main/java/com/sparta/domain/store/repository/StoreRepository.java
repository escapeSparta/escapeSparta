package com.sparta.domain.store.repository;

import com.sparta.domain.store.entity.Store;
import com.sparta.global.exception.customException.StoreException;
import com.sparta.global.exception.errorCode.StoreErrorCode;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StoreRepository extends JpaRepository<Store, Long>, StoreRepositoryCustom {
    default Store findByIdOrElseThrow(Long storeId){
        return findById(storeId).orElseThrow(
                ()-> new StoreException(StoreErrorCode.STORE_NOT_FOUND)
        );
    };
}
