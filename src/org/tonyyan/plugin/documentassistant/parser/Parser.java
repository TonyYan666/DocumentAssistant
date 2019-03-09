package org.tonyyan.plugin.documentassistant.parser;

import com.intellij.psi.PsiField;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiType;
import com.intellij.psi.search.FilenameIndex;
import com.intellij.psi.search.GlobalSearchScope;
import org.tonyyan.plugin.documentassistant.utils.MyPsiSupport;

public abstract class Parser {

    abstract public void parseDefinition();

    public PsiType getRealType(PsiType psiType, PsiField psiField){
        PsiType fieldType = MyPsiSupport.getGenericsType(psiType, psiField);
        if (fieldType == null) {
            fieldType = psiField.getType();
        }
        return fieldType;
    }




}
