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
package net.kyou.pack.param.expr;

import net.kyou.ERR;
import net.kyou.exception.KyouErr;
import net.kyou.exception.KyouException;
import net.kyou.pack.param.expr.endpoint.EndpointExpr;
import net.kyou.pack.param.expr.pipe.PipeExpr;

import org.junit.Assert;
import org.junit.Test;

public class ExprTest {
    @Test
    public void test() {
        String expr;
        Expr param;
        
        expr = "1";
        param = new ExprFactory(null, null).create(expr, null, null);
        Assert.assertTrue(param instanceof EndpointExpr);
        
        expr = "n";
        param = new ExprFactory(null, null).create(expr, null, null);
        Assert.assertTrue(param instanceof EndpointExpr);
        
        expr = "i2s 1";
        param = new ExprFactory(null, null).create(expr, null, null);
        Assert.assertTrue(param instanceof PipeExpr);
        Assert.assertEquals("1", param.__eval(null));
        
        expr = "i2s.16 15";
        param = new ExprFactory(null, null).create(expr, null, null);
        Assert.assertTrue(param instanceof PipeExpr);
        Assert.assertEquals("f", param.__eval(null));
        
        expr = "lens i2s 123456";
        param = new ExprFactory(null, null).create(expr, null, null);
        Assert.assertTrue(param instanceof PipeExpr);
        Assert.assertEquals(6, param.__eval(null));
    }
    
    @Test
    public void testEx() {
        try {
            new ExprFactory(null, null).create(null, null, null);
            Assert.fail();
        } catch (KyouException ex) {
            ERR.assertError(ex, KyouErr.StyleSpec.Expr.InitExprFail, KyouErr.StyleSpec.Expr.EmptyExpr);
        }
        try {
            new ExprFactory(null, null).create("", null, null);
            Assert.fail();
        } catch (KyouException ex) {
            ERR.assertError(ex, KyouErr.StyleSpec.Expr.InitExprFail, KyouErr.StyleSpec.Expr.EmptyExpr);
        }
        try {
            new ExprFactory(null, null).create("1-1", null, null);
            Assert.fail();
        } catch (KyouException ex) {
            ERR.assertError(ex, KyouErr.StyleSpec.Expr.InitExprFail);
        }
        try {
            new ExprFactory(null, null).create("xxx", null, null);
            Assert.fail();
        } catch (KyouException ex) {
            ERR.assertError(ex, KyouErr.StyleSpec.Expr.InitExprFail);
        }
        try {
            new ExprFactory(null, null).create("xxx 123", null, null);
            Assert.fail();
        } catch (KyouException ex) {
            ERR.assertError(ex, KyouErr.StyleSpec.Expr.InitExprFail);
        }
    }
}
