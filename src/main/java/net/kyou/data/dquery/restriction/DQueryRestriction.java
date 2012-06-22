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

import net.kyou.data.DPath;
import net.kyou.data.SchemaItem;
import net.kyou.exception.KyouErr;
import net.kyou.exception.KyouException;
import net.kyou.util.KyouString;

/**
 * 约束条件
 * <p>
 * 该类是DQueryRestriction的实际功能实现类
 * </p>
 * 
 * @author nuclearg
 */
public abstract class DQueryRestriction {
    /**
     * 
     * @param schema
     * @param path
     * @return
     */
    public abstract boolean matches(SchemaItem schema, DPath path);
    
    /**
     * 解析约束条件
     * 
     * @param str
     *            约束条件
     * @return 解析出来的约束条件。可能为null，这表示遇到了约束条件的右边界']'
     */
    public static DQueryRestriction __parse(KyouString str) {
        if (str == null || !str.hasRemaining())
            throw new KyouException(KyouErr.DQuery.Restriction.EmptyDQueryRestriction);
        
        // 获取约束条件字符串中的第一个字符，可以从该字符中判断出来这是一个什么样的约束条件
        char c = str.get();
        // 把读取出的这个字符退回去。我们只需要取出这个字符看一眼就够了，不希望把LL1String的pos搞乱。
        str.pushback();
        
        // 根据这个字符进行判断
        switch (c) {
            case ']':
                // 如果是']'字符则说明所有约束条件都已经被解析完毕了
                return null;
            case '@':
                // 如果是'@'则说明这是一个属性约束条件
                return new AttributeRestriction(str);
            case '!':
                // 如果是'!'则说明这是一个函数约束条件
                return new FuncRestriction(str);
            case 'F':
            case 'S':
                String type = str.attempt("FIELD", "STRU");
                if (type == null)
                    break;
                
                if (type.equals("FIELD"))
                    return new TypeRestriction(TypeRestriction.Type.FIELD);
                if (type.equals("STRU"))
                    return new TypeRestriction(TypeRestriction.Type.STRU);
                
                throw new KyouException(KyouErr.Unexpected, "restriction type is " + type);
            case '0':
            case '1':
            case '2':
            case '3':
            case '4':
            case '5':
            case '6':
            case '7':
            case '8':
            case '9':
                // 如果是数字则说明这是一个数组下标约束条件
                return new ArrayIndexRestriction(str);
        }
        
        // 上面的switch条件都不满足，说明这个约束条件的语法有问题
        throw new KyouException(KyouErr.DQuery.Restriction.SyntaxError, str.toString());
    }
}
