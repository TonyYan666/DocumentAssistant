package com.smileframework.plugin.documentassistant.parser.translator;


import java.util.HashSet;
import java.util.Set;

public class TypeTranslator {

    public static final String TYPE_LIST = "List";
    public static final String TYPE_MAP = "Map";
    public static final String TYPE_OBJ = "Object";


    public static Set<String> charTypeList;
    public static Set<String> intTypeList;
    public static Set<String> doubleTypeList;
    public static Set<String> bigIntTypeList;
    public static Set<String> dateTypeList;
    public static Set<String> collectionTypeList;
    public static Set<String> mapTypeList;
    public static Set<String> boolTypeList;

    public static void initCharTypeList() {
        charTypeList = new HashSet<>();
        charTypeList.add("java.lang.String");
    }

    public static void initDoubleTypeList() {
        doubleTypeList = new HashSet<>();
        doubleTypeList.add("double");
        doubleTypeList.add("float");
        doubleTypeList.add("java.lang.Double");
        doubleTypeList.add("java.lang.Float");
        doubleTypeList.add("java.math.BigDecimal");
    }

    public static void initIntTypeList() {
        intTypeList = new HashSet<>();
        intTypeList.add("int");
        intTypeList.add("short");
        intTypeList.add("java.lang.Integer");
        intTypeList.add("java.lang.Short");
    }

    public static void initBoolTypeList(){
        boolTypeList = new HashSet<>();
        boolTypeList.add("java.lang.Boolean");
        boolTypeList.add("boolean");
    }

    public static void initBigIntList() {
        bigIntTypeList = new HashSet<>();
        bigIntTypeList.add("long");
        bigIntTypeList.add("java.lang.Long");
    }

    public static void initDateTypeList() {
        dateTypeList = new HashSet<>();
        dateTypeList.add("java.util.Date");
        dateTypeList.add("java.sql.Timestamp");
        dateTypeList.add("java.sql.Date");
    }

    public static void initCollectionTypeList() {
        collectionTypeList = new HashSet<>();
        collectionTypeList.add("java.util.List");
        collectionTypeList.add("java.util.ArrayList");
        collectionTypeList.add("java.util.concurrent.CopyOnWriteArrayList");
        collectionTypeList.add("java.util.Vector");
        collectionTypeList.add("java.util.Set");
        collectionTypeList.add("java.util.HashSet");
        collectionTypeList.add("java.util.concurrent.CopyOnWriteArraySet");
        collectionTypeList.add("java.util.TreeSet");
    }

    public static void initMapTypeList() {
        mapTypeList = new HashSet<>();
        mapTypeList.add("java.util.Map");
        mapTypeList.add("java.util.TreeMap");
        mapTypeList.add("java.util.HashMap");
    }

    public static String docTypeTranslate(String javaType) {
        if (bigIntTypeList == null) {
            initBigIntList();
        }
        if (bigIntTypeList.contains(javaType)) {
            return "Long";
        }
        if (charTypeList == null) {
            initCharTypeList();
        }
        if(boolTypeList == null){
            initBoolTypeList();
        }
        if(boolTypeList.contains(javaType)){
            return "Boolean";
        }
        if (charTypeList.contains(javaType)) {
            return "String";
        }
        if (intTypeList == null) {
            initIntTypeList();
        }
        if (intTypeList.contains(javaType)) {
            return "Integer";
        }
        if (doubleTypeList == null) {
            initDoubleTypeList();
        }
        if (doubleTypeList.contains(javaType)) {
            return "Double";
        }
        if (dateTypeList == null) {
            initDateTypeList();
        }
        if (dateTypeList.contains(javaType)) {
            return "DateTime";
        }
        if (collectionTypeList == null) {
            initCollectionTypeList();
        }
        if (collectionTypeList.contains(javaType)) {
            return TypeTranslator.TYPE_LIST;
        }
        if (mapTypeList == null) {
            initMapTypeList();
        }
        if (mapTypeList.contains(javaType)) {
            return TypeTranslator.TYPE_MAP;
        }
        return TypeTranslator.TYPE_OBJ;
    }


}
