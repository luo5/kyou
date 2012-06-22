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
package net.kyou.pack.param;

import java.nio.charset.Charset;
import java.util.ArrayList;

import net.kyou.exception.KyouErr;
import net.kyou.exception.KyouException;
import net.kyou.pack.UTILS;
import net.kyou.pack.param.expr.ExprFactory;

import org.junit.Assert;
import org.junit.Test;

public class ParamTest {
    
    @Test
    public void test() {
        try {
            String xml = "<null>n</null>";
            
            Param segment = new ParamFactory(new ArrayList<Class<? extends Param>>()).create(UTILS.prepareElement(xml), Charset.forName("utf-8"), null, new ExprFactory(null,null));
            
            Assert.assertTrue(segment instanceof NullParam);
        } finally {
        }
        
        try {
            String xml = "<str>n</str>";
            
            Param segment = new ParamFactory(new ArrayList<Class<? extends Param>>()).create(UTILS.prepareElement(xml), Charset.forName("utf-8"), null, new ExprFactory(null,null));
            
            Assert.assertTrue(segment instanceof StringParam);
        } finally {
        }
    }
    
    @Test
    public void testEx() {
        try {
            String xml = "<xxx>n</xxx>";
            
            new ParamFactory(new ArrayList<Class<? extends Param>>()).create(UTILS.prepareElement(xml), Charset.forName("utf-8"), null, new ExprFactory(null,null));
            
            Assert.fail();
        } catch (KyouException ex) {
            Assert.assertEquals(KyouErr.StyleSpec.Style.ParamCreateFail, ex.err);
            Assert.assertEquals(KyouErr.StyleSpec.Style.InvalidParamTag, ((KyouException) ex.cause).err);
        }
        
        try {
            String xml = "<str>aaa</str>";
            
            new ParamFactory(new ArrayList<Class<? extends Param>>()).create(UTILS.prepareElement(xml), Charset.forName("utf-8"), null, new ExprFactory(null,null));
            
            Assert.fail();
        } catch (KyouException ex) {
            Assert.assertEquals(KyouErr.StyleSpec.Style.ParamCreateFail, ex.err);
            Assert.assertEquals(KyouErr.StyleSpec.Expr.InitExprFail, ((KyouException) ex.cause).err);
        }
    }
}
