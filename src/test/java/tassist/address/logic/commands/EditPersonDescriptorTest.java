package tassist.address.logic.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static tassist.address.logic.commands.CommandTestUtil.DESC_AMY;
import static tassist.address.logic.commands.CommandTestUtil.DESC_BOB;
import static tassist.address.logic.commands.CommandTestUtil.VALID_EMAIL_BOB;
import static tassist.address.logic.commands.CommandTestUtil.VALID_NAME_BOB;
import static tassist.address.logic.commands.CommandTestUtil.VALID_PHONE_BOB;
import static tassist.address.logic.commands.CommandTestUtil.VALID_PROGRESS_BOB;
import static tassist.address.logic.commands.CommandTestUtil.VALID_TAG_HUSBAND;

import org.junit.jupiter.api.Test;

import tassist.address.logic.commands.EditCommand.EditPersonDescriptor;
import tassist.address.testutil.EditPersonDescriptorBuilder;

public class EditPersonDescriptorTest {

    @Test
    public void equals() {
        // same values -> returns true
        EditPersonDescriptor descriptorWithSameValues = new EditPersonDescriptor(DESC_AMY);
        assertTrue(DESC_AMY.equals(descriptorWithSameValues));

        // same object -> returns true
        assertTrue(DESC_AMY.equals(DESC_AMY));

        // null -> returns false
        assertFalse(DESC_AMY.equals(null));

        // different types -> returns false
        assertFalse(DESC_AMY.equals(5));

        // different values -> returns false
        assertFalse(DESC_AMY.equals(DESC_BOB));

        // different name -> returns false
        EditPersonDescriptor editedAmy = new EditPersonDescriptorBuilder(DESC_AMY).withName(VALID_NAME_BOB).build();
        assertFalse(DESC_AMY.equals(editedAmy));

        // different phone -> returns false
        editedAmy = new EditPersonDescriptorBuilder(DESC_AMY).withPhone(VALID_PHONE_BOB).build();
        assertFalse(DESC_AMY.equals(editedAmy));

        // different email -> returns false
        editedAmy = new EditPersonDescriptorBuilder(DESC_AMY).withEmail(VALID_EMAIL_BOB).build();
        assertFalse(DESC_AMY.equals(editedAmy));

        // different tags -> returns false
        editedAmy = new EditPersonDescriptorBuilder(DESC_AMY).withTags(VALID_TAG_HUSBAND).build();
        assertFalse(DESC_AMY.equals(editedAmy));

        // different progress -> returns false
        editedAmy = new EditPersonDescriptorBuilder(DESC_AMY).withProgress(VALID_PROGRESS_BOB).build();
        assertFalse(DESC_AMY.equals(editedAmy));
    }

    @Test
    public void toStringMethod() {
        EditPersonDescriptor editPersonDescriptor = new EditPersonDescriptor();
        String expected = EditPersonDescriptor.class.getCanonicalName() + "{name="
                + editPersonDescriptor.getName().orElse(null) + ", phone="
                + editPersonDescriptor.getPhone().orElse(null) + ", email="
                + editPersonDescriptor.getEmail().orElse(null) + ", classNumber="
                + editPersonDescriptor.getClassNumber().orElse(null) + ", studentId="
                + editPersonDescriptor.getStudentId().orElse(null) + ", github="
                + editPersonDescriptor.getGithub().orElse(null) + ", tags="
                + editPersonDescriptor.getTags().orElse(null) + ", progress="
                + editPersonDescriptor.getProgress().orElse(null) + "}";
        assertEquals(expected, editPersonDescriptor.toString());
    }
}
