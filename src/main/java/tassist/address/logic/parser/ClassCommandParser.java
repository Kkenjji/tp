package tassist.address.logic.parser;

import static java.util.Objects.requireNonNull;
import static tassist.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static tassist.address.logic.commands.ClassCommand.MESSAGE_INVALID_CLASS;
import static tassist.address.logic.parser.CliSyntax.PREFIX_CLASS;
import static tassist.address.model.person.StudentId.VALIDATION_REGEX;

import java.util.logging.Logger;

import tassist.address.commons.core.index.Index;
import tassist.address.commons.exceptions.IllegalValueException;
import tassist.address.logic.commands.ClassCommand;
import tassist.address.logic.parser.exceptions.ParseException;
import tassist.address.model.person.ClassNumber;
import tassist.address.model.person.StudentId;

/**
 * Parses input arguments and creates a new ClassCommand object
 */
public class ClassCommandParser implements Parser<ClassCommand> {

    private static final Logger logger = Logger.getLogger(ClassCommandParser.class.getName());

    /**
     * Parses the given {@code String} of arguments in the context of the ClassCommand
     * and returns a ClassCommand object for execution.
     * @throws ParseException if the user input does not conform the expected format
     */
    public ClassCommand parse(String args) throws ParseException {
        requireNonNull(args);
        ArgumentMultimap argMultimap = ArgumentTokenizer.tokenize(args, PREFIX_CLASS);
        String trimmedArgs = argMultimap.getPreamble().trim();

        if (trimmedArgs.isEmpty()) {
            throw new ParseException(String.format(MESSAGE_INVALID_COMMAND_FORMAT,
                    ClassCommand.MESSAGE_USAGE));
        }

        argMultimap.verifyNoDuplicatePrefixesFor(PREFIX_CLASS);

        String classNumberString = argMultimap.getValue(PREFIX_CLASS).orElse("");
        if (argMultimap.getValue(PREFIX_CLASS).isEmpty()) {
            throw new ParseException(String.format(MESSAGE_INVALID_COMMAND_FORMAT,
                    ClassCommand.MESSAGE_USAGE));
        }

        ClassNumber classNumber;
        try {
            classNumber = new ClassNumber(classNumberString);
        } catch (IllegalArgumentException e) {
            throw new ParseException(MESSAGE_INVALID_CLASS, e);
        }

        if (trimmedArgs.matches(VALIDATION_REGEX)) {
            try {
                StudentId studentId = ParserUtil.parseStudentId(trimmedArgs);
                return new ClassCommand(studentId, classNumber);
            } catch (IllegalValueException ive) {
                throw new ParseException(String.format(MESSAGE_INVALID_COMMAND_FORMAT,
                        ClassCommand.MESSAGE_USAGE), ive);
            }
        }

        Index index;
        try {
            index = ParserUtil.parseIndex(trimmedArgs);
            return new ClassCommand(index, classNumber);
        } catch (IllegalValueException ive) {
            throw new ParseException(String.format(MESSAGE_INVALID_COMMAND_FORMAT,
                    ClassCommand.MESSAGE_USAGE), ive);
        }
    }
}
