package tassist.address.logic.commands;

import static java.util.Objects.requireNonNull;
import static tassist.address.commons.util.CollectionUtil.requireAllNonNull;
import static tassist.address.logic.parser.CliSyntax.PREFIX_CLASS;
import static tassist.address.model.Model.PREDICATE_SHOW_ALL_PERSONS;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.logging.Logger;

import tassist.address.commons.core.index.Index;
import tassist.address.logic.Messages;
import tassist.address.logic.commands.exceptions.CommandException;
import tassist.address.model.Model;
import tassist.address.model.person.ClassNumber;
import tassist.address.model.person.Person;
import tassist.address.model.person.StudentId;

/**
 * Assigns a student to a tutorial class identified using it's displayed index from the address book.
 */
public class ClassCommand extends Command {

    public static final String COMMAND_WORD = "class";

    public static final String MESSAGE_USAGE = COMMAND_WORD
            + ": Assign a tutorial class to the student identified"
            + "by the index in the displayed list or student ID.\n"
            + "Existing tutorial class will be overwritten by the input.\n"
            + "Parameters: INDEX (must be a positive integer) or STUDENTID "
            + PREFIX_CLASS + " T or R + [Tutorial Class Number xx (xx = 01-99)]\n"
            + "Example: " + COMMAND_WORD + " 1 " + PREFIX_CLASS + "T01\n"
            + "or " + COMMAND_WORD + " A0123456B " + PREFIX_CLASS + "T01";

    public static final String MESSAGE_ADD_CLASS_SUCCESS = "Assigned tutorial class to student: %1$s";
    public static final String MESSAGE_REMOVE_CLASS_SUCCESS = "Removed tutorial class from student: %1$s";
    public static final String MESSAGE_INVALID_CLASS =
            "Class numbers should be either 'No tutorial assigned'\n"
                    + "+ or of the format 'Txx/Rxx' (where xx is 01-99).\n"
                    + "'T/R' must be capitalized.";

    private static final Logger logger = Logger.getLogger(ClassCommand.class.getName());

    private final Index index;
    private final ClassNumber classNumber;
    private final StudentId studentId;

    /**
     * Constructs a {@code ClassCommand} that assigns a class to a student identified by their index in the displayed
     * list.
     *
     * @param index The index of the student in the currently displayed student list.
     * @param classNumber The class number to assign to the student.
     */
    public ClassCommand(Index index, ClassNumber classNumber) {
        requireAllNonNull(index, classNumber);

        this.index = index;
        this.classNumber = classNumber;
        this.studentId = null;
    }

    /**
     * Constructs a {@code ClassCommand} that assigns a class to a student identified by their student ID.
     *
     * @param studentId A unique ID identifying the student.
     * @param classNumber The class number to assign to the student.
     */
    public ClassCommand(StudentId studentId, ClassNumber classNumber) {
        requireAllNonNull(studentId, classNumber);

        this.classNumber = classNumber;
        this.studentId = studentId;
        this.index = null;
    }

    @Override
    public CommandResult execute(Model model) throws CommandException {
        requireNonNull(model);
        logger.info("Executing ClassCommand with " + (index != null ? "Index: " + index : "Student ID: "
                + studentId));

        Person personToEdit;

        if (index != null) {
            List<Person> lastShownList = model.getFilteredPersonList();
            if (index.getZeroBased() >= lastShownList.size()) {
                throw new CommandException(Messages.MESSAGE_INVALID_PERSON_DISPLAYED_INDEX);
            }
            personToEdit = lastShownList.get(index.getZeroBased());
        } else if (studentId != null) {
            Optional<Person> personOptional = model.getFilteredPersonList().stream()
                    .filter(person -> person.getStudentId().equals(studentId)).findFirst();
            if (personOptional.isEmpty()) {
                throw new CommandException(Messages.MESSAGE_PERSON_NOT_FOUND + studentId);
            }
            personToEdit = personOptional.get();
        } else {
            // won't reach this line, throwing an assertion just in case
            throw new AssertionError("Either index or student ID must be provided");
        }

        Person editedPerson = new Person(
                personToEdit.getName(),
                personToEdit.getPhone(),
                personToEdit.getEmail(),
                classNumber,
                personToEdit.getStudentId(),
                personToEdit.getGithub(),
                personToEdit.getProjectTeam(),
                personToEdit.getTags(),
                personToEdit.getProgress(),
                personToEdit.getTimedEventsList()
        );

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
        return Objects.equals(index, otherClassCommand.index)
                && Objects.equals(studentId, otherClassCommand.studentId)
                && Objects.equals(classNumber, otherClassCommand.classNumber);
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder()
                .append("ClassCommand{");
        if (index != null) {
            builder.append("index=").append(index);
        } else if (studentId != null) {
            builder.append("studentId=").append(studentId);
        }
        builder.append(", classNumber=").append(classNumber)
                .append("}");
        return builder.toString();
    }
}
