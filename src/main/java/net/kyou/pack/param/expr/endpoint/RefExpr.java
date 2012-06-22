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
import net.kyou.pack.PackService;
import net.kyou.pack.param.Param;
import net.kyou.pack.param.expr.ExprTag;
import net.kyou.pack.param.expr.ExprTag.ExprPostfix;
import net.kyou.pack.param.expr.ExprTag.ExprType;
import net.kyou.util.KyouByteOutputStream;

/**
 * 引用参数 <br/>
 * 字节型
 * <p>
 * 表示引用当前StyleItem中某个段的输出字节<br/>
 * 需要一个整数的后缀，指出该参数引用的是哪个段。
 * </p>
 * 
 * @author nuclearg
 */
@ExprTag(name = "r", type = ExprType.Bytes, postfix = ExprPostfix.RequiresInt)
class RefExpr extends EndpointExpr {
    /**
     * 该表达式引用的参数
     */
    private final Param ref;
    
    RefExpr(String expr, Param param, Charset encoding) {
        super(expr, param, encoding);
        
        int refId = Integer.parseInt(super.postfix);
        
        this.ref = PackService.__ref(this.param.style, refId);
    }
    
    @Override
    public Object eval(PackContext context) {
        KyouByteOutputStream s = new KyouByteOutputStream();
        this.ref.export(context, s);
        return s.export();
    }
}
