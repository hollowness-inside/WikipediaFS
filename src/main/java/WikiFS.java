import jnr.ffi.Pointer;
import jnr.ffi.types.off_t;
import ru.serce.jnrfuse.FuseFillDir;
import ru.serce.jnrfuse.FuseStubFS;
import ru.serce.jnrfuse.struct.FileStat;
import ru.serce.jnrfuse.struct.FuseFileInfo;

import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class WikiFS extends FuseStubFS {
    private static final Pattern LINK_PATTERN = Pattern.compile("<a href=\"(/wiki[^:\"()]+)\"");

    private Set<String> parseLinks(String spath) {
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

    @Override
    public int readdir(String path, Pointer buff, FuseFillDir filter, @off_t long offset, FuseFileInfo fileInfo) {
        filter.apply(buff, ".", null, 0);
        filter.apply(buff, "..", null, 0);

        for (String link : parseLinks(path))
            filter.apply(buff, link, null, 0);

        return 0;
    }

    @Override
    public int getattr(String path, FileStat fileStat) {
        fileStat.st_mode.set(FileStat.S_IFDIR | 775);
        fileStat.st_nlink.set(2);
        return 0;
    }
}
