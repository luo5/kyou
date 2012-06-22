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

import junit.framework.Assert;
import net.kyou.exception.KyouErr;
import net.kyou.exception.KyouException;
import net.kyou.pack.param.ParamFactory;
import net.kyou.pack.param.expr.ExprFactory;

import org.junit.Test;

public class StyleItemTest {
    StyleSpecification spec = new StyleSpecification(new ByteArrayInputStream("<?xml version=\"1.0\" encoding=\"utf-8\"?><spec><config><encoding>utf-8</encoding></config></spec>".getBytes()), new ParamFactory(null), new ExprFactory(null,null));
    
    @Test
    public void test() {
        String xml = "<style target=\"#\"><format>asdf</format></style>";
        
        StyleItem style = new StyleItem(UTILS.prepareElement(xml), spec, new ParamFactory(null), new ExprFactory(null,null));
        
        Assert.assertEquals(1, style.segments.size());
    }
    
    @Test
    public void test2() {
        
        String xml = "<style target=\"#\">\r\n<format>asdf%jkl;</format>\r\n<str>n</str>\r\n</style>";
        
        StyleItem style = new StyleItem(UTILS.prepareElement(xml), spec, new ParamFactory(null), new ExprFactory(null,null));
        
        Assert.assertEquals(3, style.segments.size());
    }
    
    @Test
    public void test3() {
        String xml = "<style target=\"#\"><format><![CDATA[<%>%</%>]]></format><str>n</str><str>v</str><str>n</str></style>";
        
        StyleItem style = new StyleItem(UTILS.prepareElement(xml), spec, new ParamFactory(null), new ExprFactory(null,null));
        
        Assert.assertEquals(7, style.segments.size());
    }
    
    @Test
    public void testEx() {
        
        try {
            String xml = "<style><format>asdf</format><str>n</str><str>v</str><str>n</str></style>";
            
            new StyleItem(UTILS.prepareElement(xml), spec, new ParamFactory(null), new ExprFactory(null,null));
            Assert.fail();
        } catch (KyouException ex) {
            Assert.assertEquals(KyouErr.StyleSpec.Style.EmptyTarget, ex.err);
        }
        
        try {
            String xml = "<style target=\"#\"><str>n</str><str>v</str><str>n</str></style>";
            
            new StyleItem(UTILS.prepareElement(xml), spec, new ParamFactory(null), new ExprFactory(null,null));
            Assert.fail();
        } catch (KyouException ex) {
            Assert.assertEquals(KyouErr.StyleSpec.Style.EmptyFormat, ex.err);
        }
        
        try {
            String xml = "<style target=\"#\"><format></format><str>n</str><str>v</str><str>n</str></style>";
            
            new StyleItem(UTILS.prepareElement(xml), spec, new ParamFactory(null), new ExprFactory(null,null));
            Assert.fail();
        } catch (KyouException ex) {
            Assert.assertEquals(KyouErr.StyleSpec.Style.EmptyFormat, ex.err);
        }
        
        try {
            String xml = "<style target=\"#\"><format>asdf</format><xxx>n</xxx></style>";
            
            new StyleItem(UTILS.prepareElement(xml), spec, new ParamFactory(null), new ExprFactory(null,null));
            Assert.fail();
        } catch (KyouException ex) {
            Assert.assertEquals(KyouErr.StyleSpec.Style.ParamCreateFail, ex.err);
        }
        
        try {
            String xml = "<style target=\"#\"><format>%%%%%</format><str>n</str><str>v</str><str>n</str></style>";
            
            new StyleItem(UTILS.prepareElement(xml), spec, new ParamFactory(null), new ExprFactory(null, null));
            Assert.fail();
        } catch (KyouException ex) {
            Assert.assertEquals(KyouErr.StyleSpec.Style.InsufficientParams, ex.err);
        }
    }
}
