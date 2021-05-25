package mayton.semantic.media;

import mayton.semantic.ContentProcessor;
import org.apache.commons.lang3.StringUtils;
import org.apache.jena.datatypes.xsd.XSDDatatype;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.Resource;
import org.gagravarr.ogg.audio.OggAudioHeaders;
import org.gagravarr.vorbis.VorbisFile;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.*;

import static mayton.semantic.FileUtils.escapeControl;

public class VorbisContentProcessor implements ContentProcessor {

    static Logger logger = LoggerFactory.getLogger(VorbisContentProcessor.class);

    private static VorbisContentProcessor processor = null;

    public static ContentProcessor createInstance() {
        if (processor == null) {
            processor = new VorbisContentProcessor();
        }
        return processor;
    }


    static final Set<String> numericTags = new HashSet(Arrays.asList("tracknumber", "totaltracks"));
    static final Set<String> dateTags = new HashSet(Arrays.asList("date", "year"));

    /**
     * <pre>
     * New logical stream 69ef5057 (1777291351) found
     * Processing file "/storage/music/_JAZZ/1975.Face The Music/1.Fire In High.ogg"
     *
     * Vorbis Headers:
     *   Version: 0
     *   Vendor: Xiph.Org libVorbis I 20040629
     *   Channels: 2
     *   Rate: 44100
     *
     *   Nominal Bitrate: 320000
     *   Lower Bitrate: 0
     *   Upper Bitrate: 0
     *
     * User Comments:
     *   date=1975
     *   copyright=JET Records
     *   artist=Electric Light Orchestra
     *   year=1975
     *   album=Face The Music
     *   genre=Art Rock
     *   description=
     *   produced-by=Jeff Lynn
     *   title=Fire In High
     *   tracknumber=1
     *
     * Vorbis Setup:
     *   Codebooks: 67
     * </pre>
     *
     * @param model
     * @param file
     * @param attrs
     * @param id
     * @throws IOException
     */
    @Override
    public void process(@NotNull Model model, @NotNull Resource idRes, @NotNull Path file, @NotNull BasicFileAttributes attrs, long id) throws IOException {
        VorbisFile vf = new VorbisFile(file.toFile());
        Map<String, String> tags = extractTags(vf);
        model.add(
                idRes,
                model.createProperty("http://www.w3.org/1999/02/22-rdf-syntax-ns#type"),
                "/audio/vorbis"

        );

        tags.entrySet().forEach(entry -> {
                    String key = entry.getKey();
                    String value = entry.getValue();
                    if (!StringUtils.isBlank(value)) {
                        if (numericTags.contains(key)) {
                            model.add(idRes,
                                    model.createProperty("http://vorbis.org#" + key),
                                    entry.getValue(),
                                    XSDDatatype.XSDinteger);
                        } else if (dateTags.contains(key)) {
                            model.add(idRes,
                                    model.createProperty("http://vorbis.org#" + key),
                                    entry.getValue(),
                                    XSDDatatype.XSDdate);
                        } else {
                            model.add(idRes,
                                    model.createProperty("http://vorbis.org#" + key),
                                    entry.getValue(),
                                    XSDDatatype.XSDstring);
                        }
                    }
                }

        );
    }



    public Map<String, String> extractTags(OggAudioHeaders oa) {
        LinkedHashMap<String, String> m = new LinkedHashMap<>();
        Map<String, List<String>> comments = oa.getTags().getAllComments();
        Iterator iterator = comments.keySet().iterator();

        while (iterator.hasNext()) {
            String tag = (String) iterator.next();
            Iterator tagIterator = ((List) comments.get(tag)).iterator();
            while (tagIterator.hasNext()) {
                String value = (String) tagIterator.next();
                m.put(escapeControl(tag), escapeControl(value));
            }
        }
        return m;
    }
}
