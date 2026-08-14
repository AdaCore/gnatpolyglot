//
//  Copyright (C) 2025-2026, AdaCore
//  SPDX-License-Identifier: GPL-3.0-or-later
//

package com.adacore.gnatpolyglot.proxy;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Objects;

/** Contain information about memory ownership for function parameters. */
public class Transfer implements ProxyObject {

    public enum RequiredOwner {
        /** The owner of the memory must be the user. */
        @JsonProperty("user")
        USER,
        /** The owner of the memory must be the library. */
        @JsonProperty("library")
        LIBRARY,
        /** The owner of the memory can be anything. */
        @JsonProperty("any")
        ANY,
    }

    /** Required owner of the memory before passing the argument. */
    @JsonProperty("required_owner")
    public final RequiredOwner required_owner;

    @JsonCreator
    public Transfer(
            @JsonProperty(value = "required_owner", required = true) RequiredOwner required_owner) {
        this.required_owner = required_owner;
    }

    public <T> T visit(ProxyVisitor<T> v) {
        return v.visit(this);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj instanceof Transfer other) {
            return Objects.deepEquals(required_owner, other.required_owner);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return Objects.hash(required_owner);
    }
}
