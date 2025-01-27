package com.adacore.polyglot.proxy;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.exc.StreamReadException;
import com.fasterxml.jackson.databind.DatabindException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.File;
import java.io.IOException;
import java.util.List;

/** Root class of the Proxy IR */
public class Proxy implements ProxyObject {

    private static final ObjectMapper objectMapper = new ObjectMapper();

    static {
        // To allow for future extension, any unknown field contained in a json object must not be
        // interpreted as an error, but it must be ignored instead.
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    /** Return the Jackson object mapper. */
    public static ObjectMapper getObjectMapper() {
        return objectMapper;
    }

    /**
     * Create a Proxy from a json String.
     *
     * @param json The string to read from
     * @return The proxy.
     */
    public static Proxy readProxy(String json)
            throws JsonMappingException, JsonProcessingException, ProxyException {
        Proxy p = objectMapper.readValue(json, Proxy.class);
        List<String> diagnostics = p.validate();
        if (!diagnostics.isEmpty()) throw new ProxyException(diagnostics);
        return p;
    }

    /**
     * Create a Proxy from a json file.
     *
     * @param file the file to read from.
     * @return The proxy.
     */
    public static Proxy readProxy(File file)
            throws StreamReadException, DatabindException, IOException {
        try {
            return objectMapper.readValue(file, Proxy.class);
        } catch (DatabindException e) {
            System.out.println(e.getMessage());
            return null;
        }
    }

    /** List of all the modules of the proxy */
    @JsonProperty("modules")
    public final List<Module> modules;

    public Proxy(@JsonProperty(value = "modules", required = true) List<Module> modules) {
        this.modules = modules;
    }

    /**
     * Write a proxy to a file.
     *
     * @param file The file to write to.
     */
    public void writeProxy(File file) throws Exception {
        objectMapper.writeValue(file, this);
    }

    /** Write a proxy as a string. */
    public String writeProxyAsString() throws Exception {
        return objectMapper.writeValueAsString(this);
    }

    /**
     * Verifiy the validity of the proxy.
     *
     * @return The list of diagnostic found.
     */
    public List<String> validate() {
        ProxyValidator validator = new ProxyValidator();
        validator.visit(this);
        return validator.getDiagnostics();
    }

    @Override
    public <T> T visit(ProxyVisitor<T> v) {
        return v.visit(this);
    }
}
