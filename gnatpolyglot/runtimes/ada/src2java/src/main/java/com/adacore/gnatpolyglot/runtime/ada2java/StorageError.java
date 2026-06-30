//
//  Copyright (C) 2025-2026, AdaCore
//  SPDX-License-Identifier: Apache-2.0
//

package com.adacore.gnatpolyglot.runtime.ada2java;

import com.adacore.gnatpolyglot.runtime.PolyglotData;
import com.adacore.gnatpolyglot.runtime.PolyglotData.Owner;

public class StorageError extends AdaException {
    public StorageError() {
        super(
            new PolyglotData.Pointer(
                createExceptionOccurence(StandardExceptionsKind.STORAGE_ERROR_KIND.id),
                Owner.USER
            )
        );
    }

    public StorageError(PolyglotString message) {
        super(
            new PolyglotData.Pointer(
                createExceptionOccurenceMessage(
                    StandardExceptionsKind.STORAGE_ERROR_KIND.id,
                    message._getData()
                ),
                Owner.USER
            ),
            message
        );
    }

    public StorageError(PolyglotData data) {
        super(data);
    }

    public StorageError(PolyglotData data, PolyglotString message) {
        super(data, message);
    }
}
