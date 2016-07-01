import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.*;
import java.util.concurrent.*;

/**
 * Text file reader using extra thread and a Future to return result
 */
public class TextFileNonblockingIO {
    private static final Logger LOG = LogManager.getLogger();

    private class ReaderThread implements Callable<String> {
        File f;
        public ReaderThread(String filename) throws FileNotFoundException {
            f = new File(filename);
            if (! f.isFile()) throw new FileNotFoundException(filename);
            if (! f.canRead()) throw new SecurityException(filename);
        }

        @Override
        public String call()  {
            char[] readBuf = new char[10240];
            StringBuilder sb = new StringBuilder(10240);
            LOG.debug("Began reading file " + f.getName());
            try (FileReader fr = new FileReader(f)) {
                int readChars;
                while (true) {
                    readChars = fr.read(readBuf);
                    if (readChars > 0) sb.append(readBuf, 0, readChars);
                    else break;
                }
            } catch (IOException e) {
                LOG.error(e);
                throw new RuntimeException(e);
            }
            LOG.debug("Finished reading file " + f.getName());
            return sb.toString();
        }
    }

    private class WriterThread implements Callable<Boolean> {
        File f;
        String s;

        public WriterThread(String filename, String contents)  {
            f = new File(filename);
            s = contents;
        }

        @Override
        public Boolean call() {
            LOG.debug("Began writing file " + f.getName());
            try (FileWriter fw = new FileWriter(f)) {
                fw.write(s);
            } catch (IOException e) {
                LOG.error(e);
                return false;
            }
            LOG.debug("Finished writing file " + f.getName());
            return true;
        }
    }

        static Future<String> readFileIntoString(String filename) throws FileNotFoundException {
        ExecutorService ex = Executors.newSingleThreadExecutor();
        return ex.submit(new TextFileNonblockingIO().new ReaderThread(filename));
    }

    static Future<Boolean> writeStringIntoFile(String filename, String contents) {
        ExecutorService ex = Executors.newSingleThreadExecutor();
        return ex.submit(new TextFileNonblockingIO().new WriterThread(filename, contents));
    }
}
