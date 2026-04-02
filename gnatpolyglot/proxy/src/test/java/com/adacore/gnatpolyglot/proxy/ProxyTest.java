//
//  Copyright (C) 2025-2026, AdaCore
//  SPDX-License-Identifier: GPL-3.0-or-later
//

package com.adacore.gnatpolyglot.proxy;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class ProxyTest {

    ProxyValidator.Validator validator = new ProxyValidator.Validator();

    static ObjectMapper objectMapper = Proxy.getObjectMapper();

    @BeforeEach
    public void setup() {
        validator.getDiagnostics().clear();
    }

    @Test
    public void emptyProxy() {
        assertThrows(ProxyException.class, () -> Proxy.readProxy("{}"));
    }

    @Test
    public void nonOptionalNullValue() throws Exception {
        // The list of modules in ``Proxy`` cannot be null
        String json =
                """
        {
            "name": "proxy_name",
            "input_language": "proxy_name",
            "modules": null
        }
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
                "name": { "names": ["function_0"] },
                "doc": "doc function_0",
                "symbol": "__symbol_function_0",
                "type": {
                   "kind": "function",
                   "parameters": [],
                   "return_type": { "kind": "typename", "name": { "names": [ "uint8" ] } },
                   "return_owner": "unknown"
                }
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
                "name": { "names": ["class_0"] },
                "doc": "doc",
                "size": -8,
                "inheritability": "inheritable",
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
                "type": { "kind": "typename", "name": { "names": [ "uint8" ] } },
                "field": "f"
            }
        """;
        Role classDecl = objectMapper.readValue(json, Role.class);
        validator.visit(classDecl);
        assertNotEquals(0, validator.getDiagnostics().size());
    }
}
