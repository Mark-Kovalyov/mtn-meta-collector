package mayton.semantic;

import org.apache.jena.query.Dataset;
import org.apache.jena.query.ReadWrite;
import org.apache.jena.rdf.model.*;
import org.apache.jena.tdb.TDB;
import org.apache.jena.tdb.TDBFactory;

public class ProbeModels {

    public static void main(String[] args) {

        Dataset dataset = TDBFactory.createDataset("/bigdata/tdb/music");
        dataset.begin(ReadWrite.WRITE);
        Model model = dataset.getDefaultModel() ;
        model.commit();
        dataset.end() ;

        TDB.sync(dataset);

    }

}
