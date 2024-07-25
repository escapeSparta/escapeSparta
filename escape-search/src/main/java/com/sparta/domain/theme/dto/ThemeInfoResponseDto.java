package com.sparta.domain.theme.dto;

import com.sparta.domain.theme.entity.Theme;
import lombok.Getter;

@Getter
public class ThemeInfoResponseDto {
    private Long themeId;
    private String title;
    private String contents;
    private Integer level;
    private Integer minPlayer;
    private Integer maxPlayer;
    private Integer duration;
    private String themeImage;
    private Long price;

    public ThemeInfoResponseDto(Theme theme){
        this.themeId = theme.getId();
        this.title = theme.getTitle();
        this.contents = theme.getContents();
        this.level = theme.getLevel();
        this.minPlayer = theme.getMinPlayer();;
        this.maxPlayer = theme.getMaxPlayer();
        this.duration = theme.getDuration();
        this.themeImage = theme.getThemeImage();
        this.price = theme.getPrice();
    }
}
