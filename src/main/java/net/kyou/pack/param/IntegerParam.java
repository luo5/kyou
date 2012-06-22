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

import net.kyou.exception.KyouErr;
import net.kyou.exception.KyouException;
import net.kyou.pack.PackContext;
import net.kyou.pack.StyleItem;
import net.kyou.pack.param.expr.ExprFactory;
import net.kyou.pack.param.expr.ExprTag.ExprType;
import net.kyou.util.KyouByteOutputStream;
import net.kyou.util.KyouByteUtils;

import org.apache.log4j.Logger;
import org.w3c.dom.Element;

/**
 * 整形输出参数
 * <p>
 * 将表达式的计算结果以整型出到流中
 * </p>
 * <p>
 * 参数：
 * <li>len：字节长度，必选，可选值为1、2、4、8</li>
 * <li>endian：字节序，可选，可选值为big、small，默认值为small</li>
 * </p>
 * 
 * @author nuclearg
 */
@ParamTag(name = "int", type = ExprType.Integer)
class IntegerParam extends Param {
    private static final Logger logger = Logger.getLogger(IntegerParam.class);
    
    /**
     * 输出数据的字节数
     */
    private final int len;
    /**
     * endian方式<li>true - 大尾</li><li>false - 小尾</li>
     */
    private final boolean endian;
    
    /**
     * 初始化一个整型输出段
     */
    IntegerParam(Element e, Charset encoding, StyleItem style, ExprFactory exprFactory) {
        super(e, encoding, style, exprFactory);
        
        /**
         * <pre>
         * <int len="2" endian="big">xxx</int>
         * </pre>
         */
        
        try {
            this.len = Integer.parseInt(e.getAttribute("len"));
            switch (this.len) {
                case 1:
                case 2:
                case 4:
                case 8:
                    break;
                default:
                    throw new NumberFormatException();
            }
        } catch (NumberFormatException ex) {
            throw new KyouException(KyouErr.StyleSpec.Style.InvalidSegmentParam, "len: " + e.getAttribute("len") + " expect: 1 | 2 | 4");
        }
        
        this.endian = "big".equalsIgnoreCase(e.getAttribute("endian"));
        
        logger.debug("IntegerParam created. len: " + this.len + ", bigendian: " + this.endian);
    }
    
    @Override
    protected void export(Object v, PackContext context, KyouByteOutputStream s) {
        long num = (Long) v;
        
        // 将num写到流中
        switch (this.len) {
            case 1:
                s.write(KyouByteUtils.writeInteger8(num));
                break;
            case 2:
                s.write(KyouByteUtils.writeInteger16(num, this.endian));
                break;
            case 4:
                s.write(KyouByteUtils.writeInteger32(num, this.endian));
                break;
            case 8:
                s.write(KyouByteUtils.writeInteger64(num, this.endian));
                break;
            default:
                throw new KyouException(KyouErr.Unexpected, "len=" + this.len);
        }
        
        logger.debug("integer writted. value: " + v);
    }
}
