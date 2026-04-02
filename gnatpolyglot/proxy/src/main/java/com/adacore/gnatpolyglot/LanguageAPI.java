//
//  Copyright (C) 2025-2026, AdaCore
//  SPDX-License-Identifier: GPL-3.0-or-later
//

package com.adacore.gnatpolyglot;

import com.adacore.gnatpolyglot.proxy.Name;

public abstract class LanguageAPI {
    protected int tempVarValue = 0;

    /** Return a unique name in the form of `{name}_{unique_number}`. */
    protected Name makeTempName(Name name) {
        return Name.fromLower(name.toLower().concat("_").concat(String.valueOf(tempVarValue++)));
    }
}
