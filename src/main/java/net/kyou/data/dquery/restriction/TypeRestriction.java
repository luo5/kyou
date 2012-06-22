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
import net.kyou.data.SchemaField;
import net.kyou.data.SchemaItem;
import net.kyou.data.SchemaStruct;
import net.kyou.exception.KyouErr;
import net.kyou.exception.KyouException;

/**
 * 基于报文元素类型的约束条件
 * 
 * @author nuclearg
 */
class TypeRestriction extends DQueryRestriction {
    /**
     * 匹配的报文元素类型
     */
    private final Type type;
    
    /**
     * 初始化一个基于报文元素类型的约束条件
     */
    TypeRestriction(Type type) {
        this.type = type;
    }
    
    @Override
    public boolean matches(SchemaItem schema, DPath path) {
        switch (this.type) {
            case FIELD:
                return schema instanceof SchemaField;
            case STRU:
                return schema instanceof SchemaStruct;
            default:
                throw new KyouException(KyouErr.Unexpected, String.valueOf(this.type));
        }
    }
    
    @Override
    public String toString() {
        return this.type.toString();
    }
    
    /**
     * 匹配的类型种类
     */
    static enum Type {
        /**
         * 匹配{@link SchemaField}类型
         */
        FIELD,
        /**
         * 匹配({@link SchemaStruct}类型
         */
        STRU,
    }
}
