import jnr.ffi.Platform;

import java.nio.file.Paths;

public class Main {
    public static void main(String[] args) {
        WikiFS wikiFS = new WikiFS();

        try {
            String path;
            if (Platform.getNativePlatform().getOS() == Platform.OS.WINDOWS)
                path = "J:\\";
            else
                path = "/tmp/mnt";

            wikiFS.mount(Paths.get(path), true, false);
        } finally {
            wikiFS.umount();
        }
    }
}
