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
 * 将字符串转为整形的表达式
 * 
 * @author nuclearg
 */
@ExprTag(name = "i2s", postfix = ExprPostfix.DualInt, type = ExprType.String, require = ExprType.Integer)
class ConvertI2SExpr extends PipeExpr {
    /**
     * 进制
     */
    private final int radix;
    
    /**
     * 初始化一个用于将字符串转为整形的表达式
     * <p>
     * 可以加后缀，表示转换时采用的进制。默认为10进制
     * </p>
     * 
     * @param expr
     *            表达式的字符串形式
     * @param param
     *            该表达式隶属于的参数
     * @param arg
     *            作为该表达式的参数的表达式
     * @param encoding
     *            报文总体编码 不起任何作用
     */
    ConvertI2SExpr(String expr, Param param, Expr arg, Charset encoding) {
        super(expr, param, arg, encoding);
        
        // 判断是否提供了后缀
        if (this.postfix != null)
            // 如果提供了后缀，则尝试将其解释为转换时使用的进制
            try {
                this.radix = Integer.parseInt(this.postfix);
            } catch (Exception ex) {
                throw new KyouException(KyouErr.StyleSpec.Expr.InvalidExprPostfix, "expr: " + expr + ", radix: " + this.postfix);
            }
        else
            // 否则默认为10进制
            this.radix = 10;
        
        if (this.radix < Character.MIN_RADIX || this.radix > Character.MAX_RADIX)
            throw new KyouException(KyouErr.StyleSpec.Expr.InvalidExprPostfix, "expr: " + expr + ", radix: " + this.radix + " requires: " + Character.MIN_RADIX + "~" + Character.MAX_RADIX);
    }
    
    @Override
    protected Object eval(Object result, PackContext context) {
        return Long.toString((Long) result, this.radix);
    }
}
