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

import org.w3c.dom.Element;

@ParamTag(name = "bytes", type = ExprType.Bytes)
class BytesParam extends Param {
    
    BytesParam(Element e, Charset encoding, StyleItem style, ExprFactory exprFactory) {
        super(e, encoding, style, exprFactory);
    }
    
    @Override
    protected void export(Object v, PackContext context, KyouByteOutputStream s) {
        // TODO
    }
}
