package ua.edu.nupp.kpp.client;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class CsvProjectImporterTest {
    @Test
    void acceptsCorrectHeader() {
        assertDoesNotThrow(() -> CsvProjectImporter.checkHeader("title;description;technology;difficulty"));
    }

    @Test
    void acceptsHeaderWithBomAndDifferentCase() {
        assertDoesNotThrow(() -> CsvProjectImporter.checkHeader("\uFEFFTitle;Description;Technology;Difficulty"));
    }

    @Test
    void rejectsWrongHeader() {
        assertThrows(IllegalArgumentException.class,
                () -> CsvProjectImporter.checkHeader("name;description;technology;level"));
        assertThrows(IllegalArgumentException.class, () -> CsvProjectImporter.checkHeader("title;description"));
    }

    @Test
    void parsesCorrectLine() {
        Project p = CsvProjectImporter.parseLine("Облік фондів;Облік книг;Java/JavaFX;3");
        assertEquals("Облік фондів", p.getTitle());
        assertEquals("Облік книг", p.getDescription());
        assertEquals("Java/JavaFX", p.getTechnology());
        assertEquals(3, p.getDifficulty());
    }

    @Test
    void rejectsWrongColumnCount() {
        assertThrows(IllegalArgumentException.class, () -> CsvProjectImporter.parseLine("a;b;c"));
        assertThrows(IllegalArgumentException.class, () -> CsvProjectImporter.parseLine("a;b;c;3;extra"));
    }

    @Test
    void rejectsNonNumericDifficulty() {
        assertThrows(IllegalArgumentException.class, () -> CsvProjectImporter.parseLine("a;b;Java;abc"));
    }

    @Test
    void rejectsEmptyTitleOrTechnology() {
        assertThrows(IllegalArgumentException.class, () -> CsvProjectImporter.parseLine(";b;Java;3"));
        assertThrows(IllegalArgumentException.class, () -> CsvProjectImporter.parseLine("a;b;;3"));
    }
}
