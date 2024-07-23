package com.sparta.domain.store.entity;

import com.sparta.domain.user.entity.User;
import com.sparta.global.entity.TimeStamped;
import com.sparta.global.exception.customException.StoreException;
import com.sparta.global.exception.errorCode.StoreErrorCode;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
public class Store extends TimeStamped {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String address;

    @Column(nullable = false)
    private String phoneNumber;

    @Column(nullable = false)
    private String workHours;

    @Column(nullable = false)
    private String storeImage;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "manager_id", nullable = false)
    private User manager;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private StoreStatus storeStatus;

    @Builder
    public Store(String name, String address, String phoneNumber, String workHours, String storeImage, User manager, StoreStatus storeStatus) {
        this.name = name;
        this.address = address;
        this.phoneNumber = phoneNumber;
        this.workHours = workHours;
        this.storeImage = storeImage;
        this.manager = manager;
        this.storeStatus = storeStatus;
    }

    public void updateStore(String name, String address, String phoneNumber, String workHours) {
        this.name = name;
        this.address = address;
        this.phoneNumber = phoneNumber;
        this.workHours = workHours;
    }

    public void deactivateStore() {
        this.storeStatus = StoreStatus.DEACTIVE;
    }

    public void checkManager(User manager) {
        if(!this.manager.equals(manager)) {
            throw new StoreException(StoreErrorCode.USER_NOT_STORE_MANAGER);
        }
    }
}
