//
//  Copyright (C) 2025-2026, AdaCore
//  SPDX-License-Identifier: GPL-3.0-or-later
//

package com.adacore.gnatpolyglot.proxy;

public class BadNameSyntaxException extends RuntimeException {
    public BadNameSyntaxException(String string) {
        super(string);
    }
}
