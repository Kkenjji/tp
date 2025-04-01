package tassist.address.logic.parser;

import static java.util.Objects.requireNonNull;
import static tassist.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static tassist.address.logic.parser.CliSyntax.PREFIX_REPOSITORY_NAME;
import static tassist.address.logic.parser.CliSyntax.PREFIX_USERNAME;

import java.util.stream.Stream;

import tassist.address.commons.core.index.Index;
import tassist.address.commons.exceptions.IllegalValueException;
import tassist.address.logic.commands.RepoCommand;
import tassist.address.logic.parser.exceptions.ParseException;
import tassist.address.model.person.StudentId;

/**
 * Parses input arguments and creates a new {@code RepoCommand} object
 */
public class RepoCommandParser implements Parser<RepoCommand> {

    /**
     * Parses the given {@code String} of arguments in the context of the {@code RepoCommand}
     * and returns a {@code RepoCommand} object for execution.
     * @throws ParseException if the user input does not conform the expected format
     */
    public RepoCommand parse(String args) throws ParseException {
        requireNonNull(args);
        ArgumentMultimap argMultimap = ArgumentTokenizer.tokenize(args, PREFIX_USERNAME, PREFIX_REPOSITORY_NAME);

        if (!arePrefixesPresent(argMultimap, PREFIX_USERNAME, PREFIX_REPOSITORY_NAME)) {
            throw new ParseException(String.format(MESSAGE_INVALID_COMMAND_FORMAT, RepoCommand.MESSAGE_USAGE));
        }

        argMultimap.verifyNoDuplicatePrefixesFor(PREFIX_USERNAME, PREFIX_REPOSITORY_NAME);
        String trimmedArgs = argMultimap.getPreamble().trim();

        if (trimmedArgs.isEmpty()) {
            throw new ParseException(RepoCommand.MESSAGE_NO_INDEX_STUDENTID);
        }

        String username = argMultimap.getValue(PREFIX_USERNAME).orElse(null);
        String repositoryName = argMultimap.getValue(PREFIX_REPOSITORY_NAME).orElse(null);

        if (!username.matches(RepoCommand.VALID_USERNAME_REGEX) || username == null) {
            throw new ParseException(String.format(MESSAGE_INVALID_COMMAND_FORMAT,
                    RepoCommand.MESSAGE_INVALID_USERNAME));
        }
        if (!repositoryName.matches(RepoCommand.VALID_REPOSITORY_REGEX) || repositoryName == null ) {
            throw new ParseException(String.format(MESSAGE_INVALID_COMMAND_FORMAT,
                    RepoCommand.MESSAGE_INVALID_REPOSITORY_NAME));
        }

        if (trimmedArgs.matches(StudentId.VALIDATION_REGEX)) {
            try {
                StudentId studentId = ParserUtil.parseStudentId(trimmedArgs);
                return new RepoCommand(studentId, username, repositoryName);
            } catch (IllegalValueException ive) {
                throw new ParseException(String.format(MESSAGE_INVALID_COMMAND_FORMAT,
                        RepoCommand.MESSAGE_USAGE), ive);
            }
        }

        try {
            Index index = ParserUtil.parseIndex(trimmedArgs);
            return new RepoCommand(index, username, repositoryName);
        } catch (IllegalValueException ive) {
            throw new ParseException(String.format(MESSAGE_INVALID_COMMAND_FORMAT,
                    RepoCommand.MESSAGE_USAGE), ive);
        }
    }

    /**
     * Returns true if none of the prefixes contains empty {@code Optional} values in the given
     * {@code ArgumentMultimap}.
     */
    private static boolean arePrefixesPresent(ArgumentMultimap argumentMultimap, Prefix... prefixes) {
        return Stream.of(prefixes).allMatch(prefix -> argumentMultimap.getValue(prefix).isPresent());
    }
}
