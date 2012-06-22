package net.kyou.data;

import net.kyou.exception.KyouErr;
import net.kyou.exception.KyouException;

import org.junit.Assert;
import org.junit.Test;

public class DPathTest {
    
    @Test
    public void testDPath() {
        try {
            new DPath(null);
            Assert.fail();
        } catch (KyouException ex) {
            Assert.assertEquals(KyouErr.DPath.EmptyDPath, ex.err);
        }
        
        try {
            new DPath("");
            Assert.fail();
        } catch (KyouException ex) {
            Assert.assertEquals(KyouErr.DPath.EmptyDPath, ex.err);
        }
    }
    
    @Test
    public void testHashCode() {
        DPath dpath1 = new DPath("asdf");
        DPath dpath2 = new DPath("asdf");
        DPath dpath3 = new DPath("jkl;");
        
        Assert.assertTrue(dpath1.hashCode() == dpath2.hashCode());
        Assert.assertFalse(dpath1.hashCode() == dpath3.hashCode());
    }
    
    @Test
    public void testName() {
        DPath path = new DPath("#");
        Assert.assertEquals("#", path.name());
        
        path = new DPath("a.b.c.d");
        Assert.assertEquals("d", path.name());
    }
    
    @Test
    public void testChild() {
        DPath dpath = new DPath("#.a.b.c");
        Assert.assertEquals("#.a.b.c.d", dpath.child("d").toString());
    }
    
    @Test
    public void testParent() {
        DPath dpath = new DPath("#.a.b.c");
        Assert.assertEquals("#.a.b", (dpath = dpath.parent()).toString());
        Assert.assertEquals("#.a", (dpath = dpath.parent()).toString());
        Assert.assertEquals("#", (dpath = dpath.parent()).toString());
        Assert.assertEquals("#", (dpath = dpath.parent()).toString());
    }
    
    @Test
    public void testIsRoot() {
        DPath dpath = new DPath("#.a.b");
        Assert.assertFalse((dpath = dpath.parent()).isRoot()); // #.a
        Assert.assertTrue((dpath = dpath.parent()).isRoot()); // #
        Assert.assertTrue((dpath = dpath.parent()).isRoot()); // #
    }
    
    @Test
    public void testEqualsObject() {
        DPath dpath1 = new DPath("#");
        DPath dpath2 = new DPath("#.a");
        DPath dpath3 = new DPath("#");
        
        Assert.assertTrue(dpath1.equals(dpath1));
        Assert.assertFalse(dpath1.equals(null));
        Assert.assertFalse(dpath1.equals(""));
        Assert.assertFalse(dpath1.equals(dpath2));
        Assert.assertTrue(dpath1.equals(dpath3));
    }
    
    @Test
    public void testToString() {
        Assert.assertEquals("123", new DPath("123").toString());
    }
}
