package ru.practicum.shareit.booking;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.model.BookingState;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.user.dto.UserDto;

import javax.validation.ValidationException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = BookingController.class)
class BookingControllerTest {

    @Autowired
    private ObjectMapper mapper;

    @MockBean
    private BookingService service;

    @Autowired
    private MockMvc mvc;

    private final BookingRequestDto bookingRequestDto = BookingRequestDto.builder()
                                                    .id(1L)
                                                    .itemId(10L)
                                                    .build();

    private final BookingDto bookingStatusDto
            = BookingDto.builder()
                                  .id(1L)
                                  .status(BookingStatus.WAITING)
                                  .build();

    @Test
    void whenCreateBooking_thenReturnBookingStatus2xx() throws Exception {
        when(service.create(1L, bookingRequestDto)).thenReturn(bookingStatusDto);

        mvc.perform(post("/bookings")
                   .content(mapper.writeValueAsString(bookingRequestDto))
                   .header("X-Sharer-User-Id", 1L)
                   .characterEncoding(StandardCharsets.UTF_8)
                   .contentType(MediaType.APPLICATION_JSON)
                   .accept(MediaType.APPLICATION_JSON))
           .andExpect(status().isOk())
           .andExpect(jsonPath("$.id", is(bookingStatusDto.getId()), Long.class))
           .andExpect(jsonPath("$.status", is(bookingStatusDto.getStatus()
                                                              .toString())));
    }

    @Test
    void whenCreateBookingPastStart_thenReturnStatus4xx() throws Exception {
        BookingRequestDto bookingPastStartDto = BookingRequestDto.builder()
                                                   .id(1L)
                                                   .itemId(10L)
                                                   .start(LocalDateTime.now()
                                                                       .minusHours(1))
                                                   .build();

        when(service.create(1L, bookingPastStartDto)).thenThrow(ValidationException.class);

        mvc.perform(post("/bookings")
                   .content(mapper.writeValueAsString(bookingPastStartDto))
                   .header("X-Sharer-User-Id", 1L)
                   .characterEncoding(StandardCharsets.UTF_8)
                   .contentType(MediaType.APPLICATION_JSON)
                   .accept(MediaType.APPLICATION_JSON))
           .andExpect(status().is4xxClientError());
    }

    @Test
    void whenCreateBookingPastEnd_thenReturnStatus4xx() throws Exception {
        BookingRequestDto bookingPastStartDto = BookingRequestDto.builder()
                                                   .id(1L)
                                                   .itemId(10L)
                                                   .end(LocalDateTime.now()
                                                                     .minusHours(1))
                                                   .build();

        when(service.create(1L, bookingPastStartDto)).thenThrow(ValidationException.class);

        mvc.perform(post("/bookings")
                   .content(mapper.writeValueAsString(bookingPastStartDto))
                   .header("X-Sharer-User-Id", 1L)
                   .characterEncoding(StandardCharsets.UTF_8)
                   .contentType(MediaType.APPLICATION_JSON)
                   .accept(MediaType.APPLICATION_JSON))
           .andExpect(status().is4xxClientError());
    }

    @Test
    void whenApproveBooking_thenReturnApprovedBookingStatus2xx() throws Exception {
        BookingDto approvedBookingDto
                = BookingDto.builder()
                                      .id(1L)
                                      .status(BookingStatus.APPROVED)
                                      .build();

        when(service.approve(1L, 1L, true)).thenReturn(approvedBookingDto);

        mvc.perform(patch("/bookings/" + approvedBookingDto.getId())
                   .param("approved", "true")
                   .header("X-Sharer-User-Id", 1L))
           .andExpect(status().isOk())
           .andExpect(jsonPath("$.id", is(bookingStatusDto.getId()), Long.class))
           .andExpect(jsonPath("$.status", is(approvedBookingDto.getStatus()
                                                                .toString())));
    }

    @Test
    void whenGetBookingExist_thenReturnBookingStatus2xx() throws Exception {
        when(service.getById(1L, 1L)).thenReturn(bookingStatusDto);

        mvc.perform(get("/bookings/" + bookingStatusDto.getId())
                   .header("X-Sharer-User-Id", 1L))
           .andExpect(status().isOk())
           .andExpect(jsonPath("$.id", is(bookingStatusDto.getId()), Long.class))
           .andExpect(jsonPath("$.status", is(bookingStatusDto.getStatus()
                                                              .toString())));
    }

    @Test
    void whenGetByBookerId_thenReturnListOfBookingsStatus2xx() throws Exception {
        List<BookingDto> bookingsStatusDto = new ArrayList<>();

        UserDto booker = createUserDto(1L);

        for (int i = 1; i < 4; i++) {
            BookingDto booking = BookingDto.builder()
                                                               .id((long) i)
                                                               .booker(booker)
                                                               .status(BookingStatus.WAITING)
                                                               .build();

            bookingsStatusDto.add(booking);
        }

        when(service.getByBookerId(1L, BookingState.ALL, 0, 20))
                .thenReturn(bookingsStatusDto);

        mvc.perform(get("/bookings")
                   .header("X-Sharer-User-Id", 1L)
                   .param("state", "")
                   .param("from", "")
                   .param("size", ""))
           .andExpect(status().isOk())
           .andExpect(jsonPath("$.length()").value(bookingsStatusDto.size()));
    }

    @Test
    void whenGetByOwnerId_thenReturnListOfBookingsStatus2xx() throws Exception {
        List<BookingDto> bookingsStatusDto = new ArrayList<>();

        for (int i = 1; i < 4; i++) {
            UserDto booker = createUserDto(i);

            BookingDto booking = BookingDto.builder()
                                                               .id((long) i)
                                                               .booker(booker)
                                                               .status(BookingStatus.WAITING)
                                                               .build();

            bookingsStatusDto.add(booking);
        }

        when(service.getByOwnerId(1L, BookingState.ALL, 0, 20))
                .thenReturn(bookingsStatusDto);

        mvc.perform(get("/bookings/owner")
                   .header("X-Sharer-User-Id", 1L)
                   .param("state", "")
                   .param("from", "")
                   .param("size", ""))
           .andExpect(status().isOk())
           .andExpect(jsonPath("$.length()").value(bookingsStatusDto.size()));
    }

    private UserDto createUserDto(long i) {
        return UserDto.builder()
                      .id(i)
                      .build();
    }

}
