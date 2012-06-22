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

import java.lang.reflect.Field;
import java.nio.charset.Charset;
import java.util.ArrayList;

import net.kyou.exception.KyouErr;
import net.kyou.exception.KyouException;
import net.kyou.pack.UTILS;
import net.kyou.pack.param.expr.ExprFactory;
import net.kyou.util.KyouByteOutputStream;

import org.junit.Assert;
import org.junit.Test;

public class IntegerParamTest {
    @Test
    public void test() {
        // 0(0) = 0(16)
        test(0L, "\\00\\00\\00\\00\\00\\00\\00\\00", "\\00\\00\\00\\00\\00\\00\\00\\00");
        
        // 1(10) = 1(16)
        test(1L, "\\00\\00\\00\\00\\00\\00\\00\\01", "\\01\\00\\00\\00\\00\\00\\00\\00");
        
        // 123(10) = 7b(16)
        test(123L, "\\00\\00\\00\\00\\00\\00\\00\\7b", "\\7b\\00\\00\\00\\00\\00\\00\\00");
        
        // 1234(10) = 04d2(16)
        test(1234L, "\\00\\00\\00\\00\\00\\00\\04\\d2", "\\d2\\04\\00\\00\\00\\00\\00\\00");
    }
    
    @Test
    public void testEx() throws SecurityException, NoSuchFieldException, IllegalArgumentException, IllegalAccessException {
        TEST.testEx("<int>123</int>", KyouErr.StyleSpec.Style.ParamCreateFail, KyouErr.StyleSpec.Style.InvalidSegmentParam);
        TEST.testEx("<int len=''>123</int>", KyouErr.StyleSpec.Style.ParamCreateFail, KyouErr.StyleSpec.Style.InvalidSegmentParam);
        TEST.testEx("<int len='asdf'>123</int>", KyouErr.StyleSpec.Style.ParamCreateFail, KyouErr.StyleSpec.Style.InvalidSegmentParam);
        TEST.testEx("<int len='12345'>123</int>", KyouErr.StyleSpec.Style.ParamCreateFail, KyouErr.StyleSpec.Style.InvalidSegmentParam);
        
        Param segment = new ParamFactory(new ArrayList<Class<? extends Param>>()).create(UTILS.prepareElement("<int len='2'>123</int>"), Charset.forName("utf-8"), null, new ExprFactory(null,null));
        Field f = segment.getClass().getDeclaredField("len");
        f.setAccessible(true);
        f.set(segment, 12345);
        
        try {
            segment.export(null, new KyouByteOutputStream());
            Assert.fail();
        } catch (KyouException ex) {
            Assert.assertEquals(KyouErr.Unexpected, ex.err);
        }
    }
    
    private void test(long v, String expectBig8, String expectSmall8) {
        TEST.test("<int len='1'>" + v + "</int>", expectSmall8.substring(0, 3));
        TEST.test("<int len='1' endian='big'>" + v + "</int>", expectBig8.substring(21));
        TEST.test("<int len='1' endian='small'>" + v + "</int>", expectSmall8.substring(0, 3));
        
        TEST.test("<int len='2'>" + v + "</int>", expectSmall8.substring(0, 6));
        TEST.test("<int len='2' endian='big'>" + v + "</int>", expectBig8.substring(18));
        TEST.test("<int len='2' endian='small'>" + v + "</int>", expectSmall8.substring(0, 6));
        
        TEST.test("<int len='4'>" + v + "</int>", expectSmall8.substring(0, 12));
        TEST.test("<int len='4' endian='big'>" + v + "</int>", expectBig8.substring(12));
        TEST.test("<int len='4' endian='small'>" + v + "</int>", expectSmall8.substring(0, 12));
        
        TEST.test("<int len='8'>" + v + "</int>", expectSmall8);
        TEST.test("<int len='8' endian='big'>" + v + "</int>", expectBig8);
        TEST.test("<int len='8' endian='small'>" + v + "</int>", expectSmall8);
    }
}