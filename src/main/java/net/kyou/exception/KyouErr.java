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
package net.kyou.exception;

import java.io.Serializable;

import net.kyou.data.DataDocument;
import net.kyou.util.KyouByteOutputStream;

/**
 * 错误信息条目
 * <p>
 * kyou中所有已知的错误信息都会在此处进行记录，
 * </p>
 * 
 * @author NuclearG (<a href="nuclearg@163.com">nuclearg@163.com</a>)
 */
public final class KyouErr implements Serializable {
    private static final long serialVersionUID = -5501658273930338778L;
    
    /**
     * 错误的描述
     */
    private final String message;
    
    /**
     * 初始化一条错误描述信息
     * 
     * @param name
     *            该错误条目的名称
     */
    private KyouErr() {
        this.message = ErrCache.reflectMessage();
    }
    
    @Override
    public String toString() {
        return this.message;
    }
    
    /**
     * 内部错误 表示未预料到的错误
     * <p>
     * 程序在运行过程中抛出了未被考虑到的错误<br/>
     * 如果产生了该错误，请提交bug
     * </p>
     */
    public static final KyouErr Unknown = new KyouErr();
    /**
     * 内部错误 表示未预料到的操作
     * <p>
     * 当前的执行点位于一个未预料的分支中。最典型的情形是程序执行到了一个之前未被考虑到的switch分支中。<br/>
     * 如果产生了该错误，请提交bug
     * </p>
     */
    public static final KyouErr Unexpected = new KyouErr();
    
    /**
     * 进行一些基础操作时发生的错误
     */
    public static class Base {
        public static final KyouErr UnsupportedCharset = new KyouErr();
        
        /**
         * 进行流操作时发生的错误
         */
        public static class Stream {
            public static final KyouErr NullInputStream = new KyouErr();
            public static final KyouErr NullOutputStream = new KyouErr();
            public static final KyouErr OpenInputStreamFail = new KyouErr();
            public static final KyouErr OpenOutputStreamFail = new KyouErr();
            public static final KyouErr ReadInputStreamFail = new KyouErr();
            public static final KyouErr WriteOutputStreamFail = new KyouErr();
            public static final KyouErr CloseInputStreamFail = new KyouErr();
            public static final KyouErr CloseOutputStreamFail = new KyouErr();
        }
        
        /**
         * 进行xml操作时发生的错误
         */
        public static class Xml {
            public static final KyouErr ReadFail = new KyouErr();
            public static final KyouErr SaxFail = new KyouErr();
            // TODO 加上xml验证功能
            // public static final KyouErr XmlVerifyFail = new KyouErr();
            public static final KyouErr EmptyXmlNode = new KyouErr();
            public static final KyouErr EmptyXPath = new KyouErr();
            public static final KyouErr XPathSyntaxError = new KyouErr();
        }
        
        /**
         * 进行代码动态编译/挂载时发生的错误
         */
        public static class Compile {
            public static final KyouErr CompilerInitFail = new KyouErr();
            public static final KyouErr EmptyCode = new KyouErr();
            public static final KyouErr EmptyCodeName = new KyouErr();
            public static final KyouErr CompileFail = new KyouErr();
            
            public static final KyouErr DelegateStaticMethodFail = new KyouErr();
            public static final KyouErr MethodReturnTypeMismatch = new KyouErr();
            public static final KyouErr MethodShouleStatic = new KyouErr();
        }
        
        /**
         * 进行{@link KyouByteOutputStream}操作时发生的错误
         */
        public static class ByteStream {
            public static final KyouErr IllegalBackspaceNum = new KyouErr();
        }
        
        /**
         * 进行{@link net.kyou.util.KyouString}操作时发生的错误
         */
        public static class KyouString {
            public static final KyouErr EmptyKyouString = new KyouErr();
        }
    }
    
    /**
     * 进行Schema操作时发生的错误
     */
    public static class Schema {
        public static final KyouErr EmptyAttributeName = new KyouErr();
        public static final KyouErr EmptySchemaItemName = new KyouErr();
        
        public static final KyouErr AddEmptyChild = new KyouErr();
        public static final KyouErr AddDocumentAsChild = new KyouErr();
        public static final KyouErr AddSelfAsChild = new KyouErr();
        public static final KyouErr AddParentAsChild = new KyouErr();
        
        public static final KyouErr ForEachFail = new KyouErr();
        
        public static final KyouErr InvalidSchemaBuilderArguments = new KyouErr();
        
        /**
         * 对Schema进行序列化/反序列化时发生的错误
         */
        public static class Serialization {
            public static final KyouErr EmptySchema = new KyouErr();
            
            public static final KyouErr XmlSerializeSchemaFail = new KyouErr();
            public static final KyouErr XmlDeserializeSchemaFail = new KyouErr();
            
            public static final KyouErr InvalidSchemaXmlTag = new KyouErr();
        }
    }
    
    /**
     * 对{@link DataDocument}进行操作时发生的错误
     */
    public static class Data {
        public static final KyouErr PathNotExist = new KyouErr();
        public static final KyouErr PathNotEditable = new KyouErr();
        public static final KyouErr RequireArrayPath = new KyouErr();
        public static final KyouErr RequireArrayElementPath = new KyouErr();
        public static final KyouErr RequireNotArrayElementPath = new KyouErr();
        
        public static final KyouErr ArrayElementCountParseFail = new KyouErr();
        
        /**
         * 对Data进行序列化/反序列化时发生的错误
         */
        public static class Serialization {
            public static final KyouErr EmptyData = new KyouErr();
            
            public static final KyouErr XmlSerializeDataFail = new KyouErr();
            public static final KyouErr XmlDeserializeDataFail = new KyouErr();
            
            public static final KyouErr InvalidDataXmlTag = new KyouErr();
            public static final KyouErr InvalidDataLevel = new KyouErr();
        }
    }
    
    /**
     * 初始化Kyou实例时发生的错误
     */
    public static class Init {
        public static final KyouErr ParamInitFail = new KyouErr();
        public static final KyouErr EmptyParamClass = new KyouErr();
        public static final KyouErr ParamTagNotFound = new KyouErr();
        public static final KyouErr ParamConstructorMismatch = new KyouErr();
        
        public static final KyouErr ExprInitFail = new KyouErr();
        public static final KyouErr EmptyExprClass = new KyouErr();
        public static final KyouErr ExprNotSupported = new KyouErr();
        public static final KyouErr ExprTagNotFound = new KyouErr();
        public static final KyouErr ExprConstructorMismatch = new KyouErr();
    }
    
    /**
     * {@link net.kyou.pack.StyleSpecification}中的语法或语义错误
     */
    public static class StyleSpec {
        public static final KyouErr SpecificationParseFail = new KyouErr();
        public static final KyouErr SpecifcationSyntaxError = new KyouErr();
        public static final KyouErr EmptyParamFactory = new KyouErr();
        public static final KyouErr EmptyExprFactory = new KyouErr();
        
        /**
         * 样式单元级别，包括参数的语法或语义错误
         */
        public static class Style {
            public static final KyouErr EmptyTarget = new KyouErr();
            public static final KyouErr EmptyFormat = new KyouErr();
            public static final KyouErr InsufficientParams = new KyouErr();
            
            public static final KyouErr ParamCreateFail = new KyouErr();
            public static final KyouErr InvalidParamTag = new KyouErr();
            public static final KyouErr InvalidSegmentParam = new KyouErr();
        }
        
        /**
         * 表达式级别的语法或语义错误
         */
        public static class Expr {
            public static final KyouErr EmptyExpr = new KyouErr();
            
            public static final KyouErr InitExprFail = new KyouErr();
            public static final KyouErr InvalidEndpointExprTag = new KyouErr();
            public static final KyouErr InvalidPipeExprTag = new KyouErr();
            
            public static final KyouErr InvalidExprResultType = new KyouErr();
            public static final KyouErr InvalidExprPostfix = new KyouErr();
            
            public static final KyouErr PostfixRequired = new KyouErr();
            public static final KyouErr PostfixNotRequired = new KyouErr();
            public static final KyouErr PostfixRequiresPositiveInteger = new KyouErr();
        }
        
        /**
         * 解析格式字符串时发生的错误
         */
        public static class Format {
            public static final KyouErr EmptyFormatString = new KyouErr();
            public static final KyouErr FormatStringSyntaxError = new KyouErr();
            public static final KyouErr UnsupportedEscapeSequence = new KyouErr();
            public static final KyouErr HexStringSyntaxError = new KyouErr();
            
            public static final KyouErr SimpleFormatStringRequired = new KyouErr();
        }
    }
    
    /**
     * {@link DPath}语法错误或进行{@link DPath}操作时发生的错误
     * 
     * @author nuclearg
     */
    public static class DPath {
        public static final KyouErr EmptyDPath = new KyouErr();
    }
    
    /**
     * {@link DQuery}语法错误或进行{@link DQuery}操作时发生的错误
     */
    public static class DQuery {
        public static final KyouErr EmptyDQuery = new KyouErr();
        
        public static final KyouErr EmptyDQuerySegment = new KyouErr();
        public static final KyouErr DQuerySyntaxError = new KyouErr();
        
        public static final KyouErr EmptyDQuerySegmentBody = new KyouErr();
        
        /**
         * 解析约束条件或进行约束条件判断时发生的错误
         */
        public static class Restriction {
            public static final KyouErr EmptyDQueryRestriction = new KyouErr();
            public static final KyouErr SyntaxError = new KyouErr();
            public static final KyouErr RightBracketMismatch = new KyouErr();
            
            public static final KyouErr IllegalArrayIndex = new KyouErr();
            
            public static final KyouErr FunctionSyntaxError = new KyouErr();
            public static final KyouErr IllegalFunctionName = new KyouErr();
            public static final KyouErr FunctionConditionParseFail = new KyouErr();
            public static final KyouErr FunctionValueParseFail = new KyouErr();
            public static final KyouErr IntegerFunctionValueRequired = new KyouErr();
        }
    }
    
    /**
     * 进行组包相关操作时发生的错误
     */
    public static class Pack {
        public static final KyouErr EmptySchema = new KyouErr();
        public static final KyouErr EmptyData = new KyouErr();
        public static final KyouErr EmptySpecifcation = new KyouErr();
        
        public static final KyouErr PackFail = new KyouErr();
        
        public static final KyouErr NoStyleUnitSuitable = new KyouErr();
        
        public static final KyouErr ParseStringToIntFail = new KyouErr();
        
        public static final KyouErr RequireSchemaStruct = new KyouErr();
        public static final KyouErr RequireSchemaField = new KyouErr();
        public static final KyouErr RequireSchemaArray = new KyouErr();
        public static final KyouErr RequireSchemaNotArray = new KyouErr();
        public static final KyouErr RequireSchemaStructArray = new KyouErr();
        public static final KyouErr RequireSchemaFieldArray = new KyouErr();
        
        public static final KyouErr FieldNotFound = new KyouErr();
        
        public static final KyouErr InvalidRefId = new KyouErr();
    }
}
