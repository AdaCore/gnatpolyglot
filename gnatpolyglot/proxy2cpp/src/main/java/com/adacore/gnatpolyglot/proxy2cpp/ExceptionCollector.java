//
//  Copyright (C) 2025-2026, AdaCore
//  SPDX-License-Identifier: GPL-3.0-or-later
//

package com.adacore.gnatpolyglot.proxy2cpp;

import com.adacore.gnatpolyglot.proxy.ExceptionDecl;
import com.adacore.gnatpolyglot.proxy.Proxy;
import java.util.Comparator;
import java.util.List;

public class ExceptionCollector {

    public static List<String> getIncludes(Proxy proxy) {
        return proxy.modules.stream()
                .filter(m -> m.declarations.stream().anyMatch(d -> d instanceof ExceptionDecl))
                .map(m -> m.name.join(n -> n.getLastName().toLower(), "", "_", ".h"))
                .distinct()
                .toList();
    }

    public static List<ExceptionDecl> getExceptions(Proxy proxy) {
        return proxy.modules.stream()
                .flatMap(
                        m ->
                                m.declarations.stream()
                                        .filter(d -> d instanceof ExceptionDecl)
                                        .map(d -> (ExceptionDecl) d))
                .sorted(Comparator.comparingInt(exc -> exc.enumValue))
                .toList();
    }
}
