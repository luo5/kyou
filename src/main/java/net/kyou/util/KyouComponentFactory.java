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
package net.kyou.util;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

/**
 * Kyou的各个组件类型（Param、Expr等）的工厂
 * <p>
 * 可以将各种组件类型与一个字符串关联起来，并向外提供create()方法用于创建组件实例
 * </p>
 * 
 * @author nuclearg
 * 
 * @param <T>
 */
public abstract class KyouComponentFactory<T> {
    private static final Logger logger = Logger.getLogger(KyouComponentFactory.class);
    
    /**
     * 名称与类型的对应表
     */
    private final Map<String, Class<? extends T>> map;
    
    /**
     * 派生类需要调用此构造函数，把此工厂实例支持的类型传进来
     * 
     * @param classes
     *            本工厂实例支持的类型列表
     */
    protected KyouComponentFactory(List<Class<? extends T>> classes) {
        logger.debug("Component classes: " + classes.toString());
        
        Map<String, Class<? extends T>> map = new HashMap<String, Class<? extends T>>();
        
        // 遍历类型列表，构建名称与类型的对应关系
        for (Class<? extends T> cls : classes) {
            String name = this.getName(cls);
            map.put(name, cls);
            logger.debug("Component registered. name: " + name + ", cls: " + cls);
        }
        
        logger.debug("Component map: " + map);
        this.map = Collections.unmodifiableMap(map);
    }
    
    /**
     * 创建一个对象
     * 
     * @param name
     *            对象类型的名称
     * @param args
     *            创建对象需要的参数列表
     * @return 创建好的对象
     * @throws Throwable
     */
    public abstract T create(String name, Object... args) throws Throwable;
    
    /**
     * 在派生类中实现时，该函数用于生成类型对应的名称
     * 
     * @param cls
     *            类型
     * @return 名称
     */
    protected abstract String getName(Class<? extends T> cls);
    
    /**
     * 根据名称确定要创建的对象类型
     * 
     * @param name
     *            名称
     * @return 对象的类型。如果未找到该名称则返回null
     */
    protected Class<? extends T> select(String name) {
        return this.map.get(name);
    }
    
    @Override
    public String toString() {
        return this.map.toString();
    }
}
