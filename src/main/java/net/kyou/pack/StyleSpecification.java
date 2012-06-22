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

import java.io.InputStream;
import java.nio.charset.Charset;
import java.nio.charset.UnsupportedCharsetException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import net.kyou.exception.KyouErr;
import net.kyou.exception.KyouException;
import net.kyou.pack.param.ParamFactory;
import net.kyou.pack.param.expr.ExprFactory;
import net.kyou.util.KyouXmlUtils;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * 报文样式定义
 * <p>
 * StyleSpecification定义了将数据变成实际的二进制的报文的过程。其中定义了若干个StyleItem(组包样式单元)， 每个StyleItem描述了针对某些报文元素采用的组包过程。<br/>
 * 需要定义一个针对根元素("#")的StyleItem，该StyleItem是整个组包过程的入口点。<br/>
 * 实际的组包过程大致如下：<br/>
 * <li>1. 先找到报文中的根节点</li>
 * <li>2. 由上至下遍历StyleSpecification，找到第一个与之匹配的StyleItem</li>
 * <li>3. 使用这个StyleItem中定义的格式对报文数据进行组包。如果这个StyleItem引用了其它元素（例如m参数，其表示对自身的各个子节点进行组包）则拿到这些元素，递归重复2和3。</li>
 * </p>
 * 
 * @author nuclearg
 */
public class StyleSpecification {
    /**
     * 全局配置
     */
    final StyleSpecificationConfig config;
    /**
     * 组包样式单元列表
     */
    final List<StyleItem> styles;
    /**
     * 组包脚本列表
     */
    final List<StyleScript> scripts;
    
    /**
     * XML的输入流初始化一个StyleSpecification实例
     * 
     * @param in
     *            包含XML的输入流
     * @param paramFactory
     *            参数工厂
     * @param exprFactory
     *            表达式工厂
     */
    public StyleSpecification(InputStream in, ParamFactory paramFactory, ExprFactory exprFactory) {
        try {
            if (in == null)
                throw new KyouException(KyouErr.Base.Stream.NullInputStream);
            if (paramFactory == null)
                throw new KyouException(KyouErr.StyleSpec.EmptyParamFactory);
            if (exprFactory == null)
                throw new KyouException(KyouErr.StyleSpec.EmptyExprFactory);
            
            Document doc = KyouXmlUtils.load(in);
            
            // 初始化config
            Element config = KyouXmlUtils.selectElement(doc, "/spec/config");
            this.config = new StyleSpecificationConfig(config);
            
            // 初始化styles
            Element[] styleElements = KyouXmlUtils.selectElements(doc, "/spec/style");
            List<StyleItem> styles = new ArrayList<StyleItem>();
            for (Element e : styleElements)
                styles.add(new StyleItem(e, this, paramFactory, exprFactory));
            this.styles = Collections.unmodifiableList(styles);
            
            // 初始化scripts
            Element[] scriptElements = KyouXmlUtils.selectElements(doc, "/spec/script");
            List<StyleScript> scripts = new ArrayList<StyleScript>();
            for (Element e : scriptElements)
                scripts.add(new StyleScript(e));
            this.scripts = Collections.unmodifiableList(scripts);
        } catch (Exception ex) {
            throw new KyouException(KyouErr.StyleSpec.SpecificationParseFail, ex);
        }
    }
    
    /**
     * StyleSpecification的全局配置类
     * 
     * @author nuclearg
     */
    static class StyleSpecificationConfig {
        /**
         * 整篇报文使用的编码
         */
        final Charset encoding;
        
        /**
         * 从XML中初始化一个StyleSpecificationConfig实例
         * 
         * @param e
         *            XML节点
         */
        StyleSpecificationConfig(Element e) {
            try {
                String encoding = KyouXmlUtils.selectText(e, "encoding");
                if (encoding == null)
                    throw new KyouException(KyouErr.Base.UnsupportedCharset, "<null>");
                if (encoding.length() == 0)
                    throw new KyouException(KyouErr.Base.UnsupportedCharset, "<empty>");
                
                this.encoding = Charset.forName(encoding);
            } catch (UnsupportedCharsetException ex) {
                throw new KyouException(KyouErr.Base.UnsupportedCharset, KyouXmlUtils.selectText(e, "encoding"));
            }
        }
    }
}
