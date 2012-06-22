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

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import net.kyou.exception.KyouErr;
import net.kyou.exception.KyouException;
import net.kyou.pack.param.Param;
import net.kyou.pack.param.expr.Expr;
import net.kyou.pack.param.expr.ExprTag;
import net.kyou.util.KyouComponentFactory;

import org.apache.log4j.Logger;

/**
 * 管道表达式工厂
 * 
 * @author nuclearg
 * 
 */
public class PipeExprFactory {
    private static final Logger logger = Logger.getLogger(PipeExprFactory.class);
    
    /**
     * Kyou默认的各种管道表达式类型列表
     */
    private static final List<Class<? extends PipeExpr>> classes;
    static {
        List<Class<? extends PipeExpr>> _classes = new ArrayList<Class<? extends PipeExpr>>();
        
        _classes.add(ConvertB2SExpr.class);
        _classes.add(ConvertI2SExpr.class);
        _classes.add(ConvertS2BExpr.class);
        _classes.add(ConvertS2IExpr.class);
        
        _classes.add(LenBExpr.class);
        _classes.add(LenSExpr.class);
        
        classes = Collections.unmodifiableList(_classes);
    }
    
    /**
     * 实际使用的管道表达式工厂实现
     */
    private final PipeExprFactoryImpl factory;
    
    /**
     * 初始化一个管道表达式工厂实例
     * 
     * @param pipeExprClasses
     *            用户自定义的管道表达式类型列表
     */
    public PipeExprFactory(List<Class<? extends PipeExpr>> pipeExprClasses) {
        if (pipeExprClasses == null)
            pipeExprClasses = Collections.emptyList();
        if (!pipeExprClasses.isEmpty())
            logger.debug("User defined pipe exprs: " + pipeExprClasses);
        
        // 构造一个包含了默认类型和用户自定义类型的管道表达式类型列表
        List<Class<? extends PipeExpr>> list = new ArrayList<Class<? extends PipeExpr>>();
        
        // 把默认类型放进来
        list.addAll(classes);
        // 把用户自定义类型列表放进来
        list.addAll(pipeExprClasses);
        
        this.factory = new PipeExprFactoryImpl(list);
    }
    
    /**
     * 创建一个管道表达式实例
     * 
     * @param body
     *            管道表达式的名称
     * @param postfix
     *            管道表达式的后缀
     * @param param
     *            该表达式隶属于的参数实例
     * @param arg
     *            该管道表达式的参数
     * @param encoding
     *            报文整体编码
     * @return 一个创建好的管道表达式实例
     */
    public Expr create(String body, String postfix, Param param, Expr arg, Charset encoding) {
        try {
            logger.debug("Create pipe expr. body: " + body + ", postfix: " + postfix);
            
            PipeExpr expr = this.factory.create(body, postfix, param, arg, encoding);
            
            logger.debug("Pipe expr created. name: " + param + ", expr: " + expr);
            return expr;
        } catch (Throwable ex) {
            throw new KyouException(KyouErr.StyleSpec.Style.ParamCreateFail, ex);
        }
    }
    
    /**
     * 实际的管道表达式工厂
     * 
     * @author nuclearg
     * 
     */
    private class PipeExprFactoryImpl extends KyouComponentFactory<PipeExpr> {
        /**
         * 初始化一个管道表达式工厂
         * 
         * @param classes
         *            该工厂支持的管道表达式类型列表
         */
        protected PipeExprFactoryImpl(List<Class<? extends PipeExpr>> classes) {
            super(classes);
        }
        
        @Override
        public PipeExpr create(String name, Object... args) throws Throwable {
            // 找到名称对应的表达式类型
            Class<? extends PipeExpr> cls = super.select(name);
            if (cls == null)
                throw new KyouException(KyouErr.StyleSpec.Expr.InvalidPipeExprTag, name);
            
            // 创建表达式实例
            try {
                Constructor<? extends PipeExpr> constructor = cls.getDeclaredConstructor(String.class, Param.class, Expr.class, Charset.class);
                constructor.setAccessible(true);
                return constructor.newInstance(args);
            } catch (InvocationTargetException ex) {
                throw ex.getCause();
            }
        }
        
        @Override
        protected String getName(Class<? extends PipeExpr> cls) {
            ExprTag tag = cls.getAnnotation(ExprTag.class);
            
            // 检查是否有ExprTag
            if (tag == null)
                throw new KyouException(KyouErr.Init.ExprTagNotFound, cls.getName());
            
            // 检查构造函数签名
            try {
                cls.getDeclaredConstructor(String.class, Param.class, Expr.class, Charset.class);
            } catch (Exception ex) {
                throw new KyouException(KyouErr.Init.ExprConstructorMismatch, cls.getName());
            }
            
            // 返回表达式的名称
            return tag.name();
        }
    }
    
}
