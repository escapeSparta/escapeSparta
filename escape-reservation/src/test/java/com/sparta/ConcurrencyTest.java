package com.sparta;

import com.sparta.domain.reservation.dto.ReservationCreateRequestDto;
import com.sparta.domain.reservation.entity.PaymentStatus;
import com.sparta.domain.reservation.repository.ReservationRepository;
import com.sparta.domain.reservation.service.ReservationService;
import com.sparta.domain.store.repository.StoreRepository;
import com.sparta.domain.theme.repository.ThemeRepository;
import com.sparta.domain.theme.repository.ThemeTimeRepository;
import com.sparta.domain.user.repository.UserRepository;
import com.sparta.global.exception.customException.LockException;
import com.sparta.global.exception.customException.ReservationException;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.*;
import org.mockito.BDDMockito;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.security.core.parameters.P;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@ContextConfiguration(classes = TestApplication.class)
//@ActiveProfiles("test")
public class ConcurrencyTest implements CommonData {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private StoreRepository storeRepository;

    @Autowired
    private ThemeRepository themeRepository;

    @Autowired
    private ThemeTimeRepository themeTimeRepository;

    @Autowired
    private ReservationService reservationService;

    @Mock
    ReservationCreateRequestDto requestDto;

    @BeforeEach
    public void setUp() {
        userRepository.save(consumer1);
        userRepository.save(manager);
        storeRepository.save(store);
        themeRepository.save(theme);
        themeTimeRepository.save(themeTime1);
        themeTimeRepository.save(themeTime2);
    }

    @Nested
    @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
    @Transactional
    class ConcurrencyTest1 {

        @Test
        @Order(1)
        void 에약_동시성_제어_성공() throws InterruptedException {
            System.out.println(themeTime1.getId());
            // given
            int threadNum = 10;
            AtomicInteger successCount = new AtomicInteger();
            AtomicInteger failCount = new AtomicInteger();

            BDDMockito.given(requestDto.getThemeTimeId()).willReturn(4L);
            BDDMockito.given(requestDto.getPlayer()).willReturn(2);
            BDDMockito.given(requestDto.getPrice()).willReturn(40000L);
            BDDMockito.given(requestDto.getPaymentStatus()).willReturn(PaymentStatus.COMPLETE);

            CountDownLatch doneSignal = new CountDownLatch(threadNum);
            ExecutorService executorService = Executors.newFixedThreadPool(threadNum);

            // when
            for (int i = 0; i < threadNum; i++) {
                executorService.execute(() -> {
                    try {
                        reservationService.createReservation(requestDto, consumer1);
                        successCount.getAndIncrement();
                    } catch(ReservationException | LockException e) {
                        failCount.getAndIncrement();
                    } finally {
                        doneSignal.countDown();
                    }
                });
            }

            doneSignal.await();
            executorService.shutdown();

            // then
            assertEquals(1, successCount.get());
            assertEquals(threadNum - 1, failCount.get());
        }

    }


//            IntStream.range(0, 100)
//                    .parallel()
//                    .forEach((x) -> {
//                        try {
//                            // when
//                            reservationService.createReservation(requestDto, consumer1);
//                        }
//                    });

}
