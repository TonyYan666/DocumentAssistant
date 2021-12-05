package com.smileframework.plugin.documentassistant.definition;

import java.util.List;

public class RestFulDefinition {

    private String controllerDesc;

    private String name;

    private String desc;

    private String httpMethod;

    private String uri;

    private BodyType requestBodyType;

    private List<FieldDefinition> request;

    private List<FieldDefinition> response;

    public String getControllerDesc() {
        return controllerDesc;
    }

    public void setControllerDesc(String controllerDesc) {
        this.controllerDesc = controllerDesc;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getHttpMethod() {
        return httpMethod;
    }

    public void setHttpMethod(String httpMethod) {
        this.httpMethod = httpMethod;
    }

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    public BodyType getRequestBodyType() {
        return requestBodyType;
    }

    public void setRequestBodyType(BodyType requestBodyType) {
        this.requestBodyType = requestBodyType;
    }

    public List<FieldDefinition> getRequest() {
        return request;
    }

    public void setRequest(List<FieldDefinition> request) {
        this.request = request;
    }

    public List<FieldDefinition> getResponse() {
        return response;
    }

    public void setResponse(List<FieldDefinition> response) {
        this.response = response;
    }
}
