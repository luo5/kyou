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
package net.kyou.data;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;

import net.kyou.data.DataDocument.IDataSerializer;
import net.kyou.data.SchemaDocument.ISchemaSerializer;
import net.kyou.exception.KyouErr;
import net.kyou.exception.KyouException;
import net.kyou.util.KyouRuntimeUtils;
import net.kyou.util.KyouXmlUtils;

import org.xml.sax.Attributes;
import org.xml.sax.ErrorHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;
import org.xml.sax.helpers.XMLReaderFactory;

/**
 * XML序列化/反序列化实现类
 * <p>
 * 这个类是kyou中对SchemaDocument和DataDocument的序列化/反序列化的默认实现
 * </p>
 * 
 * @author nuclearg
 */
public class XmlSerializer implements ISchemaSerializer, IDataSerializer {
    
    @Override
    public void serializeSchema(SchemaDocument doc, OutputStream out) {
        if (doc == null)
            throw new KyouException(KyouErr.Schema.Serialization.EmptySchema);
        if (out == null)
            throw new KyouException(KyouErr.Base.Stream.NullOutputStream);
        
        final StringBuffer buffer = new StringBuffer();
        try {
            doc.foreach(new ISchemaVisitor() {
                @Override
                public void docStart(SchemaDocument doc) throws Exception {
                    buffer.append("<?xml version=\"1.0\" encoding=\"utf-8\"?><schema");
                    this.writeAttributes(doc);
                    buffer.append(">");
                }
                
                @Override
                public void docEnd(SchemaDocument doc) throws Exception {
                    buffer.append("</schema>");
                }
                
                @Override
                public void struStart(SchemaStruct stru) throws Exception {
                    buffer.append("<struct");
                    this.writeAttributes(stru);
                    buffer.append(">");
                }
                
                @Override
                public void struEnd(SchemaStruct stru) throws Exception {
                    buffer.append("</struct>");
                }
                
                @Override
                public void field(SchemaField field) throws Exception {
                    buffer.append("<field");
                    this.writeAttributes(field);
                    buffer.append("/>");
                }
                
                private void writeAttributes(SchemaItem item) {
                    if (item.attrs().size() == 0)
                        return;
                    
                    for (Entry<String, String> attr : item.attrs().entrySet())
                        buffer.append(" ").append(attr.getKey()).append("=\"").append(KyouXmlUtils.xmlEncode(attr.getValue())).append("\"");
                }
            });
            
            out.write(buffer.toString().getBytes(KyouRuntimeUtils.utf8));
        } catch (Exception ex) {
            throw new KyouException(KyouErr.Schema.Serialization.XmlSerializeSchemaFail, "schema: " + doc, ex);
        }
    }
    
    @Override
    public SchemaDocument deserializeSchema(InputStream in) {
        if (in == null)
            throw new KyouException(KyouErr.Base.Stream.NullInputStream);
        
        try {
            InputSource source = new InputSource(in);
            
            final SchemaBuilder builder = new SchemaBuilder();
            
            // 使用SAX进行解析
            XMLReader reader = XMLReaderFactory.createXMLReader();
            
            // 进行解析
            reader.setContentHandler(new DefaultHandler() {
                private Map<String, String> atts = new LinkedHashMap<String, String>();
                
                @Override
                public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
                    this.fillAttributes(attributes);
                    
                    if (localName.equals("schema"))
                        ;
                    else if (localName.equals("struct"))
                        builder.beginStruct(this.atts);
                    else if (localName.equals("field"))
                        builder.field(this.atts);
                    else
                        throw new KyouException(KyouErr.Schema.Serialization.InvalidSchemaXmlTag, localName);
                }
                
                @Override
                public void endElement(String uri, String localName, String qName) throws SAXException {
                    if (localName.equals("struct"))
                        builder.endStruct();
                }
                
                @Override
                public void fatalError(SAXParseException ex) throws SAXException {
                    throw new KyouException(KyouErr.Base.Xml.SaxFail, "[" + ex.getLineNumber() + ":" + ex.getColumnNumber() + "] " + ex.getMessage());
                }
                
                private void fillAttributes(Attributes attributes) {
                    atts.clear();
                    for (int i = 0; i < attributes.getLength(); i++)
                        atts.put(attributes.getLocalName(i), attributes.getValue(i));
                }
            });
            reader.setErrorHandler((ErrorHandler) reader.getContentHandler());
            reader.parse(source);
            
            // 返回解析好的SchemaDocument
            return builder.result();
        } catch (Exception ex) {
            throw new KyouException(KyouErr.Schema.Serialization.XmlDeserializeSchemaFail, ex);
        }
    }
    
    @Override
    public void serializeData(DataDocument doc, OutputStream out) {
        if (doc == null)
            throw new KyouException(KyouErr.Data.Serialization.EmptyData);
        if (out == null)
            throw new KyouException(KyouErr.Base.Stream.NullOutputStream);
        
        final StringBuffer buffer = new StringBuffer();
        try {
            buffer.append("<?xml version=\"1.0\" encoding=\"utf-8\"?><data>");
            for (DPath path : doc.map.keySet()) {
                String n = KyouXmlUtils.xmlEncode(path.toString().substring(2));
                String v = KyouXmlUtils.xmlEncode(doc.get(path));
                buffer.append("<").append(n).append(">").append(v).append("</").append(n).append(">");
            }
            
            buffer.append("</data>");
            
            out.write(buffer.toString().getBytes(KyouRuntimeUtils.utf8));
        } catch (Exception ex) {
            throw new KyouException(KyouErr.Data.Serialization.XmlSerializeDataFail, "data: " + doc, ex);
        }
    }
    
    @Override
    public DataDocument deserializeData(InputStream in) {
        if (in == null)
            throw new KyouException(KyouErr.Base.Stream.NullInputStream);
        
        try {
            InputSource source = new InputSource(in);
            
            final DataDocument doc = new DataDocument();
            
            // 使用SAX进行解析
            XMLReader reader = XMLReaderFactory.createXMLReader();
            
            // 进行解析
            reader.setContentHandler(new DefaultHandler() {
                private StringBuilder text = new StringBuilder();
                
                private int level;
                
                @Override
                public void characters(char[] ch, int start, int length) throws SAXException {
                    text.append(ch, start, length);
                }
                
                @Override
                public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
                    this.text = new StringBuilder();
                    
                    this.level++;
                    switch (this.level) {
                        case 1:
                            if (!localName.equals("data"))
                                throw new KyouException(KyouErr.Data.Serialization.InvalidDataXmlTag, localName);
                            break;
                        case 2:
                            break;
                        default:
                            throw new KyouException(KyouErr.Data.Serialization.InvalidDataLevel, "tag: " + localName);
                    }
                }
                
                @Override
                public void endElement(String uri, String localName, String qName) throws SAXException {
                    if (this.level == 2)
                        doc.map.put(new DPath("#." + localName), text.toString());
                    
                    this.level--;
                }
                
                @Override
                public void fatalError(SAXParseException ex) throws SAXException {
                    throw new KyouException(KyouErr.Base.Xml.SaxFail, "[" + ex.getLineNumber() + ":" + ex.getColumnNumber() + "] " + ex.getMessage());
                }
            });
            reader.setErrorHandler((ErrorHandler) reader.getContentHandler());
            reader.parse(source);
            
            // 返回解析好的DataDocument
            return doc;
        } catch (Exception ex) {
            throw new KyouException(KyouErr.Data.Serialization.XmlDeserializeDataFail, ex);
        }
    }
}
