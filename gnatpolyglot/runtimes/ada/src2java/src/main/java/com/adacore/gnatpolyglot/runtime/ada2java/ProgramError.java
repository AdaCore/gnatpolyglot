package com.adacore.gnatpolyglot.runtime.ada2java;

import com.adacore.gnatpolyglot.runtime.PolyglotData;
import com.adacore.gnatpolyglot.runtime.PolyglotData.Owner;

public class ProgramError extends AdaException {
    public ProgramError() {
        super(
            new PolyglotData.Pointer(
                createExceptionOccurence(StandardExceptionsKind.PROGRAM_ERROR_KIND.id),
                Owner.USER
            )
        );
    }

    public ProgramError(PolyglotString message) {
        super(
            new PolyglotData.Pointer(
                createExceptionOccurenceMessage(
                    StandardExceptionsKind.PROGRAM_ERROR_KIND.id,
                    message.getData()
                ),
                Owner.USER
            ),
            message
        );
    }

    public ProgramError(PolyglotData data) {
        super(data);
    }

    public ProgramError(PolyglotData data, PolyglotString message) {
        super(data, message);
    }
}
