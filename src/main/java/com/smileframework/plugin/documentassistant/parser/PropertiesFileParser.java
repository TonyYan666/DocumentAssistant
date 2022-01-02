package com.smileframework.plugin.documentassistant.parser;

import com.intellij.psi.PsiFile;
import com.smileframework.plugin.documentassistant.utils.StringUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

public class PropertiesFileParser {

    private PsiFile psiFile;

    private Map<String, String> properties = new HashMap<>();

    public PropertiesFileParser(PsiFile psiFile) {
        this.psiFile = psiFile;
    }

    public void loadProperties() {
        InputStream inputStream = null;
        try {
            inputStream = psiFile.getVirtualFile().getInputStream();
        } catch (IOException e) {
            return;
        }
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
        bufferedReader.lines().forEach((line) -> {
            line = line.trim();
            if (StringUtils.isEmpty(line)) {
                return;
            }
            if (line.indexOf("=") < 0) {
                return;
            }
            if (line.indexOf("#") == 0) {
                return;
            }
            String[] property = line.split("=");
            properties.put(property[0], property[1]);
        });
    }

    public String getProperty(String propertyName) {
        return this.properties.get(propertyName);
    }

    public String getProperty(String propertyName, String defaultVal) {
        String val = this.properties.get(propertyName);
        return val == null ? defaultVal : val;
    }
}
