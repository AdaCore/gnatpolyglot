package com.adacore.gnatpolyglot.runtime.ada2java;

import com.adacore.gnatpolyglot.runtime.PolyglotData;
import com.adacore.gnatpolyglot.runtime.PolyglotData.Owner;

public class ConstraintError extends AdaException {
    public ConstraintError() {
        super(
            new PolyglotData.Pointer(
                createExceptionOccurence(StandardExceptionsKind.CONSTRAINT_ERROR_KIND.id),
                Owner.USER
            )
        );
    }

    public ConstraintError(PolyglotString message) {
        super(
            new PolyglotData.Pointer(
                createExceptionOccurenceMessage(
                    StandardExceptionsKind.CONSTRAINT_ERROR_KIND.id,
                    message.getData()
                ),
                Owner.USER
            ),
            message
        );
    }

    public ConstraintError(PolyglotData data) {
        super(data);
    }

    public ConstraintError(PolyglotData data, PolyglotString message) {
        super(data, message);
    }
}
