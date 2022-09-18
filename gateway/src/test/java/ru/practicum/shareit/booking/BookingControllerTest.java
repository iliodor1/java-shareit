package ru.practicum.shareit.booking;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.dto.BookingStatus;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = BookingController.class)
class BookingControllerTest {

    @Autowired
    private ObjectMapper mapper;

    @MockBean
    private BookingClient client;

    @Autowired
    private MockMvc mvc;

    private final BookingRequestDto bookingDto = BookingRequestDto.builder()
                                                                  .id(1L)
                                                                  .itemId(10L)
                                                                  .build();

    @Test
    void whenCreateBooking_thenReturnBookingStatus2xx() throws Exception {
        mvc.perform(post("/bookings")
                   .content(mapper.writeValueAsString(bookingDto))
                   .header("X-Sharer-User-Id", 1L)
                   .characterEncoding(StandardCharsets.UTF_8)
                   .contentType(MediaType.APPLICATION_JSON)
                   .accept(MediaType.APPLICATION_JSON))
           .andExpect(status().isOk());
    }

    @Test
    void whenCreateBookingPastStart_thenReturnStatus4xx() throws Exception {
        BookingDto bookingPastStartDto = BookingDto.builder()
                                                   .id(1L)
                                                   .start(LocalDateTime.now()
                                                                       .minusHours(1))
                                                   .build();

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
        BookingDto bookingPastStartDto = BookingDto.builder()
                                                   .id(1L)
                                                   .end(LocalDateTime.now()
                                                                     .minusHours(1))
                                                   .build();

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
        BookingDto bookingDto = BookingDto.builder()
                                          .id(1L)
                                          .status(BookingStatus.APPROVED)
                                          .build();

        mvc.perform(patch("/bookings/" + bookingDto.getId())
                   .param("approved", "true")
                   .header("X-Sharer-User-Id", 1L))
           .andExpect(status().isOk());
    }

    @Test
    void whenGetBookingExist_thenReturnBookingStatus2xx() throws Exception {

        mvc.perform(get("/bookings/" + bookingDto.getId())
                   .header("X-Sharer-User-Id", 1L))
           .andExpect(status().isOk());
    }

    @Test
    void whenGetByBookerId_thenReturnStatus2xx() throws Exception {
        mvc.perform(get("/bookings")
                   .header("X-Sharer-User-Id", 1L)
                   .param("state", "")
                   .param("from", "0")
                   .param("size", "1"))
           .andExpect(status().isOk());
    }

    @Test
    void whenGetByBookerIdWithStateNotExist_thenReturnBadRequest() throws Exception {
        mvc.perform(get("/bookings")
                   .header("X-Sharer-User-Id", 1L)
                   .param("state", "NOT-EXIST")
                   .param("from", "")
                   .param("size", ""))
           .andExpect(status().isBadRequest());
    }

    @Test
    void whenGetByBookerIdWithNegativeFrom_thenReturnBadRequest() throws Exception {
        mvc.perform(get("/bookings")
                   .header("X-Sharer-User-Id", 1L)
                   .param("state", "")
                   .param("from", "-1")
                   .param("size", ""))
           .andExpect(status().isBadRequest());
    }

    @Test
    void whenGetByBookerIdWithZeroSize_thenReturnBadRequest() throws Exception {
        mvc.perform(get("/bookings")
                   .header("X-Sharer-User-Id", 1L)
                   .param("state", "")
                   .param("from", "")
                   .param("size", "0"))
           .andExpect(status().isBadRequest());
    }

    @Test
    void whenGetByOwnerId_thenReturnStatus2xx() throws Exception {
        mvc.perform(get("/bookings/owner")
                   .header("X-Sharer-User-Id", 1L)
                   .param("state", "")
                   .param("from", "")
                   .param("size", ""))
           .andExpect(status().isOk());
    }

    @Test
    void whenGetByOwnerIdWithStateNotExist_thenReturnBadRequest() throws Exception {
        mvc.perform(get("/bookings/owner")
                   .header("X-Sharer-User-Id", 1L)
                   .param("state", "NOT-EXIST")
                   .param("from", "")
                   .param("size", ""))
           .andExpect(status().isBadRequest());
    }

    @Test
    void whenGetByOwnerIdWithNegativeFrom_thenReturnBadRequest() throws Exception {
        mvc.perform(get("/bookings/owner")
                   .header("X-Sharer-User-Id", 1L)
                   .param("state", "")
                   .param("from", "-1")
                   .param("size", ""))
           .andExpect(status().isBadRequest());
    }

    @Test
    void whenGetByOwnerIdZeroSize_thenReturnBadRequest() throws Exception {
        mvc.perform(get("/bookings/owner")
                   .header("X-Sharer-User-Id", 1L)
                   .param("state", "")
                   .param("from", "")
                   .param("size", "0"))
           .andExpect(status().isBadRequest());
    }

}
