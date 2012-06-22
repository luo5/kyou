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

import java.nio.charset.Charset;

import net.kyou.pack.PackContext;
import net.kyou.pack.param.Param;
import net.kyou.pack.param.expr.ExprTag;
import net.kyou.pack.param.expr.ExprTag.ExprPostfix;
import net.kyou.pack.param.expr.ExprTag.ExprType;

/**
 * 求当前报文元素的名称 <li>字符串型</li>
 * <p>
 * 求当前正被组包的报文元素的名称
 * </p>
 * 
 * @author nuclearg
 */
@ExprTag(name = "n", type = ExprType.String, postfix = ExprPostfix.Empty)
class NameExpr extends EndpointExpr {
    
    NameExpr(String expr, Param segment, Charset encoding) {
        super(expr, segment, encoding);
    }
    
    @Override
    public Object eval(PackContext context) {
        return context.schema.name();
    }
}
