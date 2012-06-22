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
package net.kyou.pack;

import static net.kyou.ERR.assertError;

import java.io.ByteArrayInputStream;
import java.io.UnsupportedEncodingException;

import net.kyou.Kyou;
import net.kyou.data.DataDocument;
import net.kyou.data.SchemaDocument;
import net.kyou.exception.KyouErr;
import net.kyou.exception.KyouException;

import org.junit.Assert;
import org.junit.Test;

public class PackServiceTest {
    @Test
    public void testDocument() throws UnsupportedEncodingException {
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
    
    @Test
    public void testExBasic() throws UnsupportedEncodingException {
        try {
            PackService.__document(null, null, null, null);
        } catch (KyouException ex) {
            assertError(ex, KyouErr.Pack.EmptySchema);
        }
        try {
            PackService.__document(new SchemaDocument(), null, null, null);
        } catch (KyouException ex) {
            assertError(ex, KyouErr.Pack.EmptyData);
        }
        try {
            PackService.__document(new SchemaDocument(), new DataDocument(), null, null);
        } catch (KyouException ex) {
            assertError(ex, KyouErr.Pack.EmptySpecifcation);
        }
        try {
            StringBuilder xml = new StringBuilder();
            xml.append("<?xml version='1.0' encoding='utf-8'?>");
            xml.append("<spec>");
            xml.append("<config><encoding>utf-8</encoding></config>");
            xml.append("<style target='#'>");
            xml.append("<format>asdf%jkl;</format>");
            xml.append("<str>n</str>");
            xml.append("</style>");
            xml.append("<script name='test'>");
            xml.append("<![CDATA[");
            xml.append("");
            xml.append("]]>");
            xml.append("</script>");
            xml.append("</spec>");
            
            PackService.__document(new SchemaDocument(), new DataDocument(), Kyou.instance.loadStyle(new ByteArrayInputStream(xml.toString().getBytes("utf-8"))), null);
        } catch (KyouException ex) {
            assertError(ex, KyouErr.Base.Stream.NullOutputStream);
        }
    }
}
