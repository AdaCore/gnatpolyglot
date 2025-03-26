package com.adacore.polyglot.proxy2cpp;

import com.adacore.polyglot.Printer;
import com.adacore.polyglot.proxy.ProxyException;
import gg.jte.ContentType;
import gg.jte.TemplateEngine;
import gg.jte.output.FileOutput;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Map;

public class CppPrinter extends Printer {

    /** The template rendering engine. */
    private final TemplateEngine templateEngine =
            TemplateEngine.createPrecompiled(Path.of("jte-classes"), ContentType.Plain);

    public CppPrinter(Path path) throws IOException, ProxyException {
        super(path);
    }

    @Override
    public void generate(Path outputPath) throws IOException {
        CppAPI api = new CppAPI(context, outputPath);

        for (var module : proxy.modules) {
            Path headerFilename = api.headerFilePath(module);
            try (FileOutput packageSpec = new FileOutput(headerFilename)) {
                templateEngine.render(
                        "header.jte", Map.of("api", api, "module", module), packageSpec);
            }

            Path sourceFilename = api.sourceFilePath(module);
            try (FileOutput packageSpec = new FileOutput(sourceFilename)) {
                templateEngine.render(
                        "source.jte", Map.of("api", api, "module", module), packageSpec);
            }
        }
    }
}
