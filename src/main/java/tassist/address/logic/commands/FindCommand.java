package tassist.address.logic.commands;

import static java.util.Objects.requireNonNull;
import static tassist.address.logic.Messages.MESSAGE_PERSONS_LISTED_OVERVIEW;

import java.util.Objects;
import java.util.function.Predicate;

import tassist.address.commons.util.ToStringBuilder;
import tassist.address.model.Model;
import tassist.address.model.person.NameContainsKeywordsPredicate;
import tassist.address.model.person.Person;

/**
 * Finds and lists all persons in address book whose name
 * or studentId matches any of the argument keywords.
 * Keyword matching is case-insensitive.
 */
public class FindCommand extends Command {

    public static final String COMMAND_WORD = "find";

    public static final String MESSAGE_USAGE = COMMAND_WORD + ": Finds all persons whose names contain any of "
            + "the specified keywords (case-insensitive) or whose student ID matches exactly "
            + "and displays them as a list with index numbers.\n"
            + "Parameters: KEYWORD [MORE_KEYWORDS]... or STUDENTID\n"
            + "Example: " + COMMAND_WORD + " alice bob charlie" + " or " + COMMAND_WORD + " A1234567B";

    private final NameContainsKeywordsPredicate namePredicate;
    private final Predicate<Person> studentIdPredicate;

    /**
     * Constructs a FindCommand that filters by student ID.
     *
     * @param studentIdPredicate A predicate that returns true if the student ID matches the input exactly.
     */
    public FindCommand(Predicate<Person> studentIdPredicate) {
        this.studentIdPredicate = studentIdPredicate;
        this.namePredicate = null;
    }

    /**
     * Constructs a FindCommand that filters by name keywords.
     *
     * @param namePredicate A predicate that returns true if any part of the person's name matches the given
     *                      keywords (case-insensitive).
     */
    public FindCommand(NameContainsKeywordsPredicate namePredicate) {
        this.namePredicate = namePredicate;
        this.studentIdPredicate = null;
    }

    @Override
    public CommandResult execute(Model model) {
        requireNonNull(model);
        if (studentIdPredicate != null) {
            model.updateFilteredPersonList(studentIdPredicate);
            return new CommandResult(
                    String.format(MESSAGE_PERSONS_LISTED_OVERVIEW, model.getFilteredPersonList().size()));
        } else if (namePredicate != null) {
            model.updateFilteredPersonList(namePredicate);
            return new CommandResult(
                    String.format(MESSAGE_PERSONS_LISTED_OVERVIEW, model.getFilteredPersonList().size()));
        } else {
            //won't reach this line, throwing an assertion just in case
            throw new AssertionError("Either name keywords or student ID must be provided");
        }
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(other instanceof FindCommand)) {
            return false;
        }

        FindCommand otherFindCommand = (FindCommand) other;
        return Objects.equals(studentIdPredicate, otherFindCommand.studentIdPredicate)
                && Objects.equals(namePredicate, otherFindCommand.namePredicate);
    }

    @Override
    public String toString() {
        ToStringBuilder builder = new ToStringBuilder(this);
        if (studentIdPredicate != null) {
            builder.add("studentIdPredicate", studentIdPredicate);
        } else if (namePredicate != null) {
            builder.add("namePredicate", namePredicate);
        }
        return builder.toString();
    }
}
