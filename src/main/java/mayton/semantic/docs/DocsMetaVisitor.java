package mayton.semantic.docs;

import com.itextpdf.text.pdf.*;
import com.itextpdf.text.pdf.parser.PdfTextExtractor;
import org.apache.jena.rdf.model.Model;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;

import static java.util.Objects.requireNonNull;

public class DocsMetaVisitor extends SimpleFileVisitor<Path> {

    private final Model model;
    private final int jumpSize;

    private int pos = 0;

    public DocsMetaVisitor(Model model, int jumpSize) {
        super();
        this.jumpSize = jumpSize;
        this.model = model;
        this.model.setNsPrefix("file",   "http://file.org#");
        this.model.setNsPrefix("pdf",   "http://pdf.org#");

        this.model.setNsPrefix("rdf", "http://www.w3.org/1999/02/22-rdf-syntax-ns#");
        this.model.setNsPrefix("rdfs", "http://www.w3.org/2000/01/rdf-schema#");
        this.model.setNsPrefix("xsd", "http://www.w3.org/2001/XMLSchema#");
        this.model.setNsPrefix("owl", "http://www.w3.org/2002/07/owl#");
        this.model.setNsPrefix("foaf", "http://xmlns.com/foaf/0.1/");
        this.model.setNsPrefix("dc", "http://purl.org/dc/elements/1.1/");
        this.model.setNsPrefix("dcterms", "http://purl.org/dc/terms/");
    }

    @Override
    public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
        requireNonNull(file);
        requireNonNull(attrs);
        String fileName = file.getFileName().toString();
        if (fileName.endsWith(".pdf")) {
            //PdfDocument document = new PdfDocument();
            PdfReader pdfReader = new PdfReader(new FileInputStream(file.toString()));
            PdfDictionary catalog = pdfReader.getCatalog();
            PdfDictionary names = catalog.getAsDict(PdfName.NAMES);
            PdfDictionary embeddedFiles = names.getAsDict(PdfName.EMBEDDEDFILES);
            PdfArray embeddedFilesArray = embeddedFiles.getAsArray(PdfName.NAMES);
            //extractFiles(pdfReader, embeddedFilesArray);
        }
        return FileVisitResult.CONTINUE;
    }
}
