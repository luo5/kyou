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

import org.junit.Assert;
import org.junit.Test;

public class PlainIntegerExprTest {
    @Test
    public void test() {
        try {
            String expr = "1";
            PlainIntegerExpr param = new PlainIntegerExpr(expr, null, null);
            Assert.assertEquals(1L, param.__eval(null));
        } finally {
        }

        try {
            String expr = "-1";
            PlainIntegerExpr param = new PlainIntegerExpr(expr, null, null);
            Assert.assertEquals(-1L, param.__eval(null));
        } finally {
        }

        try {
            String expr = "1000000";
            PlainIntegerExpr param = new PlainIntegerExpr(expr, null, null);
            Assert.assertEquals(1000000L, param.__eval(null));
        } finally {
        }
    }

    @Test
    public void testEx() {
        try {
            new PlainIntegerExpr("a", null, null);
            Assert.fail();
        } catch (NumberFormatException ex) {
        }

        try {
            new PlainIntegerExpr("1111111111111111111111", null, null);
            Assert.fail();
        } catch (NumberFormatException ex) {
        }
    }
}
