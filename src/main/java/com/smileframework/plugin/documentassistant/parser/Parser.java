package com.smileframework.plugin.documentassistant.parser;

import com.intellij.psi.PsiField;
import com.intellij.psi.PsiParameter;
import com.intellij.psi.PsiType;
import com.smileframework.plugin.documentassistant.contact.CommonContact;
import com.smileframework.plugin.documentassistant.contact.MyContact;
import com.smileframework.plugin.documentassistant.utils.MyPsiSupport;

public abstract class Parser {

    abstract public void parseDefinition();

    public PsiType getRealType(PsiType psiType, PsiField psiField) {
        PsiType fieldType = MyPsiSupport.getGenericsType(psiType, psiField);
        if (fieldType == null) {
            fieldType = psiField.getType();
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
