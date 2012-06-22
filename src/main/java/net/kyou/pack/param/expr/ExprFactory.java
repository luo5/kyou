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
import java.util.List;

import net.kyou.exception.KyouErr;
import net.kyou.exception.KyouException;
import net.kyou.pack.param.Param;
import net.kyou.pack.param.expr.endpoint.EndpointExpr;
import net.kyou.pack.param.expr.endpoint.EndpointExprFactory;
import net.kyou.pack.param.expr.pipe.PipeExpr;
import net.kyou.pack.param.expr.pipe.PipeExprFactory;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

/**
 * 表达式工厂
 * 
 * @author nuclearg
 * 
 */
public class ExprFactory {
    private static final Logger logger = Logger.getLogger(ExprFactory.class);
    
    /**
     * 端点表达式工厂
     */
    private final EndpointExprFactory endpointExorFactory;
    /**
     * 管道表达式工厂
     */
    private final PipeExprFactory pipeExorFactory;
    
    /**
     * 初始化一个表达式工厂实例
     * 
     * @param exprClasses
     *            用户自定义的表达式类型列表
     */
    public ExprFactory(List<Class<? extends EndpointExpr>> endpointExprClasses, List<Class<? extends PipeExpr>> pipeExprClasses) {
        // 构造EndpointExprFactory
        this.endpointExorFactory = new EndpointExprFactory(endpointExprClasses);
        // 构造PipeExprFactory
        this.pipeExorFactory = new PipeExprFactory(pipeExprClasses);
    }
    
    /**
     * 解析并创建一个表达式对象
     * 
     * @param expr
     *            表达式的字符串形式
     * @param param
     *            该表达式对象隶属于的参数实例
     * @param encoding
     *            报文整体编码
     * @return 创建好的表达式对象
     */
    public Expr create(String expr, Param param, Charset encoding) {
        logger.debug("Creating expr: " + expr);
        
        try {
            if (expr == null || (expr = expr.trim()).length() == 0)
                throw new KyouException(KyouErr.StyleSpec.Expr.EmptyExpr);
            
            // 用空格分隔参数表达式
            String[] exprs = StringUtils.split(expr);
            
            // 创建最后一个参数。这应当是一个端点参数
            Expr e;
            try {
                String[] parts = Expr.parseBodyPostfix(exprs[exprs.length - 1]);
                
                e = this.endpointExorFactory.create(parts[0], parts[1], param, encoding);
            } catch (Exception ex) {
                throw new KyouException(KyouErr.StyleSpec.Expr.InitExprFail, "expr: " + exprs[exprs.length - 1] + ", index: " + (exprs.length - 1), ex);
            }
            
            // 从后往前创建各个管道参数
            for (int i = exprs.length - 2; i >= 0; i--)
                try {
                    String[] parts = Expr.parseBodyPostfix(exprs[i]);
                    
                    e = this.pipeExorFactory.create(parts[0], parts[1], param, e, encoding);
                } catch (Exception ex) {
                    throw new KyouException(KyouErr.StyleSpec.Expr.InitExprFail, "expr: " + exprs[i] + ", index: " + i, ex);
                }
            
            // 创建完毕，返回Param实例
            logger.debug("Expr created. expr: " + e);
            return e;
        } catch (Throwable ex) {
            throw new KyouException(KyouErr.StyleSpec.Expr.InitExprFail, expr, ex);
        }
    }
}
