package com.smileframework.plugin.documentassistant.generator;

import com.smileframework.plugin.documentassistant.definition.FieldDefinition;
import com.smileframework.plugin.documentassistant.definition.RestFulDefinition;

import java.util.List;

public class RestDocumentGenerator {

    private RestFulDefinition definition;

    public RestDocumentGenerator(RestFulDefinition definition) {
        this.definition = definition;
    }

    public String generate() {
        StringBuffer docContent = new StringBuffer();
        docContent.append(interfaceNamePart());
        docContent.append("\n");
        docContent.append(urlPart());
        docContent.append("\n");
        docContent.append(methodPart());
        docContent.append("\n");
        docContent.append("\n");
        docContent.append(requestPart());
        docContent.append("\n");
        docContent.append(requestPartJson());
        docContent.append("\n");
        docContent.append(responsePart());
        docContent.append("\n");
        docContent.append("\n");
        docContent.append(responseJsonPart());
        return docContent.toString();
    }

    public String interfaceNamePart() {
        return "# " + this.definition.getDesc() + "\n";
    }

    public String urlPart() {
        StringBuffer stringBuffer = new StringBuffer("**请求URL：** \n");
        stringBuffer.append("- `" + this.definition.getUri() + "`\n");
        return stringBuffer.toString();
    }

    public String methodPart() {
        StringBuffer stringBuffer = new StringBuffer("**请求方式：**\n");
        stringBuffer.append("- " + this.definition.getHttpMethod() + "\n");
        stringBuffer.append("- " + this.definition.getRequestBodyType().toString() + "\n");
        return stringBuffer.toString();
    }

    public String requestPart() {
        StringBuffer stringBuffer = new StringBuffer("###请求参数<业务参数>\n \n");
        List<FieldDefinition> fieldDefinitions = this.definition.getRequest();
        if (fieldDefinitions == null || fieldDefinitions.isEmpty()) {
            stringBuffer.append("无参数\n");
            return stringBuffer.toString();
        }
        stringBuffer.append("|参数名|必选|类型|说明|\n");
        stringBuffer.append("|:----    |:---|:----- |-----   |\n");

        stringBuffer.append(this.fieldDefinitionTableBody(fieldDefinitions));
        return stringBuffer.toString();
    }

    public String requestPartJson() {
        List<FieldDefinition> fieldDefinitions = this.definition.getRequest();
        if (fieldDefinitions == null || fieldDefinitions.isEmpty()) {
            return "";
        }
        StringBuffer stringBuffer = new StringBuffer("###请求参数Json格式\n \n");
        stringBuffer.append("```\n");
        stringBuffer.append(this.fieldDefinitionJson(fieldDefinitions));
        stringBuffer.append("\n```\n");
        return stringBuffer.toString();
    }

    public String responsePart() {
        StringBuffer stringBuffer = new StringBuffer("###返回参数\n \n");
        stringBuffer.append("|参数名|必选|类型|说明|\n");
        stringBuffer.append("|:----    |:---|:----- |-----   |\n");
        List<FieldDefinition> fieldDefinitions = this.definition.getResponse();
        stringBuffer.append(this.fieldDefinitionTableBody(fieldDefinitions));
        return stringBuffer.toString();
    }

    public String responseJsonPart() {
        StringBuffer stringBuffer = new StringBuffer("###返回参数Json格式\n \n");
        stringBuffer.append("```\n");
        List<FieldDefinition> fieldDefinitions = this.definition.getResponse();
        stringBuffer.append(this.fieldDefinitionJson(fieldDefinitions));
        stringBuffer.append("\n```\n");
        return stringBuffer.toString();
    }

    public String fieldDefinitionTableBody(List<FieldDefinition> fieldDefinitions) {
        StringBuffer stringBuffer = new StringBuffer();
        if (fieldDefinitions == null || fieldDefinitions.isEmpty()) {
            return stringBuffer.toString();
        }
        for (FieldDefinition definition : fieldDefinitions) {
            String layerChat = this.getLayerChat(definition.getLayer());
            stringBuffer.append("|" + layerChat + definition.getName());
            stringBuffer.append("|" + (definition.isRequire() ? "是" : "否"));
            stringBuffer.append("|" + definition.getType());
            stringBuffer.append("|" + definition.getDesc());
            stringBuffer.append("|\n");
            if (definition.getSubFieldDefinitions() != null && !definition.getSubFieldDefinitions().isEmpty()) {
                stringBuffer.append(this.fieldDefinitionTableBody(definition.getSubFieldDefinitions()));
            }
        }
        return stringBuffer.toString();
    }

    public String getLayerChat(int layer) {
        StringBuffer stringBuffer = new StringBuffer();
        for (int i = 0; i < layer; i++) {
            stringBuffer.append("--");
        }
        return stringBuffer.toString();
    }

    public String getLayerJsonChat(int layer) {
        StringBuffer stringBuffer = new StringBuffer();
        layer++;
        for (int i = 0; i < layer; i++) {
            stringBuffer.append("   ");
        }
        return stringBuffer.toString();
    }


    public String fieldDefinitionJson(List<FieldDefinition> fieldDefinitions) {
        StringBuffer stringBuffer = new StringBuffer();
        if (fieldDefinitions == null || fieldDefinitions.isEmpty()) {
            return stringBuffer.toString();
        }
        stringBuffer.append("{\n");
        int firstLayer = -1;
        for (int i = 0; i < fieldDefinitions.size(); i++) {
            FieldDefinition definition = fieldDefinitions.get(i);
            if (i == 0) {
                firstLayer = definition.getLayer();
            }
            String layerChat = this.getLayerJsonChat(definition.getLayer());
            stringBuffer.append(layerChat + "\"" + definition.getName() + "\" : ");
            if (definition.getSubFieldDefinitions() != null && !definition.getSubFieldDefinitions().isEmpty()) {
                if (definition.getType().equals("List")) {
                    stringBuffer.append("[");
                }
                if (definition.getType().equals("Map")) {
                    stringBuffer.append(" { \"MapKey\" : ");
                }
                stringBuffer.append(this.fieldDefinitionJson(definition.getSubFieldDefinitions()));
                if (definition.getType().equals("Map")) {
                    stringBuffer.append("}");
                }
                if (definition.getType().equals("List")) {
                    stringBuffer.append("]");
                }
            } else {
                if (definition.getType().equals("String") || definition.getType().equals("DateTime")) {
                    stringBuffer.append("\"" + definition.getType() + "\"");
                } else if (definition.getType().equals("Boolean")) {
                    stringBuffer.append("true");
                } else if (definition.getType().equals("Object") || definition.getType().equals("Map") || definition.getType().equals("List")) {
                    stringBuffer.append("null");
                } else {
                    stringBuffer.append("0");
                }
            }
            if (i + 1 < fieldDefinitions.size()) {
                stringBuffer.append(",");
            }
            stringBuffer.append("\n");
        }
        stringBuffer.append(this.getLayerJsonChat(firstLayer - 1) + "}");
        return stringBuffer.toString();
    }


}
