package com.adacore.polyglot.proxy;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

import java.util.List;
import org.junit.jupiter.api.Test;

public class ReferenceTest {
    @Test
    public void testEquals() {
        Reference r1 =
                new Reference(
                        new FullyQualifiedName(
                                Name.fromLower("r1"), Name.fromLower("r2"), Name.fromLower("r3")),
                        false,
                        false,
                        false,
                        false);
        Reference r2 =
                new Reference(
                        new FullyQualifiedName(
                                Name.fromLower("r1"), Name.fromLower("r2"), Name.fromLower("r3")),
                        false,
                        false,
                        false,
                        false);

        assertEquals(r1, r2);
    }

    @Test
    public void testNotEqual() {
        Reference r1 =
                new Reference(
                        new FullyQualifiedName(
                                Name.fromLower("r1"), Name.fromLower("r2"), Name.fromLower("r3")),
                        false,
                        false,
                        false,
                        false);
        Reference r2 =
                new Reference(
                        new FullyQualifiedName(
                                Name.fromLower("r1bis"),
                                Name.fromLower("r2"),
                                Name.fromLower("r3")),
                        false,
                        false,
                        false,
                        false);

        assertNotEquals(r1, r2);
    }

    @Test
    public void testWith() {
        Reference r =
                new Reference(
                        new FullyQualifiedName(
                                Name.fromLower("r1"), Name.fromLower("r2"), Name.fromLower("r3")),
                        false,
                        false,
                        false,
                        false);

        Reference rPointer =
                new Reference(
                        new FullyQualifiedName(
                                List.of(
                                        Name.fromLower("r1"),
                                        Name.fromLower("r2"),
                                        Name.fromLower("r3"))),
                        true,
                        false,
                        false,
                        false);

        assertEquals(r.withIsPointer(true), rPointer);

        Reference rConst =
                new Reference(
                        new FullyQualifiedName(
                                Name.fromLower("r1"), Name.fromLower("r2"), Name.fromLower("r3")),
                        false,
                        true,
                        false,
                        false);

        assertEquals(r.withIsConst(true), rConst);

        Reference rNonNull =
                new Reference(
                        new FullyQualifiedName(
                                Name.fromLower("r1"), Name.fromLower("r2"), Name.fromLower("r3")),
                        false,
                        false,
                        true,
                        false);

        assertEquals(r.withIsNonNull(true), rNonNull);

        Reference rRef =
                new Reference(
                        new FullyQualifiedName(
                                Name.fromLower("r1"), Name.fromLower("r2"), Name.fromLower("r3")),
                        false,
                        false,
                        false,
                        true);

        assertEquals(r.withIsReference(true), rRef);
    }
}
