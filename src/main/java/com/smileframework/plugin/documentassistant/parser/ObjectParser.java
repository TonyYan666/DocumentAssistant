package com.smileframework.plugin.documentassistant.parser;

import com.intellij.openapi.project.Project;
import com.intellij.psi.*;
import com.intellij.psi.util.PsiUtil;
import com.smileframework.plugin.documentassistant.contact.CommonContact;
import com.smileframework.plugin.documentassistant.definition.FieldDefinition;
import com.smileframework.plugin.documentassistant.parser.translator.TypeTranslator;
import com.smileframework.plugin.documentassistant.utils.Convertor;
import com.smileframework.plugin.documentassistant.utils.JavaDocUtils;
import com.smileframework.plugin.documentassistant.utils.MyPsiSupport;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class ObjectParser extends Parser {

    private PsiClass psiClass;
    private Integer layer;
    private PsiType psiType;
    private Project project;
    private List<FieldDefinition> fieldDefinitions = new ArrayList<>();
    private Set<String> parentClzSet = new HashSet<>();


    public ObjectParser(PsiType psiType, Project project, Integer layer) {
        this.psiType = psiType;
        this.project = project;
        this.psiClass = MyPsiSupport.getPsiClass(psiType);
        this.layer = layer;
    }

    /**
     * 添加解析器的父引用类列表（防止循环递归）
     *
     * @param parentClzSet
     */
    public void addParentClz(Set<String> parentClzSet) {
        if (CollectionUtils.isEmpty(parentClzSet)) {
            return;
        }
        this.parentClzSet.addAll(parentClzSet);
    }


    @Override
    public void parseDefinition() {
        String type = null;
        if (psiClass == null) {
            type = TypeTranslator.docTypeTranslate(this.psiType.getCanonicalText());
        } else {
            type = TypeTranslator.docTypeTranslate(this.psiClass.getQualifiedName());
        }
        List<PsiField> psiFieldList = new ArrayList<>();
        if (TypeTranslator.TYPE_LIST.equals(type)) {
            PsiType genericsType = MyPsiSupport.getGenericsType(psiType, 0);
            if (genericsType != null) {
                PsiClass genericsClass = MyPsiSupport.getPsiClass(genericsType);
                psiFieldList = this.getAvailablePsiField(genericsClass, genericsClass.getAllFields());
            }
        } else if (TypeTranslator.TYPE_MAP.equals(type)) {
            PsiType genericsType = MyPsiSupport.getGenericsType(psiType, 1);
            if (genericsType != null) {
                PsiClass genericsClass = MyPsiSupport.getPsiClass(genericsType);
                psiFieldList = this.getAvailablePsiField(genericsClass, genericsClass.getAllFields());
            }
        } else {
            psiFieldList = this.getAvailablePsiField(this.psiClass, psiClass.getAllFields());
        }

        if (psiFieldList.isEmpty()) {
            return;
        }
        doParse(psiFieldList);
    }

    /**
     * 提前解析后的内容
     *
     * @return
     */
    public List<FieldDefinition> getFieldDefinitions() {
        return this.fieldDefinitions;
    }

    /**
     * 将没有Getter方法的字段过滤
     *
     * @return
     */
    public List<PsiField> getAvailablePsiField(PsiClass psiClass, PsiField[] psiFields) {
        List<PsiField> psiFieldList = new ArrayList<>();
        for (PsiField psiField : psiFields) {
            if (MyPsiSupport.getPsiAnnotation(psiField, CommonContact.CONSTRAINTS_JSONIGNORE) != null) {
                continue;
            }
            if (MyPsiSupport.findPsiMethod(psiClass, Convertor.getFieldGetterName(psiField.getName())) != null) {
                psiFieldList.add(psiField);
            }
            if (MyPsiSupport.findPsiMethod(psiClass, Convertor.getFieldBoolGetterName(psiField.getName())) != null) {
                psiFieldList.add(psiField);
            }
        }
        return psiFieldList;
    }

    /**
     * 解析
     *
     * @param psiFields
     */
    public void doParse(List<PsiField> psiFields) {
        for (PsiField psiField : psiFields) {
            FieldDefinition definition = parseSingleFieldDefinition(psiField);
            this.fieldDefinitions.add(definition);
        }
    }


    /**
     * 检查是否存在无限递归
     *
     * @param fieldClass
     * @return
     */
    private boolean checkInfiniteRecursion(PsiClass fieldClass) {
        if (fieldClass == null) {
            return false;
        }
        return this.parentClzSet.contains(fieldClass.getQualifiedName());
    }


    /**
     * 递归解析子字段
     *
     * @param definition
     * @param fieldType
     * @param psiField
     * @param fieldClass
     */
    private void parseSubFieldDefinitions(FieldDefinition definition, PsiType fieldType, PsiField psiField, PsiClass fieldClass) {
        Set<String> fieldParentClzSet = new HashSet<>();
        fieldParentClzSet.addAll(this.parentClzSet);
        if (fieldClass != null) {
            fieldParentClzSet.add(fieldClass.getQualifiedName());
        }
        if (definition.getType().equals(TypeTranslator.TYPE_OBJ)) {
            PsiClass psiClass = MyPsiSupport.getPsiClass(fieldType);
            if (psiClass != null) {
                ObjectParser objectParser = new ObjectParser(fieldType, this.project, layer + 1);
                objectParser.addParentClz(fieldParentClzSet);
                objectParser.parseDefinition();
                definition.setSubFieldDefinitions(objectParser.getFieldDefinitions());
            }
        } else if (definition.getType().equals(TypeTranslator.TYPE_LIST)) {
            PsiType genericsType = PsiUtil.extractIterableTypeParameter(psiField.getType(), true);
            if (genericsType == null) {
                genericsType = PsiUtil.extractIterableTypeParameter(fieldType, true);
            }
            PsiType listGenericsType = MyPsiSupport.getGenericsType(this.psiType, genericsType.getCanonicalText());
            if (listGenericsType != null) {
                genericsType = listGenericsType;
            }
            psiClass = MyPsiSupport.getPsiClass(genericsType);
            if (psiClass != null) {
                ObjectParser objectParser = new ObjectParser(genericsType, this.project, layer + 1);
                objectParser.addParentClz(fieldParentClzSet);
                objectParser.parseDefinition();
                definition.setSubFieldDefinitions(objectParser.getFieldDefinitions());
            }
        } else if (definition.getType().equals(TypeTranslator.TYPE_MAP)) {
            PsiType valueGenericsType = MyPsiSupport.getGenericsType(psiField.getType(), 1);
            psiClass = MyPsiSupport.getPsiClass(valueGenericsType);
            if (psiClass != null) {
                ObjectParser objectParser = new ObjectParser(valueGenericsType, this.project, layer + 1);
                objectParser.addParentClz(fieldParentClzSet);
                objectParser.parseDefinition();
                definition.setSubFieldDefinitions(objectParser.getFieldDefinitions());
            }
        }
    }


    /**
     * 单个字段递归解析
     *
     * @param psiField
     * @return
     */
    public FieldDefinition parseSingleFieldDefinition(PsiField psiField) {
        FieldDefinition definition = new FieldDefinition();
        String dec = JavaDocUtils.getText(psiField.getDocComment());
        String name = psiField.getName();

        boolean require = this.getIsRequire(psiField);

        definition.setLayer(layer);
        definition.setName(name);
        definition.setDesc(dec);
        definition.setRequire(require);
        PsiType fieldType = getRealType(this.psiType, psiField);
        PsiClass fieldClass = MyPsiSupport.getPsiClass(fieldType);
        if (fieldClass == null) {
            definition.setType(TypeTranslator.docTypeTranslate(fieldType.getCanonicalText()));
        } else {
            definition.setType(TypeTranslator.docTypeTranslate(fieldClass.getQualifiedName()));
        }
        if (this.checkInfiniteRecursion(fieldClass)) {
            return definition;
        }
        this.parseSubFieldDefinitions(definition, fieldType, psiField, fieldClass);
        return definition;
    }


}
