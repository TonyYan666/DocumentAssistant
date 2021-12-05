package com.smileframework.plugin.documentassistant.parser;

import com.intellij.psi.PsiFile;
import com.smileframework.plugin.documentassistant.utils.StringUtils;
import org.yaml.snakeyaml.Yaml;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;

public class YamlParser extends Parser {

    private PsiFile psiFile;
    private Yaml yaml;
    private List<Object> data;

    public YamlParser(PsiFile psiFile) {
        this.psiFile = psiFile;
        this.yaml = new Yaml();
        this.loadData();
    }

    public void loadData() {
        InputStream inputStream = null;
        try {
            inputStream = psiFile.getVirtualFile().getInputStream();
            this.data = new ArrayList<>();
            yaml.loadAll(inputStream).forEach(item->{
                data.add(item);
            });

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }


    @Override
    public void parseDefinition() {

    }

    public String findProperty(String ... keys) {
        List<String> keyPathList = Arrays.asList(keys);
        for (Object target : this.data) {
            Map<String, Object> properties = (Map<String, Object>) target;
            String result = this.findPropertyByMap(properties, keyPathList, 0);
            if (!StringUtils.isEmpty(result)) {
                return result;
            }

        }
        return "";
    }

    public String findPropertyByMap(Map<String, Object> properties, List<String> keys, int index) {
        if (index >= keys.size()) {
            return null;
        }
        String targetKey = keys.get(index);
        for (String key : properties.keySet()) {
            if (key.equals(targetKey)) {
                if (keys.size() - 1 == index) {
                    return properties.get(key).toString();
                } else {
                    if (properties.get(key) instanceof Map) {
                        Map<String, Object> subProperties = (Map<String, Object>) properties.get(key);
                        return findPropertyByMap(subProperties, keys, index + 1);
                    }
                }
            }
        }
        return null;
    }
}
