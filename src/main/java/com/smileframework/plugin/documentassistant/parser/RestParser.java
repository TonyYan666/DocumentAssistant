package com.smileframework.plugin.documentassistant.parser;

import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleUtil;
import com.intellij.psi.*;
import com.intellij.psi.search.FilenameIndex;
import com.intellij.psi.search.GlobalSearchScope;
import com.smileframework.plugin.documentassistant.contact.SpringContact;
import com.smileframework.plugin.documentassistant.definition.BodyType;
import com.smileframework.plugin.documentassistant.definition.FieldDefinition;
import com.smileframework.plugin.documentassistant.utils.MyPsiSupport;
import com.smileframework.plugin.documentassistant.utils.StringUtils;
import com.smileframework.plugin.documentassistant.definition.RestFulDefinition;
import com.smileframework.plugin.documentassistant.utils.JavaDocUtils;

import java.util.List;

public class RestParser extends Parser {

    private PsiMethod psiMethod;

    private RestFulDefinition definition;

    public RestParser(PsiMethod psiMethod) {
        this.psiMethod = psiMethod;
        this.definition = new RestFulDefinition();
    }

    public RestFulDefinition getDefinition() {
        return definition;
    }

    @Override
    public void parseDefinition() {
        RestFulDefinition definition = new RestFulDefinition();
        definition.setUri(ingestUri());
        definition.setHttpMethod(getHttpMethod());
        String desc = getMethodDesc();
        definition.setDesc(desc);
        definition.setName(psiMethod.getName());
        definition.setControllerDesc(getControllerDesc());
        BodyType bodyType = getRequestBodyType();
        definition.setRequestBodyType(bodyType);
        if (bodyType == BodyType.RequestBody) {
            definition.setRequest(getRequestBodyDefinitions());
        } else {
            definition.setRequest(getRequestParamsDefinitions());
        }
        definition.setResponse(getResponseDefinitions());
        this.definition = definition;
    }


    /**
     * 获得返回参数
     *
     * @return
     */
    private List<FieldDefinition> getResponseDefinitions() {
        ObjectParser objectParser = new ObjectParser(psiMethod.getReturnType(), psiMethod.getProject(), 0);
        objectParser.parseDefinition();
        List<FieldDefinition> responseDefinitions = objectParser.getFieldDefinitions();
        return responseDefinitions;
    }

    /**
     * RequestBody 获得请求参数
     *
     * @return
     */
    private List<FieldDefinition> getRequestBodyDefinitions() {
        PsiParameter psiParameter = getRequestBodyParam();
        if (psiParameter != null) {
            ObjectParser objectParser = new ObjectParser(psiParameter.getType(), psiMethod.getProject(), 0);
            objectParser.parseDefinition();
            List<FieldDefinition> responseDefinitions = objectParser.getFieldDefinitions();
            return responseDefinitions;
        }
        return null;
    }

    /**
     * Request Parameter 获得请求参数
     *
     * @return
     */
    private List<FieldDefinition> getRequestParamsDefinitions() {
        PsiParameter[] psiParameter = this.psiMethod.getParameterList().getParameters();
        if (psiParameter != null) {
            ParameterParser parameterParser = new ParameterParser(this.psiMethod);
            parameterParser.parseDefinition();
            List<FieldDefinition> responseDefinitions = parameterParser.getFieldDefinitions();
            return responseDefinitions;
        }
        return null;
    }


    /**
     * 获得参数请求类型
     *
     * @return
     */
    public BodyType getRequestBodyType() {
        PsiParameter[] parameters = this.psiMethod.getParameterList().getParameters();
        if (parameters != null && parameters.length > 0) {
            for (PsiParameter psiParameter : parameters) {
                PsiAnnotation annotation = MyPsiSupport.getPsiAnnotation(psiParameter, SpringContact.ANNOTATION_REQUESTBODY);
                if (annotation != null) {
                    return BodyType.RequestBody;
                }
            }
        }
        return BodyType.FormData;
    }


    /**
     * 获得参数请求类型
     *
     * @return
     */
    public PsiParameter getRequestBodyParam() {
        PsiParameter[] parameters = this.psiMethod.getParameterList().getParameters();
        if (parameters != null && parameters.length > 0) {
            for (PsiParameter psiParameter : parameters) {
                PsiAnnotation annotation = MyPsiSupport.getPsiAnnotation(psiParameter, SpringContact.ANNOTATION_REQUESTBODY);
                if (annotation != null) {
                    return psiParameter;
                }
            }
        }
        return null;
    }


    /**
     * 获得方法描述
     *
     * @return
     */
    private String getMethodDesc() {
        return JavaDocUtils.getText(psiMethod.getDocComment());
    }

    /**
     * 获得Controller 的描述
     *
     * @return
     */
    private String getControllerDesc() {
        return JavaDocUtils.getText(psiMethod.getContainingClass().getDocComment());
    }


    /***
     * 获得Http请求方法
     * @return
     */
    private String getHttpMethod() {
        String httpMethod = "Get";
        PsiAnnotation reqMapAn = MyPsiSupport.getPsiAnnotation(psiMethod, SpringContact.ANNOTATION_REQUESTMAPPING);
        if (reqMapAn != null) {
            String methodVal = MyPsiSupport.getPsiAnnotationValueByAttr(reqMapAn, "method");
            if (StringUtils.isEmpty(methodVal)) {
                httpMethod = "ALL";
            } else {
                try {
                    httpMethod = methodVal.split(".")[1];
                } catch (Exception e) {
                    httpMethod = "undefined";
                }
            }
        }

        PsiAnnotation postMapAn = MyPsiSupport.getPsiAnnotation(psiMethod, SpringContact.ANNOTATION_POSTMAPPING);
        if (postMapAn != null) {
            httpMethod = "POST";
        }
        return httpMethod;
    }


    /**
     * 获得方法级别的Uri
     *
     * @param annotation
     * @return
     */
    private String getPartOfUri(PsiAnnotation annotation) {
        String path = MyPsiSupport.getPsiAnnotationValueByAttr(annotation, "path");
        String val = MyPsiSupport.getPsiAnnotationValueByAttr(annotation, "value");
        String resultPath = path == null || path.length() <= 0 ? val : path;
        String partOfUri = resultPath != null && resultPath.length() > 0 ? resultPath : "";
        return partOfUri;
    }

    /**
     * 获得Uri
     *
     * @return
     */
    private String ingestUri() {

        Module module = ModuleUtil.findModuleForPsiElement(this.psiMethod);
        String contextPart = "";
        PsiFile[] contextFiles = FilenameIndex.getFilesByName(psiMethod.getProject(), "application.yml", GlobalSearchScope.moduleScope(module));
        if (contextFiles == null || contextFiles.length == 0) {
            contextFiles = FilenameIndex.getFilesByName(psiMethod.getProject(), "application.properties", GlobalSearchScope.moduleScope(module));
        }
        if (contextFiles != null && contextFiles.length > 0) {
            for (PsiFile psiFile : contextFiles) {
                if (psiFile.getName().contains(".yml") || psiFile.getName().contains(".yaml")) {
                    YamlParser yamlParser = new YamlParser(psiFile);
                    contextPart = yamlParser.findProperty("server", "servlet", "context-path");
                    if (StringUtils.isEmpty(contextPart)) {
                        contextPart = yamlParser.findProperty("server", "context-path");
                    }
                }else if(psiFile.getName().contains(".properties")){
                    PropertiesFileParser propertiesFileParser = new PropertiesFileParser(psiFile);
                    propertiesFileParser.loadProperties();
                    contextPart = propertiesFileParser.getProperty("server.servlet.context-path","");
                }
                if (!StringUtils.isEmpty(contextPart)) {
                    break;
                }
            }
        }

        if (!StringUtils.isEmpty(contextPart)) {
            if (contextPart.indexOf("/") != 0) {
                contextPart = "/" + contextPart;
            }
        }

        String methodUriPart = "";
        PsiAnnotation getMapAn = MyPsiSupport.getPsiAnnotation(psiMethod, SpringContact.ANNOTATION_GETMAPPING);
        if (getMapAn != null) {
            methodUriPart = getPartOfUri(getMapAn);
        }

        PsiAnnotation reqMapAn = MyPsiSupport.getPsiAnnotation(psiMethod, SpringContact.ANNOTATION_REQUESTMAPPING);
        if (reqMapAn != null) {
            methodUriPart = getPartOfUri(reqMapAn);
        }

        PsiAnnotation postMapAn = MyPsiSupport.getPsiAnnotation(psiMethod, SpringContact.ANNOTATION_POSTMAPPING);
        if (postMapAn != null) {
            methodUriPart = getPartOfUri(postMapAn);
        }


        String classUriPart = "";
        PsiAnnotation clzReqMapAn = MyPsiSupport.getPsiAnnotation(psiMethod.getContainingClass(), SpringContact.ANNOTATION_REQUESTMAPPING);
        if (clzReqMapAn != null) {
            classUriPart = getPartOfUri(clzReqMapAn);
        }
        boolean hasSeparator = false;
        boolean moreThenOneSeparator = false;
        if (!StringUtils.isEmpty(methodUriPart)) {
            hasSeparator = methodUriPart.indexOf("/") == 0;
        }
        if (!StringUtils.isEmpty(classUriPart)) {
            if (hasSeparator && classUriPart.indexOf("/") == classUriPart.length() - 1) {
                moreThenOneSeparator = true;
            } else if (!hasSeparator) {
                hasSeparator = classUriPart.indexOf("/") == classUriPart.length() - 1;
            }
        }
        String uri = "";
        if (!StringUtils.isEmpty(methodUriPart) || !StringUtils.isEmpty(classUriPart)) {
            if (moreThenOneSeparator) {
                methodUriPart = methodUriPart.substring(1, methodUriPart.length());
                uri = classUriPart + methodUriPart;
            } else if (hasSeparator) {
                uri = classUriPart + methodUriPart;
            } else {
                uri = classUriPart + "/" + methodUriPart;
            }
            if (uri.indexOf("/") != 0) {
                uri = "/" + uri;
            }
        }
        return contextPart + uri;
    }
}
