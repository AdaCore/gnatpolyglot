package com.adacore.gnatpolyglot.runtime.ada2java;

import com.adacore.gnatpolyglot.runtime.PolyglotData;
import com.adacore.gnatpolyglot.runtime.PolyglotData.Owner;

public class TaskingError extends AdaException {
    public TaskingError() {
        super(
            new PolyglotData.Pointer(
                createExceptionOccurence(StandardExceptionsKind.TASKING_ERROR_KIND.id),
                Owner.USER
            )
        );
    }

    public TaskingError(PolyglotString message) {
        super(
            new PolyglotData.Pointer(
                createExceptionOccurenceMessage(
                    StandardExceptionsKind.TASKING_ERROR_KIND.id,
                    message.getData()
                ),
                Owner.USER
            ),
            message
        );
    }

    public TaskingError(PolyglotData data) {
        super(data);
    }

    public TaskingError(PolyglotData data, PolyglotString message) {
        super(data, message);
    }
}
