/* Copyright - Apache License 2.0
 * 
 * The project "kyou" is
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *      http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package net.kyou.data;

import net.kyou.exception.KyouErr;
import net.kyou.exception.KyouException;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class SchemaTest {
    private SchemaStruct stru;
    
    @Before
    public void before() {
        SchemaBuilder builder = new SchemaBuilder();
        builder.beginStruct(Attrs.NAME, "struct");
        builder.beginStruct(Attrs.NAME, "HEAD");
        builder.field(Attrs.NAME, "TXCODE");
        builder.field(Attrs.NAME, "DATE");
        builder.endStruct();
        builder.beginStruct(Attrs.NAME, "BODY");
        builder.field(Attrs.NAME, "aaa");
        builder.field(Attrs.NAME, "bbb");
        builder.field(Attrs.NAME, "ccc");
        builder.beginStruct(Attrs.NAME, "INNER");
        builder.field(Attrs.NAME, "x1");
        builder.field(Attrs.NAME, "x2");
        builder.endStruct();
        builder.endStruct();
        builder.endStruct();
        
        this.stru = builder.result().get("struct");
    }
    
    @Test
    public void testForEach() throws Exception {
        final StringBuilder builder = new StringBuilder();
        
        this.stru.foreach(new ISchemaVisitor() {
            @Override
            public void struStart(SchemaStruct stru) throws Exception {
                builder.append(stru.name());
            }
            
            @Override
            public void struEnd(SchemaStruct stru) throws Exception {
                builder.append(stru.name());
            }
            
            @Override
            public void field(SchemaField field) throws Exception {
                builder.append(field.name());
            }
            
            @Override
            public void docStart(SchemaDocument doc) throws Exception {
            }
            
            @Override
            public void docEnd(SchemaDocument doc) throws Exception {
            }
        });
        
        Assert.assertEquals("structHEADTXCODEDATEHEADBODYaaabbbcccINNERx1x2INNERBODYstruct", builder.toString());
    }
    
    @Test
    public void testSize() {
        Assert.assertEquals(2, stru.size());
    }
    
    @Test
    public void testAdd() {
        SchemaField field = new SchemaField();
        field.attrs.put("name", "XXX");
        
        this.stru.add(field);
        
        Assert.assertNotNull(this.stru.get("XXX"));
        
        Assert.assertEquals(this.stru, field.parent);
        
        this.stru.remove("XXX");
        
        Assert.assertNull(this.stru.get("XXX"));
    }
    
    @Test
    public void testAddEx() {
        SchemaField field = new SchemaField();
        field.attrs.put("name", "XXX");
        
        try {
            this.stru.add(null);
        } catch (KyouException ex) {
            Assert.assertEquals(KyouErr.Schema.AddEmptyChild, ex.err);
        }
        
        try {
            this.stru.add(new SchemaField());
        } catch (KyouException ex) {
            Assert.assertEquals(KyouErr.Schema.EmptySchemaItemName, ex.err);
        }
        
        try {
            this.stru.add(this.stru);
        } catch (KyouException ex) {
            Assert.assertEquals(KyouErr.Schema.AddSelfAsChild, ex.err);
        }
        
        try {
            this.stru.add(new SchemaDocument());
        } catch (KyouException ex) {
            Assert.assertEquals(KyouErr.Schema.AddDocumentAsChild, ex.err);
        }
        
        try {
            ((SchemaStruct) this.stru.get("BODY")).add(this.stru);
        } catch (KyouException ex) {
            Assert.assertEquals(KyouErr.Schema.AddParentAsChild, ex.err);
        }
        
        try {
            ((SchemaStruct) ((SchemaStruct) this.stru.get("BODY")).get("INNER")).add(this.stru);
        } catch (KyouException ex) {
            Assert.assertEquals(KyouErr.Schema.AddParentAsChild, ex.err);
        }
    }
    
    @Test
    public void testRemove() {
        SchemaField field = new SchemaField();
        field.attrs.put("name", "XXX");
        
        this.stru.add(field);
        
        Assert.assertNotNull(this.stru.get("XXX"));
        
        this.stru.remove(null);
        Assert.assertNotNull(this.stru.get("XXX"));
        
        this.stru.remove("");
        Assert.assertNotNull(this.stru.get("XXX"));
        
        this.stru.remove("KKK");
        Assert.assertNotNull(this.stru.get("XXX"));
        
        this.stru.remove("XXX");
        Assert.assertNull(this.stru.get("XXX"));
    }
    
    @Test
    public void testIterator() {
        int i = 0;
        String[] expected = {
                "HEAD",
                "BODY" };
        
        for (SchemaItem item : this.stru) {
            Assert.assertEquals(expected[i], item.name());
            i++;
        }
    }
    
    @Test
    public void testGet() {
        Assert.assertNull(this.stru.get(null));
        Assert.assertNull(this.stru.get(""));
        Assert.assertNull(this.stru.get("KKK"));
        Assert.assertNotNull(this.stru.get("HEAD"));
        Assert.assertTrue(this.stru.get("BODY") instanceof SchemaStruct);
    }
    
    @Test
    public void testAttrs() {
        SchemaBuilder builder = new SchemaBuilder();
        builder.field(Attrs.NAME, "XXX", Attrs.ARRAY, "true", "test", "test");
        SchemaField f = builder.result().get("XXX");
        
        Assert.assertEquals("test", f.attr("test"));
        Assert.assertTrue(f.attrb(Attrs.ARRAY));
        Assert.assertFalse(f.attrb(Attrs.NAME));
        Assert.assertFalse(f.attrb("asdfasdf"));
    }
    
    @Test
    public void testToString() {
        String xml = "";
        xml += "<?xml version=\"1.0\" encoding=\"utf-8\"?>\r\n";
        xml += "<schema>\r\n";
        xml += "  <struct name=\"struct\">\r\n";
        xml += "    <struct name=\"HEAD\">\r\n";
        xml += "      <field name=\"TXCODE\"/>\r\n";
        xml += "      <field name=\"DATE\"/>\r\n";
        xml += "    </struct>\r\n";
        xml += "    <struct name=\"BODY\">\r\n";
        xml += "      <field name=\"aaa\"/>\r\n";
        xml += "      <field name=\"bbb\"/>\r\n";
        xml += "      <field name=\"ccc\"/>\r\n";
        xml += "      <struct name=\"INNER\">\r\n";
        xml += "        <field name=\"x1\"/>\r\n";
        xml += "        <field name=\"x2\"/>\r\n";
        xml += "      </struct>\r\n";
        xml += "    </struct>\r\n";
        xml += "  </struct>\r\n";
        xml += "</schema>";
        
        Assert.assertEquals(xml, this.stru.parent().toString());
    }
    
    @Test
    public void testDefaultSchemaVisitor() {
        this.stru.parent.foreach(new DefaultSchemaVisitor());
    }
    
    @Test
    public void testForEachEx() {
        try {
            this.stru.parent.foreach(new DefaultSchemaVisitor() {
                @Override
                public void docStart(SchemaDocument doc) throws Exception {
                    throw new NullPointerException();
                }
            });
            
            Assert.fail();
        } catch (KyouException ex) {
            Assert.assertEquals(KyouErr.Schema.ForEachFail, ex.err);
        }
        try {
            this.stru.parent.foreach(new DefaultSchemaVisitor() {
                
                @Override
                public void struStart(SchemaStruct stru) throws Exception {
                    throw new NullPointerException();
                }
            });
            
            Assert.fail();
        } catch (KyouException ex) {
            Assert.assertEquals(KyouErr.Schema.ForEachFail, ex.err);
        }
        try {
            this.stru.parent.foreach(new DefaultSchemaVisitor() {
                @Override
                public void field(SchemaField field) throws Exception {
                    throw new NullPointerException();
                }
            });
            
            Assert.fail();
        } catch (KyouException ex) {
            Assert.assertEquals(KyouErr.Schema.ForEachFail, ex.err);
        }
    }
}
