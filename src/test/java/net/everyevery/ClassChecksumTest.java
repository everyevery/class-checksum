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
        assertEquals("06f321001b48603d90eaf5fe19557835", hashValue);
    }

    @Test
    public void testSimpleInterface() throws Exception {
        String hashValue = ClassChecksum.checksum(new Class[]{SimpleInterface.class});
        assertEquals("586bb52ddbaa233abace9985243a35c2", hashValue);
    }

    @Test
    public void testSimpleWithParentAndInterface() throws Exception {
        String hashValue = ClassChecksum.checksum(new Class[]{SimpleWithParentAndInterface.class});
        assertEquals("2e40616ba518492ace342f6cffb8334c", hashValue);
    }

    @Test
    public void testGenericClass() throws Exception {
        String hashValue = ClassChecksum.checksum(new Class[]{GenericClass.class});
        assertEquals("af55c62483b970977ba731a4a03028a4", hashValue);
    }


    @Test
    public void testChildClassOfGenericSuperAndInterfaces() throws Exception {
        String hashValue = ClassChecksum.checksum(new Class[]{ChildExtendsGenericClass.class});
        assertEquals("c7d081b502c87ad45d8ae4cdd3e74bc0", hashValue);
    }

    @Test
    public void testSimpleClassWithEnum() throws Exception {
        String hashValue = ClassChecksum.checksum(new Class[]{SimpleClassWithEnum.class});
        assertEquals("718401f558116e4cde6f20c038fac367", hashValue);
    }
}

