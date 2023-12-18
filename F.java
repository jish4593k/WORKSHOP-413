import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.Random;

public class PDFGenerator {

    private String url;
    private String script;
    private String tempDir;
    private String filename;
    private String filepath;
    private String paperformat;
    private double zoom;
    private byte[] pdfData;
    private String header;
    private String footer;
    private String margin;
    private String orientation;

    public PDFGenerator(String url, String paperformat, double zoom, String script, String tempDir,
                        String header, String footer, String margin, String orientation) {
        this.url = url;
        this.paperformat = paperformat;
        this.zoom = zoom;
        this.script = script;
        this.tempDir = tempDir;
        this.header = header;
        this.footer = footer;
        this.margin = margin;
        this.orientation = orientation;
        this.filename = getRandomFilename();
        this.filepath = getFilePath();
        generate();
        setPdfData();
        removeSourceFile();
    }

    private String getRandomFilename() {
        // Implement your own logic for generating a random filename
        Random random = new Random();
        return String.format("%s.pdf", random.nextInt(100000));
    }

    private String getFilePath() {
        return Path.of(tempDir, filename).toString();
    }

    private void generate() {
        try {
            String command = String.format("phantomjs --ssl-protocol=any %s %s %s %s %s %s %s %s %f",
                    script, url, filepath, paperformat, header, footer, margin, orientation, zoom);

            Process process = Runtime.getRuntime().exec(command);
            process.waitFor();

        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void setPdfData() {
        try {
            pdfData = Files.readAllBytes(Path.of(filepath));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public byte[] getPdfData() {
        return pdfData;
    }

    public void save(String filename, String title, String description) {
        try {
            Files.write(Path.of(filename), pdfData, StandardOpenOption.CREATE);
            // Perform your logic for saving document details (title, description)
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void removeSourceFile() {
        File file = new File(filepath);
        if (file.delete()) {
            System.out.println("Source file deleted successfully");
        } else {
            System.out.println("Failed to delete source file");
        }
    }

    public static void main(String[] args) {
        // Examples of using the PDFGenerator in Java
        PDFGenerator pdfGenerator = new PDFGenerator(
                "https://example.com",
                "A4",
                1.0,
                "rasterize.js",
                "/path/to/temp",
                "",
                "",
                "0cm",
                "portrait"
        );

        byte[] pdfData = pdfGenerator.getPdfData();
        // Perform additional operations with the generated PDF data

        // Example: Save PDF to file
        pdfGenerator.save("output.pdf", "Document Title", "Document Description");
    }
}
