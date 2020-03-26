package com.ap.models;

import com.ap.Interceptor;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.MethodSpec;

import javax.lang.model.element.Modifier;
import javax.lang.model.element.VariableElement;
import java.util.List;

public class InfoModel {
    private String className;
    private List<? extends VariableElement> params;
    private int index = -1;
    private String packageName;
    private String fullName;

    public InfoModel(String className, List<? extends VariableElement> params, int index, String packageName, String fullName){
        this.className = className;
        this.params = params;
        this.index = index;
        this.packageName = packageName;
        this.fullName = fullName;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public List<? extends VariableElement> getParams() {
        return params;
    }

    public void setParams(List<? extends VariableElement> params) {
        this.params = params;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public MethodSpec genericMethod() {
        MethodSpec.Builder builder = MethodSpec.methodBuilder("generic_" + className)
                .addModifiers(Modifier.PRIVATE)
                .returns(Interceptor.class)
                .beginControlFlow("try")
                .addStatement("Class clazz = Class.forName($S)",fullName)
                .beginControlFlow("if ($N != null)", "clazz")
                .addStatement("return ($T)($N.$N())", Interceptor.class, "clazz","newInstance")
                .endControlFlow()
                .nextControlFlow("catch ($T | $T | $T $N)",ClassNotFoundException.class, IllegalAccessException.class, InstantiationException.class, "e")
                .addStatement("$N.$N()","e", "printStackTrace")
                .endControlFlow()
                .addStatement("return null");
        return builder.build();
    }
}
