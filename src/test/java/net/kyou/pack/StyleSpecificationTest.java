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

import java.io.ByteArrayInputStream;
import java.nio.charset.Charset;

import net.kyou.ERR;
import net.kyou.exception.KyouErr;
import net.kyou.exception.KyouException;
import net.kyou.pack.param.ParamFactory;
import net.kyou.pack.param.expr.ExprFactory;

import org.junit.Assert;
import org.junit.Test;

/**
 * 定义报文样式
 * 
 * @author nuclearg
 */
public class StyleSpecificationTest {
    @Test
    public void test() {
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
        
        StyleSpecification spec = new StyleSpecification(new ByteArrayInputStream(xml.toString().getBytes()), new ParamFactory(null), new ExprFactory(null, null));
        
        Assert.assertEquals(Charset.forName("utf-8"), spec.config.encoding);
        Assert.assertEquals(1, spec.styles.size());
        Assert.assertEquals("#[]", spec.styles.get(0).target.toString());
        Assert.assertEquals(3, spec.styles.get(0).segments.size());
        Assert.assertEquals(1, spec.scripts.size());
        Assert.assertEquals("test", spec.scripts.get(0).name);
    }
    
    @Test
    public void testEx() {
        try {
            new StyleSpecification(null, null, null);
            Assert.fail();
        } catch (KyouException ex) {
            ERR.assertError(ex, KyouErr.StyleSpec.SpecificationParseFail, KyouErr.Base.Stream.NullInputStream);
        }
        try {
            new StyleSpecification(new ByteArrayInputStream("".getBytes()), null, null);
            Assert.fail();
        } catch (KyouException ex) {
            ERR.assertError(ex, KyouErr.StyleSpec.SpecificationParseFail, KyouErr.StyleSpec.EmptyParamFactory);
        }
        try {
            new StyleSpecification(new ByteArrayInputStream("".getBytes()), new ParamFactory(null), null);
            Assert.fail();
        } catch (KyouException ex) {
            ERR.assertError(ex, KyouErr.StyleSpec.SpecificationParseFail, KyouErr.StyleSpec.EmptyExprFactory);
        }
        
        try {
            StringBuilder xml = new StringBuilder();
            xml.append("<?xml version='1.0' encoding='utf-8'?>");
            xml.append("<spec>");
            xml.append("<config></config>");
            xml.append("</spec>");
            
            new StyleSpecification(new ByteArrayInputStream(xml.toString().getBytes()), new ParamFactory(null), new ExprFactory(null, null));
            Assert.fail();
        } catch (KyouException ex) {
            ERR.assertError(ex, KyouErr.StyleSpec.SpecificationParseFail, KyouErr.Base.UnsupportedCharset);
        }
        try {
            StringBuilder xml = new StringBuilder();
            xml.append("<?xml version='1.0' encoding='utf-8'?>");
            xml.append("<spec>");
            xml.append("<config><encoding></encoding></config>");
            xml.append("</spec>");
            
            new StyleSpecification(new ByteArrayInputStream(xml.toString().getBytes()), new ParamFactory(null), new ExprFactory(null, null));
            Assert.fail();
        } catch (KyouException ex) {
            ERR.assertError(ex, KyouErr.StyleSpec.SpecificationParseFail, KyouErr.Base.UnsupportedCharset);
        }try {
            StringBuilder xml = new StringBuilder();
            xml.append("<?xml version='1.0' encoding='utf-8'?>");
            xml.append("<spec>");
            xml.append("<config><encoding>xxx</encoding></config>");
            xml.append("</spec>");
            
            new StyleSpecification(new ByteArrayInputStream(xml.toString().getBytes()), new ParamFactory(null), new ExprFactory(null, null));
            Assert.fail();
        } catch (KyouException ex) {
            ERR.assertError(ex, KyouErr.StyleSpec.SpecificationParseFail, KyouErr.Base.UnsupportedCharset);
        }
    }
}
