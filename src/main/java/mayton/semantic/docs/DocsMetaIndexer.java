package mayton.semantic.docs;

import mayton.semantic.media.MetaVisitor;
import org.apache.jena.query.Dataset;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.tdb.TDBFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class DocsMetaIndexer {

    public static void main(String[] args) throws IOException {
        String docTdbOutputFolder = "/";//args[0];
        Dataset dataset = TDBFactory.createDataset(docTdbOutputFolder);
        Model model = dataset.getDefaultModel();
        DocsMetaVisitor metaVisitor = new DocsMetaVisitor(model, 10);
        Files.walkFileTree(Path.of("/storage/documents"), metaVisitor);
    }

}
