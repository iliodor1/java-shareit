package ru.practicum.shareit.item.dto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.validator.Marker.OnCreate;
import ru.practicum.shareit.validator.Marker.OnUpdate;
import ru.practicum.shareit.validator.NullOrNotBlank;

@Data
@Builder
@AllArgsConstructor
public class ItemDto {
    private final Long id;
    @NotBlank(groups = OnCreate.class, message = "The name should not be null or blank.")
    @NullOrNotBlank(groups = OnUpdate.class, message = "The name should be null or not blank.")
    private final String name;

    @NotBlank(groups = OnCreate.class, message = "Description should not be null or blank.")
    @NullOrNotBlank(groups = OnUpdate.class, message = "Description should be null or not blank.")
    private final String description;

    @NotNull(groups = OnCreate.class, message = "The available should be not null.")
    private final Boolean available;

    private final Long requestId;

}
