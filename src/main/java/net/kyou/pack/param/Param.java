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

import net.kyou.exception.KyouErr;
import net.kyou.exception.KyouException;
import net.kyou.pack.PackContext;
import net.kyou.pack.StyleItem;
import net.kyou.pack.param.expr.Expr;
import net.kyou.pack.param.expr.ExprFactory;
import net.kyou.pack.param.expr.ExprTag;
import net.kyou.pack.param.expr.ExprTag.ExprType;
import net.kyou.util.KyouByteOutputStream;

import org.w3c.dom.Element;

/**
 * 定义样式的参数
 * 
 * @author nuclearg
 */
public abstract class Param {
    /**
     * 本参数隶属于的组包样式单元
     */
    public final StyleItem style;
    /**
     * 本参数的表达式
     */
    private final Expr expr;
    
    /**
     * 初始化一个参数
     * 
     * @param e
     *            XML元素
     * @param encoding
     *            整篇报文的编码
     * @param style
     *            本段隶属于的组包样式单元
     * @param exprFactory
     *            表达式工厂
     */
    protected Param(Element e, Charset encoding, StyleItem style, ExprFactory exprFactory) {
        this.style = style;
        this.expr = exprFactory.create(e.getTextContent(), this, encoding);
        
        // 检查表达式的计算结果类型是否与参数期望期望的类型吻合
        ExprType type = expr.getClass().getAnnotation(ExprTag.class).type();
        
        ParamTag tag = this.getClass().getAnnotation(ParamTag.class);
        ExprType expect = tag.type();
        
        if (expect != ExprType.Irrelevant && type != expect)
            throw new KyouException(KyouErr.StyleSpec.Expr.InvalidExprResultType, "expr:" + expr + ", type: " + type + ", requires: " + expect);
    }
    
    /**
     * 将参数的结果输出到流中
     * 
     * @param v
     *            参数的计算结果
     * @param context
     *            组包上下文
     */
    protected abstract void export(Object v, PackContext context, KyouByteOutputStream s);
    
    public void export(PackContext context, KyouByteOutputStream s) {
        this.export(this.expr.__eval(context), context, s);
    }
}
