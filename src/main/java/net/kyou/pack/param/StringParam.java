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
import java.util.Arrays;

import net.kyou.exception.KyouErr;
import net.kyou.exception.KyouException;
import net.kyou.pack.PackContext;
import net.kyou.pack.StyleItem;
import net.kyou.pack.param.expr.ExprFactory;
import net.kyou.pack.param.expr.ExprTag.ExprType;
import net.kyou.util.KyouByteOutputStream;

import org.w3c.dom.Element;

/**
 * 字符串输出段
 * <p>
 * 将参数的计算结果以字符串形式输出到流中
 * </p>
 * 
 * @author nuclearg
 */
@ParamTag(name = "str", type = ExprType.String)
class StringParam extends Param {
    /**
     * 
     */
    private static final int LEN_AUTO = 0;
    /**
     * 常量 表示当前段的长度等于当前正被组包的元素的len属性的值
     */
    private static final int LEN_VALUE = -1;
    
    /**
     * 该段采用的编码
     */
    private final Charset encoding;
    /**
     * 长度 该长度指的是字符串的长度
     */
    private final int len;
    /**
     * 对齐方式<li>true - 左对齐</li><li>right - 右对齐</li>
     */
    private final boolean align;
    /**
     * 填充字符
     */
    private final char filling;
    
    /**
     * 初始化一个字符串输出段
     */
    StringParam(Element e, Charset encoding, StyleItem style, ExprFactory exprFactory) {
        super(e, encoding, style, exprFactory);
        
        // encoding
        if (e.hasAttribute("encoding"))
            try {
                this.encoding = Charset.forName(e.getAttribute("encoding"));
            } catch (Exception ex) {
                throw new KyouException(KyouErr.StyleSpec.Style.InvalidSegmentParam, "encoding: " + e.getAttribute("encoding"));
            }
        else
            this.encoding = encoding;
        
        // len
        if (e.hasAttribute("len"))
            if (e.getAttribute("len").equals("value"))
                this.len = LEN_VALUE;
            else
                try {
                    this.len = Integer.parseInt(e.getAttribute("len"));
                    if (len <= 0)
                        throw new NumberFormatException();
                } catch (NumberFormatException ex) {
                    throw new KyouException(KyouErr.StyleSpec.Style.InvalidSegmentParam, "len: " + e.getAttribute("len"));
                }
        else
            this.len = LEN_AUTO;
        
        // align
        if (e.hasAttribute("align"))
            if (e.getAttribute("align").equals("left"))
                this.align = true;
            else if (e.getAttribute("align").equals("right"))
                this.align = false;
            else
                throw new KyouException(KyouErr.StyleSpec.Style.InvalidSegmentParam, "align: " + e.getAttribute("align"));
        else
            this.align = true;
        
        // filling
        if (e.hasAttribute("filling"))
            if (e.getAttribute("filling").length() == 1)
                this.filling = e.getAttribute("filling").charAt(0);
            else
                throw new KyouException(KyouErr.StyleSpec.Style.InvalidSegmentParam, "filling: " + e.getAttribute("filling"));
        else
            this.filling = ' ';
    }
    
    @Override
    protected void export(Object v, PackContext context, KyouByteOutputStream s) {
        String str = (String) v;
        
        // 如果长度为LEN_VALUE则取当前报文元素的len属性的值
        int len = this.len;
        if (len == LEN_VALUE)
            try {
                len = Integer.parseInt(context.schema.attr("len"));
            } catch (NumberFormatException ex) {
                len = LEN_AUTO;
            }
        
        // 判断是否需要考虑对齐和补位
        if (len != LEN_AUTO) {
            // 如果字符串的长度大于len则截掉右边的部分
            if (str.length() > this.len)
                s.write(str.substring(0, this.len).getBytes(this.encoding));
            else {
                // 如果字符串的长度小于len则根据对齐方式进行补位
                char[] chars = new char[this.len - str.length()];
                Arrays.fill(chars, this.filling);
                
                if (this.align)
                    // 左对齐，在右边补位
                    str = str + new String(chars);
                else
                    // 右对齐，在左边补位
                    str = new String(chars) + str;
            }
        }
        
        // 将字符串写到流里
        s.write(str.getBytes(this.encoding));
    }
}
