package com.smileframework.plugin.documentassistant.checker.impl;

import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.LangDataKeys;
import com.intellij.psi.PsiAnnotation;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiMethod;
import com.smileframework.plugin.documentassistant.checker.EventChecker;
import com.smileframework.plugin.documentassistant.utils.MyPsiSupport;

import java.util.Arrays;
import java.util.Objects;
import java.util.Optional;

public class SpringControllerChecker implements EventChecker {

    @Override
    public boolean check(AnActionEvent event) {
        PsiClass[] psiClasses = MyPsiSupport.getPsiClass(event);
        if (psiClasses == null) {
            return false;
        }
        boolean hasEntityAnnotation = false;
        for (PsiClass pClass : psiClasses) {
            for (PsiAnnotation psiAnnotation : pClass.getAnnotations()) {
                if (Objects.equals(psiAnnotation.getQualifiedName(), "org.springframework.web.bind.annotation.RestController")) {
                    hasEntityAnnotation = true;
                }
            }
        }
        if (hasEntityAnnotation) return true;
        boolean hasControllerAnnotation = false;
        boolean hasResponseBodyAnnotation = false;
        for (PsiClass pClass : psiClasses) {
            for (PsiAnnotation psiAnnotation : pClass.getAnnotations()) {
                if (Objects.equals(psiAnnotation.getQualifiedName(), "org.springframework.stereotype.Controller")) {
                    hasControllerAnnotation = true;
                }
            }
        }
        String methodName = Objects.requireNonNull(event.getData(LangDataKeys.EDITOR)).getSelectionModel().getSelectedText();
        PsiAnnotation[] annotations = new PsiAnnotation[0];
        Optional<PsiMethod> controllerMethod = Arrays.stream(psiClasses[0].getMethods()).filter(method -> method.getName().equals(methodName)).findFirst();
        if (controllerMethod.isPresent()) {
            annotations = controllerMethod.get().getAnnotations();
        }
        if (annotations.length > 0) {
            for (PsiAnnotation annotation : annotations) {
                if (Objects.equals(annotation.getQualifiedName(), "org.springframework.web.bind.annotation.ResponseBody")) {
                    hasResponseBodyAnnotation = true;
                }
            }
        }
        return hasControllerAnnotation && hasResponseBodyAnnotation;
    }

}
