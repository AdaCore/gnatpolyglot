package com.adacore.polyglot.proxy;

import com.adacore.polyglot.proxy.Role.RoleKind;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

/** Inspect a Proxy and verify that values are correct according to the Proxy IR specification. */
public class ProxyValidator implements ProxyVisitor<Boolean> {

    /** List accumulating diagnostics from errors found in the proxies visited. */
    private List<String> diagnostics;

    /** Stack containing the path of all fields and list items traversed. */
    private Stack<String> location;

    public ProxyValidator() {
        this.diagnostics = new ArrayList<>();
        this.location = new Stack<>();
    }

    /** Return all diagnostics found. */
    public List<String> getDiagnostics() {
        return diagnostics;
    }

    /** Empty the list of diagnostics. */
    public void emptyDiagnostics() {
        diagnostics.clear();
    }

    /** Add a new diagnostic with the following format: `{location}: {message}` */
    private void addDiagnostic(String message) {
        diagnostics.add(String.join("", location) + ": " + message);
    }

    /** Visit an optional {@link ProxyObject} */
    private void visitOptional(String fieldName, ProxyObject proxyObject) {
        if (proxyObject != null) {
            location.add("." + fieldName);
            proxyObject.visit(this);
            location.pop();
        }
    }

    /** Validate that a non ProxyObject value is not null. If it is, add a diagnostic instead. */
    private <T> void validateNonNull(String fieldName, T value) {
        if (value == null) {
            location.add("." + fieldName);
            addDiagnostic("cannot not be null");
            location.pop();
        }
    }

    /**
     * Visit a non optional {@link ProxyObject}. If proxyObject is null, add a diagnostic instead.
     */
    private void validateNonNull(String fieldName, ProxyObject proxyObject) {
        location.add("." + fieldName);
        if (proxyObject == null) addDiagnostic("cannot not be null");
        else proxyObject.visit(this);
        location.pop();
    }

    /**
     * Visit a non optional list of {@link ProxyObject}. If proxyObjects or one of its element is
     * null, add a diagnostic instead.
     */
    private <T extends ProxyObject> void validateNonNull(String fieldName, List<T> proxyObjects) {
        location.add("." + fieldName);
        if (proxyObjects == null) addDiagnostic("cannot be null");
        else {
            for (int i = 0; i < proxyObjects.size(); i++) {
                location.add("[" + i + "]");
                ProxyObject proxyObject = proxyObjects.get(i);
                if (proxyObject == null) addDiagnostic("cannot be null");
                else proxyObject.visit(this);
                location.pop();
            }
        }
        location.pop();
    }

    @Override
    public Boolean visit(Proxy proxy) {
        // The root is represented with '$'
        location.add("$");

        // ``module`` cannot be null.
        validateNonNull("module", proxy.modules);

        location.pop();
        return Boolean.valueOf(diagnostics.isEmpty());
    }

    @Override
    public Boolean visit(Module module) {
        validateNonNull("name", module.name);
        validateNonNull("declarations", module.declarations);
        visitOptional("parent", module.parent);

        return Boolean.valueOf(diagnostics.isEmpty());
    }

    @Override
    public Boolean visit(FunctionDecl functionDecl) {
        validateNonNull("name", functionDecl.name);
        validateNonNull("doc", functionDecl.doc);
        visitOptional("role", functionDecl.role);
        validateNonNull("symbol", functionDecl.symbol);
        validateNonNull("parameters", functionDecl.parameters);
        validateNonNull("return_type", functionDecl.returnType);
        validateNonNull("return_owner", functionDecl.returnOwner);
        return Boolean.valueOf(diagnostics.isEmpty());
    }

    @Override
    public Boolean visit(ClassDecl classDecl) {
        validateNonNull("name", classDecl.name);
        validateNonNull("doc", classDecl.doc);
        visitOptional("parent", classDecl.parent);
        validateNonNull("fields", classDecl.fields);

        // Negative sized class types cannot exist
        location.add(".size");
        if (classDecl.size < 0) addDiagnostic("cannot be negative");
        location.pop();

        return Boolean.valueOf(diagnostics.isEmpty());
    }

    @Override
    public Boolean visit(EnumerationDecl enumerationDecl) {
        validateNonNull("name", enumerationDecl.name);
        validateNonNull("doc", enumerationDecl.doc);
        validateNonNull("items", enumerationDecl.items);
        return Boolean.valueOf(diagnostics.isEmpty());
    }

    @Override
    public Boolean visit(Role role) {
        validateNonNull("type", role.type);
        validateNonNull("kind", role.kind);
        // The ``field`` field should only be non null when ``kind`` is ``GETTER`` or ``SETTER``
        location.add(".field");
        if (role.field != null && role.kind != RoleKind.GETTER && role.kind != RoleKind.SETTER)
            addDiagnostic("must be null when role is neither `GETTER` nor `SETTER`");
        location.pop();
        return Boolean.valueOf(diagnostics.isEmpty());
    }

    @Override
    public Boolean visit(Field field) {
        validateNonNull("name", field.name);
        validateNonNull("doc", field.doc);
        validateNonNull("type", field.type);
        return Boolean.valueOf(diagnostics.isEmpty());
    }

    @Override
    public Boolean visit(EnumItem enumItem) {
        validateNonNull("name", enumItem.name);
        validateNonNull("doc", enumItem.doc);
        return Boolean.valueOf(diagnostics.isEmpty());
    }

    @Override
    public Boolean visit(Reference reference) {
        validateNonNull("name", reference.name);
        validateNonNull("kind", reference.kind);
        visitOptional("suffix", reference.suffix);
        return Boolean.valueOf(diagnostics.isEmpty());
    }

    @Override
    public Boolean visit(Transfer transfer) {
        validateNonNull("required_owner", transfer.required_owner);
        return Boolean.valueOf(diagnostics.isEmpty());
    }

    @Override
    public Boolean visit(Parameter parameter) {
        validateNonNull("name", parameter.name);
        validateNonNull("kind", parameter.type);
        validateNonNull("transfer", parameter.transfer);
        return Boolean.valueOf(diagnostics.isEmpty());
    }

    public static void main(String[] args) {
        if (args.length != 1) {
            System.err.println("Usage: ./validator json_proxy");
            System.exit(1);
        }

        try {
            // Read the proxy in the argument file.
            Proxy.readProxy(new File(args[0]));
            System.out.println("No error found in the json");
        } catch (ProxyException e) {
            // If the exception has no message, look at its cause.
            if (e.getMessages().isEmpty()) {
                System.err.print("Could not deserialize the json:");
                System.err.println(e.getCause().getMessage());
            } else {
                // Print the diagnostics raised by the validator.
                System.out.println("Errors found in the proxy:");
                for (var m : e.getMessages()) {
                    System.out.print(" * ");
                    System.out.println(m);
                }
            }
            System.exit(1);
        } catch (IOException e) {
            System.err.println("Could not read the file:" + e.getMessage());
            System.exit(1);
        }
    }
}
