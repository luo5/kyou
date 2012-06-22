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
 * 回退参数
 * <p>
 * 从当前的组包输出流中回退若干个字节
 * </p>
 * 
 * @author nuclearg
 */
@ParamTag(name = "bk", type = ExprType.Integer)
class BackspaceParam extends Param {
    private static final Logger logger = Logger.getLogger(BackspaceParam.class);
    
    /**
     * 初始化一个回退参数
     */
    BackspaceParam(Element e, Charset encoding, StyleItem style, ExprFactory exprFactory) {
        super(e, encoding, style, exprFactory);
        
        logger.debug("BackspaceParam created.");
    }
    
    @Override
    protected void export(Object v, PackContext context, KyouByteOutputStream s) {
        s.backspace((int) (long) (Long) v);
        
        logger.debug("backspace len: " + v);
    }
}
