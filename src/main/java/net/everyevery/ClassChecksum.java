package net.everyevery;

import java.lang.reflect.*;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.*;

public class ClassChecksum {

    private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(ClassChecksum.class);

    private static final char[] HEX_VALUES = "0123456789abcdef".toCharArray();
    private static final String JAVA_PACKAGE = "java.";

    private MessageDigest messageDigest;
    private String targetPackage;
    private Map<String,Set<String>> klassMap = new TreeMap<>();


    public static String checksum(Class<?>... klasses) {
        return checksum(new ClassChecksum(DigestAlgorithm.MD5, ""), klasses);
    }

    public static String checksum(String customTargetPackage, Class<?>... klasses) {
        return checksum(new ClassChecksum(DigestAlgorithm.MD5, customTargetPackage), klasses);
    }

    public static String checksum(DigestAlgorithm digestAlgorithm, Class<?>... klasses) {
        return checksum(new ClassChecksum(digestAlgorithm, ""), klasses);
    }

    public static String checksum(String customTargetPackage, DigestAlgorithm digestAlgorithm, Class[] klasses) {
        return checksum(new ClassChecksum(digestAlgorithm, customTargetPackage), klasses);
    }

    private static String checksum(ClassChecksum classChecksum, Class<?>... klasses) {
        for (Class<?> klass : klasses) {
            classChecksum.update(klass);
        }
        return classChecksum.digestedString();
    }

    public ClassChecksum(DigestAlgorithm digestAlgorithm, String targetPackage) {
        try {
            this.messageDigest = MessageDigest.getInstance(digestAlgorithm.toString());
        } catch (NoSuchAlgorithmException e) {
            this.messageDigest = null;
        }
        this.targetPackage = targetPackage;
    }

    public void reset() {
        messageDigest.reset();
        klassMap.clear();
    }

    public String digestedString() {
        if (messageDigest == null) {
            return "";
        }

        for (Map.Entry<String, Set<String>> entry : klassMap.entrySet()) {
            String className = entry.getKey();
            for (String location : entry.getValue()) {
                messageDigest.update((className + location).getBytes());
            }
        }
        return bytesToHex(messageDigest.digest());
    }

    private String getFieldProperties(Field field) {
        return new StringBuilder()
                .append(field.getModifiers()).append(":")
                .append(getClassName(field)).append(":")
                .append(field.getName()).toString();
    }

    public void update(Type type) {
        if (type == null) {
            return;
        }

        if (klassMap.containsKey(getClassName(type)) || !isTargetClass(type)) {
            return;
        }

        if (type instanceof Class<?>) {
            processClass((Class<?>) type);
        }
        if (type instanceof ParameterizedType) {
            processParameterizedType((ParameterizedType) type);
        }
        if (type instanceof GenericArrayType) {
            processGenericArrayType((GenericArrayType) type);
        }
        if (type instanceof TypeVariable) {
            processTypeVariable((TypeVariable) type);
        }
        if (type instanceof WildcardType) {
            processWildcardType((WildcardType) type);
        }
    }

    private void processClass(Class klass) {
        if (klassMap.containsKey(klass.getTypeName())) {
            return;
        }

        saveClassItself(klass);

        update(klass.getGenericSuperclass());

        for (Type type : klass.getGenericInterfaces()) {
            update(type);
        }

        for (Field field : klass.getDeclaredFields()) {
            update(field.getGenericType());
            klassMap.get(klass.getTypeName()).add(getFieldProperties(field));
        }

        for (Class c : klass.getDeclaredClasses()) {
            update(c);
        }

        for (TypeVariable tv : klass.getTypeParameters()) {
            for (Type t : tv.getBounds()) {
                update(t);
            }
        }
    }

    private void saveClassItself(Class<?> klass) {
        Set<String> newSet = new TreeSet<>();
        newSet.add(klass.getDeclaringClass() == null ? "." : klass.getDeclaringClass().getTypeName());
        klassMap.put(klass.getTypeName(), newSet);
    }

    private void processParameterizedType(ParameterizedType type) {
        log.debug("ParameterizedType");
        Type owner = type.getRawType();
        if (owner != null) {
            update(owner);
        }
        Type[] typeArguments = type.getActualTypeArguments();
        for (Type argument : typeArguments) {
            update(argument);
        }
    }

    private void processGenericArrayType(GenericArrayType type) {
        log.debug("GenericArrayType");
        Type componentType = type.getGenericComponentType();
        if (componentType != null) {
            update(componentType);
        }
    }

    private void processTypeVariable(TypeVariable type) {
        log.debug("TypeVariable");
        for (Type bound : type.getBounds()) {
            update(bound);
        }
    }

    private void processWildcardType(WildcardType type) {
        log.debug("WildcardType");
        for (Type bound : type.getLowerBounds()) {
            update(bound);
        }
        for (Type bound : type.getUpperBounds()) {
            update(bound);
        }
    }

    private void saveField(Field field) {
        klassMap.get(field.getDeclaringClass()).add(getFieldProperties(field));
    }

    private boolean isTargetClass(Class klass) {
        return klass != null
                && !klass.getTypeName().startsWith("JAVA_PACKAGE")
                && klass.getTypeName().startsWith(targetPackage);
    }

    private boolean isTargetClass(Type type) {
        return type != null
                && !type.getTypeName().startsWith(JAVA_PACKAGE)
                && type.getTypeName().startsWith(targetPackage);
    }

    private String getClassName(Field field) {
        return field.getGenericType().getTypeName();
    }

    private String getClassName(Type type) {
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
