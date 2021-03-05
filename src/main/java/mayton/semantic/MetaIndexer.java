package mayton.semantic;

import org.apache.jena.Jena;
import org.apache.jena.query.Dataset;
import org.apache.jena.query.ReadWrite;
import org.apache.jena.rdf.model.*;
import org.apache.jena.riot.RDFDataMgr;
import org.apache.jena.riot.RDFFormat;
import org.apache.jena.tdb.TDB;
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

    public static final int PROCESS_EVERY = -1;

    public static int collectMetaToDataSet(String inputFolder, Dataset dataset) throws IOException {

        Model model = dataset.getDefaultModel();
        MetaVisitor metaVisitor = new MetaVisitor(model);
        Files.walkFileTree(Path.of(inputFolder), metaVisitor);
        modelReport(model);
        logger.info("Dataset commited");
        TDB.sync(dataset);
        logger.info("Dataset closed");
        return metaVisitor.getCnt();
    }

    private static void modelReport(Model model) {
        logger.info("Concrete model subclass = {}", model.getClass());
        logger.info("Namespaces count = {}", model.listNameSpaces().toList().stream().count());
        model.listNameSpaces().toList().forEach(item -> {
            logger.info(" Namespace : '{}'", item);
        });
        logger.info("Subjects count   = {}", model.listSubjects().toList().stream().count());
        logger.info("Statements count = {}", model.listStatements().toList().stream().count());
        logger.info("Nodes count      = {}", model.listObjects().toList().stream().count());
    }

    public static void exportToFiles(Model model, String ttlOutputFile, String xmlOutputFile) {
        try(OutputStream outputStream = new FileOutputStream(ttlOutputFile)) {
            requireNonNull(model);
            requireNonNull(model.getGraph());
            requireNonNull(outputStream);

            logger.info("Checkpoint [1]");

            RDFDataMgr.write(
                    outputStream,
                    model,
                    RDFFormat.TURTLE);

            logger.info("Checkpoint [2]");

            RDFDataMgr.write(
                    outputStream,
                    model,
                    RDFFormat.RDFXML);

            logger.info("Checkpoint [3]");
        } catch (IOException ex) {
            logger.error("IOException", ex);
        }
    }

    public static void main(String[] args) throws IOException {

        String inputFolder     = args[0];
        String ttlOutputFile   = args[1];
        String xmlOutputFile   = args[2];
        String tdbOutputFolder = args[3];

        logger.info("java.version    = {}", getProperty("java.version"));
        logger.info("java.home       = {}", getProperty("java.home"));
        logger.info("user.dir        = {}", getProperty("user.dir"));
        logger.info("jena.version    = {}", Jena.VERSION);
        logger.info("jena.build_date = {}", Jena.BUILD_DATE);

        Dataset dataset = TDBFactory.createDataset(tdbOutputFolder);
        int collected = collectMetaToDataSet(inputFolder, dataset);
        logger.info("Collected {} files", collected);
        exportToFiles(dataset.getDefaultModel(), ttlOutputFile, xmlOutputFile);
        dataset.close();

        // tdbReport(tdbOutputFolder);
    }

    private static void tdbReport(String tdbOutputFolder) {
        Dataset dataset = TDBFactory.createDataset(tdbOutputFolder) ;
        dataset.begin(ReadWrite.READ) ;
        Model model = dataset.getDefaultModel() ;
        modelReport(model);
        dataset.end() ;
    }

}
