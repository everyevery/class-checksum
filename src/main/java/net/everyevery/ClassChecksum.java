package net.everyevery;

import java.lang.reflect.*;
import java.security.MessageDigest;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class ClassChecksum {

    private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(ClassChecksum.class);
    private static final char[] HEX_VALUES = "0123456789abcdef".toCharArray();
    private static final String TARGET_PACKAGE = "net.everyevery";

    public static String generate(Class<?>... klasses) {
        Map<String, Set<String>> klassMap = new HashMap<>();
        for (Class<?> klass : klasses) {
            generate(klass, klassMap);
        }
        return generateDigestedString(klassMap);
    }

    private static String generateDigestedString(Map<String, Set<String>> klassMap) {
        try {
            MessageDigest digest = MessageDigest.getInstance("MD5");
            for (Map.Entry<String, Set<String>> entry : klassMap.entrySet()) {
                String className = entry.getKey();
                for (String location : entry.getValue()) {
                    digest.update((className + location).getBytes());
                }
            }
            return bytesToHex(digest.digest());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private static String getFieldProperties(Field field) {
        return new StringBuilder()
                .append(field.getModifiers()).append(":")
                .append(getClassName(field)).append(":")
                .append(field.getName()).toString();
    }

    private static void generate(Type type, Map<String, Set<String>> klassMap) {
        if (type == null) {
            return;
        }

        if (klassMap.containsKey(getClassName(type)) || !isTargetClass(type)) {
            return;
        }

        if (type instanceof Class<?>) {
            processClass((Class<?>) type, klassMap);
        }
        if (type instanceof ParameterizedType) {
            processParameterizedType((ParameterizedType) type, klassMap);
        }
        if (type instanceof GenericArrayType) {
            processGenericArrayType((GenericArrayType) type, klassMap);
        }
        if (type instanceof TypeVariable) {
            processTypeVariable((TypeVariable) type, klassMap);
        }
        if (type instanceof WildcardType) {
            processWildcardType((WildcardType) type, klassMap);
        }
    }

    private static void processClass(Class klass, Map<String, Set<String>> klassMap) {
        if (klassMap.containsKey(klass.getTypeName())) {
            return;
        }

        saveClassItself(klass, klassMap);

        generate(klass.getGenericSuperclass(), klassMap);

        for (Type type : klass.getGenericInterfaces()) {
            generate(type, klassMap);
        }

        for (Field field : klass.getDeclaredFields()) {
            generate(field.getGenericType(), klassMap);
            klassMap.get(klass.getTypeName()).add(getFieldProperties(field));
        }

        for (Class c : klass.getDeclaredClasses()) {
            generate(c, klassMap);
        }

        for (TypeVariable tv : klass.getTypeParameters()) {
            for (Type t : tv.getBounds()) {
                generate(t, klassMap);
            }
        }
    }

    private static void saveClassItself(Class<?> klass, Map<String,Set<String>> klassMap) {
        Set<String> newSet = new HashSet<>();
        newSet.add(klass.getDeclaringClass() == null ? "." : klass.getDeclaringClass().getTypeName());
        klassMap.put(klass.getTypeName(), newSet);
    }

    private static void processParameterizedType(ParameterizedType type, Map<String, Set<String>> klassMap) {
        log.debug("ParameterizedType");
        Type owner = type.getRawType();
        if (owner != null) {
            generate(owner, klassMap);
        }
        Type[] typeArguments = type.getActualTypeArguments();
        for (Type argument : typeArguments) {
            generate(argument, klassMap);
        }
    }

    private static void processGenericArrayType(GenericArrayType type, Map<String, Set<String>> klassMap) {
        log.debug("GenericArrayType");
        Type componentType = type.getGenericComponentType();
        if (componentType != null) {
            generate(componentType, klassMap);
        }
    }

    private static void processTypeVariable(TypeVariable type, Map<String, Set<String>> klassMap) {
        log.debug("TypeVariable");
        for (Type bound : type.getBounds()) {
            generate(bound, klassMap);
        }
    }

    private static void processWildcardType(WildcardType type, Map<String, Set<String>> klassMap) {
        log.debug("WildcardType");
        for (Type bound : type.getLowerBounds()) {
            generate(bound, klassMap);
        }
        for (Type bound : type.getUpperBounds()) {
            generate(bound, klassMap);
        }
    }

    private static void saveField(Field field, Map<String, Set<String>> klassMap) {
        klassMap.get(field.getDeclaringClass()).add(getFieldProperties(field));
    }

    private static boolean isTargetClass(Class klass) {
        return klass != null && klass.getTypeName().startsWith(TARGET_PACKAGE);
    }

    private static boolean isTargetClass(Type type) {
        return type != null && type.getTypeName().startsWith(TARGET_PACKAGE);
    }

    private static String getClassName(Field field) {
        return field.getGenericType().getTypeName();
    }

    private static String getClassName(Type type) {
        return type.getTypeName();
    }

    public static String bytesToHex(byte[] bytes) {
        char[] hexChars = new char[bytes.length * 2];
        for ( int j = 0; j < bytes.length; j++ ) {
            int v = bytes[j] & 0xFF;
            hexChars[j * 2] = HEX_VALUES[v >>> 4];
            hexChars[j * 2 + 1] = HEX_VALUES[v & 0x0F];
        }
        return new String(hexChars);
    }
}
