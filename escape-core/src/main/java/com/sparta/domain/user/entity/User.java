package com.sparta.domain.user.entity;

import com.sparta.domain.reaction.entity.Reaction;
import com.sparta.domain.reservation.entity.Reservation;
import com.sparta.global.entity.TimeStamped;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor
@Table(name = "users")
public class User extends TimeStamped {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private Long point = 0L;

    @Column(nullable = true)
    private String inviteCode;

    // 초대 코드 만료시간
    private LocalDateTime inviteCodeExpirationTime;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private UserType userType;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private UserStatus userStatus;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private OAuthProvider oAuthProvider;

    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY, orphanRemoval = true, cascade = CascadeType.ALL)
    private List<Reservation> reservations;

    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY, orphanRemoval = true, cascade = CascadeType.ALL)
    private List<Reaction> reactions;


    public User(String name, String email, String password,OAuthProvider oAuthProvider,UserType userType, UserStatus userStatus) {
        this.name = name;
        this.email = email;
        this.password = password;
        this.oAuthProvider = oAuthProvider;
        this.userType = userType;
        this.userStatus = userStatus;
    }

    public void activeUser() { // 이메일 인증받은 유저 상태 업데이트
        this.userStatus = UserStatus.ACTIVE;
    }

    public void changeStatus(UserStatus userStatus) {
        this.userStatus = UserStatus.WITHDRAW;
    }

    public void editUser(String name) {
        this.name = name;
    }

    public void changePassword(String newPassword) {
        this.password = newPassword;
    }

    public void sendInviteCode(String inviteCode, LocalDateTime expirationTime) { // 초대 코드에 따라 권한이 결정되는 코드
        this.inviteCode = inviteCode;
        this.inviteCodeExpirationTime = expirationTime;
    }

}
