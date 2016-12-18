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

public class ClassChecksumTest {

    @Test
    public void testSimpleClassWithVariousFieldsAndMethods() throws Exception {
        String hashValue = ClassChecksum.checksum(new Class[]{SimpleClass.class});
        assertEquals("65067852568df80bda119feaf7b84f6e", hashValue);
    }

    @Test
    public void testSimpleInterface() throws Exception {
        String hashValue = ClassChecksum.checksum(new Class[]{SimpleInterface.class});
        assertEquals("c2fd6c366758033dd0bd706d1b0318ae", hashValue);
    }

    @Test
    public void testSimpleWithParentAndInterface() throws Exception {
        String hashValue = ClassChecksum.checksum(new Class[]{SimpleWithParentAndInterface.class});
        assertEquals("946edc0fec4d93f44bcf45ec0e5dfb62", hashValue);
    }

    @Test
    public void testGenericClass() throws Exception {
        String hashValue = ClassChecksum.checksum(new Class[]{GenericClass.class});
        assertEquals("10846449b8a53390daab725e1a68a2c1", hashValue);
    }


    @Test
    public void testChildClassOfGenericSuperAndInterfaces() throws Exception {
        String hashValue = ClassChecksum.checksum(new Class[]{ChildExtendsGenericClass.class});
        assertEquals("f0f335340d27c67e1a247fe5446227de", hashValue);
    }

    @Test
    public void testSimpleClassWithEnum() throws Exception {
        String hashValue = ClassChecksum.checksum(new Class[]{SimpleClassWithEnum.class});
        assertEquals("e2348c516b55159ec9ddbe1f860915eb", hashValue);
    }
}

