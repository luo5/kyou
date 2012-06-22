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
package net.kyou.pack;

import net.kyou.Kyou;
import net.kyou.data.Attrs;
import net.kyou.data.DPath;
import net.kyou.data.DataDocument;
import net.kyou.data.SchemaItem;
import net.kyou.data.SchemaStruct;
import net.kyou.exception.KyouErr;
import net.kyou.exception.KyouException;
import net.kyou.pack.param.Param;
import net.kyou.util.KyouByteOutputStream;

/**
 * 组包服务核心类 提供kyou的通用组包服务
 * <p>
 * <b>不应在用户代码中手工调用此类中的函数，请使用net.kyou.Kyou中的pack方法。</b>
 * </p>
 * 
 * @author nuclearg
 * @see Kyou
 */
public class PackService {
    /**
     * 对整个报文执行组包过程
     * 
     * @param schema
     *            报文结构
     * @param data
     *            报文数据
     * @param spec
     *            组包样式定义
     * @param s
     *            字节流
     */
    public static void __document(SchemaItem schema, DataDocument data, StyleSpecification spec, KyouByteOutputStream s) {
        if (schema == null)
            throw new KyouException(KyouErr.Pack.EmptySchema);
        if (data == null)
            throw new KyouException(KyouErr.Pack.EmptyData);
        if (spec == null)
            throw new KyouException(KyouErr.Pack.EmptySpecifcation);
        if (s == null)
            throw new KyouException(KyouErr.Base.Stream.NullOutputStream);
        
        PackContext context = new PackContext(DPath.root, schema, data, spec);
        
        try {
            __item(context, s);
        } catch (Exception ex) {
            throw new KyouException(KyouErr.Pack.PackFail, "path: #", ex);
        }
    }
    
    /**
     * 对某个指定的报文元素执行组包过程
     * 
     * @param context
     *            组包上下文
     */
    public static void __item(PackContext context, KyouByteOutputStream s) {
        StyleItem style = select(context);
        
        for (Segment segment : style.segments)
            segment.export(context, s);
    }
    
    /**
     * 对某个报文元素的子元素执行组包过程
     * <p>
     * <li>当该元素的array为true时会遍历该数组的各个元素</li>
     * <li>否则将假设当前元素为一个结构并遍历该结构的各个子元素</li>
     * </p>
     * 
     * @param context
     *            组包上下文
     */
    public static byte[] __member(PackContext context) {
        KyouByteOutputStream s = new KyouByteOutputStream();
        
        if (context.schema.attrb(Attrs.ARRAY)) {
            // 遍历该数组的各个项
            
            int count = Integer.parseInt(context.data.get(context.path));
            for (int i = 0; i < count; i++)
                __item(new PackContext(context.path.child(String.valueOf(i)), context.schema, context.data, context.spec), s);
        } else {
            // 遍历该结构的各个项
            if (!(context.schema instanceof SchemaStruct))
                throw new KyouException(KyouErr.Pack.RequireSchemaStruct, context.path.toString());
            
            SchemaStruct stru = (SchemaStruct) context.schema;
            for (SchemaItem item : stru)
                __item(new PackContext(context.path.child(item.name()), item, context.data, context.spec), s);
        }
        
        return s.export();
    }
    
    /**
     * 获取对某个指定段的引用
     * 
     * @param style
     *            组包样式单元
     * @param refId
     *            段编号
     * @return 由段编号指定的段
     */
    public static Param __ref(StyleItem style, int refId) {
        if (refId < 0 || refId >= style.segments.size())
            throw new KyouException(KyouErr.Pack.InvalidRefId, String.valueOf(refId));
        return null;// style.segments.get(refId);
        // FIXME ref
    }
    
    /**
     * 从组包样式定义中选择出一个与当前的组包上下文相配套的样式单元
     * 
     * @param context
     *            组包上下文
     * @return 适合当前组包上下文的样式单元
     */
    private static StyleItem select(PackContext context) {
        for (StyleItem style : context.spec.styles)
            if (style.target.matches(context.schema, context.path))
                return style;
        
        throw new KyouException(KyouErr.Pack.NoStyleUnitSuitable, "path: " + context.path);
    }
}
