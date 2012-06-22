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
import net.kyou.util.KyouByteOutputStream;
import net.kyou.util.KyouFormatString;

import org.junit.Assert;

class TEST {
    public static void test(String xml, String expect) {
        Param param = new ParamFactory(new ArrayList<Class<? extends Param>>()).create(UTILS.prepareElement(xml), Charset.forName("utf-8"), null, new ExprFactory(null, null));
        
        KyouByteOutputStream s = new KyouByteOutputStream();
        
        param.export(null, s);
        
        Assert.assertArrayEquals(new KyouFormatString(expect, Charset.forName("utf-8")).segment(0), s.export());
    }
    
    public static void test(String xml, String expect, String encoding) {
        Param param = new ParamFactory(new ArrayList<Class<? extends Param>>()).create(UTILS.prepareElement(xml), Charset.forName("utf-8"), null, new ExprFactory(null, null));
        
        KyouByteOutputStream s = new KyouByteOutputStream();
        
        param.export(null, s);
        
        Assert.assertArrayEquals(new KyouFormatString(expect, Charset.forName(encoding)).segment(0), s.export());
    }
    
    public static void testEx(String xml, KyouErr... errs) {
        try {
            new ParamFactory(new ArrayList<Class<? extends Param>>()).create(UTILS.prepareElement(xml), Charset.forName("utf-8"), null, new ExprFactory(null, null));
        } catch (KyouException ex) {
            for (KyouErr err : errs) {
                Assert.assertEquals(err, ex.err);
                ex = (KyouException) ex.cause;
            }
        }
    }
}
