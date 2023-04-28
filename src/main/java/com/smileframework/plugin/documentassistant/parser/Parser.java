package com.smileframework.plugin.documentassistant.parser;

import com.intellij.psi.*;
import com.smileframework.plugin.documentassistant.utils.MyPsiSupport;
import com.smileframework.plugin.documentassistant.contact.CommonContact;
import com.smileframework.plugin.documentassistant.contact.MyContact;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public abstract class Parser {

    abstract public void parseDefinition();

    public PsiType getRealType(PsiType psiType, PsiField psiField) {
        PsiType fieldType = MyPsiSupport.getGenericsType(psiType, psiField);
        if (fieldType != null) {
            return fieldType;
        }
        fieldType = psiField.getType();
        if (fieldType instanceof PsiClassType) {
            PsiClassType classType = (PsiClassType) fieldType;
            if (classType.getParameterCount() <= 0) {
                return fieldType;
            }
            Map<PsiTypeParameter, PsiType> fieldMap = MyPsiSupport.resolveGenericsMap(classType);
            Map<PsiTypeParameter, PsiType> fieldOwnerMap = MyPsiSupport.resolveGenericsMap(psiType);
            List<PsiType> realParameterTypes = new ArrayList<>();
            for (Map.Entry<PsiTypeParameter, PsiType> fieldEntry : fieldMap.entrySet()) {
                String genericParameterName = fieldEntry.getValue().getCanonicalText();
                PsiType genericRealType = fieldEntry.getValue();
                for (Map.Entry<PsiTypeParameter, PsiType> ownerEntry : fieldOwnerMap.entrySet()) {
                    String genericOwnerName = ownerEntry.getKey().getText();
                    if (genericParameterName.equals(genericOwnerName)) {
                        genericRealType = ownerEntry.getValue();
                        break;
                    }
                }
                realParameterTypes.add(genericRealType);
            }
            fieldType = MyPsiSupport.generatePsiClassType(MyPsiSupport.getPsiClass(fieldType), realParameterTypes);
        }
        return fieldType;
    }


    public boolean getIsRequire(PsiField psiField) {
        boolean require = MyPsiSupport.getPsiAnnotation(psiField, MyContact.VALIDATOR_NOTEMPTYCHECK) != null;
        if (!require) {
            require = MyPsiSupport.getPsiAnnotation(psiField, CommonContact.CONSTRAINTS_NOTNULL) != null;
        }
        if (!require) {
            require = MyPsiSupport.getPsiAnnotation(psiField, MyContact.REABAM_VALIDATOR_NOTEMPTYCHECK) != null;
        }
        return require;
    }


    public boolean getIsRequire(PsiParameter psiParameter) {
        boolean require = MyPsiSupport.getPsiAnnotation(psiParameter, MyContact.VALIDATOR_NOTEMPTYCHECK) != null;
        if (!require) {
            require = MyPsiSupport.getPsiAnnotation(psiParameter, CommonContact.CONSTRAINTS_NOTNULL) != null;
        }
        if (!require) {
            require = MyPsiSupport.getPsiAnnotation(psiParameter, MyContact.REABAM_VALIDATOR_NOTEMPTYCHECK) != null;
        }
        return require;
    }

}
