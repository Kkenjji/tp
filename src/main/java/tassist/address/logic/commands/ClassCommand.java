package tassist.address.logic.commands;

import static java.util.Objects.requireNonNull;
import static tassist.address.commons.util.CollectionUtil.requireAllNonNull;
import static tassist.address.logic.parser.CliSyntax.PREFIX_CLASS;
import static tassist.address.model.Model.PREDICATE_SHOW_ALL_PERSONS;

import java.util.List;

import tassist.address.commons.core.index.Index;
import tassist.address.logic.Messages;
import tassist.address.logic.commands.exceptions.CommandException;
import tassist.address.model.Model;
import tassist.address.model.person.ClassNumber;
import tassist.address.model.person.Person;

/**
 * Assigns a student to a tutorial class identified using it's displayed index from the address book.
 */
public class ClassCommand extends Command {

    public static final String COMMAND_WORD = "class";

    public static final String MESSAGE_USAGE = COMMAND_WORD
            + ": Assign a tutorial class to the student identified"
            + "by the index. " // will be changed to student id once ready
            + "Existing tutorial class will be overwritten by the input.\n"
            + "Parameters: INDEX (must be a positive integer) "
            + PREFIX_CLASS + " T/R + [Tutorial Class Number xx (xx = 01-99)]\n"
            + "Example: " + COMMAND_WORD + " 1 "
            + PREFIX_CLASS + " T01";

    public static final String MESSAGE_ADD_CLASS_SUCCESS = "Assigned tutorial class to student: %1$s";
    public static final String MESSAGE_REMOVE_CLASS_SUCCESS = "Removed tutorial class from student: %1$s";
    public static final String MESSAGE_INVALID_CLASS =
            "Class numbers should be either 'No tutorial assigned'\n"
                    + "+ or of the format 'Txx/Rxx' (where xx is 01-99).\n"
                    + "'T/R' must be capitalized.";

    private final Index index;
    private final ClassNumber classNumber;

    /**
     * @param index of the student in the filtered student list to edit the tutorial class number
     * @param classNumber of the student to be updated to
     */
    public ClassCommand(Index index, ClassNumber classNumber) {
        requireAllNonNull(index, classNumber);

        this.index = index;
        this.classNumber = classNumber;
    }

    @Override
    public CommandResult execute(Model model) throws CommandException {
        requireNonNull(model);
        List<Person> lastShownList = model.getFilteredPersonList();

        if (index.getZeroBased() >= lastShownList.size()) {
            throw new CommandException(Messages.MESSAGE_INVALID_PERSON_DISPLAYED_INDEX);
        }

        Person personToEdit = lastShownList.get(index.getZeroBased());
        Person editedPerson = new Person(
                personToEdit.getName(), personToEdit.getPhone(), personToEdit.getEmail(),
                classNumber, personToEdit.getStudentId(),
                personToEdit.getGithub(), personToEdit.getTags(), personToEdit.getProgress());

        model.setPerson(personToEdit, editedPerson);
        model.updateFilteredPersonList(PREDICATE_SHOW_ALL_PERSONS);

        return new CommandResult(generateSuccessMessage(editedPerson));
    }

    /**
     * Generates a command execution success message based on whether
     * the class number is added to or removed from
     * {@code personToEdit}.
     */
    private String generateSuccessMessage(Person personToEdit) {
        String message = !classNumber.value.isBlank() ? MESSAGE_ADD_CLASS_SUCCESS : MESSAGE_REMOVE_CLASS_SUCCESS;
        return String.format(message, Messages.format(personToEdit));
    }

    @Override
    public boolean equals(Object other) {
        // short circuit if same object
        if (other == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(other instanceof ClassCommand)) {
            return false;
        }

        // state check
        ClassCommand otherClassCommand = (ClassCommand) other;
        return index.equals(otherClassCommand.index)
                && classNumber.equals(otherClassCommand.classNumber);
    }
}
