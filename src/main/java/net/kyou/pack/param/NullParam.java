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
package net.kyou.pack.param;

import java.nio.charset.Charset;

import net.kyou.pack.PackContext;
import net.kyou.pack.StyleItem;
import net.kyou.pack.param.expr.ExprFactory;
import net.kyou.pack.param.expr.ExprTag.ExprType;
import net.kyou.util.KyouByteOutputStream;

import org.apache.log4j.Logger;
import org.w3c.dom.Element;

/**
 * 空输出参数
 * <p>
 * 将表达式的计算结果抛弃，不向流中输出任何东西。该段可以用来调用一些脚本，以便在组包过程中进行一些操作。
 * </p>
 * 
 * @author nuclearg
 */
@ParamTag(name = "null", type = ExprType.Irrelevant)
class NullParam extends Param {
    private static final Logger logger = Logger.getLogger(NullParam.class);
    
    /**
     * 初始化一个空输出参数
     */
    NullParam(Element e, Charset encoding, StyleItem style, ExprFactory exprFactory) {
        super(e, encoding, style, exprFactory);
        
        logger.debug("NullParam created.");
    }
    
    @Override
    protected void export(Object v, PackContext context, KyouByteOutputStream s) {
        // 不进行任何操作
        // 此时该参数对应的表达式已经计算完毕。
        
        logger.debug("null writted. value: " + v);
    }
}
