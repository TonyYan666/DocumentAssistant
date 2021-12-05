package org.tonyyan.plugin.documentassistant.delegate;

import org.tonyyan.plugin.documentassistant.ControllerDocumentGenerator;
import org.tonyyan.plugin.documentassistant.delegate.impl.ControllerDocumentDelegate;
import org.tonyyan.plugin.documentassistant.delegate.impl.DefaultDelegateFactory;

public class DelegateFactory {

    public static GeneratorDelegate getGeneratorDelegate(Class clazz) {
        if (clazz.equals(ControllerDocumentGenerator.class)) {
            return new ControllerDocumentDelegate();
        }
        return new DefaultDelegateFactory();
    }

}
