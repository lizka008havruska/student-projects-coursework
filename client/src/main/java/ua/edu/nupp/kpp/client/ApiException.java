package ua.edu.nupp.kpp.client;

/** Помилка взаємодії із сервером: недоступність, код 400/500 або некоректний JSON. */
public class ApiException extends RuntimeException {
    public ApiException(String message) { super(message); }
}
