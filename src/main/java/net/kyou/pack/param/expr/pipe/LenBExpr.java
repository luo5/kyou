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
package net.kyou.pack.param.expr.pipe;

import java.nio.charset.Charset;

import net.kyou.pack.PackContext;
import net.kyou.pack.param.Param;
import net.kyou.pack.param.expr.Expr;
import net.kyou.pack.param.expr.ExprTag;
import net.kyou.pack.param.expr.ExprTag.ExprPostfix;
import net.kyou.pack.param.expr.ExprTag.ExprType;

/**
 * 求字节数组的长度
 * 
 * @author nuclearg
 */
@ExprTag(name = "lenb", postfix = ExprPostfix.Empty, type = ExprType.Integer, require = ExprType.Bytes)
class LenBExpr extends PipeExpr {
    LenBExpr(String expr, Param segment, Expr arg, Charset encoding) {
        super(expr, segment, arg, encoding);
    }

    @Override
    protected Object eval(Object result, PackContext context) {
        return ((byte[]) result).length;
    }
}
