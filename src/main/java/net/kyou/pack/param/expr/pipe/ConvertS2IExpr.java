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

import net.kyou.exception.KyouErr;
import net.kyou.exception.KyouException;
import net.kyou.pack.PackContext;
import net.kyou.pack.param.Param;
import net.kyou.pack.param.expr.Expr;
import net.kyou.pack.param.expr.ExprTag;
import net.kyou.pack.param.expr.ExprTag.ExprPostfix;
import net.kyou.pack.param.expr.ExprTag.ExprType;

/**
 * 将整形转为字符串
 * 
 * @author nuclearg
 */
@ExprTag(name = "s2i", postfix = ExprPostfix.DualInt, type = ExprType.Integer, require = ExprType.String)
class ConvertS2IExpr extends PipeExpr {
    /**
     * 进制 默认为十进制
     */
    private final int radix;

    ConvertS2IExpr(String expr, Param segment, Expr arg, Charset encoding) {
        super(expr, segment, arg, encoding);

        if (this.postfix == null)
            this.radix = 10;
        else
            this.radix = Integer.parseInt(this.postfix);

        if (this.radix < Character.MIN_RADIX || this.radix > Character.MAX_RADIX)
            throw new KyouException(KyouErr.StyleSpec.Expr.InvalidExprPostfix, "radix: " + this.radix + " expect: " + Character.MIN_RADIX + "~" + Character.MAX_RADIX);
    }

    @Override
    protected Object eval(Object result, PackContext context) {
        return Integer.parseInt((String) result, radix);
    }
}
