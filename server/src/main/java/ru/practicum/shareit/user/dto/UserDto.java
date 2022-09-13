package ru.practicum.shareit.user.dto;

import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@Builder
@RequiredArgsConstructor
public class UserDto {
    private final Long id;
    private final String name;
    private final String email;

}
