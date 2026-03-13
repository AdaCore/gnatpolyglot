<%--
Copyright (C) 2025-2026, AdaCore
SPDX-License-Identifier: GPL-3.0-or-later
vim: filetype=jteada
Template file to render the user-given header into generated ada sources.
--%>

@import com.adacore.gnatpolyglot.GNATpolyglotUtils

@for(var line : GNATpolyglotUtils.getHeaderContent())
-- ${line}@endfor
