package org.tonyyan.plugin.documentassistant.utils;

import com.intellij.lang.jvm.annotation.JvmAnnotationAttribute;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.actionSystem.LangDataKeys;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.psi.*;
import com.intellij.psi.impl.source.tree.java.PsiLiteralExpressionImpl;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.util.PsiUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * @author Tony Yan
 */
public abstract class MyPsiSupport {

    /**
     * 通过AnActionEvent 对象获得 PsiFile 对象
     *
     * @param event
     * @return
     */
    public static PsiFile getPsiFileByEvent(AnActionEvent event) {
        return event.getData(LangDataKeys.PSI_FILE);
    }

    /**
     * 通过AnActionEvent 对象获得 PsiJavaFile 对象 如果当前Event的File不是JavaFile类型则返回空
     *
     * @param event
     * @return
     */
    public static PsiJavaFile getPsiJavaFile(AnActionEvent event) {
        PsiFile file = getPsiFileByEvent(event);
        if (file instanceof PsiJavaFile) {
            return (PsiJavaFile) file;
        }
        return null;
    }

    /**
     * 通过AnActionEvent对象获得 PsiClass对象数组 如果当前Event的File不是JavaFile类型则返回空
     *
     * @param event
     * @return
     */
    public static PsiClass[] getPsiClass(AnActionEvent event) {
        PsiJavaFile javaFile = getPsiJavaFile(event);
        if (javaFile != null) {
            return javaFile.getClasses();
        }
        return null;
    }


    /**
     * 获得当前类的方法
     *
     * @param psiClass
     * @return
     */
    public static PsiMethod[] getPsiMethods(PsiClass psiClass) {
        return psiClass.getMethods();
    }


    /**
     * 获得当前类的标识某annotation的方法
     *
     * @param psiClass
     * @param annotation
     * @return
     */
    public static List<PsiMethod> getPsiMethods(PsiClass psiClass, String annotation) {

        PsiMethod[] methods = psiClass.getMethods();
        List<PsiMethod> psiMethods = new ArrayList<>();
        for (PsiMethod method : methods) {
            if (getPsiAnnotation(method, annotation) != null) {
                psiMethods.add(method);
            }
        }
        return psiMethods;
    }

    /**
     * 获得当前类的标识某annotation的方法
     *
     * @param psiClasses
     * @param annotation
     * @return
     */
    public static List<PsiMethod> getPsiMethods(List<PsiClass> psiClasses, String annotation) {

        List<PsiMethod> psiMethods = new ArrayList<>();
        for (PsiClass psiClass : psiClasses) {
            psiMethods.addAll(getPsiMethods(psiClass, annotation));
        }
        return psiMethods;
    }

    /**
     * 获得第一个拥有该annotation 全限定名称的 psiClass对象
     *
     * @param javaFile
     * @param annotation
     * @return
     */
    public static PsiClass getFirstPsiClass(PsiJavaFile javaFile, String annotation) {
        PsiClass[] psiClasses = javaFile.getClasses();
        for (PsiClass psiClass : psiClasses) {
            if (getPsiAnnotation(psiClass, annotation) != null) {
                return psiClass;
            }
        }
        return null;
    }

    /**
     * 获得所有拥有该annotation 全限定名称的 psiClass对象
     *
     * @param javaFile
     * @param annotation
     * @return
     */
    public static List<PsiClass> getPsiClasses(PsiJavaFile javaFile, String annotation) {
        PsiClass[] psiClasses = javaFile.getClasses();
        List<PsiClass> psiClassList = new ArrayList<>();
        for (PsiClass psiClass : psiClasses) {
            if (getPsiAnnotation(psiClass, annotation) != null) {
                psiClassList.add(psiClass);
            }
        }
        return psiClassList;
    }

    /**
     * 通过annotation 的全限定名称 获得PsiClass 的某个PsiAnnotation 对象
     *
     * @param psiClass
     * @param annotation
     * @return
     */
    public static PsiAnnotation getPsiAnnotation(PsiClass psiClass, String annotation) {
        PsiAnnotation[] psiAnnotations = psiClass.getAnnotations();
        for (PsiAnnotation psiAnnotation : psiAnnotations) {
            if (psiAnnotation.getQualifiedName().equals(annotation)) {
                return psiAnnotation;
            }
        }
        return null;
    }



    /**
     * 通过annotation 的全限定名称 获得psiField 的某个PsiAnnotation 对象
     *
     * @param psiField
     * @param annotation
     * @return
     */
    public static PsiAnnotation getPsiAnnotation(PsiField psiField, String annotation) {
        PsiAnnotation[] psiAnnotations = psiField.getAnnotations();
        for (PsiAnnotation psiAnnotation : psiAnnotations) {
            if (psiAnnotation.getQualifiedName().equals(annotation)) {
                return psiAnnotation;
            }
        }
        return null;
    }

    /**
     * 通过annotation 的全限定名称 获得PsiMethod 的某个PsiAnnotation 对象
     *
     * @param psiMethod
     * @param annotation
     * @return
     */
    public static PsiAnnotation getPsiAnnotation(PsiMethod psiMethod, String annotation) {
        PsiAnnotation[] psiAnnotations = psiMethod.getAnnotations();
        for (PsiAnnotation psiAnnotation : psiAnnotations) {
            if (psiAnnotation.getQualifiedName().equals(annotation)) {
                return psiAnnotation;
            }
        }
        return null;
    }

    /**
     * 通过annotation 的全限定名称 获得PsiParameter 的某个PsiAnnotation 对象
     *
     * @param psiParameter
     * @param annotation
     * @return
     */
    public static PsiAnnotation getPsiAnnotation(PsiParameter psiParameter, String annotation) {
        PsiAnnotation[] psiAnnotations = psiParameter.getAnnotations();
        for (PsiAnnotation psiAnnotation : psiAnnotations) {
            if (psiAnnotation.getQualifiedName().equals(annotation)) {
                return psiAnnotation;
            }
        }
        return null;
    }

    /**
     * 获得annotation 某属性的值
     *
     * @param psiAnnotation
     * @param attrName
     * @return
     */
    public static String getPsiAnnotationValueByAttr(PsiAnnotation psiAnnotation, String attrName) {
        List<JvmAnnotationAttribute> attributes = psiAnnotation.getAttributes();
        for (JvmAnnotationAttribute attr : attributes) {
            if (attr.getAttributeName().trim().equals(attrName.trim())) {
//                String txt = attr.getAttributeValue().getSourceElement().getText();
                String txt = attr.getAttributeValue().toString();
                if(attr instanceof PsiNameValuePair){
                    PsiNameValuePair psiNameValuePair = (PsiNameValuePair) attr;
                    txt = psiNameValuePair.getValue().getText();
                }
                if (txt.contains("\"")) {
                    txt = txt.substring(1, txt.length() - 1);
                }
                if (txt != null && txt.length() > 0) {
                    txt = txt.trim();
                }
                return txt;
            }
        }
        return null;
    }


    /**
     * 获得当前选中的文本
     *
     * @param anActionEvent
     * @return
     */
    public static String getSelectedText(AnActionEvent anActionEvent) {
        final Editor editor = anActionEvent.getData(CommonDataKeys.EDITOR);
        String selectedText = editor.getSelectionModel().getSelectedText();
        return selectedText;
    }

    /**
     * 通过方法名获得psiMethod对象
     *
     * @param psiClass
     * @param methodName
     * @return
     */
    public static PsiMethod findPsiMethod(PsiClass psiClass, String methodName) {
        PsiMethod[] methods = psiClass.getAllMethods();
        for (PsiMethod psiMethod : methods) {
            if (psiMethod.getName().equals(methodName)) {
                return psiMethod;
            }
        }
        return null;
    }

    /**
     * 通过类名获得 相应的PsiClass对象
     *
     * @param qualifiedName
     * @param project
     * @return
     */
    public static PsiClass getPsiClass(String qualifiedName, Project project) {
        PsiClass psiClass = JavaPsiFacade.getInstance(project)
                .findClass(qualifiedName, GlobalSearchScope.allScope(project));
        return psiClass;
    }


    /**
     * 通过PsiType对象获得 相应的PsiClass对象
     *
     * @param psiType
     * @return
     */
    public static PsiClass getPsiClass(PsiType psiType) {
        PsiClass psiClass = PsiUtil.resolveClassInClassTypeOnly(psiType);
        return psiClass;
    }

    /**
     * 解析泛型对应表
     *
     * @return
     */
    public static Map<PsiTypeParameter, PsiType> resolveGenericsMap(PsiType psiType) {
        PsiClassType psiClassType = (PsiClassType) psiType;
        Map<PsiTypeParameter, PsiType> map = psiClassType.resolveGenerics().getSubstitutor().getSubstitutionMap();
        return map;
    }

    /**
     * 获得泛型PsiType
     *
     * @param psiType
     * @param index
     * @return
     */
    public static PsiType getGenericsType(PsiType psiType, Integer index) {
        Map<PsiTypeParameter, PsiType> map = resolveGenericsMap(psiType);
        PsiClassType psiClassType = (PsiClassType) psiType;
        if(psiClassType.resolve().getTypeParameters() == null || psiClassType.resolve().getTypeParameters().length - 1 < index){
            return null;
        }
        PsiType paramType = map.get(psiClassType.resolve().getTypeParameters()[index]);
        return paramType;
    }

    /**
     * 获得字段的泛型信息
     *
     * @param psiType
     * @param psiField
     * @return
     */
    public static PsiType getGenericsType(PsiType psiType, PsiField psiField) {
        String typeName = psiField.getType().getCanonicalText();
        Map<PsiTypeParameter, PsiType> map = resolveGenericsMap(psiType);
        for (Map.Entry<PsiTypeParameter, PsiType> entry : map.entrySet()) {
            if (entry.getKey().getText().equals(typeName)) {
                return entry.getValue();
            }
        }
        return null;
    }

    /**
     * 获得字段的泛型信息
     *
     * @param psiType
     * @param psiParameter
     * @return
     */
    public static PsiType getGenericsType(PsiType psiType, PsiParameter psiParameter) {
        String typeName = psiParameter.getType().getCanonicalText();
        Map<PsiTypeParameter, PsiType> map = resolveGenericsMap(psiType);
        for (Map.Entry<PsiTypeParameter, PsiType> entry : map.entrySet()) {
            if (entry.getKey().getText().equals(typeName)) {
                return entry.getValue();
            }
        }
        return null;
    }

    /**
     * 通过泛型名称获得泛型PsiType
     * @param psiType
     * @param genericsName
     * @return
     */
    public static PsiType getGenericsType(PsiType psiType, String genericsName) {
        Map<PsiTypeParameter, PsiType> map = resolveGenericsMap(psiType);
        for (Map.Entry<PsiTypeParameter, PsiType> entry : map.entrySet()) {
            if (entry.getKey().getText().equals(genericsName)) {
                return entry.getValue();
            }
        }
        return null;
    }

    /**
     * 获得泛型数量
     *
     * @param psiType
     * @return
     */
    public static Integer getCenericsTypeCount(PsiType psiType) {
        PsiClassType psiClassType = (PsiClassType) psiType;
        if (psiClassType.resolve().getTypeParameters() == null) {
            return 0;
        }
        return psiClassType.resolve().getTypeParameters().length;
    }


}
