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
import net.kyou.pack.param.expr.ExprTag.ExprType;

/**
 * 管道表达式
 * <p>
 * 这种类型的表达式接受并处理另外一个表达式的计算结果
 * </p>
 * 
 * @author nuclearg
 */
public abstract class PipeExpr extends Expr {
    /**
     * 作为该管道表达式的参数的表达式
     */
    private final Expr param;
    
    /**
     * 初始化一个管道表达式
     * 
     * @param expr
     *            管道表达式的字符串形式
     * @param param
     *            该管道表达式隶属于的参数
     * @param arg
     *            作为该管道表达式的参数的表达式
     * @param encodinge
     *            报文整体编码
     */
    protected PipeExpr(String expr, Param param, Expr arg, Charset encoding) {
        super(expr, param, encoding);
        
        // 检查参数的类型与需要的类型是否一致
        ExprType expect = this.getClass().getAnnotation(ExprTag.class).require();
        ExprType type = arg.getClass().getAnnotation(ExprTag.class).type();
        if (expect != ExprType.Irrelevant && type != expect)
            throw new KyouException(KyouErr.StyleSpec.Expr.InvalidExprResultType, "param:" + param + ", type: " + type + ", requires: " + expect);
        
        this.param = arg;
    }
    
    @Override
    protected Object eval(PackContext context) {
        Object result = this.param.__eval(context);
        return this.eval(result, context);
    }
    
    /**
     * 进一步处理前一个参数的计算结果
     * 
     * @param result
     *            前一个参数的计算结果
     * @param context
     *            组包上下文
     * @return 当前参数的计算结果
     */
    protected abstract Object eval(Object result, PackContext context);
    
    @Override
    public String toString() {
        return (this.postfix == null ? this.body : this.body + "." + this.postfix) + " " + this.param.toString();
    }
}
