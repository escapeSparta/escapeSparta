package com.sparta.domain.theme.dto;

import com.sparta.domain.theme.entity.ThemeTime;
import com.sparta.domain.theme.entity.ThemeTimeStatus;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
public class ThemeTimeResponseDto {
    private Long themeTimeId;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private ThemeTimeStatus themeTimeStatus;

    public ThemeTimeResponseDto(ThemeTime themeTime){
        this.themeTimeId = themeTime.getId();
        this.startTime = themeTime.getStartTime();
        this.endTime = themeTime.getEndTime();
        this.themeTimeStatus = themeTime.getThemeTimeStatus();
    }
}
