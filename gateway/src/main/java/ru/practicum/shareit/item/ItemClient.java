package ru.practicum.shareit.item;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.shareit.client.BaseClient;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;

import java.util.Map;

@Service
public class ItemClient extends BaseClient {

    private static final String API_PREFIX = "/items";

    public ItemClient(@Value("${shareit-server.url}") String serverUrl, RestTemplateBuilder builder) {
        super(
                builder.uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl + API_PREFIX))
                       .requestFactory(HttpComponentsClientHttpRequestFactory::new)
                       .build()
        );
    }


    public ResponseEntity<Object> getItem(Long itemId, Long userId) {
        return get("/" + itemId, userId);
    }

    public ResponseEntity<Object> getOwnItems(Long ownerId, Integer from, Integer size) {
        Map<String, Object> parameters = Map.of(
                "from", from,
                "size", size
        );

        return get("?from={from}&size={size}", ownerId, parameters);
    }

    public ResponseEntity<Object> create(Long ownerId, ItemDto itemDto) {
        return post("/", ownerId, itemDto);
    }

    public ResponseEntity<Object> update(Long ownerId, Long id, ItemDto itemDto) {
        return patch("/" + id, ownerId, itemDto);
    }

    public ResponseEntity<Object> searchItem(
            Long userId,
            String text,
            Integer from,
            Integer size
    ) {
        Map<String, Object> parameters = Map.of(
                "text", text,
                "from", from,
                "size", size
        );

        return get("/search?text={text}&from={from}&size={size}", userId, parameters);
    }

    public ResponseEntity<Object> createComment(Long bookerId, Long itemId, CommentDto commentDto) {
        String path = String.format("/%s/comment", itemId);

        return post(path, bookerId, commentDto);
    }

}
