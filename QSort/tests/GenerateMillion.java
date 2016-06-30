import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.Date;
import java.util.Random;

/**
 * Generates an array of 1M random integers
 */
public class GenerateMillion {
    public static void main(String[] args) {
        Random rnd = new Random(new Date().getTime());
        StringBuilder outBuf = new StringBuilder(11_000_000);
        int rand;
        for (int i = 0; i < 1_000_000; i++) {
            rand = rnd.nextInt(Integer.MAX_VALUE);
            outBuf.append(Integer.toString(rand)).append(',');
        }
        outBuf.setLength(outBuf.length() - 1);

        try (FileChannel fc = new FileOutputStream("million_ints.txt").getChannel()) {
            fc.write(ByteBuffer.wrap(outBuf.toString().getBytes()));
        } catch (IOException e) {
        }
    }
}
