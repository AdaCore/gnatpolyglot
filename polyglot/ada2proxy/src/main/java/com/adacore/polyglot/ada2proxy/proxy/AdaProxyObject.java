//
//  Copyright (C) 2025-2026, AdaCore
//  SPDX-License-Identifier: GPL-3.0-or-later
//

package com.adacore.polyglot.ada2proxy.proxy;

/** Interface to visit the Proxy. */
public interface AdaProxyObject {
    <T> T accept(AdaProxyVisitor<T> visitor);
}
