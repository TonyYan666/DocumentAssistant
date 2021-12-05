package com.smileframework.plugin.documentassistant.checker.impl;

import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.PlatformDataKeys;
import com.intellij.openapi.editor.Editor;
import com.smileframework.plugin.documentassistant.checker.EventChecker;

public class EditorAvailableChecker implements EventChecker {

    @Override
    public boolean check(AnActionEvent event) {
        Editor editor = event.getData(PlatformDataKeys.EDITOR);

        //如果没有打开编辑框就不显示
        if (editor == null) {
            return false;
        }
        return true;
    }
}
