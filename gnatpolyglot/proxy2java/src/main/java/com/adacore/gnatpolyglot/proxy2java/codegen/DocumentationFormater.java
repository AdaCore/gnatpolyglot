//
//  Copyright (C) 2025-2026, AdaCore
//  SPDX-License-Identifier: GPL-3.0-or-later
//

package com.adacore.gnatpolyglot.proxy2java.codegen;

import com.adacore.gnatpolyglot.proxy.Declaration;
import com.adacore.gnatpolyglot.proxy.FunctionDecl;
import com.adacore.gnatpolyglot.proxy.Role.RoleKind;
import com.adacore.gnatpolyglot.proxy.Transfer.RequiredOwner;
import com.adacore.gnatpolyglot.proxy2java.JavaAPI;
import java.util.ArrayList;
import java.util.List;

public class DocumentationFormater {

    private String tag(String tag, String content) {
        return "{@%s %s}".formatted(tag, content);
    }

    private List<String> split(String content) {
        ArrayList<String> res = new ArrayList<>();
        String[] split = content.split("\n");
        for (int i = 0; i < split.length; i++) {
            // Create a new paragraph is the previous line is blank.
            if (i != 0 && split[i - 1].isBlank()) {
                res.add("<p>");
            }
            res.add(split[i]);
        }
        return res;
    }

    private String wrap(List<String> lines, int indent) {
        if (lines.isEmpty()) return "";
        if (lines.size() == 1) return "/** %s */".formatted(lines.getFirst());
        StringBuilder builder = new StringBuilder("/**\n");

        for (var l : lines) {
            builder.append("    ".repeat(indent)).append(" * ").append(l).append("\n");
        }
        builder.append("    ".repeat(indent)).append(" */");

        return builder.toString();
    }

    JavaAPI api;

    public DocumentationFormater(JavaAPI api) {
        this.api = api;
    }

    private String formatFunctionDoc(FunctionDecl decl, int indent) {
        ArrayList<String> lines = new ArrayList<>();
        if (!decl.doc.isBlank()) {
            lines.addAll(split(decl.doc));
            lines.add("");
        }
        for (var param : decl.type.parameters) {
            StringBuilder builder =
                    new StringBuilder("@param ")
                            .append(api.javaArgName(param.name))
                            .append(" ")
                            .append(
                                    param.transfer.required_owner != RequiredOwner.LIBRARY
                                            ? "can"
                                            : "must")
                            .append(" be owned by ")
                            .append(param.transfer.required_owner);
            lines.add(builder.toString());
        }
        if (!api.returnsVoid(decl.type)
                && !api.getContext().isNativeScalar(decl.type.returnType.referencedType())
                && (decl.role == null
                        || (decl.role.kind != RoleKind.ALLOC
                                && decl.role.kind != RoleKind.SHADOW_ALLOC))) {
            lines.add(
                    new StringBuilder("@return ")
                            .append(tag("link", api.javaTypename(decl.type.returnType)))
                            .append(" owned by ")
                            .append(tag("link", api.javaOwner(decl.type.returnOwner)))
                            .toString());
        }
        return wrap(lines, indent);
    }

    public String formatAnyDoc(String doc, int indent) {
        ArrayList<String> lines = new ArrayList<>();
        if (!doc.isBlank()) {
            lines.addAll(split(doc));
        }
        return wrap(lines, indent);
    }

    public String formatDoc(Declaration decl, int indent) {
        if (decl instanceof FunctionDecl functionDecl)
            return formatFunctionDoc(functionDecl, indent);
        return formatAnyDoc(decl.doc, indent);
    }
}
