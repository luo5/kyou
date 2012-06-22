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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import net.kyou.data.dquery.DQuery;
import net.kyou.exception.KyouErr;
import net.kyou.exception.KyouException;
import net.kyou.pack.param.Param;
import net.kyou.pack.param.ParamFactory;
import net.kyou.pack.param.expr.ExprFactory;
import net.kyou.util.KyouFormatString;
import net.kyou.util.KyouXmlUtils;

import org.apache.commons.lang.StringUtils;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * 报文组包样式单元
 * <p>
 * 组包样式单元描述了对于某一类元素的组包方式
 * </p>
 * 
 * @author nuclearg
 */
public class StyleItem {
    /**
     * 该组包样式单元隶属于的组包样式定义
     */
    public final StyleSpecification spec;
    
    /**
     * 该组包样式单元适用于的元素
     */
    final DQuery target;
    /**
     * 段列表
     */
    final List<Segment> segments;
    
    /**
     * 从XML中初始化一个StyleItem实例
     * 
     * @param e
     *            XML节点
     * @param spec
     *            该组包样式单元隶属于的组包样式定义
     * @param paramFactory
     *            参数工厂
     * @param exprFactory
     *            表达式工厂
     */
    StyleItem(Element e, StyleSpecification spec, ParamFactory paramFactory, ExprFactory exprFactory) {
        this.spec = spec;
        
        /**
         * <pre>
         *      <style target="...">
         *          <format><![CDATA[<%>%</%>]]></format>
         *          <s>n</s>
         *          <s>m</s>
         *          <s>n</s>
         *      </style>
         * </pre>
         */
        
        // 初始化target
        String target = KyouXmlUtils.selectText(e, "@target");
        if (StringUtils.isEmpty(target))
            throw new KyouException(KyouErr.StyleSpec.Style.EmptyTarget);
        this.target = new DQuery(target);
        
        // 初始化format
        String fstr = KyouXmlUtils.selectText(e, "format");
        if (StringUtils.isEmpty(fstr))
            throw new KyouException(KyouErr.StyleSpec.Style.EmptyFormat, "target: " + target);
        KyouFormatString format = new KyouFormatString(fstr, spec.config.encoding);
        
        // 解析用户定义的参数
        NodeList nodes = e.getChildNodes();
        List<Param> params = new ArrayList<Param>();
        for (int i = 0; i < nodes.getLength(); i++)
            try {
                Node node = nodes.item(i);
                if (node instanceof Element && !((Element) node).getNodeName().equals("format"))
                    params.add(paramFactory.create((Element) node, this.spec.config.encoding, this, exprFactory));
            } catch (Exception ex) {
                throw new KyouException(KyouErr.StyleSpec.Style.ParamCreateFail, "taget: " + target + ", param: " + i, ex);
            }
        
        // 遍历format的各个段，初始化segments
        List<Segment> segments = new ArrayList<Segment>();
        int paramId = 0;
        for (int i = 0; i < format.size(); i++) {
            byte[] text = format.segment(i);
            
            // 判断这个段的类型
            if (text == null)
                // 如果为null表示这是一个参数段
                try {
                    Param param = params.get(paramId);
                    segments.add(new ParamSegment(param));
                    paramId++;
                } catch (IndexOutOfBoundsException ex) {
                    throw new KyouException(KyouErr.StyleSpec.Style.InsufficientParams, "target: " + target + ", param: " + String.valueOf(paramId));
                }
            else
                // 如果不为null表示这是一个文本段
                segments.add(new TextSegment(text));
        }
        
        this.segments = Collections.unmodifiableList(segments);
    }
}
