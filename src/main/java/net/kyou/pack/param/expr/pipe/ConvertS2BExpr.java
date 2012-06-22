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
 * 将字节数组转为字符串的表达式
 * 
 * @author nuclearg
 */
@ExprTag(name = "s2b", postfix = ExprPostfix.Dual, type = ExprType.Bytes, require = ExprType.String)
class ConvertS2BExpr extends PipeExpr {
    /**
     * 转换时使用的编码
     */
    private final Charset encoding;

    ConvertS2BExpr(String expr, Param segment, Expr arg, Charset encoding) {
        super(expr, segment, arg, encoding);

        if (this.postfix != null)
            try {
                this.encoding = Charset.forName(this.postfix);
            } catch (Exception ex) {
                throw new KyouException(KyouErr.StyleSpec.Expr.InvalidExprPostfix, "encoding: " + this.postfix);
            }
        else
            this.encoding = encoding;
    }

    @Override
    protected Object eval(Object result, PackContext context) {
        return new String((byte[]) result, this.encoding);
    }
}
