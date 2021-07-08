import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class WikiArticle {
    private static final Pattern LINK_PATTERN = Pattern.compile("<a href=\"(/wiki[^:\"()]+)\"");

    public static Set<String> parseLinks(String spath) {
        Set<String> links = new HashSet<>();

        if (spath.equals("/"))
            spath = "/United_States";

        try {
            URL url = new URL("https://en.wikipedia.org/wiki/" + Paths.get(spath).getFileName().toString());
            final URLConnection connection = url.openConnection();
            String pageData = new String(connection.getInputStream().readAllBytes(), StandardCharsets.UTF_8);

            final Matcher matcher = LINK_PATTERN.matcher(pageData);
            while (matcher.find()) {
                final String found = matcher.group(1);
                links.add(Paths.get(found).getFileName().toString());
            }

        } catch (Exception e) {
            System.err.println(e.getMessage());
        }

        return links;
    }
}
