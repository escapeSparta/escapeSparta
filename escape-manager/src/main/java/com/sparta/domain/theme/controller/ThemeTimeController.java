package com.sparta.domain.theme.controller;

import com.sparta.domain.theme.dto.request.ThemeTimeCreateRequestDto;
import com.sparta.domain.theme.dto.request.ThemeTimeModifyRequestDto;
import com.sparta.domain.theme.dto.response.ThemeTimeDetailResponseDto;
import com.sparta.domain.theme.service.ThemeTimeService;
import com.sparta.global.response.ResponseMessage;
import com.sparta.security.UserDetailsImpl;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@Secured("MANAGER")
@RequestMapping("/manager/stores/themes")
@RequiredArgsConstructor
public class ThemeTimeController {
    private final ThemeTimeService themeTimeService;

    @PostMapping("/{themeId}/theme-time")
    public ResponseEntity<ResponseMessage<ThemeTimeDetailResponseDto>> createThemeTime(
            @PathVariable Long themeId,
            @Valid @RequestBody ThemeTimeCreateRequestDto requestDto,
            @AuthenticationPrincipal UserDetailsImpl userDetails
    ) {
        ThemeTimeDetailResponseDto responseDto = themeTimeService.createThemeTime(themeId, requestDto, userDetails.getUser());

        ResponseMessage<ThemeTimeDetailResponseDto> responseMessage = ResponseMessage.<ThemeTimeDetailResponseDto>builder()
                .statusCode(HttpStatus.CREATED.value())
                .message("방탈출 테마 예약 시간대 등록이 완료되었습니다.")
                .data(responseDto)
                .build();

        return ResponseEntity.status(HttpStatus.CREATED).body(responseMessage);
    }

    @GetMapping("/{themeId}/theme-time")
    public ResponseEntity<ResponseMessage<List<ThemeTimeDetailResponseDto>>> getThemeTimes(
            @PathVariable Long themeId,
            @RequestParam(value = "date", required = false) String date,
            @AuthenticationPrincipal UserDetailsImpl userDetails
    ) {
        List<ThemeTimeDetailResponseDto> responseDto = themeTimeService.getThemeTimes(themeId, date, userDetails.getUser());

        ResponseMessage<List<ThemeTimeDetailResponseDto>> responseMessage = ResponseMessage.<List<ThemeTimeDetailResponseDto>>builder()
                .statusCode(HttpStatus.OK.value())
                .message("방탈출 테마 예약 시간대 조회가 완료되었습니다.")
                .data(responseDto)
                .build();

        return ResponseEntity.status(HttpStatus.OK).body(responseMessage);
    }

    @PutMapping("/theme-time/{themeTimeId}")
    public ResponseEntity<ResponseMessage<ThemeTimeDetailResponseDto>> modifyThemeTime(
            @PathVariable Long themeTimeId,
            @Valid @RequestBody ThemeTimeModifyRequestDto requestDto,
            @AuthenticationPrincipal UserDetailsImpl userDetails
    ) {
        ThemeTimeDetailResponseDto responseDto = themeTimeService.modifyThemeTime(themeTimeId, requestDto, userDetails.getUser());

        ResponseMessage<ThemeTimeDetailResponseDto> responseMessage = ResponseMessage.<ThemeTimeDetailResponseDto>builder()
                .statusCode(HttpStatus.OK.value())
                .message("방탈출 테마 예약 시간대 수정이 완료되었습니다.")
                .data(responseDto)
                .build();

        return ResponseEntity.status(HttpStatus.OK).body(responseMessage);
    }

    @DeleteMapping("/theme-time/{themeTimeId}")
    public ResponseEntity<ResponseMessage<Void>> deleteThemeTime(
            @PathVariable Long themeTimeId,
            @AuthenticationPrincipal UserDetailsImpl userDetails
    ) {
        themeTimeService.deleteThemeTime(themeTimeId, userDetails.getUser());

        ResponseMessage<Void> responseMessage = ResponseMessage.<Void>builder()
                .statusCode(HttpStatus.OK.value())
                .message("방탈출 테마 예약 시간대 삭제가 완료되었습니다.")
                .build();

        return ResponseEntity.status(HttpStatus.OK).body(responseMessage);
    }

}
