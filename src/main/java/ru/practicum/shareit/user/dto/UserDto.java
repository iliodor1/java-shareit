package ru.practicum.shareit.user.dto;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import ru.practicum.shareit.validator.Marker.OnCreate;
import ru.practicum.shareit.validator.Marker.OnUpdate;
import ru.practicum.shareit.validator.NullOrNotBlank;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

@Data
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@Builder
@RequiredArgsConstructor
public class UserDto {
    Long id;

    @NotBlank(groups = OnCreate.class, message = "The name should not be null or blank.")
    @NullOrNotBlank(groups = OnUpdate.class, message = "The name should be null or not blank.")
    String name;

    @Email
    @NotBlank(groups = OnCreate.class, message = "The email should not be null or blank.")
    @NullOrNotBlank(groups = OnUpdate.class, message = "The email should be null or not blank.")
    String email;

}
