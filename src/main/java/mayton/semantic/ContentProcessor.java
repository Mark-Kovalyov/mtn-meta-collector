package mayton.semantic;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.Resource;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;

public interface ContentProcessor {

    void process(@NotNull Model model,@NotNull Resource idRes, @NotNull Path file, @NotNull BasicFileAttributes attrs, long id) throws IOException;

}
