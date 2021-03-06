package seedu.address.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.model.Model.PREDICATE_SHOW_ALL_FLASHCARD;
import static seedu.address.testutil.Assert.assertThrows;
import static seedu.address.testutil.TypicalFlashcards.BUBBLE_SORT;
import static seedu.address.testutil.TypicalFlashcards.HEAPING;

import java.nio.file.Path;
import java.nio.file.Paths;

import org.junit.jupiter.api.Test;

import seedu.address.commons.core.GuiSettings;
import seedu.address.model.person.NameContainsKeywordsPredicate;
import seedu.address.testutil.FlashcardBookBuilder;

public class ModelManagerTest {

    private ModelManager modelManager = new ModelManager();

    @Test
    public void constructor() {
        assertEquals(new UserPrefs(), modelManager.getUserPrefs());
        assertEquals(new GuiSettings(), modelManager.getGuiSettings());
        assertEquals(new FlashcardBook(), new FlashcardBook(modelManager.getFlashcardBook()));
    }

    @Test
    public void setUserPrefs_nullUserPrefs_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> modelManager.setUserPrefs(null));
    }

    @Test
    public void setUserPrefs_validUserPrefs_copiesUserPrefs() {
        UserPrefs userPrefs = new UserPrefs();
        userPrefs.setAddressBookFilePath(Paths.get("address/book/file/path"));
        userPrefs.setGuiSettings(new GuiSettings(1, 2, 3, 4));
        modelManager.setUserPrefs(userPrefs);
        assertEquals(userPrefs, modelManager.getUserPrefs());

        // Modifying userPrefs should not modify modelManager's userPrefs
        UserPrefs oldUserPrefs = new UserPrefs(userPrefs);
        userPrefs.setAddressBookFilePath(Paths.get("new/address/book/file/path"));
        assertEquals(oldUserPrefs, modelManager.getUserPrefs());
    }

    @Test
    public void setGuiSettings_nullGuiSettings_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> modelManager.setGuiSettings(null));
    }

    @Test
    public void setGuiSettings_validGuiSettings_setsGuiSettings() {
        GuiSettings guiSettings = new GuiSettings(1, 2, 3, 4);
        modelManager.setGuiSettings(guiSettings);
        assertEquals(guiSettings, modelManager.getGuiSettings());
    }

    @Test
    public void setFlashcardBookFilePath_nullPath_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> modelManager.setFlashcardBookFilePath(null));
    }

    @Test
    public void setFlashcardBookFilePath_validPath_setsFlashcardBookFilePath() {
        Path path = Paths.get("address/book/file/path");
        modelManager.setFlashcardBookFilePath(path);
        assertEquals(path, modelManager.getFlashcardBookFilePath());
    }

    @Test
    public void hasFlashcard_nullFlashcard_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> modelManager.hasFlashcard(null));
    }

    @Test
    public void hasFlashcard_flashcardNotInFlashcardBook_returnsFalse() {
        assertFalse(modelManager.hasFlashcard(BUBBLE_SORT));
    }

    @Test
    public void hasFlashcard_flashcardInFlashcardBook_returnsTrue() {
        modelManager.addFlashcard(BUBBLE_SORT);
        assertTrue(modelManager.hasFlashcard(BUBBLE_SORT));
    }

    @Test
    public void getFilteredPersonList_modifyList_throwsUnsupportedOperationException() {
        assertThrows(UnsupportedOperationException.class, () -> modelManager.getFilteredFlashcardList().remove(0));
    }

    @Test
    public void equals() {
        FlashcardBook flashcardBook = new FlashcardBookBuilder().withFlashcard(BUBBLE_SORT)
            .withFlashcard(HEAPING).build();
        FlashcardBook differentFlashcardBook = new FlashcardBook();
        UserPrefs userPrefs = new UserPrefs();

        // same values -> returns true
        modelManager = new ModelManager(flashcardBook, userPrefs);
        ModelManager modelManagerCopy = new ModelManager(flashcardBook, userPrefs);
        assertTrue(modelManager.equals(modelManagerCopy));

        // same object -> returns true
        assertTrue(modelManager.equals(modelManager));

        // null -> returns false
        assertFalse(modelManager.equals(null));

        // different types -> returns false
        assertFalse(modelManager.equals(5));

        // different addressBook -> returns false
        assertFalse(modelManager.equals(new ModelManager(differentFlashcardBook, userPrefs)));

        // different filteredList -> returns false
        String[] keywords = BUBBLE_SORT.getTitle().fullTitle.split("\\s+");
        modelManager.updateFilteredFlashcardList(new NameContainsKeywordsPredicate(keywords[0]));
        assertFalse(modelManager.equals(new ModelManager(flashcardBook, userPrefs)));

        // resets modelManager to initial state for upcoming tests
        modelManager.updateFilteredFlashcardList(PREDICATE_SHOW_ALL_FLASHCARD);

        // different userPrefs -> returns false
        UserPrefs differentUserPrefs = new UserPrefs();
        differentUserPrefs.setAddressBookFilePath(Paths.get("differentFilePath"));
        assertFalse(modelManager.equals(new ModelManager(flashcardBook, differentUserPrefs)));
    }
}
