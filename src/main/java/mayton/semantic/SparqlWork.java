package mayton.semantic;

import org.apache.jena.query.*;

public class SparqlWork {

    public static void main(String[] args) {

        ARQ.getContext().set(ARQ.symLogExec,true);

        try(QueryExecution qExec = QueryExecutionFactory.create("SELECT * WHERE { ?a ?b ?c }")) {
            qExec.getContext().set(ARQ.symLogExec,true) ;
            ResultSet rs = qExec.execSelect();
            while(rs.hasNext()) {
                QuerySolution qs = rs.next();
            }
        }
    }

}
