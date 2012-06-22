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

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import net.kyou.exception.KyouErr;
import net.kyou.exception.KyouException;
import net.kyou.pack.param.Param;
import net.kyou.pack.param.expr.ExprTag;
import net.kyou.util.KyouComponentFactory;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

/**
 * 端点表达式工厂
 * 
 * @author nuclearg
 * 
 */
public class EndpointExprFactory {
    private static final Logger logger = Logger.getLogger(EndpointExprFactory.class);
    
    /**
     * Kyou默认的各种端点表达式类型列表
     */
    private static final List<Class<? extends EndpointExpr>> classes;
    static {
        List<Class<? extends EndpointExpr>> _classes = new ArrayList<Class<? extends EndpointExpr>>();
        
        _classes.add(PlainTextExpr.class);
        
        _classes.add(NameExpr.class);
        _classes.add(ValueExpr.class);
        _classes.add(MemberExpr.class);
        
        _classes.add(RefExpr.class);
        
        classes = Collections.unmodifiableList(_classes);
    }
    
    /**
     * 实际使用的端点表达式工厂实现
     */
    private final EndpointExprFactoryImpl factory;
    
    /**
     * 初始化一个端点表达式工厂实例
     * 
     * @param pipeExprClasses
     *            用户自定义的端点表达式类型列表
     */
    public EndpointExprFactory(List<Class<? extends EndpointExpr>> endpointExprClasses) {
        if (endpointExprClasses == null)
            endpointExprClasses = Collections.emptyList();
        if (!endpointExprClasses.isEmpty())
            logger.debug("User defined pipe exprs: " + endpointExprClasses);
        
        // 构造一个包含了默认类型和用户自定义类型的端点表达式类型列表
        List<Class<? extends EndpointExpr>> list = new ArrayList<Class<? extends EndpointExpr>>();
        
        // 把默认类型放进来
        list.addAll(classes);
        // 把用户自定义类型列表放进来
        list.addAll(endpointExprClasses);
        
        this.factory = new EndpointExprFactoryImpl(list);
    }
    
    /**
     * 创建一个端点表达式实例
     * 
     * @param body
     *            端点表达式的名称
     * @param postfix
     *            端点表达式的后缀
     * @param param
     *            该表达式隶属于的参数实例
     * @param encoding
     *            报文整体编码
     * @return 一个创建好的端点表达式实例
     */
    public EndpointExpr create(String body, String postfix, Param param, Charset encoding) {
        try {
            logger.debug("Creating endpoint expr. body: " + body + ", postfix: " + postfix);
            
            // 首先看一下是不是数字字面量
            if (postfix == null)
                // 如果body都是由数字组成的，
                // 或者body的长度大于1,并且第一个字符是负号，且后面的字符都是由数字组成的
                // 则尝试将其解释为一个整数字面量
                if (StringUtils.isNumeric(body) || (body.length() > 1 && body.charAt(0) == '-' && StringUtils.isNumeric(body.substring(1))))
                    return new PlainIntegerExpr(body, param, encoding);
            
            // 根据body创建表达式实例
            EndpointExpr expr = this.factory.create(body, postfix, param, encoding);
            
            logger.debug("Endpoint expr created. name: " + param + ", expr: " + expr);
            return expr;
        } catch (Throwable ex) {
            throw new KyouException(KyouErr.StyleSpec.Style.ParamCreateFail, ex);
        }
    }
    
    /**
     * 实际的端点表达式工厂
     * 
     * @author nuclearg
     * 
     */
    private class EndpointExprFactoryImpl extends KyouComponentFactory<EndpointExpr> {
        /**
         * 初始化一个端点表达式工厂
         * 
         * @param classes
         *            该工厂支持的端点表达式类型列表
         */
        protected EndpointExprFactoryImpl(List<Class<? extends EndpointExpr>> classes) {
            super(classes);
        }
        
        @Override
        public EndpointExpr create(String name, Object... args) throws Throwable {
            // 找到名称对应的表达式类型
            Class<? extends EndpointExpr> cls = super.select(name);
            if (cls == null)
                throw new KyouException(KyouErr.StyleSpec.Expr.InvalidEndpointExprTag, name);
            
            // 创建表达式实例
            try {
                Constructor<? extends EndpointExpr> constructor = cls.getDeclaredConstructor(String.class, Param.class, Charset.class);
                constructor.setAccessible(true);
                return constructor.newInstance(args);
            } catch (InvocationTargetException ex) {
                throw ex.getCause();
            }
        }
        
        @Override
        protected String getName(Class<? extends EndpointExpr> cls) {
            ExprTag tag = cls.getAnnotation(ExprTag.class);
            
            // 检查是否有ExprTag
            if (tag == null)
                throw new KyouException(KyouErr.Init.ExprTagNotFound, cls.getName());
            
            // 检查构造函数签名
            try {
                cls.getDeclaredConstructor(String.class, Param.class, Charset.class);
            } catch (Exception ex) {
                throw new KyouException(KyouErr.Init.ExprConstructorMismatch, cls.getName());
            }
            
            // 返回表达式的名称
            return tag.name();
        }
    }
}
