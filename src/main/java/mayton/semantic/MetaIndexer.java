package mayton.semantic;

import org.apache.jena.Jena;
import org.apache.jena.query.Dataset;
import org.apache.jena.rdf.model.*;
import org.apache.jena.riot.RDFDataMgr;
import org.apache.jena.riot.RDFFormat;
import org.apache.jena.tdb.TDBFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;

import static java.lang.System.getProperty;
import static java.util.Objects.requireNonNull;

public class MetaIndexer {

    static Logger logger = LoggerFactory.getLogger(MetaIndexer.class);

    public static void write() {
        String directory = "/bigdata/semantic-meta";
        Dataset dataset = TDBFactory.createDataset(directory);

        //Retrieve Named Graph from Dataset, or use Default Graph.
        String graphURI = "http://example.org/myGraph";
        Model model = dataset.getNamedModel(graphURI);

        //Create RDFS Inference Model, or use other Reasoner e.g. OWL.
        InfModel infModel = ModelFactory.createRDFSModel(model);
    }

    public static void main(String[] args) throws IOException {

        // [INFO ] : MetaIndexer : id =  : java.version = 11.0.10
        // [INFO ] : MetaIndexer : id =  : java.home    = /usr/lib/jvm/java-11-openjdk-amd64
        // [INFO ] : MetaIndexer : id =  : jena.version = 3.12.0
        // [INFO ] : MetaIndexer : id =  : user.dir     = /home/mayton/git/mtn-meta-collector
        // [INFO ] : MetaIndexer : id =  : jena.build_date = 2019-05-27T16:07:27+0000
        logger.info("java.version = {}", getProperty("java.version"));
        logger.info("java.home    = {}", getProperty("java.home"));
        logger.info("user.dir     = {}", getProperty("user.dir"));
        logger.info("jena.version    = {}", Jena.VERSION);
        logger.info("jena.build_date = {}", Jena.BUILD_DATE);
        //logger.info("current.classloader = {}", MetaIndexer.class.getClassLoader().toString());

        MetaVisitor metaVisitor = new MetaVisitor(ModelFactory.createDefaultModel());

        String outputFilePrefix = "music";

        Files.walkFileTree(Path.of("/storage/music"), metaVisitor);

        Model model = metaVisitor.getModel();

        logger.info("Concrete model subclass = {}", model.getClass());

        logger.info("Namespaces count = {}", model.listNameSpaces().toList().stream().count());
        model.listNameSpaces().toList().forEach(item -> {
            logger.info(" Namespace : '{}'", item);
        });
        logger.info("Subjects count   = {}", model.listSubjects().toList().stream().count());
        logger.info("Statements count = {}", model.listStatements().toList().stream().count());
        logger.info("Nodes count      = {}", model.listObjects().toList().stream().count());

        logger.info("Checkpoint [2] ");

        try(OutputStream outputStream = new FileOutputStream(String.format("/bigdata/semantic/%s.ttl", outputFilePrefix))) {
            requireNonNull(model);
            requireNonNull(model.getGraph());
            requireNonNull(outputStream);
            RDFDataMgr.write(
                    outputStream,
                    model,
                    RDFFormat.TURTLE);
            logger.info("Checkpoint [3]");
        } catch (IOException ex) {
            logger.error("IOException", ex);
        }






    }

}
