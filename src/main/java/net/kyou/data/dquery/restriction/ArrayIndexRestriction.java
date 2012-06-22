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
import java.util.List;

import net.kyou.data.DPath;
import net.kyou.data.SchemaItem;
import net.kyou.exception.KyouErr;
import net.kyou.exception.KyouException;
import net.kyou.util.KyouString;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;

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
class ArrayIndexRestriction extends DQueryRestriction {
    /**
     * 下标列表
     */
    private final int[] indexes;
    
    /**
     * 初始化一个基于数组下标的约束条件
     */
    ArrayIndexRestriction(KyouString str) {
        List<Integer> indexes = new ArrayList<Integer>();
        
        do {
            indexes.add(this.parseNumber(str));
        } while (this.parseDelimeter(str));
        
        this.indexes = ArrayUtils.toPrimitive(indexes.toArray(ArrayUtils.EMPTY_INTEGER_OBJECT_ARRAY));
    }
    
    @Override
    public boolean matches(SchemaItem schema, DPath path) {
        String name = path.name();
        try {
            int index = Integer.parseInt(name);
            return ArrayUtils.contains(indexes, index);
        } catch (Exception ex) {
            return false;
        }
    }
    
    /**
     * 从约束条件字符串中解析出下一个数组下标
     */
    private int parseNumber(KyouString str) {
        int start = str.pos();
        try {
            // 把可能的空白匹配掉
            str.attemptMore(' ');
            
            String value = str.attemptMore('0', '1', '2', '3', '4', '5', '6', '7', '8', '9');
            
            // 如果匹配不成功则抛异常
            if (value == null)
                throw new KyouException(KyouErr.DQuery.Restriction.IllegalArrayIndex, "<null>");
            
            // 把匹配出的下标转换为数字
            try {
                return Integer.parseInt(value);
            } catch (Exception ex) {
                throw new KyouException(KyouErr.DQuery.Restriction.IllegalArrayIndex, value, ex);
            }
        } catch (Exception ex) {
            str.pos(start);
            throw new KyouException(KyouErr.DQuery.Restriction.IllegalArrayIndex, str.toString(), ex);
        }
    }
    
    /**
     * 从约束条件字符串中匹配出下一个分隔符
     */
    private boolean parseDelimeter(KyouString str) {
        // 把分隔符前面可能的空白匹配掉
        str.attemptMore(' ');
        
        // 匹配分隔符
        return str.attempt('|') != null;
    }
    
    @Override
    public String toString() {
        return StringUtils.join(ArrayUtils.toObject(this.indexes), '|');
    }
}
