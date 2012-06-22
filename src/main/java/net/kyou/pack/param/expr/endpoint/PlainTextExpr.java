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

import net.kyou.exception.KyouErr;
import net.kyou.exception.KyouException;
import net.kyou.pack.PackContext;
import net.kyou.pack.param.Param;
import net.kyou.pack.param.expr.ExprTag;
import net.kyou.pack.param.expr.ExprTag.ExprPostfix;
import net.kyou.pack.param.expr.ExprTag.ExprType;
import net.kyou.util.KyouFormatString;

/**
 * 文本字面量 <li>字节型</li>
 * <p>
 * 表示一个KyouFormatString字面量，其中不能有参数。编码使用报文全局编码。
 * </p>
 * 
 * @author nuclearg
 * 
 */
@ExprTag(name = "text", type = ExprType.Bytes, postfix = ExprPostfix.Required)
class PlainTextExpr extends EndpointExpr {
    /**
     * 文本字面量
     */
    private final byte[] text;
    
    PlainTextExpr(String expr, Param segment, Charset encoding) {
        super(expr, segment, encoding);
        KyouFormatString str = new KyouFormatString(this.postfix, encoding);
        if (!str.isSimple())
            throw new KyouException(KyouErr.StyleSpec.Format.SimpleFormatStringRequired, this.postfix);
        this.text = str.segment(0);
    }
    
    @Override
    protected Object eval(PackContext context) {
        return this.text;
    }
}
