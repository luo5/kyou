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
package net.kyou.util;

import java.io.InputStream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import net.kyou.exception.KyouErr;
import net.kyou.exception.KyouException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.helpers.DefaultHandler;

/**
 * 工具类 提供一些xml相关的工具类
 * 
 * @author nuclearg
 */
public class KyouXmlUtils {
    private static DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
    private static XPathFactory xpathFactory = XPathFactory.newInstance();
    
    /**
     * 从给定的输入流中解析出一篇XML文档
     * 
     * @param in
     *            要从中读取XML的输入流 <br/>
     *            读取完毕时该流仍将保持打开状态
     * @return 从流中读取出的XML文档
     */
    public static Document load(InputStream in) {
        if (in == null)
            throw new KyouException(KyouErr.Base.Stream.NullInputStream);
        
        try {
            DocumentBuilder builder = builderFactory.newDocumentBuilder();
            builder.setErrorHandler(new DefaultHandler() {
                @Override
                public void fatalError(SAXParseException ex) throws SAXException {
                    throw new KyouException(KyouErr.Base.Xml.SaxFail, "[" + ex.getLineNumber() + ":" + ex.getColumnNumber() + "] " + ex.getMessage());
                }
            });
            return builder.parse(in);
        } catch (Exception ex) {
            throw new KyouException(KyouErr.Base.Xml.ReadFail, ex);
        }
    }
    
    /**
     * 使用xpath查询出指定的元素节点
     * 
     * @param base
     *            查询的起点
     * @param xpath
     *            XPath表达式
     * @return 查询出的节点或null
     */
    public static Element selectElement(Node base, String xpath) {
        if (base == null)
            throw new KyouException(KyouErr.Base.Xml.EmptyXmlNode);
        if (xpath == null)
            throw new KyouException(KyouErr.Base.Xml.EmptyXPath);
        
        try {
            return (Element) xpathFactory.newXPath().evaluate(xpath, base, XPathConstants.NODE);
        } catch (Exception ex) {
            throw new KyouException(KyouErr.Base.Xml.XPathSyntaxError, "" + xpath, ex);
        }
    }
    
    /**
     * 使用xpath查询出指定的文本，查询出的文本的前后空格将被trim掉
     * 
     * @param base
     *            查询的起点
     * @param xpath
     *            XPath表达式
     * @return 查询出的字符串或null
     */
    public static String selectText(Node base, String xpath) {
        if (base == null)
            throw new KyouException(KyouErr.Base.Xml.EmptyXmlNode);
        if (xpath == null)
            throw new KyouException(KyouErr.Base.Xml.EmptyXPath);
        
        try {
            Node node = (Node) xpathFactory.newXPath().evaluate(xpath, base, XPathConstants.NODE);
            return node == null ? null : node.getTextContent().trim();
        } catch (Exception ex) {
            throw new KyouException(KyouErr.Base.Xml.XPathSyntaxError, "" + xpath, ex);
        }
    }
    
    /**
     * 使用xpath查询出指定的节点列表
     * 
     * @param base
     *            查询的起点
     * @param xpath
     *            XPath表达式
     * @return 查询出的节点列表
     */
    public static Element[] selectElements(Node base, String xpath) {
        if (base == null)
            throw new KyouException(KyouErr.Base.Xml.EmptyXmlNode);
        if (xpath == null)
            throw new KyouException(KyouErr.Base.Xml.EmptyXPath);
        
        try {
            NodeList list = (NodeList) xpathFactory.newXPath().evaluate(xpath, base, XPathConstants.NODESET);
            
            Element[] nodes = new Element[list.getLength()];
            for (int i = 0; i < nodes.length; i++)
                nodes[i] = (Element) list.item(i);
            return nodes;
        } catch (XPathExpressionException ex) {
            throw new KyouException(KyouErr.Base.Xml.XPathSyntaxError, ex);
        }
    }
    
    /**
     * 对一个字符串进行xml编码，处理掉字符串中的&lt; &gt; &amp; &quot;字符
     * 
     * @param str
     *            要进行xml编码的字符串
     * @return 进行过xml编码处理的字符串
     */
    public static String xmlEncode(String str) {
        if (str == null)
            return null;
        
        boolean flag = false;
        for (char c : str.toCharArray())
            switch (c) {
                case '<':
                case '>':
                case '&':
                case '"':
                    flag = true;
                    break;
            }
        
        if (!flag)
            return str;
        
        StringBuffer buffer = new StringBuffer();
        
        int len = str.length();
        for (int i = 0; i < len; i++) {
            char c = str.charAt(i);
            switch (c) {
                case '<':
                    buffer.append("&lt;");
                    break;
                case '>':
                    buffer.append("&gt;");
                    break;
                case '&':
                    buffer.append("&amp;");
                    break;
                case '"':
                    buffer.append("&quot;");
                    break;
                default:
                    buffer.append(c);
                    break;
            }
        }
        return buffer.toString();
    }
}
