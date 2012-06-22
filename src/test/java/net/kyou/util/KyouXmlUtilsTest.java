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
package net.kyou.util;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

import net.kyou.exception.KyouErr;
import net.kyou.exception.KyouException;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

public class KyouXmlUtilsTest {
    @Before
    public void before() {
        new KyouXmlUtils();
    }

    @Test
    public void testLoad() {
        String xml = "";
        xml += "<?xml version=\"1.0\"?>";
        xml += "<schema>";
        xml += "<struct name=\"s1\">";
        xml += "<field name=\"aaa\" />";
        xml += "<field name=\"bbb\" />";
        xml += "</struct>";
        xml += "<field name=\"f1\"/>";
        xml += "</schema>";

        Document doc = KyouXmlUtils.load(new ByteArrayInputStream(xml.getBytes()));
        Assert.assertNotNull(doc);
    }

    @Test
    public void testLoadEx() {
        try {
            KyouXmlUtils.load(null);
        } catch (KyouException ex) {
            Assert.assertEquals(KyouErr.Base.Stream.NullInputStream, ex.err);
        }
    }

    @Test
    public void testLoadEx2() {
        try {
            String xml = "xxx";

            KyouXmlUtils.load(new ByteArrayInputStream(xml.getBytes()));
        } catch (KyouException ex) {
            Assert.assertEquals(KyouErr.Base.Xml.ReadFail, ex.err);
            Assert.assertEquals(KyouErr.Base.Xml.SaxFail, ((KyouException) ex.cause).err);
        }
    }

    @Test
    public void testLoadEx3() {
        try {
            KyouXmlUtils.load(new InputStream() {

                @Override
                public int read() throws IOException {
                    throw new IOException("hahaha");
                }
            });
        } catch (KyouException ex) {
            Assert.assertEquals(KyouErr.Base.Xml.ReadFail, ex.err);
        }
    }

    @Test
    public void testSelectNode() {
        String xml = "";
        xml += "<?xml version=\"1.0\"?>";
        xml += "<schema>";
        xml += "<struct name=\"s1\">";
        xml += "<field name=\"aaa\" />";
        xml += "<field name=\"bbb\" />";
        xml += "</struct>";
        xml += "<field name=\"f1\"/>";
        xml += "</schema>";

        Document doc = KyouXmlUtils.load(new ByteArrayInputStream(xml.getBytes()));

        Assert.assertNotNull(KyouXmlUtils.selectElement(doc, "/schema/struct[@name='s1']"));
        Assert.assertNull(KyouXmlUtils.selectElement(doc, "/schema/struct[@name='s2']"));
        Assert.assertNull(KyouXmlUtils.selectElement(doc, "s2"));
    }

    @Test
    public void testSelectNodeEx() {
        String xml = "";
        xml += "<?xml version=\"1.0\"?>";
        xml += "<schema>";
        xml += "<struct name=\"s1\">";
        xml += "<field name=\"aaa\" >\r\n";
        xml += "    %s=%s";
        xml += "</field>\r\n";
        xml += "<field name=\"bbb\" />";
        xml += "</struct>";
        xml += "<field name=\"f1\">\r\n";
        xml += "<![CDATA[   \r\n";
        xml += "public final class AAA{\r\n";
        xml += "public void foo{System.out.println(\"hello world!\");}\r\n";
        xml += "}\r\n";
        xml += "]]>\r\n";
        xml += "</field>";
        xml += "</schema>";

        Document doc = KyouXmlUtils.load(new ByteArrayInputStream(xml.getBytes()));

        try {
            KyouXmlUtils.selectElement(doc, null);
        } catch (KyouException ex) {
            Assert.assertEquals(KyouErr.Base.Xml.EmptyXPath, ex.err);
        }
        try {
            KyouXmlUtils.selectElement(null, "/schema/field[@name='f1']");
        } catch (KyouException ex) {
            Assert.assertEquals(KyouErr.Base.Xml.EmptyXmlNode, ex.err);
        }

        try {
            KyouXmlUtils.selectElement(doc, "!!!syntax_error!!!");
        } catch (KyouException ex) {
            Assert.assertEquals(KyouErr.Base.Xml.XPathSyntaxError, ex.err);
        }
    }

    @Test
    public void testSelectText() {
        String xml = "";
        xml += "<?xml version=\"1.0\"?>";
        xml += "<schema>";
        xml += "<struct name=\"s1\">";
        xml += "<field name=\"aaa\" >\r\n";
        xml += "    %s=%s";
        xml += "</field>\r\n";
        xml += "<field name=\"bbb\" />";
        xml += "</struct>";
        xml += "<field name=\"f1\">\r\n";
        xml += "<![CDATA[   \r\n";
        xml += "public final class AAA{\r\n";
        xml += "public void foo{System.out.println(\"hello world!\");}\r\n";
        xml += "}\r\n";
        xml += "]]>\r\n";
        xml += "</field>";
        xml += "</schema>";

        Document doc = KyouXmlUtils.load(new ByteArrayInputStream(xml.getBytes()));

        Assert.assertEquals("s1", KyouXmlUtils.selectText(doc, "/schema/struct[@name='s1']/@name"));
        Assert.assertNull(KyouXmlUtils.selectText(doc, "/schema/struct[@name='s2']/@name"));
        Assert.assertEquals("%s=%s", KyouXmlUtils.selectText(doc, "/schema/struct/field[@name='aaa']"));
    }

    @Test
    public void testSelectTextEx() {
        String xml = "";
        xml += "<?xml version=\"1.0\"?>";
        xml += "<schema>";
        xml += "<struct name=\"s1\">";
        xml += "<field name=\"aaa\" />";
        xml += "<field name=\"bbb\" />";
        xml += "</struct>";
        xml += "<field name=\"f1\"/>";
        xml += "</schema>";

        Document doc = KyouXmlUtils.load(new ByteArrayInputStream(xml.getBytes()));

        try {
            KyouXmlUtils.selectText(doc, null);
        } catch (KyouException ex) {
            Assert.assertEquals(KyouErr.Base.Xml.EmptyXPath, ex.err);
        }
        try {
            KyouXmlUtils.selectText(null, "/schema/field");
        } catch (KyouException ex) {
            Assert.assertEquals(KyouErr.Base.Xml.EmptyXmlNode, ex.err);
        }

        try {
            KyouXmlUtils.selectText(doc, "!!!syntax_error!!!");
        } catch (KyouException ex) {
            Assert.assertEquals(KyouErr.Base.Xml.XPathSyntaxError, ex.err);
        }
    }

    @Test
    public void testSelectNodes() {
        String xml = "";
        xml += "<?xml version=\"1.0\"?>";
        xml += "<schema>";
        xml += "<struct name=\"s1\">";
        xml += "<field name=\"aaa\" />";
        xml += "<field name=\"bbb\" />";
        xml += "</struct>";
        xml += "<field name=\"f1\"/>";
        xml += "</schema>";

        Document doc = KyouXmlUtils.load(new ByteArrayInputStream(xml.getBytes()));

        Node[] nodes = KyouXmlUtils.selectElements(doc, "//field");
        Assert.assertEquals(3, nodes.length);
    }

    @Test
    public void testSelectNodesEx() {
        String xml = "";
        xml += "<?xml version=\"1.0\"?>";
        xml += "<schema>";
        xml += "<struct name=\"s1\">";
        xml += "<field name=\"aaa\" >\r\n";
        xml += "    %s=%s";
        xml += "</field>\r\n";
        xml += "<field name=\"bbb\" />";
        xml += "</struct>";
        xml += "<field name=\"f1\">\r\n";
        xml += "<![CDATA[   \r\n";
        xml += "public final class AAA{\r\n";
        xml += "public void foo{System.out.println(\"hello world!\");}\r\n";
        xml += "}\r\n";
        xml += "]]>\r\n";
        xml += "</field>";
        xml += "</schema>";

        Document doc = KyouXmlUtils.load(new ByteArrayInputStream(xml.getBytes()));

        try {
            KyouXmlUtils.selectElements(doc, null);
        } catch (KyouException ex) {
            Assert.assertEquals(KyouErr.Base.Xml.EmptyXPath, ex.err);
        }
        try {
            KyouXmlUtils.selectElements(null, "/schema/field[@name='f1']");
        } catch (KyouException ex) {
            Assert.assertEquals(KyouErr.Base.Xml.EmptyXmlNode, ex.err);
        }

        try {
            KyouXmlUtils.selectElements(doc, "!!!syntax_error!!!");
        } catch (KyouException ex) {
            Assert.assertEquals(KyouErr.Base.Xml.XPathSyntaxError, ex.err);
        }
    }

    @Test
    public void testXmlEncode() {
        Assert.assertEquals(null, KyouXmlUtils.xmlEncode(null));
        Assert.assertEquals("", KyouXmlUtils.xmlEncode(""));
        Assert.assertEquals("asdf", KyouXmlUtils.xmlEncode("asdf"));
        Assert.assertEquals("&lt;asdf&gt;", KyouXmlUtils.xmlEncode("<asdf>"));
        Assert.assertEquals("&quot;as'df&quot;", KyouXmlUtils.xmlEncode("\"as'df\""));
        Assert.assertEquals("工要在地一", KyouXmlUtils.xmlEncode("工要在地一"));
        Assert.assertEquals("as&amp;df", KyouXmlUtils.xmlEncode("as&df"));
    }
}
