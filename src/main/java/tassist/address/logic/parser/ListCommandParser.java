package tassist.address.logic.parser;

import static tassist.address.logic.parser.CliSyntax.PREFIX_FILTER;
import static tassist.address.logic.parser.CliSyntax.PREFIX_FILTER_VALUE;
import static tassist.address.logic.parser.CliSyntax.PREFIX_ORDER;
import static tassist.address.logic.parser.CliSyntax.PREFIX_SORT;

import java.util.Arrays;
import java.util.List;

import tassist.address.logic.commands.ListCommand;
import tassist.address.logic.parser.exceptions.ParseException;
import tassist.address.model.person.Progress;

/**
 * Parses input arguments and creates a new ListCommand object.
 */
public class ListCommandParser implements Parser<ListCommand> {

    private static final List<String> VALID_SORT_TYPES = Arrays.asList("name", "progress", "github");
    private static final List<String> VALID_SORT_ORDERS = Arrays.asList("asc", "des");
    private static final List<String> VALID_FILTER_TYPES = Arrays.asList("course", "team", "progress");

    @Override
    public ListCommand parse(String args) throws ParseException {
        ArgumentMultimap argMultimap = ArgumentTokenizer.tokenize(args, PREFIX_SORT, PREFIX_ORDER, PREFIX_FILTER,
                PREFIX_FILTER_VALUE);

        String sortType = argMultimap.getValue(PREFIX_SORT).orElse(null);
        String sortOrder = argMultimap.getValue(PREFIX_ORDER).orElse(null);
        String filterType = argMultimap.getValue(PREFIX_FILTER).orElse(null);
        String filterValue = argMultimap.getValue(PREFIX_FILTER_VALUE).orElse(null);

        validateSortType(sortType, sortOrder);
        validateSortOrder(sortOrder);
        validateFilterType(filterType, filterValue);
        validateFilterValue(filterValue, filterType);

        return new ListCommand(sortType, sortOrder, filterType, filterValue);
    }

    private static void validateFilterValue(String filterValue, String filterType) throws ParseException {
        if (filterValue != null && filterType != null && filterType.equalsIgnoreCase("progress")) {
            try {
                int value = Integer.parseInt(filterValue.trim());
                if (value < 0 || value > 100) {
                    throw new ParseException(Progress.MESSAGE_CONSTRAINTS);
                }
            } catch (NumberFormatException e) {
                throw new ParseException(Progress.MESSAGE_CONSTRAINTS);
            }
        }
    }

    private static void validateFilterType(String filterType, String filterValue) throws ParseException {
        if (filterType != null) {
            if (!VALID_FILTER_TYPES.contains(filterType.toLowerCase())) {
                throw new ParseException(ListCommand.MESSAGE_INVALID_FILTER);
            }
            if (filterValue == null) {
                throw new ParseException(ListCommand.MESSAGE_MISSING_FILTER_VALUE);
            }
        }
    }

    private static void validateSortOrder(String sortOrder) throws ParseException {
        if (sortOrder != null && !VALID_SORT_ORDERS.contains(sortOrder.toLowerCase())) {
            throw new ParseException(ListCommand.MESSAGE_INVALID_SORT_ORDER);
        }
    }

    private static void validateSortType(String sortType, String sortOrder) throws ParseException {
        if (sortType != null) {
            if (!VALID_SORT_TYPES.contains(sortType.toLowerCase())) {
                throw new ParseException(ListCommand.MESSAGE_INVALID_SORT);
            }
            if (sortOrder == null) {
                throw new ParseException(ListCommand.MESSAGE_MISSING_SORT_ORDER); // You can define this message
            }
        }
    }
}
