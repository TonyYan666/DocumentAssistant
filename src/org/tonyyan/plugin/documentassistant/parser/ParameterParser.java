package org.tonyyan.plugin.documentassistant.parser;

import com.intellij.psi.*;
import org.tonyyan.plugin.documentassistant.contact.CommonContact;
import org.tonyyan.plugin.documentassistant.contact.MyContact;
import org.tonyyan.plugin.documentassistant.definition.FieldDefinition;
import org.tonyyan.plugin.documentassistant.parser.translator.TypeTranslator;
import org.tonyyan.plugin.documentassistant.utils.JavaDocUtils;
import org.tonyyan.plugin.documentassistant.utils.MyPsiSupport;

import java.util.ArrayList;
import java.util.List;

public class ParameterParser extends Parser {

    private PsiParameter[] psiParameters;
    private PsiMethod psiMethod;
    private List<FieldDefinition> fieldDefinitions = new ArrayList<>();

    public ParameterParser(PsiMethod psiMethod) {
        this.psiMethod = psiMethod;
        this.psiParameters = psiMethod.getParameterList().getParameters();
    }

    public List<FieldDefinition> getFieldDefinitions() {
        return fieldDefinitions;
    }

    @Override
    public void parseDefinition() {

        if (psiParameters == null || psiParameters.length == 0) {
            return;
        }
        PsiParameter firstParamter = this.psiParameters[0];
        PsiClass psiClass = MyPsiSupport.getPsiClass(firstParamter.getType());
        if (psiClass != null && TypeTranslator.docTypeTranslate(psiClass.getQualifiedName()).equals(TypeTranslator.TYPE_OBJ)) {
            ObjectParser objectParser = new ObjectParser(firstParamter.getType(), firstParamter.getProject(), 0);
            objectParser.parseDefinition();
            this.fieldDefinitions = objectParser.getFieldDefinitions();
            return;
        }
        doParse();
    }

    public void doParse(){
        if (psiParameters == null || psiParameters.length == 0) {
            return;
        }
        for (PsiParameter psiParameter : this.psiParameters){
            FieldDefinition definition = this.parseSingleParameterDefinition(psiParameter);
            if(definition!=null){
                this.fieldDefinitions.add(definition);
            }
        }
    }


    public FieldDefinition parseSingleParameterDefinition(PsiParameter psiParameter){
        if(psiParameter == null){
            return null;
        }
        String paramName = psiParameter.getName();
        String desc = JavaDocUtils.getParamsDesc(psiMethod.getDocComment(),paramName);
        FieldDefinition definition = new FieldDefinition();
        definition.setName(paramName);
        definition.setLayer(0);
        definition.setDesc(desc);
        PsiType fieldType = psiParameter.getType();
        PsiClass fieldClass = MyPsiSupport.getPsiClass(fieldType);
        if (fieldClass == null) {
            definition.setType(TypeTranslator.docTypeTranslate(fieldType.getCanonicalText()));
        } else {
            definition.setType(TypeTranslator.docTypeTranslate(fieldClass.getQualifiedName()));
        }
        boolean require = MyPsiSupport.getPsiAnnotation(psiParameter, MyContact.VALIDATOR_NOTEMPTYCHECK) != null;
        if (!require) {
            require = MyPsiSupport.getPsiAnnotation(psiParameter, CommonContact.CONSTRAINTS_NOTNULL) != null;
        }
        definition.setRequire(require);
        return definition;
    }
}
