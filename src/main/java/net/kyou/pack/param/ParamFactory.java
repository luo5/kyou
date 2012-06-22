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

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import net.kyou.exception.KyouErr;
import net.kyou.exception.KyouException;
import net.kyou.pack.StyleItem;
import net.kyou.pack.param.expr.ExprFactory;
import net.kyou.util.KyouComponentFactory;

import org.apache.log4j.Logger;
import org.w3c.dom.Element;

/**
 * 参数工厂
 * 
 * @author nuclearg
 * 
 */
public class ParamFactory {
    private static final Logger logger = Logger.getLogger(ParamFactory.class);
    
    /**
     * Kyou默认的各种参数类型列表
     */
    private static final List<Class<? extends Param>> classes;
    static {
        List<Class<? extends Param>> _classes = new ArrayList<Class<? extends Param>>();
        
        _classes.add(NullParam.class);
        
        _classes.add(IntegerParam.class);
        _classes.add(StringParam.class);
        _classes.add(BytesParam.class);
        
        _classes.add(FloatParam.class);
        _classes.add(BCDParam.class);
        _classes.add(BackspaceParam.class);
        
        classes = Collections.unmodifiableList(_classes);
    }
    
    /**
     * 参数工厂实例
     */
    private final ParamFactoryImpl factory;
    
    /**
     * 初始化一个参数工厂实例
     * 
     * @param paramClasses
     *            用户自定义的参数类型列表
     */
    public ParamFactory(List<Class<? extends Param>> paramClasses) {
        if (paramClasses == null)
            paramClasses = Collections.emptyList();
        if (!paramClasses.isEmpty())
            logger.debug("User defined params: " + paramClasses);
        
        // 构造一个包含了默认类型和用户自定义类型的参数类型列表
        List<Class<? extends Param>> list = new ArrayList<Class<? extends Param>>();
        
        // 把默认类型放进来
        list.addAll(classes);
        // 把用户自定义类型列表放进来
        list.addAll(paramClasses);
        
        // 创建参数类型注册表
        this.factory = new ParamFactoryImpl(list);
        
        logger.debug("Param map: " + this.factory);
    }
    
    /**
     * 从XML中初始化一个参数实例
     * 
     * @param e
     *            XML节点
     * @param encoding
     *            StyleSpecification中指定的报文整体编码
     * @param style
     *            该参数隶属于的组包样式单元
     * @param exprFactory
     *            表达式工厂
     * @return 根据XML建立的参数实例
     */
    public Param create(Element e, Charset encoding, StyleItem style, ExprFactory exprFactory) {
        /*
         * <paramName>expr</paramName>
         */
        
        try {
            String name = e.getNodeName();
            logger.debug("Create param. name: " + name);
            
            Param param = this.factory.create(name, e, encoding, style, exprFactory);
            
            logger.debug("Param created. param: " + param);
            return param;
        } catch (Throwable ex) {
            throw new KyouException(KyouErr.StyleSpec.Style.ParamCreateFail, "name: " + e.getNodeName() + ", expr: " + e.getTextContent(), ex);
        }
    }
    
    /**
     * 实际的参数工厂
     * 
     * @author nuclearg
     * 
     */
    private class ParamFactoryImpl extends KyouComponentFactory<Param> {
        /**
         * 初始化一个参数类型注册表
         * 
         * @param classes
         *            参数类型列表
         */
        private ParamFactoryImpl(List<Class<? extends Param>> classes) {
            super(classes);
        }
        
        @Override
        public Param create(String name, Object... args) throws Throwable {
            // 找到名称对应的参数类型
            Class<? extends Param> cls = super.select(name);
            if (cls == null)
                throw new KyouException(KyouErr.StyleSpec.Style.InvalidParamTag, name);
            
            // 创建
            try {
                Constructor<? extends Param> constructor = cls.getDeclaredConstructor(Element.class, Charset.class, StyleItem.class, ExprFactory.class);
                constructor.setAccessible(true);
                return constructor.newInstance(args);
            } catch (InvocationTargetException ex) {
                throw ex.getCause();
            }
        }
        
        @Override
        protected String getName(Class<? extends Param> cls) {
            ParamTag tag = cls.getAnnotation(ParamTag.class);
            
            // 检查是否有ParamTag
            if (tag == null)
                throw new KyouException(KyouErr.Init.ParamTagNotFound, cls.getName());
            
            // 检查构造函数签名
            try {
                cls.getDeclaredConstructor(Element.class, Charset.class, StyleItem.class, ExprFactory.class);
            } catch (Exception ex) {
                throw new KyouException(KyouErr.Init.ParamConstructorMismatch, cls.toString() + ", constructors: " + Arrays.toString(cls.getDeclaredConstructors()));
            }
            
            // 返回参数的名称
            return tag.name();
        }
    }
}
