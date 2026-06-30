//
//  Copyright (C) 2025-2026, AdaCore
//  SPDX-License-Identifier: Apache-2.0
//

package com.adacore.gnatpolyglot.runtime;

import java.lang.ref.Cleaner;
import java.util.function.Consumer;

import com.adacore.gnatpolyglot.runtime.PolyglotData.Owner;

public class PolyglotCleaner {
    /** Global Cleaner for all bound libraries. */
    private static final Cleaner CLEANER = Cleaner.create();

    /** Runnable object to free a pointer. */
    private static class CleaningAction implements Runnable {

        /** The data to free. */
        private PolyglotData data;

        /** The function to free the data. */
        private Consumer<PolyglotData> free;

        CleaningAction(PolyglotData data, Consumer<PolyglotData> free) {
            this.data = data;
            this.free = free;
        }

        @Override
        final public void run() {
            if (this.data != null && this.data.getOwner() == Owner.USER) {
                free.accept(this.data);
                this.data = null;
            }
        }
    }

    public static Cleaner.Cleanable register(
        Object root, PolyglotData data, Consumer<PolyglotData> freeFunction
    ) {
        return CLEANER.register(root, new CleaningAction(data, freeFunction));
    }
}

