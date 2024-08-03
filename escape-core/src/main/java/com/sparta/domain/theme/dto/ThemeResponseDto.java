package com.sparta.domain.theme.dto;

import com.sparta.domain.theme.entity.Theme;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ThemeResponseDto {
    private Long themeId;
    private String themeImage;
    private String title;

    public ThemeResponseDto(Theme theme){
        this.themeId = theme.getId();
        this.themeImage = theme.getThemeImage();
        this.title = theme.getTitle();
    }
}
