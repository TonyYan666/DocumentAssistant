package com.smileframework.plugin.documentassistant.delegate.impl;

import com.smileframework.plugin.documentassistant.checker.impl.JavaFileChecker;
import com.smileframework.plugin.documentassistant.delegate.GeneratorDelegate;
import com.smileframework.plugin.documentassistant.checker.impl.EditorAvailableChecker;
import com.smileframework.plugin.documentassistant.checker.impl.SpringControllerChecker;

public class ControllerDocumentDelegate extends GeneratorDelegate {

    public ControllerDocumentDelegate(){
        this.addChecker(new EditorAvailableChecker());
        this.addChecker(new JavaFileChecker());
        this.addChecker(new SpringControllerChecker());
    }

}
