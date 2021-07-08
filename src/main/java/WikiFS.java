import jnr.ffi.Pointer;
import jnr.ffi.types.off_t;
import ru.serce.jnrfuse.FuseFillDir;
import ru.serce.jnrfuse.FuseStubFS;
import ru.serce.jnrfuse.struct.FileStat;
import ru.serce.jnrfuse.struct.FuseFileInfo;

public class WikiFS extends FuseStubFS {
    @Override
    public int readdir(String path, Pointer buff, FuseFillDir filter, @off_t long offset, FuseFileInfo fileInfo) {
        filter.apply(buff, ".", null, 0);
        filter.apply(buff, "..", null, 0);

        for (String link : WikiArticle.parseLinks(path))
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
