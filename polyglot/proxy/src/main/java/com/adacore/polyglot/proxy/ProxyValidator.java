package com.adacore.polyglot.proxy;

import com.adacore.polyglot.proxy.Reference.ReferenceKind;
import com.adacore.polyglot.proxy.Role.RoleKind;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Stack;

/** Inspect a Proxy and verify that values are correct according to the Proxy IR specification. */
public class ProxyValidator {

    public static class Validator implements ProxyVisitor<Boolean> {

        /** List accumulating diagnostics from errors found in the proxies visited. */
        private List<String> diagnostics;

        /** Stack containing the path of all fields and list items traversed. */
        private Stack<String> location;

        /** Set of all symbols found while exploring the proxy. */
        private Set<String> symbols;

        /** Name resolution context. */
        private ProxyContext context;

        public Validator() {
            this.diagnostics = new ArrayList<>();
            this.location = new Stack<>();
            this.symbols = new HashSet<>();
            this.context = new ProxyContext();
        }

        /** Return all the diagnostics emitted. */
        public List<String> getDiagnostics() {
            return this.diagnostics;
        }

        /** Add a new diagnostic with the following format: `{location}: {message}` */
        private void addDiagnostic(String lastLocation, String message) {
            location.add(lastLocation);
            diagnostics.add(String.join("", location) + ": " + message);
            location.pop();
        }

        /** Add a new diagnostic with the following format: `{location}: {message}` */
        private void addDiagnostic(String message) {
            diagnostics.add(String.join("", location) + ": " + message);
        }

        /** Verifies if ``ref`` is a reference to a valid existing type. */
        private void validateTypeRef(String fieldName, Reference ref) {
            if (ref != null) {
                location.add("." + fieldName);
                if (ref.getFinalKind() != ReferenceKind.CLASS
                        && ref.getFinalKind() != ReferenceKind.SCALAR)
                    addDiagnostic("reference is not a type");
                else if (context.getDeclaration(ref) == null) addDiagnostic("type does not exist");
                location.pop();
            }
        }

        /** Visit an optional {@link ProxyObject} */
        private void visitOptional(String fieldName, ProxyObject proxyObject) {
            if (proxyObject != null) {
                location.add("." + fieldName);
                proxyObject.visit(this);
                location.pop();
            }
        }

        /**
         * Validate that a non ProxyObject value is not null. If it is, add a diagnostic instead.
         */
        private <T> void validateNonNull(String fieldName, T value) {
            if (value == null) {
                location.add("." + fieldName);
                addDiagnostic("cannot not be null");
                location.pop();
            }
        }

        /**
         * Visit a non optional {@link ProxyObject}. If proxyObject is null, add a diagnostic
         * instead.
         */
        private void validateNonNull(String fieldName, ProxyObject proxyObject) {
            location.add("." + fieldName);
            if (proxyObject == null) addDiagnostic("cannot not be null");
            else proxyObject.visit(this);
            location.pop();
        }

        /**
         * Visit a non optional list of {@link ProxyObject}. If proxyObjects or one of its element
         * is null, add a diagnostic instead.
         */
        private <T extends ProxyObject> void validateNonNull(
                String fieldName, List<T> proxyObjects) {
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

            if (proxy.modules != null) {
                location.add(".modules");
                for (int i = 0; i < proxy.modules.size(); i++) {
                    location.add("[" + i + "]");
                    if (!context.register(proxy.modules.get(i)))
                        addDiagnostic("an other module exists with the same name and parent");
                    location.pop();
                }
                location.pop();
            }
            validateNonNull("modules", proxy.modules);

            location.pop();
            return Boolean.valueOf(diagnostics.isEmpty());
        }

        @Override
        public Boolean visit(Module module) {
            validateNonNull("name", module.name);

            if (module.declarations != null) {
                for (int i = 0; i < module.declarations.size(); i++) {
                    location.add("[" + i + "]");
                    if (!context.register(module, module.declarations.get(i)))
                        addDiagnostic("an other type exists with the same name and parent");
                    location.pop();
                }
            }
            validateNonNull("declarations", module.declarations);

            // It is not necessary to check if a parent module exists. Such cases are many
            // languages.
            visitOptional("parent", module.parent);
            // It needs to be a module however
            if (module.parent != null && module.parent.getFinalKind() != ReferenceKind.MODULE) {
                addDiagnostic(".parent", "parent is not a module");
            }

            return Boolean.valueOf(diagnostics.isEmpty());
        }

        @Override
        public Boolean visit(FunctionDecl functionDecl) {
            validateNonNull("name", functionDecl.name);
            validateNonNull("doc", functionDecl.doc);
            visitOptional("role", functionDecl.role);
            if (functionDecl.role != null) {
                Role role = functionDecl.role;
                location.add(".role");
                Declaration decl = context.getDeclaration(role.type);
                if (decl instanceof ClassDecl classDecl) {
                    if (role.field != null) {
                        if (!classDecl.fields.stream().anyMatch(f -> f.name.equals(role.field)))
                            addDiagnostic(".field", "the field does not exist");
                    }
                } else {
                    if (decl != null) addDiagnostic(".type", "type is not a class");
                    else addDiagnostic(".type", "type does not exist");
                }
                location.pop();
            }

            validateNonNull("symbol", functionDecl.symbol);
            if (functionDecl.symbol != null && !symbols.add(functionDecl.symbol)) {
                addDiagnostic(".symbol", "duplicate symbol");
            }

            validateNonNull("parameters", functionDecl.parameters);
            validateNonNull("return_type", functionDecl.returnType);
            validateTypeRef("return_type", functionDecl.returnType);
            validateNonNull("return_owner", functionDecl.returnOwner);

            return Boolean.valueOf(diagnostics.isEmpty());
        }

        /** Return whether the given class is sane (i.e. has no circular inheritance.) */
        public Boolean isSaneClass(ClassDecl classDecl) {
            HashSet<ClassDecl> visited = new HashSet<>();
            boolean added = true;
            while (added) {
                added = visited.add(classDecl);
                // Get the parent decl. If the parent does not exist, or is not a class, return true
                // in order to ignore this error as it should be detected in an other check.
                Declaration parentDecl = context.getDeclaration(classDecl.parent);
                if (parentDecl instanceof ClassDecl parentClass) classDecl = parentClass;
                else break;
            }
            return added;
        }

        @Override
        public Boolean visit(ClassDecl classDecl) {
            validateNonNull("name", classDecl.name);
            validateNonNull("doc", classDecl.doc);
            visitOptional("parent", classDecl.parent);
            if (classDecl.parent != null) {
                validateTypeRef("parent", classDecl.parent);
                if (classDecl.parent.getFinalKind() != ReferenceKind.CLASS)
                    addDiagnostic(".parent", "must inherit from a class");
                else if (!isSaneClass(classDecl))
                    addDiagnostic(".parent", "class inheritance is not sane");
            }

            validateNonNull("fields", classDecl.fields);
            if (classDecl.fields != null) {
                location.add(".fields");
                HashSet<Name> fieldNames = new HashSet<>(classDecl.fields.size());
                for (int i = 0; i < classDecl.fields.size(); i++) {
                    location.add("[" + i + "]");
                    if (!fieldNames.add(classDecl.fields.get(i).name))
                        addDiagnostic("duplicate field");
                    location.pop();
                }
                location.pop();
            }

            // Negative sized class types cannot exist
            if (classDecl.size < 0) addDiagnostic(".size", "cannot be negative");

            return Boolean.valueOf(diagnostics.isEmpty());
        }

        @Override
        public Boolean visit(EnumerationDecl enumerationDecl) {
            validateNonNull("name", enumerationDecl.name);
            validateNonNull("doc", enumerationDecl.doc);

            validateNonNull("items", enumerationDecl.items);
            if (enumerationDecl.items != null) {
                location.add(".items");
                HashSet<Integer> values = new HashSet<>(0);
                HashSet<Name> names = new HashSet<>(0);
                for (int i = 0; i < enumerationDecl.items.size(); i++) {
                    location.add("[" + i + "]");
                    if (!values.add(enumerationDecl.items.get(i).value))
                        addDiagnostic("duplicate item value in enumeration");
                    if (!names.add(enumerationDecl.items.get(i).name))
                        addDiagnostic("duplicate item name in enumeration");
                    location.pop();
                }
                location.pop();
            }

            return Boolean.valueOf(diagnostics.isEmpty());
        }

        @Override
        public Boolean visit(Role role) {
            validateNonNull("type", role.type);
            validateTypeRef("type", role.type);

            validateNonNull("kind", role.kind);
            // The ``field`` field should only be non null when ``kind`` is ``GETTER`` or ``SETTER``
            location.add(".field");
            if (role.field != null && role.kind != RoleKind.GETTER && role.kind != RoleKind.SETTER)
                addDiagnostic("must be null when role is neither `getter` nor `setter`");
            location.pop();

            return Boolean.valueOf(diagnostics.isEmpty());
        }

        @Override
        public Boolean visit(Field field) {
            validateNonNull("name", field.name);
            validateNonNull("doc", field.doc);

            validateNonNull("type", field.type);
            validateTypeRef("type", field.type);

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
            validateNonNull("type", parameter.type);
            validateTypeRef("type", parameter.type);
            validateNonNull("transfer", parameter.transfer);
            return Boolean.valueOf(diagnostics.isEmpty());
        }
    }

    private ProxyValidator() {}

    /** Run the validator on a proxy and return the list of errors found. */
    public static List<String> validate(Proxy proxy) {
        Validator validator = new Validator();
        validator.visit(proxy);
        return validator.getDiagnostics();
    }

    /**
     * Run the validator on a proxy and return a {@link ProxyContext}. If any error was found,
     * throws a {@link ProxyException}.
     */
    public static ProxyContext validateAndGetContext(Proxy proxy) throws ProxyException {
        Validator validator = new Validator();
        validator.visit(proxy);
        if (!validator.getDiagnostics().isEmpty())
            throw new ProxyException(validator.getDiagnostics());
        return validator.context;
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
