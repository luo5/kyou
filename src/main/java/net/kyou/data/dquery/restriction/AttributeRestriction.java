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
package net.kyou.data.dquery.restriction;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import net.kyou.data.DPath;
import net.kyou.data.SchemaItem;
import net.kyou.util.KyouString;

import org.apache.commons.lang.StringUtils;

/**
 * 基于属性值的约束条件
 * <p>
 * </p>
 * 
 * @author nuclearg
 */
class AttributeRestriction extends DQueryRestriction {
    /**
     * 属性的名称
     */
    private final String name;
    /**
     * 属性可能的值
     */
    private final List<String> values;
    
    AttributeRestriction(KyouString str) {
        // 把开头的'@'字符去掉
        if (str.attempt('@') == null)
            ;
        
        // 获取属性名称
        this.name = this.parseName(str);
        // 去掉'='
        str.get();
        
        // 获取属性值列表
        ArrayList<String> values = new ArrayList<String>();
        values.add(this.parseValue(str));
        while (this.parseDelimeter(str))
            values.add(this.parseValue(str));
        
        this.values = Collections.unmodifiableList(values);
    }
    
    @Override
    public boolean matches(SchemaItem schema, DPath path) {
        String attr = schema.attr(this.name);
        return values.contains(attr);
    }
    
    /**
     * 解析属性名称
     */
    private String parseName(KyouString str) {
        return str.attemptUntil('=');
    }
    
    /**
     * 解析属性的值
     */
    private String parseValue(KyouString str) {
        return str.attemptUntil('|', ',', ']');
    }
    
    /**
     * 解析属性值之间的分隔符
     */
    private boolean parseDelimeter(KyouString str) {
        return str.attempt('|', '=') != null;
    }
    
    @Override
    public String toString() {
        return "@" + this.name + "=" + StringUtils.join(this.values, '|');
    }
}
