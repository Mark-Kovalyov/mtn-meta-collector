package mayton.semantic.media;

import com.mpatric.mp3agic.ID3v1;
import com.mpatric.mp3agic.InvalidDataException;
import com.mpatric.mp3agic.Mp3File;
import com.mpatric.mp3agic.UnsupportedTagException;
import mayton.semantic.ContentProcessor;
import org.apache.jena.datatypes.xsd.XSDDatatype;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.Resource;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;

import static java.lang.String.format;
import static java.lang.String.valueOf;
import static mayton.semantic.FileUtils.escapeControl;
import static org.apache.commons.lang3.StringUtils.isBlank;

public class Mp3ContentProcessor implements ContentProcessor {

    private static Mp3ContentProcessor instance = null;

    static Logger logger = LoggerFactory.getLogger(Mp3ContentProcessor.class);

    private Mp3ContentProcessor() {

    }

    public static Mp3ContentProcessor createInstance() {
        if (instance == null) {
            instance = new Mp3ContentProcessor();
        }
        return instance;
    }

    // Overall : 20943 mp3 files
    public void process(@NotNull Model model, @NotNull Resource idRes, @NotNull Path file,@NotNull BasicFileAttributes attrs, long id) throws IOException {
        try {
            model.add(
                    idRes,
                    model.createProperty("http://www.w3.org/1999/02/22-rdf-syntax-ns#type"),
                    "/audio/mpeg"
            );
            Mp3File mp3file = new Mp3File(file);
            long lengthInSeconds = mp3file.getLengthInSeconds();
            logger.debug(" Length of this mp3 is: {} seconds", lengthInSeconds);
            model.add(idRes,
                    model.createProperty("http://mp3.org#lengthInSeconds"),
                    valueOf(lengthInSeconds),
                    XSDDatatype.XSDlong);

            boolean isVbr = mp3file.isVbr();
            int bitRate = mp3file.getBitrate();
            logger.debug(" Bitrate: {} kbps {}", bitRate, isVbr ? "VBR" : "CBR");
            model.add(idRes, model.createProperty("http://mp3.org#bitRate"),
                    valueOf(bitRate),
                    XSDDatatype.XSDinteger);

            model.addLiteral(idRes, model.createProperty("http://mp3.org#isVbr"), isVbr);

            int sampleRate = mp3file.getSampleRate();
            logger.debug(" Sample rate: {} Hz", sampleRate);
            model.add(idRes,
                    model.createProperty("http://mp3.org#sampleRate"),
                    valueOf(sampleRate),
                    XSDDatatype.XSDinteger);
            boolean hasId3v1Tag = mp3file.hasId3v1Tag();
            model.addLiteral(idRes, model.createProperty("http://mp3.org#hasId3v1Tag"), hasId3v1Tag);
            logger.debug(" Has ID3v1 tag?: {}", hasId3v1Tag);
            if (hasId3v1Tag) {
                ID3v1 id3v1Tag = mp3file.getId3v1Tag();

                String track = id3v1Tag.getTrack();
                if (!isBlank(track)) {
                    model.add(idRes,
                            model.createProperty("http://mp3.org#Track"),
                            track,
                            XSDDatatype.XSDinteger);
                }

                String artist = id3v1Tag.getArtist();
                if (!isBlank(artist)) {
                    logger.debug("  Artist: '{}'", escapeControl(artist));
                    model.add(idRes,
                            model.createProperty("http://mp3.org#Artist"),
                            escapeControl(artist),
                            XSDDatatype.XSDstring
                    );
                }

                String title = id3v1Tag.getTitle();
                logger.debug("  Title: '{}'", escapeControl(title));
                if (!isBlank(title)) {
                    model.add(idRes, model.createProperty("http://mp3.org#Title"), escapeControl(title), XSDDatatype.XSDstring);
                }

                String album = id3v1Tag.getAlbum();
                logger.debug("  Album: {}", escapeControl(album));
                if (!isBlank(album)) {
                    model.add(idRes, model.createProperty("http://mp3.org#Album"), escapeControl(album), XSDDatatype.XSDstring);
                }

                String year = id3v1Tag.getYear();
                logger.debug("  Year: '{}'", year);
                if (!isBlank(year)) {
                    try {
                        int yearInt = Integer.parseInt(year);
                        if (yearInt < 100) {
                            yearInt += 1900;
                            logger.warn("year {} less than 1900", yearInt);
                        } else {
                            if (XSDDatatype.XSDdate.isValid(year + "-01-01")) {
                                model.add(idRes,
                                        model.createProperty("http://mp3.org#Year"),
                                        year,
                                        XSDDatatype.XSDdate);
                            } else {
                                logger.warn("Unable to apply {} to XSDDatatype.XSDdate format", year + "-01-01");
                            }
                        }
                    } catch (NumberFormatException ex) {
                        logger.warn("Unable to parse '" + year + "' as integer", ex);
                    }
                }
                int genre = id3v1Tag.getGenre();
                String genreDesc = id3v1Tag.getGenreDescription();

                logger.debug("  Genre: {} ({})", genre, genreDesc);
                model.add(idRes, model.createProperty("http://mp3.org#genre"), valueOf(genre), XSDDatatype.XSDinteger);

                if (!isBlank(genreDesc)) {
                    model.add(idRes, model.createProperty("http://mp3.org#genreDesc"), genreDesc, XSDDatatype.XSDstring);
                }

                String comment = id3v1Tag.getComment();
                if (!isBlank(comment)) {
                    logger.debug("  Comment: {}", escapeControl(comment));
                    model.add(idRes, model.createProperty("http://mp3.org#Comment"), escapeControl(comment), XSDDatatype.XSDstring);
                }

                boolean hasId3v2Tag = mp3file.hasId3v2Tag();
                model.add(idRes, model.createProperty("http://mp3.org#hasId3v2Tag"), valueOf(hasId3v2Tag), XSDDatatype.XSDboolean);
            }
        } catch (UnsupportedTagException e) {
            logger.error("UnsupportedTagException",e);
        } catch (InvalidDataException e) {
            logger.error("InvalidDataException",e);
        } catch (IllegalArgumentException e) {
            logger.error("Illegal argument exception", e);
        }
    }


                    /*logger.debug("Has ID3v2 tag?: " + (mp3file.hasId3v2Tag() ? "YES" : "NO"));
                logger.debug("Has custom tag?: " + (mp3file.hasCustomTag() ? "YES" : "NO"));
                if (mp3file.hasId3v2Tag()) {
                    ID3v2 id3v2Tag = mp3file.getId3v2Tag();
                    logger.debug("Track: " + id3v2Tag.getTrack());
                    logger.debug("Artist: " + id3v2Tag.getArtist());
                    logger.debug("Title: " + id3v2Tag.getTitle());
                    logger.debug("Album: " + id3v2Tag.getAlbum());
                    logger.debug("Year: " + id3v2Tag.getYear());
                    logger.debug("Genre: " + id3v2Tag.getGenre() + " (" + id3v2Tag.getGenreDescription() + ")");
                    logger.debug("Comment: " + id3v2Tag.getComment());
                    logger.debug("Lyrics: " + id3v2Tag.getLyrics());
                    logger.debug("Composer: " + id3v2Tag.getComposer());
                    logger.debug("Publisher: " + id3v2Tag.getPublisher());
                    logger.debug("Original artist: " + id3v2Tag.getOriginalArtist());
                    logger.debug("Album artist: " + id3v2Tag.getAlbumArtist());
                    logger.debug("Copyright: " + id3v2Tag.getCopyright());
                    logger.debug("URL: " + id3v2Tag.getUrl());
                    logger.debug("Encoder: " + id3v2Tag.getEncoder());
                }*/

}
