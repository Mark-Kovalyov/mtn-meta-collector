package mayton.semantic;

import org.apache.jena.rdf.model.Model;

import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;

public class JumpMetaVisitor extends SimpleFileVisitor<Path> {

    private final Model model;
    private final int jumpSize;

    private int pos = 0;

    public JumpMetaVisitor(Model model, int jumpSize) {
        super();
        this.jumpSize = jumpSize;
        this.model = model;
        this.model.setNsPrefix("file",   "http://file.org#");
        this.model.setNsPrefix("mp3",    "http://mp3.org#");
        this.model.setNsPrefix("vorbis", "http://vorbis.org#");
        this.model.setNsPrefix("p2p",    "http://p2p.org#");

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
        if (pos == jumpSize) {

            pos = 0;
        }
        return FileVisitResult.CONTINUE;
    }
}
