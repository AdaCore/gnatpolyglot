<%--
Copyright (C) 2025-2026, AdaCore
SPDX-License-Identifier: GPL-3.0-or-later
vim: filetype=jtecpp
Template file to render the user-given header into generated cpp sources.
--%>

@import com.adacore.polyglot.PolyglotUtils

@for(var line : PolyglotUtils.getHeaderContent())
// ${line}@endfor

