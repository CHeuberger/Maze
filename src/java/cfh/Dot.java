package cfh;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;


public final class Dot {

    static final String CMD;
    static {
        String cmd = System.getProperty("Graph");
        if (cmd == null) {
            String path = System.getenv("GRAPHVIZ_HOME");
            if (path == null) {
                cmd = "/usr/local/bin/dot";
            } else {
                cmd = path + "/bin/dot";
            }
        }
        CMD = cmd;
    }

    public static void dotToSvg(InputStream dotInput, OutputStream svgOutput) throws IOException, InterruptedException {
        Process process = Runtime.getRuntime().exec(new String[] { CMD, "-Tsvg" });

        try (OutputStream processIn = process.getOutputStream()) {
            writeAll(dotInput, processIn);
        }

        try (InputStream processOut = process.getInputStream()) {
            writeAll(processOut, svgOutput);
        }

        try (InputStream processErr = process.getErrorStream()) {
            writeAll(processErr, System.err);
        }
        
        int ret = process.waitFor();
        if (ret != 0)
            throw new RuntimeException("dot to svg conversion failed, returned: " + ret);
    }
    
    private static void writeAll(InputStream input, OutputStream output) throws IOException {
        byte[] buffer = new byte[4096];

        int count;
        while ((count = input.read(buffer)) != -1) {
            output.write(buffer, 0, count);
        }
    }
}
