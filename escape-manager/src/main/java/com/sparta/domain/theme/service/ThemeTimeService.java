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
import com.sparta.global.exception.customException.ThemeTimeException;
import com.sparta.global.exception.errorCode.ThemeTimeErrorCode;
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
public class ThemeTimeService {
    private final ThemeTimeRepository themeTimeRepository;
    private final ThemeRepository themeRepository;
    private final ReservationRepository reservationRepository;

    @Transactional
    public ThemeTimeDetailResponseDto createThemeTime(Long themeId, ThemeTimeCreateRequestDto requestDto, User user) {
        Theme theme = themeRepository.findThemeOfActiveStore(themeId);
        theme.getStore().checkManager(user);

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

    public List<ThemeTimeDetailResponseDto> getThemeTimes(Long themeId, String date, User user) {
        Theme theme = themeRepository.findThemeOfActiveStore(themeId);
        theme.getStore().checkManager(user);

        List<ThemeTime> themeTimes;
        if(date == null) {
            // 매니저는 비활성화된 Theme의 예약 시간대도 조회 가능
            themeTimes = themeTimeRepository.findAllByThemeId(themeId);
        } else {
            LocalDate searchDate = parseDateStringToLocalDate(date);
            themeTimes = themeTimeRepository.findThemeTimesByDate(themeId, searchDate);
        }

        return themeTimes.stream().map(ThemeTimeDetailResponseDto::new).toList();
    }

    @Transactional
    @DistributedLock(key = "themeTimeId:#themeTimeId")
    public ThemeTimeDetailResponseDto modifyThemeTime(Long themeTimeId, ThemeTimeModifyRequestDto requestDto, User user) {
        ThemeTime themeTime = themeTimeRepository.findThemeTimeOfActiveStore(themeTimeId);
        themeTime.getTheme().getStore().checkManager(user);

        checkForExistingReservation(themeTime);

        LocalDateTime startTime = parseDateTimeStringToLocalDateTime(requestDto.getStartTime());
        LocalDateTime endTime = calculateEndTime(startTime, themeTime.getTheme().getDuration());

        themeTime.updateThemeTime(startTime, endTime);
        themeTimeRepository.save(themeTime);

        return new ThemeTimeDetailResponseDto(themeTime);
    }

    @Transactional
    @DistributedLock(key = "themeTimeId:#themeTimeId")
    public void deleteThemeTime(Long themeTimeId, User user) {
        ThemeTime themeTime = themeTimeRepository.findThemeTimeOfActiveStore(themeTimeId);
        themeTime.getTheme().getStore().checkManager(user);

        checkForExistingReservation(themeTime);

        themeTimeRepository.delete(themeTime);
    }

    private void checkForExistingReservation(ThemeTime themeTime) {
        if(reservationRepository.findByThemeTime(themeTime) != null) {
            throw new ThemeTimeException(ThemeTimeErrorCode.THEME_TIME_HAS_RESERVATION);
        }
    }

}