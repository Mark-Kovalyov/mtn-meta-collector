package mayton.semantic;

import org.apache.jena.datatypes.xsd.XSDDatatype;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

import static java.lang.String.valueOf;
import static java.util.Objects.requireNonNull;

public class MetaVisitor extends SimpleFileVisitor<Path> {

    static Logger logger = LoggerFactory.getLogger(MetaVisitor.class);

    private Model model;

    private long id;

    private int cnt;

    public MetaVisitor(Model model) {
        super();

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
    public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
        requireNonNull(dir);
        requireNonNull(attrs);
        String dirName = dir.toString();
        if (dirName.equals("/storage/music")) {
            logger.warn("Enter subtree {}", dirName);
            return FileVisitResult.CONTINUE;
        } else if (dirName.startsWith("/storage/music/")) {
            if (dirName.startsWith("/storage/music/Erik_Trufazz")
                //dirName.startsWith("/storage/music/De-Phazz") ||
                //dirName.startsWith("/storage/music/Glen Miller") ||
                //dirName.startsWith("/storage/music/VA - Science Fiction Jazz vol.1-12 (1996-2010)") ||
                //dirName.startsWith("/storage/music/John Coltrane - A Love Supreme (1964) [FLAC]")
                ) {
                logger.info("Enter subtree {}", dirName);
                return FileVisitResult.CONTINUE;
            } else {
                logger.warn("Skip subtree {}", dirName);
                return FileVisitResult.SKIP_SUBTREE;
            }
        } else {
            return FileVisitResult.SKIP_SUBTREE;
        }
    }

    private void processFileHeader(Path file, Resource idRes) {
        logger.info("file = {}", file);
        long length = file.toFile().length();
        model.add(
                idRes,
                model.createProperty("http://file.org#length"),
                valueOf(length),
                XSDDatatype.XSDlong
        );
        long lastMod = file.toFile().lastModified();
        LocalDateTime ldt = Instant.ofEpochMilli(lastMod).atZone(ZoneId.systemDefault()).toLocalDateTime();
        String s = ldt.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
        model.add(
                idRes,
                model.createProperty("http://file.org#lastModified"),
                s,
                XSDDatatype.XSDdateTime
        );
        P2PLinks p2PLinks = P2PLinkProcessor.createInstance().extractP2Plinks(file.toFile());
        if (length < 9728000) {
            String ed2k = p2PLinks.ed2k;
            model.add(
                    idRes,
                    model.createProperty("http://p2p.org#ed2k"),
                    ed2k,
                    XSDDatatype.XSDstring
            );
        }
        model.add(
                idRes,
                model.createProperty("http://p2p.org#crc32"),
                p2PLinks.crc32,
                XSDDatatype.XSDstring
        );
        model.add(
                idRes,
                model.createProperty("http://p2p.org#gnutella2"),
                p2PLinks.gnutella2,
                XSDDatatype.XSDstring
        );
        model.add(
                idRes,
                model.createProperty("http://p2p.org#btih"),
                p2PLinks.bitTorrentInfoHash,
                XSDDatatype.XSDstring
        );
    }

    @Override
    public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
        requireNonNull(file);
        requireNonNull(attrs);
        String fileName = file.getFileName().toString();
        if (fileName.endsWith(".mp3")) {
            id++;
            MDC.put("id", valueOf(id));
            Resource idRes = model.createResource("http://file.org#id" + id);
            processFileHeader(file, idRes);
            Mp3ContentProcessor.createInstance().process(model, idRes, file, attrs, id);
            cnt++;
        } else if (fileName.endsWith(".ogg")) {
            id++;
            MDC.put("id", valueOf(id));
            Resource idRes = model.createResource("http://file.org#id" + id);
            processFileHeader(file, idRes);
            VorbisContentProcessor.createInstance().process(model, idRes, file, attrs, id);
            cnt++;
        } else {
            // Unknown file
        }
        return FileVisitResult.CONTINUE;
    }

    @Override
    public FileVisitResult visitFileFailed(Path file, IOException exc) throws IOException {
        requireNonNull(file);
        logger.error("Visit file failed");
        return super.visitFileFailed(file, exc);
    }

    @Override
    public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
        requireNonNull(dir);
        return super.postVisitDirectory(dir, exc);
    }

    public Model getModel() {
        return model;
    }

    public long getId() {
        return id;
    }

    public int getCnt() {
        return cnt;
    }
}
