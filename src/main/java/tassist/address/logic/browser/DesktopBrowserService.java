package tassist.address.logic.browser;

import java.awt.Desktop;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

/**
 * Default implementation of BrowserService that uses Desktop.browse().
 */
public class DesktopBrowserService implements BrowserService {
    @Override
    public void openUrl(String url) throws IOException, URISyntaxException {
        Desktop.getDesktop().browse(new URI(url));
    }
}
