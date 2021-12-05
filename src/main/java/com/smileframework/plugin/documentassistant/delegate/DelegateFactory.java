package com.smileframework.plugin.documentassistant.delegate;

import com.smileframework.plugin.documentassistant.ControllerDocumentGenerator;
import com.smileframework.plugin.documentassistant.delegate.impl.ControllerDocumentDelegate;
import com.smileframework.plugin.documentassistant.delegate.impl.DefaultDelegateFactory;

public class DelegateFactory {

    public static GeneratorDelegate getGeneratorDelegate(Class clazz) {
        if (clazz.equals(ControllerDocumentGenerator.class)) {
            return new ControllerDocumentDelegate();
        }
        return new DefaultDelegateFactory();
    }

}
