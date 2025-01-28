package com.adacore.polyglot.proxy;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class ProxyTest {

    ProxyValidator validator = new ProxyValidator();

    static ObjectMapper objectMapper = Proxy.getObjectMapper();

    @BeforeEach
    public void setup() {
        validator.emptyDiagnostics();
    }

    @Test
    public void emptyProxy() {
        assertThrows(JsonMappingException.class, () -> Proxy.readProxy("{}"));
    }

    @Test
    public void nonOptionalNullValue() throws Exception {
        // The list of modules in ``Proxy`` cannot be null
        String json = """
        {"modules": null}
        """;
        Proxy proxy = objectMapper.readValue(json, Proxy.class);
        validator.visit(proxy);
        assertNotEquals(0, validator.getDiagnostics().size());
    }

    @Test
    public void optionalNullValue() throws Exception {
        // Functions have optional fields
        String json =
                """
            {
                "kind": "function",
                "name": "function_0",
                "doc": "doc function_0",
                "symbol": "__symbol_function_0",
                "parameters": [],
                "return_type": { "name": "uint8", "kind": "scalar" },
                "return_owner": "unknown"
            }
        """;
        FunctionDecl proxy = objectMapper.readValue(json, FunctionDecl.class);
        validator.visit(proxy);
        assertEquals(0, validator.getDiagnostics().size());
    }

    @Test
    public void negativeSizeClass() throws Exception {
        // A class type cannot have a negative size
        String json =
                """
            {
                "kind": "class",
                "name": "class_0",
                "doc": "doc",
                "size": -8,
                "is_final": true,
                "fields": []
            }
        """;
        ClassDecl classDecl = objectMapper.readValue(json, ClassDecl.class);
        validator.visit(classDecl);
        assertNotEquals(0, validator.getDiagnostics().size());
    }

    @Test
    public void nonNullFieldMethod() throws Exception {
        // Roles can only have a field if they are of ``SETTER` or ``GETTER`` kind
        String json =
                """
            {
                "kind": "method",
                "type": { "name": "uint8", "kind": "scalar" },
                "field": "f"
            }
        """;
        Role classDecl = objectMapper.readValue(json, Role.class);
        validator.visit(classDecl);
        assertNotEquals(0, validator.getDiagnostics().size());
    }
}
