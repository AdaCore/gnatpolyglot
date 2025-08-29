package com.adacore.polyglot.proxy;

import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import java.io.IOException;
import java.util.Objects;
import java.util.regex.Pattern;

/**
 * Utility class around names. Unifies possible notations for names and is able to convert a name
 * from a notation to an other.
 */
@JsonDeserialize(using = Name.NameJsonDeserializer.class)
@JsonSerialize(using = Name.NameJsonSerializer.class)
public class Name {

    /** Utility class to help Jackson serialize Names into json. */
    public static class NameJsonSerializer extends StdSerializer<Name> {
        protected NameJsonSerializer() {
            this(null);
        }

        protected NameJsonSerializer(java.lang.Class<Name> t) {
            super(t);
        }

        @Override
        public void serialize(Name name, JsonGenerator generator, SerializerProvider provider)
                throws IOException {
            generator.writeString(name.name);
        }
    }

    /** Utility class to help Jackson deserialize Names from json. */
    public static class NameJsonDeserializer extends StdDeserializer<Name> {
        public NameJsonDeserializer() {
            this(null);
        }

        public NameJsonDeserializer(java.lang.Class<?> vc) {
            super(vc);
        }

        @Override
        public Name deserialize(JsonParser parser, DeserializationContext context)
                throws IOException, JacksonException {
            String name = parser.getText();
            return Name.fromLower(name);
        }
    }

    private final String name;

    /** Constructor using the lower syntax. */
    public Name(String name) {
        if (name.isEmpty()) throw new IllegalArgumentException("A name cannot be empty");
        if (!isLower(name)) throw new IllegalArgumentException(name + " is not lower");
        this.name = name;
    }

    /** Return whether the name uses the lower syntax. */
    public static boolean isLower(String name) {
        return name.matches("[a-z][a-z0-9]*(_[a-z0-9]+)*");
    }

    /** Return whether the name uses the camel syntax. */
    public static boolean isCamel(String name) {
        return name.matches("[a-z][a-z0-9]*([A-Z0-9][a-z0-9]*)*");
    }

    /** Return whether the name uses the Pascal syntax. */
    public static boolean isPascal(String name) {
        return name.matches("[A-Z][a-z0-9]*([A-Z0-9][a-z0-9]*)*");
    }

    /** Return whether the name uses the Pascal syntax. */
    public static boolean isPascalWithUnderscore(String name) {
        return name.matches("[A-Z][a-z0-9]*(_[A-Z0-9][a-z0-9]*)*");
    }

    /** Return a new Name from a string using the lower syntax. */
    public static Name fromLower(String name) {
        if (!isLower(name)) throw new IllegalArgumentException(name + " is not lower");
        return new Name(name);
    }

    /** Return a new Name from a string using the camel syntax. */
    public static Name fromCamel(String name) {
        if (!isCamel(name)) throw new IllegalArgumentException(name + " is not in camel syntax");
        return new Name(
                Pattern.compile("[A-Z]")
                        .matcher(name)
                        .replaceAll(
                                mr -> {
                                    return "_" + mr.group().toLowerCase();
                                }));
    }

    /** Return a new name from a string using the Pascal syntax. */
    public static Name fromPascal(String name) {
        if (!isPascal(name)) throw new IllegalArgumentException(name + " is not in Pascal syntax");
        return new Name(
                Pattern.compile("[A-Z]")
                        .matcher(name)
                        .replaceAll(
                                mr -> {
                                    if (mr.start() == 0) return mr.group().toLowerCase();
                                    return "_" + mr.group().toLowerCase();
                                }));
    }

    /** Return a new name from a string using the Pascal syntax with additional underscores. */
    public static Name fromPascalWithUnderscore(String name) {
        if (!isPascalWithUnderscore(name))
            throw new IllegalArgumentException(
                    name + " is not in Pascal syntax with additional underescores");
        return new Name(name.toLowerCase());
    }

    /** Return the name with the lower syntax. */
    public String toLower() {
        return name.toLowerCase();
    }

    /** Return the name with the camel syntax. */
    public String toCamel() {
        return Pattern.compile("_.")
                .matcher(name)
                .replaceAll(
                        mr -> {
                            if (mr.start() == 0) return mr.group();
                            return String.valueOf(mr.group().charAt(1)).toUpperCase();
                        });
    }

    /** Return the name with the Pascal syntax. */
    public String toPascal() {
        return Pattern.compile("^.|_.")
                .matcher(name)
                .replaceAll(
                        mr -> {
                            if (mr.start() == 0) return mr.group().toUpperCase();
                            return String.valueOf(mr.group().charAt(1)).toUpperCase();
                        });
    }

    /** Return the name using the Pascal syntax with additional underscores. */
    public String toPascalWithUnderscore() {
        return Pattern.compile("^.|_.")
                .matcher(name)
                .replaceAll(
                        mr -> {
                            return mr.group().toUpperCase();
                        });
    }

    public String toUpper() {
        return name.toUpperCase();
    }

    /**
     * Return a new name that is the concatenation of this and rhs with an underscore in between.
     */
    public Name concat(Name rhs) {
        return Name.fromLower(this.name + "_" + rhs.name);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Name other) return this.name.equals(other.name);
        return false;
    }

    @Override
    public int hashCode() {
        return Objects.hash(name.hashCode());
    }
}
