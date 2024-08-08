package com.sparta.domain.review.entity;

import com.sparta.domain.reaction.entity.Reaction;
import com.sparta.domain.reservation.entity.Reservation;
import com.sparta.domain.theme.entity.Theme;
import com.sparta.domain.user.entity.User;
import com.sparta.global.entity.TimeStamped;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;


@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Review extends TimeStamped {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Double rating;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String contents;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "theme_id", nullable = false)
    private Theme theme;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reservation_id", nullable = false)
    private Reservation reservation;

    @OneToMany(mappedBy = "review", fetch = FetchType.LAZY, orphanRemoval = true, cascade = CascadeType.ALL)
    private List<Reaction> reactions;

    @Builder
    public Review(Double rating, String title, String contents, User user, Theme theme, Reservation reservation){
        this.rating = rating;
        this.title = title;
        this.contents = contents;
        this.user = user;
        this.theme = theme;
        this.reservation = reservation;
    }

    public void update(String title, String contents, Double rating){
        this.title = title;
        this.contents = contents;
        this.rating = rating;
    }
}
