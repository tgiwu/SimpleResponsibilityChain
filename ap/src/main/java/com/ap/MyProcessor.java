package com.ap;

import com.ap.exceptions.ClassAbstractException;
import com.ap.exceptions.InterfaceNotImplementException;
import com.ap.models.InfoModel;
import com.google.auto.service.AutoService;
import com.ap.annotation.BackupInterceptor;
import com.squareup.javapoet.*;

import javax.annotation.processing.*;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.*;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Elements;
import javax.tools.Diagnostic;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

@SupportedAnnotationTypes("com.ap.annotation.BackupInterceptor")
@SupportedSourceVersion(SourceVersion.RELEASE_8)
@AutoService(Processor.class)
public class MyProcessor extends AbstractProcessor {


    //    private String LOG_PATH = "/Users/tgiwu/StudioProjects/oc/process_log.csv";
    private Elements elementsUtils;
    //    private int count = 0;
    private Map<Integer, InfoModel> infos = new HashMap<>();

    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);
        elementsUtils = processingEnv.getElementUtils();
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {

//        processingEnv.getMessager().printMessage(Diagnostic.Kind.NOTE, "process !!!!! " + count++);
        Set<? extends Element> elements = roundEnv.getElementsAnnotatedWith(BackupInterceptor.class);

        String fqClassName, className, packageName = "";
        for (Element element : elements) {

//            processingEnv.getMessager().printMessage(Diagnostic.Kind.NOTE, "ele = " + element);

            if (element.getKind() == ElementKind.CLASS) {
                TypeElement classElement = (TypeElement) element;
                fqClassName = classElement.getQualifiedName().toString();
                processingEnv.getMessager().printMessage(Diagnostic.Kind.NOTE,"fullName = " + fqClassName);

                if (classElement.getModifiers().contains(Modifier.ABSTRACT)) {
                    throw new ClassAbstractException(fqClassName);
                }

                if (!checkIfImplementsInterface(classElement)) {
                    throw new InterfaceNotImplementException(fqClassName);
                }

                PackageElement packageElement = elementsUtils.getPackageOf(classElement);
                packageName = packageElement.getQualifiedName().toString();
                className = getClassName(classElement, packageName);

                int index = element.getAnnotation(BackupInterceptor.class).index();

//                processingEnv.getMessager().printMessage(Diagnostic.Kind.NOTE,"packageName = " + packageName);
//                processingEnv.getMessager().printMessage(Diagnostic.Kind.NOTE,"className = " + className);

                infos.put(index, new InfoModel(className, null, index, packageName, fqClassName));

            }
        }

        if (!infos.isEmpty()) {
//            processingEnv.getMessager().printMessage(Diagnostic.Kind.NOTE,"start build");
            try {
                JavaFile javaFile = JavaFile.builder("com.ap", genericFactory(infos, "com.ap")).build();
//                processingEnv.getMessager().printMessage(Diagnostic.Kind.NOTE, "start write");
                javaFile.writeTo(processingEnv.getFiler());
            } catch (IOException e) {
                e.printStackTrace();
            }

            infos.clear();
        }

//        processingEnv.getMessager().printMessage(Diagnostic.Kind.NOTE,"!!!!!!!!!!!!!!!!!!!!!!!!!!");
        return true;
    }

    private boolean checkIfImplementsInterface(TypeElement element) {
        List<? extends TypeMirror> impls = element.getInterfaces();
        if (null != impls && !impls.isEmpty()) {
            for (TypeMirror i : impls) {
                if (Interceptor.class.getCanonicalName().equals(i.toString())) {
                    return true;
                }
            }
        }
        TypeMirror superClass = element.getSuperclass();
        if (null != superClass) return checkIfImplementsInterface(superClass, Interceptor.class);
        return false;
    }

    private boolean checkIfImplementsInterface(TypeMirror clazz, Class inter) {
        try {
            List<? extends TypeMirror> impls = ((TypeElement) ((DeclaredType) clazz).asElement()).getInterfaces();
            if (null != impls && !impls.isEmpty()) {
                for (TypeMirror i : impls) {
                    if (inter.getCanonicalName().equals(i.toString())) {
                        return true;
                    }
                }
            }
            TypeMirror superClass = ((TypeElement) ((DeclaredType) clazz).asElement()).getSuperclass();
            if (null != superClass) return checkIfImplementsInterface(superClass, inter);
        } catch (Exception e) {
            processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR, "exception occurred when check impl " + e.getMessage());
        }
        return false;

    }

//    private void writeLog(String str) {
//        FileWriter fileWriter = null;
//        try {
//            fileWriter = new FileWriter(new File(LOG_PATH), true);
//            fileWriter.write(str + "\n");
//            fileWriter.flush();
//        } catch (IOException e) {
//            e.printStackTrace();
//        } finally {
//            if (null != fileWriter) {
//                try {
//                    fileWriter.close();
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//            }
//        }
//    }

    private static String getClassName(TypeElement type, String packageName) {
        int packageLen = packageName.length() + 1;
        return type.getQualifiedName().toString().substring(packageLen).replace('.', '$');
    }

    private TypeSpec genericFactory(Map<Integer, InfoModel> infos, String packageName) {
        TypeSpec.Builder builder = TypeSpec.classBuilder("InterceptorsFactory")
                .addModifiers(Modifier.PUBLIC)
//                .addMethod(genericInitMethod(packageName))
                .addSuperinterface(IInterceptorFactory.class)
//                .addField(genericFieldSpec(packageName))
                .addMethod(genericFactoryAPI(infos));
        for (Integer className : infos.keySet()) {
            InfoModel infoModel = infos.get(className);
            builder.addMethod(infoModel.genericMethod());
        }
        return builder.build();
    }

    private FieldSpec genericFieldSpec(String packageName) {
        FieldSpec.Builder builder = FieldSpec.builder(ClassName.get(packageName, "InterceptorsFactory"), "instance")
                .addModifiers(Modifier.STATIC, Modifier.PRIVATE);
        return builder.build();
    }

    private MethodSpec genericFactoryAPI(Map<Integer, InfoModel> infos) {
        Set<Integer> indexes = new TreeSet<>((o1, o2) -> o1 - o2);
        indexes.addAll(infos.keySet());
        MethodSpec.Builder builder = MethodSpec.methodBuilder("getInterceptors")
                .addModifiers(Modifier.PUBLIC)
                .addAnnotation(Override.class)
                .addStatement("List<$T> $N = new $T()", Interceptor.class, "interceptors", ArrayList.class)
                .returns(ParameterizedTypeName.get(ClassName.get(List.class), ClassName.get(Interceptor.class)));
        for (Integer index: indexes) {
            InfoModel infoModel = infos.get(index);
            builder.addStatement("interceptors.add($N())", "generic_" + infoModel.getClassName());
        }
        builder.addStatement("return interceptors");

        return builder.build();
    }

    private MethodSpec genericInitMethod(String packageName) {
        MethodSpec.Builder builder = MethodSpec.methodBuilder("getInstance")
                .addModifiers(Modifier.STATIC, Modifier.PUBLIC)
                .returns(ClassName.get(packageName, "InterceptorsFactory"))
                .beginControlFlow("if (null == $N)", "instance")
                .addStatement("$N = new $N()", "instance", "InterceptorsFactory")
                .endControlFlow()
                .addStatement("return $N", "instance");
        return builder.build();
    }
}
