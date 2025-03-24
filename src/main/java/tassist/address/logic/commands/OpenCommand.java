package tassist.address.logic.commands;

import static java.util.Objects.requireNonNull;

import java.awt.Desktop;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

import tassist.address.commons.core.index.Index;
import tassist.address.commons.util.ToStringBuilder;
import tassist.address.logic.Messages;
import tassist.address.logic.commands.exceptions.CommandException;
import tassist.address.model.Model;
import tassist.address.model.person.Person;

/**
 * Opens the GitHub link of a person identified using its displayed index from the address book.
 */
public class OpenCommand extends Command {

    public static final String COMMAND_WORD = "open";

    public static final String MESSAGE_USAGE = COMMAND_WORD
            + ": Opens the GitHub link of the person identified by"
            + "the index number used in the displayed person list.\n"
            + "Parameters: INDEX (must be a positive integer)\n"
            + "Example: " + COMMAND_WORD + " 1";

    public static final String MESSAGE_OPEN_GITHUB_SUCCESS = "Opening GitHub link for: %1$s";
    public static final String MESSAGE_OPEN_GITHUB_FAILURE = "Failed to open GitHub link for: %1$s";

    private final Index targetIndex;
    private final BrowserService browserService;

    /**
     * Constructs an OpenCommand with a target index.
     *
     * @param targetIndex The index of the person whose GitHub link will be opened.
     */
    public OpenCommand(Index targetIndex) {
        this(targetIndex, new DesktopBrowserService());
    }

    /**
     * Constructs an OpenCommand with a target index and a specified browser service.
     *
     * @param targetIndex    The index of the person whose GitHub link will be opened.
     * @param browserService The browser service to handle opening URLs.
     */
    public OpenCommand(Index targetIndex, BrowserService browserService) {
        this.targetIndex = targetIndex;
        this.browserService = browserService;
    }

    /**
     * Gets the target index.
     *
     * @return The target index.
     */
    public Index getTargetIndex() {
        return targetIndex;
    }

    @Override
    public CommandResult execute(Model model) throws CommandException {
        requireNonNull(model);
        List<Person> lastShownList = model.getFilteredPersonList();

        if (targetIndex.getZeroBased() >= lastShownList.size()) {
            throw new CommandException(Messages.MESSAGE_INVALID_PERSON_DISPLAYED_INDEX);
        }

        Person personToOpen = lastShownList.get(targetIndex.getZeroBased());

        try {
            browserService.openUrl(personToOpen.getGithub().value);
            return new CommandResult(
                    String.format(MESSAGE_OPEN_GITHUB_SUCCESS, Messages.format(personToOpen))
            );
        } catch (IOException | URISyntaxException e) {
            return new CommandResult(
                    String.format(MESSAGE_OPEN_GITHUB_FAILURE, Messages.format(personToOpen))
            );
        }
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        if (!(other instanceof OpenCommand)) { // Removed space after `!`
            return false;
        }

        OpenCommand otherOpenCommand = (OpenCommand) other;
        return targetIndex.equals(otherOpenCommand.targetIndex);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .add("targetIndex", targetIndex)
                .toString();
    }

    /**
     * Service interface for opening URLs in a browser.
     */
    public interface BrowserService {
        /**
         * Opens the specified URL in a browser.
         *
         * @param url The URL to open.
         * @throws IOException        If there is an error opening the URL.
         * @throws URISyntaxException If the URL is malformed.
         */
        void openUrl(String url) throws IOException, URISyntaxException;
    }

    /**
     * Default implementation of BrowserService that uses Desktop.browse().
     */
    public static class DesktopBrowserService implements BrowserService {
        @Override
        public void openUrl(String url) throws IOException, URISyntaxException {
            // First validate the URL by creating a URI object
            URI uri = new URI(url);

            // Check if Desktop is supported
            if (!Desktop.isDesktopSupported()) {
                throw new IOException("Desktop is not supported on this platform");
            }

            Desktop desktop = Desktop.getDesktop();
            // Check if browsing is supported
            if (!desktop.isSupported(Desktop.Action.BROWSE)) {
                throw new IOException("Opening URLs is not supported on this platform");
            }

            desktop.browse(uri);
        }
    }
}
