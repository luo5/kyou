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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.LinkedHashMap;
import java.util.Map;

import net.kyou.data.DataDocument;
import net.kyou.data.SchemaBuilder;
import net.kyou.data.SchemaDocument;
import net.kyou.data.SchemaField;
import net.kyou.data.SchemaStruct;
import net.kyou.exception.KyouErr;
import net.kyou.exception.KyouException;
import net.kyou.util.KyouRuntimeUtils;

import org.junit.Assert;
import org.junit.Test;

public class XmlSerializerTest {
    @Test
    public void testSerializeSchema() {
        SchemaBuilder builder = new SchemaBuilder();
        
        Map<String, String> atts = new LinkedHashMap<String, String>();
        atts.put("name", "a");
        builder.beginStruct(atts);
        
        atts.put("name", "a1");
        builder.field(atts);
        
        builder.endStruct();
        
        String expect = "<?xml version=\"1.0\" encoding=\"utf-8\"?><schema><struct name=\"a\"><field name=\"a1\"/></struct></schema>";
        
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        new XmlSerializer().serializeSchema(builder.result(), out);
        
        Assert.assertEquals(expect, new String(out.toByteArray(), KyouRuntimeUtils.utf8));
    }
    
    @Test
    public void testSerializeSchemaEx1() {
        try {
            new XmlSerializer().serializeSchema(null, new ByteArrayOutputStream());
        } catch (KyouException ex) {
            Assert.assertEquals(KyouErr.Schema.Serialization.EmptySchema, ex.err);
        }
    }
    
    @Test()
    public void testSerializeSchemaEx2() {
        try {
            new XmlSerializer().serializeSchema(new SchemaDocument(), null);
        } catch (KyouException ex) {
            Assert.assertEquals(KyouErr.Base.Stream.NullOutputStream, ex.err);
        }
    }
    
    @Test()
    public void testSerializeSchemaEx3() {
        OutputStream out = new OutputStream() {
            @Override
            public void write(int b) throws IOException {
                throw new IOException("hahaha");
            }
        };
        
        try {
            new XmlSerializer().serializeSchema(new SchemaDocument(), out);
        } catch (KyouException ex) {
            Assert.assertEquals(KyouErr.Schema.Serialization.XmlSerializeSchemaFail, ex.err);
        }
    }
    
    @Test
    public void testDeserializeSchema() {
        String xml = "<?xml version=\"1.0\" encoding=\"utf-8\"?><schema><struct name=\"a\" asdf=\"asdf\"><field name=\"a1\" array=\"true\"/></struct></schema>";
        
        ByteArrayInputStream in = new ByteArrayInputStream(xml.getBytes(KyouRuntimeUtils.utf8));
        
        SchemaDocument doc = new XmlSerializer().deserializeSchema(in);
        
        Assert.assertNotNull(doc);
        
        SchemaStruct a = (SchemaStruct) doc.get("a");
        Assert.assertNotNull(a);
        Assert.assertEquals(a.attrs.get("asdf"), "asdf");
        
        SchemaField a1 = (SchemaField) a.get("a1");
        Assert.assertNotNull(a1);
        Assert.assertEquals(a1.attrs.get("array"), "true");
        Assert.assertNull(a1.attrs.get("asdf"));
    }
    
    @Test
    public void testDeserializeSchemaEx1() {
        try {
            new XmlSerializer().deserializeSchema(null);
            Assert.fail();
        } catch (KyouException ex) {
            Assert.assertEquals(KyouErr.Base.Stream.NullInputStream, ex.err);
        }
    }
    
    @Test
    public void testDeserializeSchemaEx2() {
        try {
            new XmlSerializer().deserializeSchema(new ByteArrayInputStream("<?xml version=\"1.0\"?><aaa></aaa>".getBytes()));
            Assert.fail();
        } catch (KyouException ex) {
            Assert.assertEquals(KyouErr.Schema.Serialization.XmlDeserializeSchemaFail, ex.err);
            Assert.assertEquals(KyouErr.Schema.Serialization.InvalidSchemaXmlTag, ((KyouException) ex.cause).err);
        }
    }
    
    @Test
    public void testDeserializeSchemaEx3() {
        try {
            new XmlSerializer().deserializeSchema(new ByteArrayInputStream("<?xml version=\"1.0\"?><schema><aaa></aaa></schema>".getBytes()));
            Assert.fail();
        } catch (KyouException ex) {
            Assert.assertEquals(KyouErr.Schema.Serialization.XmlDeserializeSchemaFail, ex.err);
            Assert.assertEquals(KyouErr.Schema.Serialization.InvalidSchemaXmlTag, ((KyouException) ex.cause).err);
        }
    }
    
    @Test
    public void testDeserializeSchemaEx4() {
        try {
            new XmlSerializer().deserializeSchema(new ByteArrayInputStream("XXX".getBytes()));
            Assert.fail();
        } catch (KyouException ex) {
            Assert.assertEquals(KyouErr.Schema.Serialization.XmlDeserializeSchemaFail, ex.err);
            Assert.assertEquals(KyouErr.Base.Xml.SaxFail, ((KyouException) ex.cause).err);
        }
    }
    
    @Test
    public void testSerializeData() {
        DataDocument doc = new DataDocument();
        
        doc.map.put(new DPath("#.a"), "3");
        doc.map.put(new DPath("#.b"), "4");
        doc.map.put(new DPath("#.c.a.b.c.d.e"), "12");
        
        String expect = "<?xml version=\"1.0\" encoding=\"utf-8\"?><data><a>3</a><b>4</b><c.a.b.c.d.e>12</c.a.b.c.d.e></data>";
        
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        new XmlSerializer().serializeData(doc, out);
        
        Assert.assertEquals(expect, new String(out.toByteArray(), KyouRuntimeUtils.utf8));
    }
    
    @Test
    public void testSerializeDataEx1() {
        try {
            new XmlSerializer().serializeData(null, new ByteArrayOutputStream());
            Assert.fail();
        } catch (KyouException ex) {
            Assert.assertEquals(KyouErr.Data.Serialization.EmptyData, ex.err);
        }
    }
    
    @Test()
    public void testSerializeDataEx2() {
        try {
            new XmlSerializer().serializeData(new DataDocument(), null);
            Assert.fail();
        } catch (KyouException ex) {
            Assert.assertEquals(KyouErr.Base.Stream.NullOutputStream, ex.err);
        }
    }
    
    @Test()
    public void testSerializeDataEx3() {
        OutputStream out = new OutputStream() {
            @Override
            public void write(int b) throws IOException {
                throw new IOException("hahaha");
            }
        };
        
        try {
            new XmlSerializer().serializeData(new DataDocument(), out);
            Assert.fail();
        } catch (KyouException ex) {
            Assert.assertEquals(KyouErr.Data.Serialization.XmlSerializeDataFail, ex.err);
        }
    }
    
    @Test
    public void testDeserializeData() {
        String xml = "<?xml version=\"1.0\" encoding=\"utf-8\"?><data><a>3</a><b>4</b><c.a.b.c.d.e>12</c.a.b.c.d.e></data>";
        
        ByteArrayInputStream in = new ByteArrayInputStream(xml.getBytes(KyouRuntimeUtils.utf8));
        
        DataDocument doc = new XmlSerializer().deserializeData(in);
        
        Assert.assertNotNull(doc);
        
        Assert.assertEquals(3, doc.map.size());
        Assert.assertEquals("3", doc.get(new DPath("#.a")));
        Assert.assertEquals("4", doc.get(new DPath("#.b")));
        Assert.assertEquals("12", doc.get(new DPath("#.c.a.b.c.d.e")));
    }
    
    @Test
    public void testDeserializeDataEx1() {
        try {
            new XmlSerializer().deserializeData(null);
            Assert.fail();
        } catch (KyouException ex) {
            Assert.assertEquals(KyouErr.Base.Stream.NullInputStream, ex.err);
        }
    }
    
    @Test
    public void testDeserializeDataEx2() {
        try {
            new XmlSerializer().deserializeData(new ByteArrayInputStream("<?xml version=\"1.0\"?><aaa></aaa>".getBytes()));
            Assert.fail();
        } catch (KyouException ex) {
            Assert.assertEquals(KyouErr.Data.Serialization.XmlDeserializeDataFail, ex.err);
            Assert.assertEquals(KyouErr.Data.Serialization.InvalidDataXmlTag, ((KyouException) ex.cause).err);
        }
    }
    
    @Test
    public void testDeserializeDataEx3() {
        try {
            new XmlSerializer().deserializeData(new ByteArrayInputStream("XXX".getBytes()));
            Assert.fail();
        } catch (KyouException ex) {
            Assert.assertEquals(KyouErr.Data.Serialization.XmlDeserializeDataFail, ex.err);
            Assert.assertEquals(KyouErr.Base.Xml.SaxFail, ((KyouException) ex.cause).err);
        }
    }
    
    @Test
    public void testDeserializeDataEx4() {
        try {
            new XmlSerializer().deserializeData(new ByteArrayInputStream("<?xml version=\"1.0\"?><data><a>3</a><b><c></c></b></data>".getBytes()));
            Assert.fail();
        } catch (KyouException ex) {
            Assert.assertEquals(KyouErr.Data.Serialization.XmlDeserializeDataFail, ex.err);
            Assert.assertEquals(KyouErr.Data.Serialization.InvalidDataLevel, ((KyouException) ex.cause).err);
        }
    }
}
