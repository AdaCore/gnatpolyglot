//
//  Copyright (C) 2025-2026, AdaCore
//  SPDX-License-Identifier: GPL-3.0-or-later
//

package com.adacore.gnatpolyglot.proxy;

import java.util.List;

/**
 * Exception to be thrown when errors related to the Proxy are detected. May contain multiple
 * messages.
 */
public class ProxyException extends Exception {

    /** List of all messages to emit when the proxy has errors. */
    private List<String> messages;

    /** Construct an exception with a single message. */
    public ProxyException(String message) {
        this.messages = List.of(message);
    }

    /** Construct an exception with a list of messages. */
    public ProxyException(List<String> messages) {
        this.messages = messages;
    }

    /** Construct an exception with a single message anda cause. */
    public ProxyException(String message, Throwable cause) {
        super(cause);
        this.messages = List.of(message);
    }

    /** Construct an exception with a list of messages and a cause. */
    public ProxyException(List<String> messages, Throwable cause) {
        super(cause);
        this.messages = messages;
    }

    /** Construct an exception with a cause. */
    public ProxyException(Throwable cause) {
        super(cause);
        this.messages = List.of();
    }

    @Override
    /** Return a single message with by joining all the contained, separated by a comma. */
    public String getMessage() {
        return String.join(", ", messages);
    }

    /** Return all contained messages. */
    public List<String> getMessages() {
        if (messages != null) return messages;
        else return List.of(super.getMessage());
    }
}
