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
 * 整数字面量 <li>整数型</li>
 * <p>
 * 表示一个整数字面量
 * </p>
 * 
 * @author nuclearg
 */
@ExprTag(name = "", type = ExprType.Integer, postfix = ExprPostfix.Empty)
class PlainIntegerExpr extends EndpointExpr {
    /**
     * 字面量
     */
    private final long value;
    
    /**
     * 初始化一个整数字面量表达式
     * 
     * @param postfix
     *            字面量。虽然名称上为“后缀”，但实际上整数字面量的创建方式和其它的表达式不同
     * @param param
     *            该表达式隶属于的参数
     * @param encoding
     *            报文整体编码
     */
    PlainIntegerExpr(String postfix, Param param, Charset encoding) {
        super(null, param, encoding);
        this.value = Long.parseLong(postfix);
    }
    
    @Override
    public Object eval(PackContext context) {
        return this.value;
    }
}
