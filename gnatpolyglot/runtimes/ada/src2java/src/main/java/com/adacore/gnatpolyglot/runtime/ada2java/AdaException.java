//
//  Copyright (C) 2025-2026, AdaCore
//  SPDX-License-Identifier: Apache-2.0
//

package com.adacore.gnatpolyglot.runtime.ada2java;

import java.util.function.Consumer;

import com.adacore.gnatpolyglot.runtime.PolyglotData;
import com.adacore.gnatpolyglot.runtime.PolyglotData.Owner;
import com.adacore.gnatpolyglot.runtime.PolyglotException;
import com.adacore.gnatpolyglot.runtime.PolyglotKernel;

public class AdaException extends PolyglotException {

    enum StandardExceptionsKind {
        CONSTRAINT_ERROR_KIND(-4),
        PROGRAM_ERROR_KIND(-3),
        STORAGE_ERROR_KIND(-2),
        TASKING_ERROR_KIND(-1),
        ANONYMOUS_ERROR_KIND(0);

        final public int id;

        StandardExceptionsKind(int id) {
            this.id = id;
        }
    }

    public static void rethrowStandardException() {
        long addr = PolyglotKernel.ExceptionInformation.getExceptionData();
        if (addr == 0) return;
        com.adacore.gnatpolyglot.runtime.PolyglotData data =
            new com.adacore.gnatpolyglot.runtime.PolyglotData.Pointer(
                addr, com.adacore.gnatpolyglot.runtime.PolyglotData.Owner.USER);

        PolyglotKernel.ExceptionInformation.setExceptionData(0);

        int kind = PolyglotKernel.ExceptionInformation.getExceptionKind();

        String message = PolyglotKernel.ExceptionInformation.getMessage();
        PolyglotKernel.ExceptionInformation.setExceptionData(0);
        com.adacore.gnatpolyglot.runtime.PolyglotException exc = switch (kind) {
            case -4 -> 
                new ConstraintError(new PolyglotString(message));
            case -3 ->
                new ProgramError(new PolyglotString(message));
            case -2 ->
                new StorageError(new PolyglotString(message));
            case -1 ->
                new TaskingError(new PolyglotString(message));
            default -> null;
        };

        if (exc != null) {
            PolyglotKernel.ExceptionInformation.clearException();
            throw exc;
        }
    }

    protected static native long createExceptionOccurence(int kind);

    protected static native long createExceptionOccurenceMessage(int kind, ArrayData message);

    public AdaException() {
        super(
            new PolyglotData.Pointer(
                createExceptionOccurence(StandardExceptionsKind.ANONYMOUS_ERROR_KIND.id),
                Owner.USER
            )
        );
    }

    public AdaException(PolyglotString message) {
        super(
            new PolyglotData.Pointer(
                createExceptionOccurenceMessage(
                    StandardExceptionsKind.ANONYMOUS_ERROR_KIND.id, message._getData()),
                Owner.USER
            ),
            message.toString()
        );
    }

    /** Internal use only. */
    public AdaException(PolyglotData data) {
        super(data);
    }

    /** Internal use only. */
    public AdaException(PolyglotData data, PolyglotString message) {
        super(data, message.toString());
    }

    private native void freeException(long addr);

    /** Return the function to free the heap memory. */
    final protected Consumer<PolyglotData> _getFree() {
        return  (data) -> { freeException(data.getAddress()); };
    }

    static {
        Library.loadLibrary();
    }
}
