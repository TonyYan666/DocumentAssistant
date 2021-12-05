package com.smileframework.plugin.documentassistant.checker;

import com.intellij.openapi.actionSystem.AnActionEvent;

public interface EventChecker {

    boolean check(AnActionEvent event);

}
