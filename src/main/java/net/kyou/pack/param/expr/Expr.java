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

import java.nio.charset.Charset;

import net.kyou.exception.KyouErr;
import net.kyou.exception.KyouException;
import net.kyou.pack.PackContext;
import net.kyou.pack.param.Param;
import net.kyou.pack.param.expr.endpoint.EndpointExpr;
import net.kyou.pack.param.expr.pipe.PipeExpr;

import org.apache.log4j.Logger;

/**
 * 表示在参数中可以定义的表达式
 * <p>
 * 表达式用于将报文元素的有关信息通过指定的方式计算出来后传给相应的参数段以输出到报文流中。所有的表达式都向外输出一个值。 有两种表达式：端点表达式（{@link EndpointExpr}）和管道表达式（{@link PipeExpr}）
 * <li>端点表达式可以输出一个值</li>
 * <li>管道表达式可以接受一个值，进行一些处理之后将结果输出</li>
 * </p>
 * <p>
 * 表达式有着不同的返回类型和接受类型，参数也有各自的接受类型，需要确保类型匹配，否则会抛异常。<br/>
 * 特别的，可以使用数字字面量。数字字面量将作为一个返回类型为Integer的表达式参与运算。
 * </p>
 * <p>
 * 各种管道表达式可以在保证类型正确的前提下自由组合，组合方法是将表达式前后并列，以空格分隔。<br/>
 * 此时后面的表达式的计算结果将会传给前面的表达式。即计算顺序是从后往前的。<br/>
 * 端点表达式必须放在表达式序列的最后。
 * </p>
 * <p>
 * 例：
 * <li>n 表示求当前报文元素的名称</li>
 * <li>v 表示求当前报文元素的值</li>
 * <li>lens n 表示求当前报文元素的名称的长度</li>
 * <li>lenb s2b.utf8 v 表示求当前报文元素的值，并以utf8编码转成字节数组，并返回这个字节数组的长度</li>
 * </p>
 * 
 * @author nuclearg
 */
public abstract class Expr {
    private static final Logger logger = Logger.getLogger(Expr.class);
    
    /**
     * 该表达式隶属于的参数实例
     */
    public final Param param;
    /**
     * 表达式的本体
     */
    protected final String body;
    /**
     * 表达式的后缀 如果未提供后缀则为null
     */
    protected final String postfix;
    
    /**
     * 初始化表达式实例
     * <p>
     * 派生类在构造函数中必须调用此构造函数，以初始化一些基础信息
     * </p>
     * 
     * @param postfix
     *            后缀
     * @param param
     *            该表达式隶属于的参数
     * @param encoding
     *            编码
     */
    protected Expr(String postfix, Param param, Charset encoding) {
        this.body = this.getClass().getAnnotation(ExprTag.class).name();
        this.postfix = postfix;
        this.param = param;
        
        this.checkPostfix();
        logger.debug("Expr init ok. body: " + this.body + ", postfix: " + this.postfix);
    }
    
    /**
     * 计算该表达式
     * 
     * @param context
     *            组包上下文
     * @return 表达式的计算结果
     */
    public Object __eval(PackContext context) {
        return this.eval(context);
    }
    
    /**
     * 计算该表达式
     * 
     * @param context
     *            组包上下文
     * @return 该表达式的计算结果
     */
    protected abstract Object eval(PackContext context);
    
    /**
     * 派生类必须提供自定义的toString()实现
     */
    @Override
    public abstract String toString();
    
    /**
     * 检查为当前表达式提供的后缀是否符合表达式的声明
     */
    private void checkPostfix() {
        ExprTag tag = this.getClass().getAnnotation(ExprTag.class);
        switch (tag.postfix()) {
            case Empty:
                if (this.postfix != null)
                    throw new KyouException(KyouErr.StyleSpec.Expr.PostfixNotRequired, this.body + "." + this.postfix);
                break;
            case Required:
                if (this.postfix == null)
                    throw new KyouException(KyouErr.StyleSpec.Expr.PostfixRequired, this.body);
                break;
            case Dual:
                // 不做判断
                break;
            case RequiresInt:
                if (this.postfix == null)
                    throw new KyouException(KyouErr.StyleSpec.Expr.PostfixRequired, this.body);
                try {
                    int v = Integer.parseInt(this.postfix);
                    if (v < 0)
                        throw new KyouException(KyouErr.StyleSpec.Expr.PostfixRequiresPositiveInteger, this.body + "." + this.postfix);
                } catch (NumberFormatException ex) {
                    throw new KyouException(KyouErr.StyleSpec.Expr.PostfixRequiresPositiveInteger, this.body + "." + this.postfix);
                }
                break;
            case DualInt:
                if (this.postfix != null)
                    try {
                        int v = Integer.parseInt(this.postfix);
                        if (v < 0)
                            throw new KyouException(KyouErr.StyleSpec.Expr.PostfixRequiresPositiveInteger, this.body + "." + this.postfix);
                        
                    } catch (NumberFormatException ex) {
                        throw new KyouException(KyouErr.StyleSpec.Expr.PostfixRequiresPositiveInteger, this.body + "." + this.postfix);
                    }
                break;
        }
    }
    
    /**
     * 将表达式字符串解析为body和postfix两部分
     * 
     * @param expr
     *            表达式字符串
     * @return 一个包含两项的数组，第0项表示body，第1项表示postfix。如果没有postfix则第1项为null
     */
    static String[] parseBodyPostfix(String expr) {
        String[] parts = new String[2];
        if (expr.contains(".")) {
            int pos = expr.indexOf('.');
            parts[0] = expr.substring(0, pos);
            parts[1] = expr.substring(pos + 1);
        } else {
            parts[0] = expr;
            parts[1] = null;
        }
        
        return parts;
    }
}
