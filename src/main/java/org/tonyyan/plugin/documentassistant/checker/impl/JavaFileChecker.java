package org.tonyyan.plugin.documentassistant.checker.impl;

import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.psi.PsiJavaFile;
import org.tonyyan.plugin.documentassistant.checker.EventChecker;
import org.tonyyan.plugin.documentassistant.utils.MyPsiSupport;

public class JavaFileChecker implements EventChecker {
    @Override
    public boolean check(AnActionEvent event) {
        //不是JAVA类型不显示
        PsiJavaFile javaFile = MyPsiSupport.getPsiJavaFile(event);
        if (javaFile == null) {
            return false;
        }
        return true;
    }
}
