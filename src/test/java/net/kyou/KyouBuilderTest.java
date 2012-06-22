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
package net.kyou;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;

import net.kyou.data.DataDocument;
import net.kyou.data.DataDocument.IDataSerializer;
import net.kyou.data.SchemaDocument;
import net.kyou.data.SchemaDocument.ISchemaSerializer;
import net.kyou.pack.PackContext;
import net.kyou.pack.StyleItem;
import net.kyou.pack.param.Param;
import net.kyou.pack.param.ParamTag;
import net.kyou.pack.param.expr.Expr;
import net.kyou.pack.param.expr.ExprFactory;
import net.kyou.pack.param.expr.ExprTag;
import net.kyou.pack.param.expr.ExprTag.ExprPostfix;
import net.kyou.pack.param.expr.ExprTag.ExprType;
import net.kyou.pack.param.expr.endpoint.EndpointExpr;
import net.kyou.pack.param.expr.pipe.PipeExpr;
import net.kyou.util.KyouByteOutputStream;

import org.junit.Assert;
import org.junit.Test;
import org.w3c.dom.Element;

public class KyouBuilderTest {
    
    @Test
    public void testSetSchemaSerializer() {
        KyouBuilder builder = new KyouBuilder();
        builder.setSchemaSerializer(new ISchemaSerializer() {
            
            @Override
            public void serializeSchema(SchemaDocument doc, OutputStream out) {
                throw new RuntimeException("XXXA");
            }
            
            @Override
            public SchemaDocument deserializeSchema(InputStream in) {
                throw new RuntimeException("XXXB");
            }
        });
        
        Kyou kyou = builder.export();
        
        try {
            kyou.saveSchema(new SchemaDocument(), new ByteArrayOutputStream());
            Assert.fail();
        } catch (RuntimeException ex) {
            Assert.assertEquals("XXXA", ex.getMessage());
        }
        try {
            kyou.loadSchema(new ByteArrayInputStream("".getBytes()));
            Assert.fail();
        } catch (RuntimeException ex) {
            Assert.assertEquals("XXXB", ex.getMessage());
        }
    }
    
    @Test
    public void testSetDataSerializer() {
        KyouBuilder builder = new KyouBuilder();
        builder.setDataSerializer(new IDataSerializer() {
            
            @Override
            public void serializeData(DataDocument doc, OutputStream out) {
                throw new RuntimeException("XXXA");
            }
            
            @Override
            public DataDocument deserializeData(InputStream in) {
                throw new RuntimeException("XXXB");
            }
        });
        
        Kyou kyou = builder.export();
        
        try {
            kyou.saveData(new DataDocument(), new ByteArrayOutputStream());
            Assert.fail();
        } catch (RuntimeException ex) {
            Assert.assertEquals("XXXA", ex.getMessage());
        }
        try {
            kyou.loadData(new ByteArrayInputStream("".getBytes()));
            Assert.fail();
        } catch (RuntimeException ex) {
            Assert.assertEquals("XXXB", ex.getMessage());
        }
    }
    
    @SuppressWarnings("unchecked")
    @Test
    public void testRegisterPackParams() throws UnsupportedEncodingException {
        StringBuilder xml = new StringBuilder();
        xml.append("<?xml version='1.0' encoding='utf-8'?>");
        xml.append("<spec>");
        xml.append("<config><encoding>gb2312</encoding></config>");
        xml.append("<style target='#'>");
        xml.append("<format>[[[%]]]</format>");
        xml.append("<test>i2s 123</test>");
        xml.append("</style>");
        xml.append("</spec>");
        
        KyouBuilder builder1 = new KyouBuilder();
        builder1.registerPackParams(Param1.class);
        Kyou kyou1 = builder1.export();
        byte[] bytes1 = kyou1.pack(new DataDocument(), new SchemaDocument(), kyou1.loadStyle(new ByteArrayInputStream(xml.toString().getBytes("utf-8"))));
        org.junit.Assert.assertArrayEquals("[[[Param1]]]".getBytes("utf-8"), bytes1);
        
        KyouBuilder builder2 = new KyouBuilder();
        builder2.registerPackParams(Param1.class, Param2.class);
        Kyou kyou2 = builder2.export();
        byte[] bytes2 = kyou2.pack(new DataDocument(), new SchemaDocument(), kyou2.loadStyle(new ByteArrayInputStream(xml.toString().getBytes("utf-8"))));
        org.junit.Assert.assertArrayEquals("[[[Param2]]]".getBytes("utf-8"), bytes2);
    }
    
    @ParamTag(name = "test", type = ExprType.String)
    static class Param1 extends Param {
        
        Param1(Element e, Charset encoding, StyleItem style, ExprFactory exprFactory) {
            super(e, encoding, style, exprFactory);
        }
        
        @Override
        protected void export(Object v, PackContext context, KyouByteOutputStream s) {
            s.write("Param1".getBytes(Charset.forName("utf-8")));
        }
    }
    
    @ParamTag(name = "test", type = ExprType.String)
    static class Param2 extends Param {
        
        Param2(Element e, Charset encoding, StyleItem style, ExprFactory exprFactory) {
            super(e, encoding, style, exprFactory);
        }
        
        @Override
        protected void export(Object v, PackContext context, KyouByteOutputStream s) {
            s.write("Param2".getBytes(Charset.forName("utf-8")));
        }
    }
    
    @SuppressWarnings("unchecked")
    @Test
    public void testRegisterPackEndpointExprs() throws UnsupportedEncodingException {
        StringBuilder xml = new StringBuilder();
        xml.append("<?xml version='1.0' encoding='utf-8'?>");
        xml.append("<spec>");
        xml.append("<config><encoding>gb2312</encoding></config>");
        xml.append("<style target='#'>");
        xml.append("<format>[[[%]]]</format>");
        xml.append("<str>test</str>");
        xml.append("</style>");
        xml.append("</spec>");
        
        KyouBuilder builder1 = new KyouBuilder();
        builder1.registerPackEndpointExprs(Endpoint1.class);
        Kyou kyou1 = builder1.export();
        byte[] bytes1 = kyou1.pack(new DataDocument(), new SchemaDocument(), kyou1.loadStyle(new ByteArrayInputStream(xml.toString().getBytes("utf-8"))));
        org.junit.Assert.assertArrayEquals("[[[Endpoint1]]]".getBytes("utf-8"), bytes1);
        
        KyouBuilder builder2 = new KyouBuilder();
        builder2.registerPackEndpointExprs(Endpoint1.class, Endpoint2.class);
        Kyou kyou2 = builder2.export();
        byte[] bytes2 = kyou2.pack(new DataDocument(), new SchemaDocument(), kyou2.loadStyle(new ByteArrayInputStream(xml.toString().getBytes("utf-8"))));
        org.junit.Assert.assertArrayEquals("[[[Endpoint2]]]".getBytes("utf-8"), bytes2);
    }
    
    @ExprTag(name = "test", postfix = ExprPostfix.Dual, type = ExprType.String)
    static class Endpoint1 extends EndpointExpr {
        
        Endpoint1(String postfix, Param param, Charset encoding) {
            super(postfix, param, encoding);
        }
        
        @Override
        protected Object eval(PackContext context) {
            return "Endpoint1";
        }
    }
    
    @ExprTag(name = "test", postfix = ExprPostfix.Dual, type = ExprType.String)
    static class Endpoint2 extends EndpointExpr {
        
        Endpoint2(String postfix, Param param, Charset encoding) {
            super(postfix, param, encoding);
        }
        
        @Override
        protected Object eval(PackContext context) {
            return "Endpoint2";
        }
    }
    
    @SuppressWarnings("unchecked")
    @Test
    public void testRegisterPackPipeExprs() throws UnsupportedEncodingException {
        StringBuilder xml = new StringBuilder();
        xml.append("<?xml version='1.0' encoding='utf-8'?>");
        xml.append("<spec>");
        xml.append("<config><encoding>gb2312</encoding></config>");
        xml.append("<style target='#'>");
        xml.append("<format>[[[%]]]</format>");
        xml.append("<str>test 123</str>");
        xml.append("</style>");
        xml.append("</spec>");
        
        KyouBuilder builder1 = new KyouBuilder();
        builder1.registerPackPipeExprs(Pipe1.class);
        Kyou kyou1 = builder1.export();
        byte[] bytes1 = kyou1.pack(new DataDocument(), new SchemaDocument(), kyou1.loadStyle(new ByteArrayInputStream(xml.toString().getBytes("utf-8"))));
        org.junit.Assert.assertArrayEquals("[[[Pipe1|123]]]".getBytes("utf-8"), bytes1);
        
        KyouBuilder builder2 = new KyouBuilder();
        builder2.registerPackPipeExprs(Pipe1.class, Pipe2.class);
        Kyou kyou2 = builder2.export();
        byte[] bytes2 = kyou1.pack(new DataDocument(), new SchemaDocument(), kyou2.loadStyle(new ByteArrayInputStream(xml.toString().getBytes("utf-8"))));
        org.junit.Assert.assertArrayEquals("[[[Pipe2|123]]]".getBytes("utf-8"), bytes2);
    }
    
    @ExprTag(name = "test", postfix = ExprPostfix.Dual, type = ExprType.String, require = ExprType.Integer)
    static class Pipe1 extends PipeExpr {
        
        Pipe1(String expr, Param param, Expr arg, Charset encoding) {
            super(expr, param, arg, encoding);
        }
        
        @Override
        protected Object eval(Object result, PackContext context) {
            return "Pipe1|" + result;
        }
    }
    
    @ExprTag(name = "test", postfix = ExprPostfix.Dual, type = ExprType.String, require = ExprType.Integer)
    static class Pipe2 extends PipeExpr {
        
        public Pipe2(String expr, Param param, Expr arg, Charset encoding) {
            super(expr, param, arg, encoding);
        }
        
        @Override
        protected Object eval(Object result, PackContext context) {
            return "Pipe2|" + result;
        }
    }
}
