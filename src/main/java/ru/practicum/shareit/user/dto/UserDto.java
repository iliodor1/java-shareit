package ru.practicum.shareit.user.dto;

import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import ru.practicum.shareit.validator.Marker.OnCreate;
import ru.practicum.shareit.validator.Marker.OnUpdate;
import ru.practicum.shareit.validator.NullOrNotBlank;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

@Data
@Builder
@RequiredArgsConstructor
public class UserDto {
    private final Long id;

    @NotBlank(groups = OnCreate.class, message = "The name should not be null or blank.")
    @NullOrNotBlank(groups = OnUpdate.class, message = "The name should be null or not blank.")
    private final String name;

    @Email
    @NotBlank(groups = OnCreate.class, message = "The email should not be null or blank.")
    @NullOrNotBlank(groups = OnUpdate.class, message = "The email should be null or not blank.")
    private final String email;

}
