<%--
Copyright (C) 2025-2026, AdaCore
SPDX-License-Identifier: GPL-3.0-or-later
vim: filetype=java
Template file to generate a class.
--%>
@import com.adacore.gnatpolyglot.NativeType
@import com.adacore.gnatpolyglot.proxy.EnumerationDecl
@import com.adacore.gnatpolyglot.proxy2java.JavaAPI
@import com.adacore.gnatpolyglot.proxy2java.codegen.TypenameGenerator

@param JavaAPI api
@param EnumerationDecl enumDecl

!{
    String enumName = enumDecl.name.getLastName().toPascal();
    NativeType nativeType = enumDecl.representationType();
    String longSuffix = nativeType == NativeType.SINT64 ? "L" : "";
    String nativeTypename = api.javaPrimitiveTypename(nativeType);
    String nativeWrapper = TypenameGenerator.nativeWrapperTypename(nativeType);
}

@template.text_header()

package ${api.packagePath(enumDecl.name)};

${api.formatDoc(enumDecl, 0)}
public enum ${enumName} {
@for (var lit : enumDecl.items)
    ${api.formatDoc(lit.doc, 1)}
    ${lit.name.toUpper()}((${nativeTypename}) ${lit.value}${longSuffix}),
@endfor
    ;

    public final ${nativeTypename} value;

    ${enumName}(${nativeTypename} value) {
        this.value = value;
    }

    public static final java.util.Map<${nativeWrapper}, ${enumName}> fromValue =
        java.util.Arrays.stream(${enumName}.values())
            .collect(java.util.stream.Collectors.toUnmodifiableMap(
                e -> e.value, 
                java.util.function.Function.identity()
            ));

    public static class Ref extends com.adacore.gnatpolyglot.runtime.ScalarRef {

        public Ref(${enumName} enumValue) {
            super(${nativeWrapper}.BYTES);
            setValue(enumValue);
        }

        public Ref(java.nio.ByteBuffer buffer) {
            super(buffer);
        }

        public ${enumName} getValue() {
            return fromValue.get(
                this.value.${api.enumByteBufferGet(nativeType)}(0)
            );
        }

        public void setValue(${enumName} enumValue) {
            this.value.${api.enumByteBufferPut(nativeType)}(
                0,
                (${nativeTypename}) enumValue.value
            );
        }

        @Override
        public String toString() {
            return getValue().toString();
        }
    }
}
