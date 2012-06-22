package net.kyou.util;

import net.kyou.exception.KyouErr;
import net.kyou.exception.KyouException;
import net.kyou.util.KyouString.AttemptMatcher;

import org.junit.Assert;
import org.junit.Test;

public class KyouStringTest {
    
    @Test
    public void testGet() {
        KyouString str = new KyouString("asdf");
        Assert.assertEquals('a', str.get());
        Assert.assertEquals('s', str.get());
        Assert.assertEquals('d', str.get());
        Assert.assertEquals('f', str.get());
    }
    
    @Test
    public void testGetIntInt() {
        KyouString str = new KyouString("asdf");
        Assert.assertEquals("as", str.get(0, 2));
        Assert.assertEquals("df", str.get(2, 2));
        Assert.assertEquals("asdf", str.get(0, 4));
    }
    
    @Test
    public void testPushback() {
        KyouString str = new KyouString("asdf");
        Assert.assertEquals('a', str.get());
        str.pushback();
        Assert.assertEquals('a', str.get());
        
        str.pushback();
        try {
            str.pushback();
        } catch (Exception ex) {
            Assert.assertTrue(ex instanceof IndexOutOfBoundsException);
        }
    }
    
    @Test
    public void testPos() {
        KyouString str = new KyouString("asdf");
        Assert.assertEquals(0, str.pos());
        str.get();
        Assert.assertEquals(1, str.pos());
        str.get();
        Assert.assertEquals(2, str.pos());
        str.pushback();
        Assert.assertEquals(1, str.pos());
        str.get();
        Assert.assertEquals(2, str.pos());
    }
    
    @Test
    public void testPosInt() {
        KyouString str = new KyouString("asdf");
        Assert.assertEquals(0, str.pos());
        str.pos(3);
        Assert.assertEquals(3, str.pos());
    }
    
    @Test
    public void testLength() {
        KyouString str = new KyouString("asdf");
        Assert.assertEquals(4, str.length());
    }
    
    @Test
    public void testHasRemaining() {
        KyouString str = new KyouString("asdf");
        Assert.assertTrue(str.hasRemaining());
        str.get();
        Assert.assertTrue(str.hasRemaining());
        str.get();
        Assert.assertTrue(str.hasRemaining());
        str.get();
        Assert.assertTrue(str.hasRemaining());
        str.get();
        Assert.assertFalse(str.hasRemaining());
    }
    
    @Test
    public void testAttemptILL1StringMatcher() {
        KyouString str = new KyouString("asdf");
        
        Assert.assertEquals("a", str.attemptMore('a'));
        Assert.assertEquals("s", str.attemptMore('s'));
        str.pos(0);
        Assert.assertEquals("as", str.attemptMore('a', 's'));
        str.pos(0);
        Assert.assertEquals("asd", str.attemptMore('a', 's', 'd'));
        str.pos(0);
        Assert.assertEquals("asdf", str.attemptMore('a', 's', 'd', 'f'));
        str.pos(0);
        Assert.assertEquals(null, str.attemptMore());
        str.pos(0);
        Assert.assertEquals(null, str.attemptMore('b'));
        
        str = new KyouString("a=3&b=4");
        
        Assert.assertEquals("a", str.attemptUntil('='));
        str.pos(0);
        Assert.assertEquals("a=3", str.attemptUntil('&'));
        str.pos(0);
        Assert.assertEquals("a=3&b=4", str.attemptUntil('|'));
        str.pos(0);
        Assert.assertEquals("a=3&b=4", str.attemptUntil());
        str.pos(0);
        Assert.assertEquals(null, str.attemptUntil('a', 'b', 'c', 'd'));
        
        str = new KyouString("");
        Assert.assertEquals(null, str.attempt(new AttemptMatcher() {
            @Override
            public boolean matches(char c, int pos) {
                return true;
            }
        }));
    }
    
    @Test
    public void testAttemptCharArray() {
        KyouString str = new KyouString("asdf");
        
        Assert.assertEquals('a', str.attempt('a', 's', 'd', 'f').charValue());
        Assert.assertEquals('s', str.attempt('a', 's', 'd', 'f').charValue());
        Assert.assertEquals('d', str.attempt('a', 's', 'd', 'f').charValue());
        Assert.assertEquals('f', str.attempt('a', 's', 'd', 'f').charValue());
        Assert.assertEquals(null, str.attempt('a', 's', 'd', 'f'));
        
        str.pos(0);
        
        Assert.assertEquals(null, str.attempt('1', '2', '3'));
        
        str = new KyouString("");
        Assert.assertEquals(null, str.attempt('a', 's', 'd', 'f'));
    }
    
    @Test
    public void testAttemptStringArray() {
        KyouString str = new KyouString("asdf");
        
        Assert.assertEquals("asdf", str.attempt("a", "sd", "asd", "asdf"));
        str.pos(0);
        Assert.assertEquals(null, str.attempt("111", "222"));
        
        str = new KyouString("a");
        Assert.assertEquals(null, str.attempt("abc", "def"));
        
        str = new KyouString("");
        Assert.assertEquals(null, str.attempt("abc", "def"));
    }
    
    @Test
    public void testToString() {
        KyouString str = new KyouString("asdf");
        Assert.assertEquals("asdf", str.toString());
        str.pos(2);
        Assert.assertEquals("df", str.toString());
    }
    
    @Test
    public void testEx() {
        try {
            new KyouString(null);
            Assert.fail();
        } catch (KyouException ex) {
            Assert.assertEquals(KyouErr.Base.KyouString.EmptyKyouString, ex.err);
        }
    }
}
