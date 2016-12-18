package net.everyevery;

import org.junit.Test;
import static org.junit.Assert.*;

import java.util.List;
import java.util.Map;


class SimpleClass {
    public int publicInt;
    private int privateInt;
    public Integer publicInteger;
    String defaultString;
    protected List<Integer> integerList;

    public String test() {
        return defaultString + String.valueOf(privateInt);
    }
}

interface SimpleInterface {
    int intField = 0;
    String stringField = "";
}

class SimpleWithParentAndInterface extends SimpleClass implements SimpleInterface {
    public int publicChildInt;
    private String[] privateChildString;
    private Map<Integer, SimpleClass> privateChildMap;
}

class GenericClass<T> extends SimpleWithParentAndInterface implements SimpleInterface {
    private T privateT;
    public T publicT;
}

interface AnotherInterface<S> {
}

class ChildExtendsGenericClass extends GenericClass<Integer> implements AnotherInterface<Integer> { }

enum SimpleEnum { A,B,C; }

class SimpleClassWithEnum {
    private SimpleEnum simpleEnum;
    protected List list;
    public List<Integer> integerList;
}

class SimpleOtherClass {
    private int intValue;
}

class SimpleOtherClass2 {
    private int intValue;
}

class SimpleClassWithGeneric {
    private Map<SimpleOtherClass, SimpleOtherClass2> otherClasses;
}

public class ClassChecksumTest {

    @Test
    public void testSimpleClassWithVariousFieldsAndMethods() throws Exception {
        String hashValue = ClassChecksum.checksum("net.everyevery", new Class[]{SimpleClass.class});
        assertEquals("f0c7dbee6513ea8cb6fc4730fc4f251e", hashValue);
    }

    @Test
    public void testSimpleInterface() throws Exception {
        String hashValue = ClassChecksum.checksum("net.everyevery", new Class[]{SimpleInterface.class});
        assertEquals("1b4bcb4b57ce98455d6bf2676de475ad", hashValue);
    }

    @Test
    public void testSimpleWithParentAndInterface() throws Exception {
        String hashValue = ClassChecksum.checksum("net.everyevery", new Class[]{SimpleWithParentAndInterface.class});
        assertEquals("69ebb530239e0b4cf14a300ab3a07fd2", hashValue);
    }

    @Test
    public void testGenericClass() throws Exception {
        String hashValue = ClassChecksum.checksum("net.everyevery", new Class[]{GenericClass.class});
        assertEquals("a21f42f99a8c0726540b952070323b41", hashValue);
    }


    @Test
    public void testChildClassOfGenericSuperAndInterfaces() throws Exception {
        String hashValue = ClassChecksum.checksum("net.everyevery", new Class[]{ChildExtendsGenericClass.class});
        assertEquals("7d89b488e9627319ebfe379c14f5276e", hashValue);
    }

    @Test
    public void testSimpleClassWithEnum() throws Exception {
        String hashValue = ClassChecksum.checksum("net.everyevery", new Class[]{SimpleClassWithEnum.class});
        assertEquals("718401f558116e4cde6f20c038fac367", hashValue);
    }

    @Test
    public void testSimpleClassWithGeneric() throws Exception {
        String hashValue = ClassChecksum.checksum("net.everyevery", new Class[]{SimpleClassWithGeneric.class});
        assertEquals("b9ae59e8e1cc20ebc070edda92d8d166", hashValue);
    }
}

