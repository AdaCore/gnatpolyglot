//
//  Copyright (C) 2025-2026, AdaCore
//  SPDX-License-Identifier: GPL-3.0-or-later
//

package com.adacore.gnatpolyglot.proxy;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

public class NamesTest {
    @Test
    public void testFromLower() {
        Name name = Name.fromLower("name_from_lower");
        assertEquals("name_from_lower", name.toLower());
        assertEquals("nameFromLower", name.toCamel());
        assertEquals("NameFromLower", name.toPascal());
        assertEquals("Name_From_Lower", name.toPascalWithUnderscore());
    }

    @Test
    public void testFromCamel() {
        Name name = Name.fromCamel("nameFromCamel");
        assertEquals("name_from_camel", name.toLower());
        assertEquals("nameFromCamel", name.toCamel());
        assertEquals("NameFromCamel", name.toPascal());
        assertEquals("Name_From_Camel", name.toPascalWithUnderscore());
    }

    @Test
    public void testFromPascal() {
        Name name = Name.fromPascal("NameFromPascal");
        assertEquals("name_from_pascal", name.toLower());
        assertEquals("nameFromPascal", name.toCamel());
        assertEquals("NameFromPascal", name.toPascal());
        assertEquals("Name_From_Pascal", name.toPascalWithUnderscore());
    }

    @Test
    public void testFromCamelWithUnderscore() {
        Name name = Name.fromPascalWithUnderscore("Name_From_Pascal_Score");
        assertEquals("name_from_pascal_score", name.toLower());
        assertEquals("nameFromPascalScore", name.toCamel());
        assertEquals("NameFromPascalScore", name.toPascal());
        assertEquals("Name_From_Pascal_Score", name.toPascalWithUnderscore());
    }
}
