package org.tonyyan.plugin.documentassistant.delegate.impl;

import org.tonyyan.plugin.documentassistant.checker.impl.EditorAvailableChecker;
import org.tonyyan.plugin.documentassistant.checker.impl.JavaFileChecker;
import org.tonyyan.plugin.documentassistant.checker.impl.SpringControllerChecker;
import org.tonyyan.plugin.documentassistant.delegate.GeneratorDelegate;

public class ControllerDocumentDelegate extends GeneratorDelegate {

    public ControllerDocumentDelegate(){
        this.addChecker(new EditorAvailableChecker());
        this.addChecker(new JavaFileChecker());
        this.addChecker(new SpringControllerChecker());
    }

}
