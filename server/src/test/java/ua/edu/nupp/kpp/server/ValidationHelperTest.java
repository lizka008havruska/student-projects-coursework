package ua.edu.nupp.kpp.server;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class ValidationHelperTest {
    @Test
    void requireTextTrimsValue() {
        assertEquals("Назва", Validation.requireText("  Назва  ", "Назва", 50));
    }

    @Test
    void requireTextRejectsNullAndBlank() {
        assertThrows(IllegalArgumentException.class, () -> Validation.requireText(null, "Назва", 50));
        assertThrows(IllegalArgumentException.class, () -> Validation.requireText("   ", "Назва", 50));
    }

    @Test
    void requireTextRejectsTooLongValue() {
        assertThrows(IllegalArgumentException.class, () -> Validation.requireText("abcdef", "Назва", 5));
    }

    @Test
    void rangeAcceptsBoundariesAndRejectsOutside() {
        assertDoesNotThrow(() -> Validation.range(0, 0, 100, "F1"));
        assertDoesNotThrow(() -> Validation.range(100, 0, 100, "F1"));
        assertThrows(IllegalArgumentException.class, () -> Validation.range(100.01, 0, 100, "F1"));
        assertThrows(IllegalArgumentException.class, () -> Validation.range(Double.NaN, 0, 100, "F1"));
    }

    @Test
    void positiveIdRejectsZeroAndNegative() {
        assertDoesNotThrow(() -> Validation.positiveId(1, "Проєкт"));
        assertThrows(IllegalArgumentException.class, () -> Validation.positiveId(0, "Проєкт"));
        assertThrows(IllegalArgumentException.class, () -> Validation.positiveId(-5, "Проєкт"));
    }

    @Test
    void emailFormatCheck() {
        assertTrue(Validation.isEmail("student@example.com"));
        assertFalse(Validation.isEmail("student@"));
        assertFalse(Validation.isEmail("student example.com"));
        assertFalse(Validation.isEmail(null));
    }
}
