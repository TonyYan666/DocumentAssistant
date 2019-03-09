package org.tonyyan.plugin.documentassistant.generator;

import org.tonyyan.plugin.documentassistant.definition.FieldDefinition;
import org.tonyyan.plugin.documentassistant.definition.RestFulDefinition;

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
        docContent.append(responsePart());
        return docContent.toString();
    }

    public String interfaceNamePart() {
        return "# " + this.definition.getDesc() + "\n";
    }

    public String urlPart() {
        StringBuffer stringBuffer = new StringBuffer("**请求URL：** \n");
        stringBuffer.append("- `" + this.definition.getUri() + " `\n");
        return stringBuffer.toString();
    }

    public String methodPart() {
        StringBuffer stringBuffer = new StringBuffer("**请求方式：**\n");
        stringBuffer.append("- " + this.definition.getHttpMethod()+"\n");
        stringBuffer.append("- " + this.definition.getRequestBodyType().toString()+"\n");
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

    public String responsePart() {
        StringBuffer stringBuffer = new StringBuffer("###返回参数\n \n");
        stringBuffer.append("|参数名|必选|类型|说明|\n");
        stringBuffer.append("|:----    |:---|:----- |-----   |\n");
        List<FieldDefinition> fieldDefinitions = this.definition.getResponse();
        stringBuffer.append(this.fieldDefinitionTableBody(fieldDefinitions));
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


}
