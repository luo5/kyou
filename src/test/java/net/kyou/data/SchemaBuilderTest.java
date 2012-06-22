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

import java.util.LinkedHashMap;
import java.util.Map;

import net.kyou.exception.KyouErr;
import net.kyou.exception.KyouException;

import org.junit.Assert;
import org.junit.Test;

public class SchemaBuilderTest {
    @Test
    public final void testBuilder() {
        Map<String, String> atts = new LinkedHashMap<String, String>();
        SchemaBuilder builder = new SchemaBuilder();

        atts.put("name", "s1");
        atts.put("asdf", "asdf");
        builder.beginStruct(atts);

        atts.clear();
        atts.put("name", "f1");
        atts.put("array", "true");
        builder.field(atts);

        SchemaDocument doc = builder.result();

        Assert.assertNotNull(doc);

        SchemaStruct s1 = (SchemaStruct) doc.get("s1");
        Assert.assertNotNull(s1);
        Assert.assertEquals("asdf", s1.attr("asdf"));

        SchemaField f1 = (SchemaField) s1.get("f1");
        Assert.assertNotNull(f1);
        Assert.assertEquals("true", f1.attr("array"));
        Assert.assertNull(f1.attr("asdf"));
    }

    @Test
    public void testBuilder2() {
        SchemaBuilder builder = new SchemaBuilder();

        builder.beginStruct("name", "s1", "asdf", "asdf");

        builder.field("name", "f1", "array", "true");

        SchemaDocument doc = builder.result();

        Assert.assertNotNull(doc);

        SchemaStruct s1 = (SchemaStruct) doc.get("s1");
        Assert.assertNotNull(s1);
        Assert.assertEquals("asdf", s1.attrs.get("asdf"));

        SchemaField f1 = (SchemaField) s1.get("f1");
        Assert.assertNotNull(f1);
        Assert.assertEquals("true", f1.attrs.get("array"));
        Assert.assertNull(f1.attrs.get("asdf"));
    }

    @Test
    public void testBuilder3() {
        SchemaBuilder builder = new SchemaBuilder();
        builder.beginStruct("name", "struct");
        builder.beginStruct("name", "HEAD");
        builder.field("name", "TXCODE");
        builder.field("name", "DATE");
        builder.endStruct();
        builder.beginStruct("name", "BODY");
        builder.field("name", "aaa");
        builder.field("name", "bbb");
        builder.field("name", "ccc");
        builder.beginStruct("name", "INNER");
        builder.field("name", "x1");
        builder.field("name", "x2");
        builder.endStruct();
        builder.endStruct();
        builder.endStruct();

        SchemaDocument doc = builder.result();
        SchemaStruct struct = builder.result().get("struct");
        Assert.assertEquals(doc, struct.parent);

        SchemaStruct HEAD = struct.get("HEAD");
        Assert.assertEquals(struct, HEAD.parent);

        SchemaField TXCODE = HEAD.get("TXCODE");
        Assert.assertEquals(HEAD, TXCODE.parent);
        SchemaField DATE = HEAD.get("DATE");
        Assert.assertEquals(HEAD, DATE.parent);

        SchemaStruct BODY = struct.get("BODY");
        Assert.assertEquals(struct, BODY.parent);

        SchemaField aaa = BODY.get("aaa");
        Assert.assertEquals(BODY, aaa.parent);
        SchemaField bbb = BODY.get("bbb");
        Assert.assertEquals(BODY, bbb.parent);
        SchemaField ccc = BODY.get("ccc");
        Assert.assertEquals(BODY, ccc.parent);

        SchemaStruct INNER = BODY.get("INNER");
        Assert.assertEquals(BODY, INNER.parent);

        SchemaField x1 = INNER.get("x1");
        Assert.assertEquals(INNER, x1.parent);
        SchemaField x2 = INNER.get("x2");
        Assert.assertEquals(INNER, x2.parent);
    }

    @Test
    public void testBuilderEx() {
        SchemaBuilder builder = new SchemaBuilder();

        try {
            builder.beginStruct("asdf", "asdf", "asdf");
        } catch (KyouException ex) {
            Assert.assertEquals(KyouErr.Schema.InvalidSchemaBuilderArguments, ex.err);
        }
    }
}
