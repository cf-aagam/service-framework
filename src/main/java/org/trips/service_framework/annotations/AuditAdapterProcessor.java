package org.trips.service_framework.annotations;

import com.google.auto.service.AutoService;
import com.squareup.javapoet.*;
import lombok.extern.slf4j.Slf4j;

import javax.annotation.processing.*;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.Elements;
import javax.tools.Diagnostic;
import java.io.Writer;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author anomitra on 26/08/24
 */
@SupportedAnnotationTypes("org.trips.service_framework.annotations.AuditAdapter")
@SupportedSourceVersion(SourceVersion.RELEASE_11)
@AutoService(Processor.class)
@Slf4j
public class AuditAdapterProcessor extends AbstractProcessor {
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        Elements elementUtils = processingEnv.getElementUtils();
        for (Element element : roundEnv.getElementsAnnotatedWith(AuditAdapter.class)) {
            String className = element.getSimpleName().toString();
            String packageName = elementUtils.getPackageOf(element).getQualifiedName().toString();
            String adapterClassName = className + "Adapter";

            FieldSpec gsonField = FieldSpec.builder(ClassName.get("com.google.gson", "Gson"), "gson")
                    .addModifiers(Modifier.PRIVATE, Modifier.FINAL)
                    .build();

            MethodSpec constructor = MethodSpec.constructorBuilder()
                    .addModifiers(Modifier.PUBLIC)
                    .addStatement("this.$N = new $T()", "gson", ClassName.get("com.google.gson", "Gson"))
                    .build();

            // Generate methods
            MethodSpec fromJson = MethodSpec.methodBuilder("fromJson")
                    .addAnnotation(Override.class)
                    .addModifiers(Modifier.PUBLIC)
                    .returns(ClassName.get(packageName, className))
                    .addParameter(ClassName.get("com.google.gson", "JsonElement"), "json")
                    .addParameter(ClassName.get("com.google.gson", "JsonDeserializationContext"), "jsonDeserializationContext")
                    .addStatement("return gson.fromJson(json, $T.class)", ClassName.get(packageName, className))
                    .build();

            MethodSpec toJson = MethodSpec.methodBuilder("toJson")
                    .addAnnotation(Override.class)
                    .addModifiers(Modifier.PUBLIC)
                    .returns(ClassName.get("com.google.gson", "JsonElement"))
                    .addParameter(ClassName.get(packageName, className), "sourceValue")
                    .addParameter(ClassName.get("com.google.gson", "JsonSerializationContext"), "jsonSerializationContext")
                    .addStatement("return gson.toJsonTree(sourceValue, $T.class)", ClassName.get(packageName, className))
                    .build();

            MethodSpec getValueTypes = MethodSpec.methodBuilder("getValueTypes")
                    .addAnnotation(Override.class)
                    .addModifiers(Modifier.PUBLIC)
                    .returns(ParameterizedTypeName.get(ClassName.get(List.class), ClassName.get(Class.class)))
                    .addStatement("return List.of($T.class)", ClassName.get(packageName, className))
                    .build();

            // Generate class
            TypeSpec adapterClass = TypeSpec.classBuilder(adapterClassName)
                    .addModifiers(Modifier.PUBLIC)
                    .addField(gsonField)
                    .addSuperinterface(ParameterizedTypeName.get(
                            ClassName.get("org.javers.core.json", "JsonTypeAdapter"),
                            ClassName.get(packageName, className)))
                    .addAnnotation(ClassName.get("org.springframework.stereotype", "Component"))
                    .addMethod(constructor)
                    .addMethod(fromJson)
                    .addMethod(toJson)
                    .addMethod(getValueTypes)
                    .build();

            String destination = getAuditClassPath(packageName);

            // Write the generated class to a file
            try(Writer sourceFileWriter = processingEnv
                    .getFiler()
                    .createSourceFile(String.format("%s.%s", destination, adapterClassName))
                    .openWriter()
            ) {
                log.debug("Destination: {}", destination);
                JavaFile javaFile = JavaFile.builder(destination, adapterClass)
                        .build();
                javaFile.writeTo(sourceFileWriter);
            } catch (Exception e) {
                log.error("writing error {}", e.getMessage());
                processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR, "Failed to generate class: " + e.getMessage());
            }
        }
        return true;
    }

    private String getAuditClassPath(String packageName) {
        String AUDIT_PACKAGE = ".audit.adapters";
        String[] parts = packageName.split("\\.");
        String basePackageName = Arrays.stream(parts)
                .limit(3)
                .collect(Collectors.joining("."));
        return basePackageName + AUDIT_PACKAGE;

    }
}
