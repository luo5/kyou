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
package net.kyou;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.UnsupportedEncodingException;

import net.kyou.data.DataDocument;
import net.kyou.data.SchemaDocument;
import net.kyou.pack.StyleSpecification;

import org.junit.Assert;
import org.junit.Test;

public class KyouTest {
    
    @Test
    public void testLoadSaveSchema() throws UnsupportedEncodingException {
        String xml = "<?xml version=\"1.0\" encoding=\"utf-8\"?><schema><struct name=\"a\"><field name=\"a1\"/></struct></schema>";
        
        SchemaDocument schema = Kyou.instance.loadSchema(new ByteArrayInputStream(xml.getBytes("utf-8")));
        
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        
        Kyou.instance.saveSchema(schema, out);
        
        Assert.assertArrayEquals(xml.getBytes("utf-8"), out.toByteArray());
    }
    
    @Test
    public void testLoadSaveData() throws UnsupportedEncodingException {
        String xml = "<?xml version=\"1.0\" encoding=\"utf-8\"?><data><a>3</a><b>4</b><c.a.b.c.d.e>12</c.a.b.c.d.e></data>";
        
        DataDocument data = Kyou.instance.loadData(new ByteArrayInputStream(xml.getBytes("utf-8")));
        
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        
        Kyou.instance.saveData(data, out);
        
        Assert.assertArrayEquals(xml.getBytes("utf-8"), out.toByteArray());
    }
    
    @Test
    public void testLoadStylePack() throws UnsupportedEncodingException {
        StringBuilder xml = new StringBuilder();
        xml.append("<?xml version='1.0' encoding='utf-8'?>");
        xml.append("<spec>");
        xml.append("<config><encoding>gb2312</encoding></config>");
        xml.append("<style target='#'>");
        xml.append("<format>我人有的和%主产不为这</format>");
        xml.append("<str>i2s 123</str>");
        xml.append("</style>");
        xml.append("</spec>");
        
        SchemaDocument schema = new SchemaDocument();
        DataDocument data = new DataDocument();
        
        StyleSpecification style = Kyou.instance.loadStyle(new ByteArrayInputStream(xml.toString().getBytes("utf-8")));
        byte[] bytes = Kyou.instance.pack(data, schema, style);
        
        Assert.assertArrayEquals("我人有的和123主产不为这".getBytes("gb2312"), bytes);
    }
}
