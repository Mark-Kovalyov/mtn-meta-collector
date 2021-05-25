package mayton.semantic.media;

import org.apache.jena.datatypes.BaseDatatype;
import org.apache.jena.datatypes.DatatypeFormatException;
import org.apache.jena.datatypes.RDFDatatype;
import org.apache.jena.graph.impl.LiteralLabel;

public class Mp3TechnicalInfo extends BaseDatatype {

    public static final String theTypeURI = "urn:mp3:technical-info";
    public static final RDFDatatype theRationalType = new Mp3TechnicalInfo(theTypeURI);

    private Mp3TechnicalInfo() {
        super(theTypeURI);
    }

    public Mp3TechnicalInfo(String uri) {
        super(uri);
    }

    @Override
    public String unparse(Object value) {
        // Example
        //        Rational r = (Rational) value;
        //        return Integer.toString(r.getNumerator()) + "/" + r.getDenominator();
        return super.unparse(value);
    }

    @Override
    public Object parse(String lexicalForm) throws DatatypeFormatException {
        return super.parse(lexicalForm);
    }

    @Override
    public boolean isEqual(LiteralLabel litLabel1, LiteralLabel litLabel2) {
        return super.isEqual(litLabel1, litLabel2);
    }
}
