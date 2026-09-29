package io.lemonjuice.flandre_bot_framework.processor;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.*;
import javax.lang.model.util.ElementFilter;
import javax.tools.FileObject;
import javax.tools.JavaFileObject;
import javax.tools.StandardLocation;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Writer;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

@SupportedAnnotationTypes("io.lemonjuice.flandre_bot_framework.event.annotation.EventSubscriber")
@SupportedSourceVersion(SourceVersion.RELEASE_25)
public class EventSubscriberProcessor extends AbstractProcessor {
    private static final String SPI_RESOURCE = "META-INF/services/io.lemonjuice.flandre_bot_framework.event.ISubscriberRegister";

    private final Set<String> registrarNames = new LinkedHashSet<>();
    private FileObject spiFile = null;

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        if (roundEnv.processingOver()) {
            return false;
        }

        TypeElement annotation = processingEnv.getElementUtils().getTypeElement("io.lemonjuice.flandre_bot_framework.event.annotation.EventSubscriber");
        Set<TypeElement> subscriberClasses = new HashSet<>();
        for(Element element : roundEnv.getElementsAnnotatedWith(annotation)) {
            if(element.getKind() != ElementKind.CLASS) {
                processingEnv.getMessager().printError("@EventSubscriber can only be applied to class", element);
                continue;
            }

            TypeElement typeElement = (TypeElement) element;
            if(!validateTypeElement(typeElement)) {
                continue;
            }

            subscriberClasses.add(typeElement);
        }

        for(TypeElement element : subscriberClasses) {
            String className = element.getQualifiedName().toString();
            String registerName = className.replace(".", "_") + "_" + Integer.toHexString(className.hashCode());
            String fullQualifiedName = "io.lemonjuice.flandre_bot_framework.generated.subscriber." + registerName;

            if(!this.registrarNames.add(fullQualifiedName)) {
                continue;
            }

            try {
                JavaFileObject fileObject = processingEnv.getFiler().createSourceFile(String.format("io.lemonjuice.flandre_bot_framework.generated.subscriber.%s", registerName));

                try (PrintWriter writer = new PrintWriter(fileObject.openWriter())) {
                    writer.println("package io.lemonjuice.flandre_bot_framework.generated.subscriber;");
                    writer.println();
                    writer.println("import io.lemonjuice.flandre_bot_framework.event.BotEventBus;");
                    writer.println("import io.lemonjuice.flandre_bot_framework.event.ISubscriberRegister;");
                    writer.println();
                    writer.println(String.format("public class %s implements ISubscriberRegister {", registerName));
                    writer.println("    @Override");
                    writer.println("    public void register() {");
                    writer.println(String.format("      BotEventBus.register(new %s());", className));
                    writer.println("    }");
                    writer.println("}");
                }

            } catch (IOException e) {
                this.registrarNames.remove(fullQualifiedName);
                processingEnv.getMessager().printError("Failed to generate event subscriber register class: " + e.getMessage(), element);
                return true;
            }
        }

        if(!this.registrarNames.isEmpty()) {
            writeSpi();
        }

        return true;
    }

    private void writeSpi() {
        try {
            if (this.spiFile == null) {
                this.spiFile = processingEnv.getFiler().createResource(StandardLocation.CLASS_OUTPUT, "", SPI_RESOURCE);
            }
            try (PrintWriter writer = new PrintWriter(spiFile.openWriter())) {
                for (String fqn : registrarNames) {
                    writer.println(fqn);
                }
            }
        } catch (IOException e) {
            processingEnv.getMessager().printError("Failed to write into SPI file: " + e.getMessage());
        }
    }

    private boolean validateTypeElement(TypeElement element) {
        if(isNonStaticNestedClass(element)) {
            processingEnv.getMessager().printError("Class with annotation @EventSubscriber must be a static nested class or a top-level class", element);
            return false;
        }

        if (!isAccessible(element)) {
            processingEnv.getMessager().printError("Class with annotation @EventSubscriber must be accessible", element);
            return false;
        }

        if(element.getModifiers().contains(Modifier.ABSTRACT)) {
            processingEnv.getMessager().printError("Class with annotation @EventSubscriber can't be abstract", element);
            return false;
        }

        if(!hasValidConstructor(element)) {
            processingEnv.getMessager().printError("Class with annotation @EventSubscriber must have a public constructor with no params", element);
            return false;
        }

        return true;
    }

    private boolean isAccessible(TypeElement element) {
        Element current = element;
        while (current.getKind().isClass() || current.getKind() == ElementKind.INTERFACE) {
            if (!current.getModifiers().contains(Modifier.PUBLIC)) {
                return false;
            }
            current = current.getEnclosingElement();
        }
        return true;
    }

    private boolean isNonStaticNestedClass(TypeElement element) {
        Element enclosing = element.getEnclosingElement();
        if (enclosing.getKind() != ElementKind.CLASS) {
            return false;
        }
        return !element.getModifiers().contains(Modifier.STATIC);
    }

    private boolean hasValidConstructor(TypeElement element) {
        List<ExecutableElement> constructors = ElementFilter.constructorsIn(element.getEnclosedElements());

        if(constructors.isEmpty()) {
            return true;
        }

        for(ExecutableElement constructor : constructors) {
            if(constructor.getParameters().isEmpty()) {
                return constructor.getModifiers().contains(Modifier.PUBLIC);
            }
        }

        return false;
    }
}
