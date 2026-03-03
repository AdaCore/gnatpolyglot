//
//  Copyright (C) 2025-2026, AdaCore
//  SPDX-License-Identifier: GPL-3.0-or-later
//

package com.adacore.polyglot.proxy;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import java.io.File;
import java.io.IOException;
import java.util.List;

/** Root class of the Proxy IR */
public class Proxy implements ProxyObject {

    private static final ObjectMapper objectMapper =
            JsonMapper.builder()
                    // To allow for future extension, any unknown field contained in a json object
                    // must not be
                    // interpreted as an error, but it must be ignored instead.
                    .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
                    // Getter-like functions may be added to ease the manipulation of proxy objects.
                    // Only fields should be included in the resulting json unless specified.
                    .configure(MapperFeature.AUTO_DETECT_GETTERS, false)
                    .configure(MapperFeature.AUTO_DETECT_IS_GETTERS, false)
                    .build();

    /** Return the Jackson object mapper. */
    public static ObjectMapper getObjectMapper() {
        return objectMapper;
    }

    /**
     * Create a Proxy from a json String.
     *
     * @param json The string to read from
     * @return The proxy.
     * @throws ProxyException When the json string could not be parsed or the json proxy is not
     *     valid.
     */
    public static Proxy readProxy(String json) throws ProxyException {
        try {
            Proxy p = objectMapper.readValue(json, Proxy.class);
            List<String> diagnostics = p.validate();
            if (!diagnostics.isEmpty()) throw new ProxyException(diagnostics);
            return p;
        } catch (JacksonException e) {
            // Wrap the JacksonException inside the ProxyException.
            throw new ProxyException(e);
        }
    }

    /**
     * Create a Proxy from a json file.
     *
     * @param file the file to read from.
     * @return The proxy.
     * @throws IOException When there was an error when trying to read the file.
     * @throws ProxyException When the json string could not be parsed or the json proxy is not
     *     valid.
     */
    public static Proxy readProxy(File file) throws IOException, ProxyException {
        try {
            Proxy p = objectMapper.readValue(file, Proxy.class);
            List<String> diagnostics = p.validate();
            if (!diagnostics.isEmpty()) throw new ProxyException(diagnostics);
            return p;
        } catch (JacksonException e) {
            // Wrap the JacksonException inside the ProxyException.
            throw new ProxyException(e);
        }
    }

    /** Name of the proxy library */
    @JsonProperty("name")
    public final Name name;

    /** Language of the input library */
    @JsonProperty("input_language")
    public final String inputLanguage;

    /** List of all the modules of the proxy */
    @JsonProperty("modules")
    public final List<Module> modules;

    public Proxy(
            @JsonProperty(value = "name", required = true) Name name,
            @JsonProperty(value = "input_language", required = true) String inputLanguage,
            @JsonProperty(value = "modules", required = true) List<Module> modules) {
        this.name = name;
        this.inputLanguage = inputLanguage;
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
        return ProxyValidator.validate(this);
    }

    @Override
    public <T> T visit(ProxyVisitor<T> v) {
        return v.visit(this);
    }
}
