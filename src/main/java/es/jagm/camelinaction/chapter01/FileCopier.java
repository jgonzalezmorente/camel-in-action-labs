package es.jagm.camelinaction.chapter01;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;

public class FileCopier {

    public static void main(String[] args) throws  Exception {

        File inboxDirectory = new File("data/inbox");
        File outboxDirectory = new File("data/outbox");

        outboxDirectory.mkdir();

        File[] files = inboxDirectory.listFiles();

        for (File source: files) {
            if (source.isFile()) {

                File dest = new File(outboxDirectory.getPath()
                        + File.separator
                        + source.getName());

                copyFile(source, dest);
            }
        }
    }

    private static void copyFile(File source, File dest) throws IOException {
        OutputStream out = Files.newOutputStream(dest.toPath());
        byte[] buffer = new byte[(int) source.length()];
        FileInputStream in = new FileInputStream(source);
        in.read(buffer);

        try {
            out.write(buffer);
        } finally {
            out.close();
            in.close();
        }
    }
}
