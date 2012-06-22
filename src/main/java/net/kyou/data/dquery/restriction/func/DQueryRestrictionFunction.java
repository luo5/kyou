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
package net.kyou.data.dquery.restriction.func;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import net.kyou.data.DPath;
import net.kyou.data.SchemaItem;

/**
 * 基于数组下标的约束条件
 * <p>
 * 可以指定多个下标，中间用'|'分隔，例如：
 * <li>0 表示匹配第0个数组元素</li>
 * <li>0|1 表示匹配第0个和第一个数组元素</li>
 * <li>1|3|5|7 表示匹配第1、3、5、7个数组元素</li>
 * </p>
 * 
 * @author nuclearg
 */
public abstract class DQueryRestrictionFunction {
    /**
     * 当前支持的约束函数列表
     */
    private static final Map<String, DQueryRestrictionFunction> functions;
    
    static {
        DQueryRestrictionFunction[] funcs = {
            new ChildrenCountFunction(), };
        
        Map<String, DQueryRestrictionFunction> _functions = new HashMap<String, DQueryRestrictionFunction>();
        for (DQueryRestrictionFunction function : funcs)
            _functions.put(function.name(), function);
        
        functions = Collections.unmodifiableMap(_functions);
    }
    
    public static DQueryRestrictionFunction __parse(String name) {
        return functions.get(name);
    }
    
    /**
     * 在派生类中实现时，该函数返回约束函数的名称
     * 
     * @return 约束函数名称
     */
    public abstract String name();
    
    /**
     * 计算约束条件函数
     * 
     * @param schema
     *            当前正被计算的元素的schema
     * @param path
     *            当前正被计算的元素的dpath
     * 
     * @return 约束函数的计算结果。将通过对比这个计算结果和约束条件定义的期望来决定这个是否满足约束条件
     */
    public abstract String calc(SchemaItem schema, DPath path);
}
