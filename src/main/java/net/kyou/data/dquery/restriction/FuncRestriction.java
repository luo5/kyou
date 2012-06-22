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
import net.kyou.data.dquery.restriction.func.DQueryRestrictionFunction;
import net.kyou.exception.KyouErr;
import net.kyou.exception.KyouException;
import net.kyou.util.KyouString;

/**
 * 基于函数的约束条件
 * 
 * @author nuclearg
 */
class FuncRestriction extends DQueryRestriction {
    /**
     * 实际的约束函数
     */
    private final DQueryRestrictionFunction func;
    /**
     * 期望值
     * <p>
     * 实际进行匹配时，如果约束函数的执行结果和期望值相同则认为符合约束条件
     * </p>
     */
    private final String value;
    
    /**
     * 初始化一个基于函数的约束条件
     * <p>
     * 约束条件函数应当以!开头，类似<code>!func_name=expect</code>，表示只有这个函数执行出的结果满足期望时才认为其符合约束
     * </p>
     * 
     * @param str
     */
    FuncRestriction(KyouString str) {
        // 约束条件函数应当以!开头，类似<code>!func_name=expect</code>，表示只有这个函数执行出的结果满足期望时才认为其符合约束
        int start = str.pos();
        
        try {
            // 匹配掉第一个'!'字符 
            if (str.attempt('!') == null)
                throw new KyouException(KyouErr.DQuery.Restriction.FunctionSyntaxError, str.toString());
            
            // 匹配出函数名称
            String name = str.attemptUntil('=');
            if (name == null)
                throw new KyouException(KyouErr.DQuery.Restriction.IllegalFunctionName, str.toString());
            this.func = DQueryRestrictionFunction.__parse(name);
            if (this.func == null)
                throw new KyouException(KyouErr.DQuery.Restriction.IllegalFunctionName, name);
            
            // 匹配掉'='号
            if (str.attempt('=') == null)
                throw new KyouException(KyouErr.DQuery.Restriction.FunctionConditionParseFail, str.toString());
            
            // 匹配出函数的值
            String value = str.attemptUntil(',', ']');
            if (value == null)
                throw new KyouException(KyouErr.DQuery.Restriction.FunctionValueParseFail, str.toString());
            this.value = value;
        } catch (Exception ex) {
            str.pos(start);
            throw new KyouException(KyouErr.DQuery.Restriction.FunctionSyntaxError, str.toString(), ex);
        }
    }
    
    @Override
    public boolean matches(SchemaItem schema, DPath path) {
        return this.value.equals(this.func.calc(schema, path));
    }
    
    @Override
    public String toString() {
        return "!" + this.func.name() + "=" + this.value;
    }
}
