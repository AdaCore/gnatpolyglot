package com.adacore.polyglot.proxy;

import com.fasterxml.jackson.annotation.JsonProperty;

/** Represent the possible owners of a memory location. */
public enum Owner {
    /** The owner of the memory cannot be defined. */
    @JsonProperty("unknown")
    UNKNOWN,
    /** The owner of the memory is the user. */
    @JsonProperty("user")
    USER,
    /** The owner of the memory is the library. */
    @JsonProperty("library")
    LIBRARY,
    /** The memory is statically allocated, its owner cannot change. */
    @JsonProperty("static")
    STATIC
}
