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
package net.kyou.pack.param.expr.endpoint;

import net.kyou.pack.param.expr.Expr;

import org.junit.Assert;
import org.junit.Test;

public class EndpointExprTest {
    @Test
    public void test() {
        String body;
        String postfix;
        Expr expr;
        
        body = "1";
        postfix = null;
        expr = new EndpointExprFactory(null).create(body, postfix, null, null);
        Assert.assertTrue(expr instanceof PlainIntegerExpr);
        
        body = "n";
        postfix = null;
        expr = new EndpointExprFactory(null).create(body, postfix, null, null);
        Assert.assertTrue(expr instanceof NameExpr);
        
        body = "v";
        postfix = null;
        expr = new EndpointExprFactory(null).create(body, postfix, null, null);
        Assert.assertTrue(expr instanceof ValueExpr);
        
        body = "m";
        postfix = null;
        expr = new EndpointExprFactory(null).create(body, postfix, null, null);
        Assert.assertTrue(expr instanceof MemberExpr);
    }
}
