package tassist.address.logic.browser;

import java.io.IOException;
import java.net.URISyntaxException;

/**
 * Service interface for opening URLs in a browser.
 */
public interface BrowserService {
    /**
     * Opens the specified URL in a browser.
     *
     * @param url The URL to open
     * @throws IOException If there is an error opening the URL
     * @throws URISyntaxException If the URL is malformed
     */
    void openUrl(String url) throws IOException, URISyntaxException;
}
