package ru.practicum.shareit.exeption;

public class ConflictRequestException extends RuntimeException {
    public ConflictRequestException() {
    }

    public ConflictRequestException(String message) {
        super(message);
    }

}
