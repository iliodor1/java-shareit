package ru.practicum.shareit.booking;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.dto.BookingState;
import ru.practicum.shareit.client.BaseClient;

import java.util.Map;

@Service
public class BookingClient extends BaseClient {
    private static final String API_PREFIX = "/bookings";

    @Autowired
    public BookingClient(@Value("${shareit-server.url}") String serverUrl, RestTemplateBuilder builder) {
        super(
                builder
                        .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl + API_PREFIX))
                        .requestFactory(HttpComponentsClientHttpRequestFactory::new)
                        .build()
        );
    }

    public ResponseEntity<Object> create(Long bookerId, BookingRequestDto bookingRequestDto) {
        return post("/", bookerId, bookingRequestDto);
    }

    public ResponseEntity<Object> approve(
            Long ownerId,
            Long bookingId,
            boolean approved
    ) {
        Map<String, Object> parameters = Map.of("approved", approved);

        return patch("/" + bookingId + "?approved={approved}", ownerId, parameters, null);
    }

    public ResponseEntity<Object> getById(Long userId, Long bookingId) {
        return get("/" + bookingId, userId);
    }

    public ResponseEntity<Object> getByBookerId(
            Long bookerId,
            BookingState enumState,
            Integer from,
            Integer size
    ) {

        Map<String, Object> parameters = Map.of(
                "state", enumState.name(),
                "from", from,
                "size", size
        );

        return get("?state={state}&from={from}&size={size}", bookerId, parameters);
    }

    public ResponseEntity<Object> getByOwnerId(
            Long ownerId,
            BookingState enumState,
            Integer from,
            Integer size
    ) {
        Map<String, Object> parameters = Map.of(
                "state", enumState.name(),
                "from", from,
                "size", size
        );

        return get("/owner?state={state}&from={from}&size={size}", ownerId, parameters);
    }

}
