package com.smileframework.plugin.documentassistant.utils;

import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.javadoc.PsiDocComment;
import com.intellij.psi.javadoc.PsiDocTag;
import com.intellij.psi.javadoc.PsiDocToken;

public class JavaDocUtils {

    /**
     * 文档注释整理
     *
     * @param docStr
     * @return
     */
    public static String clearDesc(String docStr) {
        if (docStr != null && docStr.length() > 0) {
            docStr = docStr.replace("/**", "");
            docStr = docStr.replace("*/", "");
            docStr = docStr.replace("*", "");
            docStr = docStr.replace("\n", "");
            docStr = docStr.trim();
        }
        return docStr;
    }

    public static String getText(PsiDocComment comment) {
        if (comment == null) {
            return "";
        }
        PsiElement[] elements = comment.getDescriptionElements();
        StringBuffer stringBuffer = new StringBuffer();
        boolean hasLastEnter = false;
        for (PsiElement psiElement : elements) {
            if (psiElement instanceof PsiDocToken) {
                PsiDocToken psiDocToken = (PsiDocToken) psiElement;
                stringBuffer.append(psiDocToken.getText() + "\n");
                hasLastEnter = true;
            }
        }
        if (hasLastEnter) {
            return stringBuffer.substring(0, stringBuffer.length() - 1);
        }
        return stringBuffer.toString();
    }

    /**
     * 获得某个参数的描述
     * @param comment
     * @param paramName
     * @return
     */
    public static String getParamsDesc(PsiDocComment comment, String paramName) {
        if (comment == null) {
            return "";
        }
        PsiDocTag[] psiDocTags = comment.getTags();
        StringBuffer stringBuffer = new StringBuffer();
        for (PsiDocTag psiDocTag : psiDocTags) {
            if (psiDocTag.getText() == null || psiDocTag.getValueElement() == null || psiDocTag.getValueElement().getText() == null) {
                continue;
            }
            if (psiDocTag.getName().equals("param") && psiDocTag.getValueElement().getText().equals(paramName)) {
                int index = 0;
                for (PsiElement e : psiDocTag.getDataElements()) {
                    if (index++ == 0) {
                        continue;
                    }
                    stringBuffer.append(e.getText());
                }
                break;
            }
        }
        return stringBuffer.toString();
    }

}
