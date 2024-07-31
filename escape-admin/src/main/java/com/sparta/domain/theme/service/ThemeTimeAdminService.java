package com.sparta.domain.theme.service;

import com.sparta.domain.reservation.repository.ReservationRepository;
import com.sparta.domain.theme.dto.request.ThemeTimeCreateRequestDto;
import com.sparta.domain.theme.dto.request.ThemeTimeModifyRequestDto;
import com.sparta.domain.theme.dto.response.ThemeTimeDetailResponseDto;
import com.sparta.domain.theme.entity.Theme;
import com.sparta.domain.theme.entity.ThemeTime;
import com.sparta.domain.theme.repository.ThemeRepository;
import com.sparta.domain.theme.repository.ThemeTimeRepository;
import com.sparta.domain.user.entity.User;
import com.sparta.global.lock.DistributedLock;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static com.sparta.global.util.LocalDateTimeUtil.*;

@Service
@RequiredArgsConstructor
public class ThemeTimeAdminService {
    private final ThemeTimeRepository themeTimeRepository;
    private final ThemeRepository themeRepository;

    @Transactional
    public ThemeTimeDetailResponseDto createThemeTime(Long themeId, ThemeTimeCreateRequestDto requestDto) {
        Theme theme = themeRepository.findThemeOfActiveStore(themeId);

        LocalDateTime startTime = parseDateTimeStringToLocalDateTime(requestDto.getStartTime());
        LocalDateTime endTime = calculateEndTime(startTime, theme.getDuration());

        ThemeTime themeTime = ThemeTime.builder()
                .startTime(startTime)
                .endTime(endTime)
                .theme(theme)
                .build();

        themeTimeRepository.save(themeTime);
        return new ThemeTimeDetailResponseDto(themeTime);
    }

    public List<ThemeTimeDetailResponseDto> getThemeTimes(Long themeId, String date) {
        Theme theme = themeRepository.findThemeOfActiveStore(themeId);
        List<ThemeTime> themeTimes;

        if(date == null) {
            themeTimes = themeTimeRepository.findAllByThemeId(theme.getId());
        } else {
            LocalDate searchDate = parseDateStringToLocalDate(date);
            themeTimes = themeTimeRepository.findThemeTimesByDate(themeId, searchDate);
        }

        return themeTimes.stream().map(ThemeTimeDetailResponseDto::new).toList();
    }

    @Transactional
    @DistributedLock(key = "themeTimeId:#themeTimeId")
    public ThemeTimeDetailResponseDto modifyThemeTime(Long themeTimeId, ThemeTimeModifyRequestDto requestDto) {
        ThemeTime themeTime = themeTimeRepository.findThemeTimeOfActiveStore(themeTimeId);

        LocalDateTime startTime = parseDateTimeStringToLocalDateTime(requestDto.getStartTime());
        LocalDateTime endTime = calculateEndTime(startTime, themeTime.getTheme().getDuration());

        themeTime.updateThemeTime(startTime, endTime);
        themeTimeRepository.save(themeTime);

        return new ThemeTimeDetailResponseDto(themeTime);
    }

    @Transactional
    @DistributedLock(key = "themeTimeId:#themeTimeId")
    public void deleteThemeTime(Long themeTimeId) {
        ThemeTime themeTime = themeTimeRepository.findThemeTimeOfActiveStore(themeTimeId);
        themeTimeRepository.delete(themeTime);
    }

}