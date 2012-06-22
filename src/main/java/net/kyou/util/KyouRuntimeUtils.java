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

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.net.URI;
import java.nio.charset.Charset;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.Arrays;
import java.util.Iterator;

import javax.tools.JavaCompiler;
import javax.tools.JavaCompiler.CompilationTask;
import javax.tools.JavaFileObject;
import javax.tools.JavaFileObject.Kind;
import javax.tools.SimpleJavaFileObject;
import javax.tools.StandardJavaFileManager;
import javax.tools.ToolProvider;

import net.kyou.exception.KyouErr;
import net.kyou.exception.KyouException;

/**
 * 工具类 提供一些基础的工具函数
 * 
 * @author nuclearg
 */
public class KyouRuntimeUtils {
    /**
     * utf8编码。这是kyou内部使用的标准编码。
     */
    public static final Charset utf8 = Charset.forName("utf8");
    
    /**
     * 获取指向某个静态方法的函数指针
     * 
     * @param cl
     *            类加载器
     * @param clsName
     *            类名
     * @param methodName
     *            方法名
     * @param returnType
     *            方法的返回值 当提供的参数为null时默认为void.class
     * @param parameterTypes
     *            方法的参数列表
     * @return 指向期望的静态方法的指针
     */
    public static Method delegate(ClassLoader cl, String clsName, String methodName, Class<?> returnType, Class<?>... parameterTypes) {
        if (returnType == null)
            returnType = void.class;
        
        try {
            Class<?> cls = cl.loadClass(clsName);
            Method method = cls.getMethod(methodName, parameterTypes);
            if (method.getReturnType() != returnType)
                throw new KyouException(KyouErr.Base.Compile.MethodReturnTypeMismatch, "expect: " + returnType.getName() + " actually: " + method.getReturnType());
            if ((method.getModifiers() & Modifier.STATIC) == 0)
                throw new KyouException(KyouErr.Base.Compile.MethodShouleStatic);
            
            return method;
        } catch (Exception ex) {
            throw new KyouException(KyouErr.Base.Compile.DelegateStaticMethodFail, returnType.getName() + " " + clsName + "::" + methodName + Arrays.toString(parameterTypes), ex);
        }
    }
    
    /**
     * 编译一个类并返回指向该类中的同名静态方法的函数指针
     * 
     * @param code
     *            完整的编译单元的源代码
     * @param name
     *            类名/方法名 这两个名称必须一致
     * @param returnType
     *            方法的返回值 当提供的参数为null时默认为void.class
     * @param parameterTypes
     *            方法的参数列表
     * @return 指向期望的静态方法的指针
     */
    public static Method compile(final String code, final String name, Class<?> returnType, Class<?>... parameterTypes) {
        if (code == null || code.length() == 0)
            throw new KyouException(KyouErr.Base.Compile.EmptyCode);
        if (name == null || name.length() == 0)
            throw new KyouException(KyouErr.Base.Compile.EmptyCodeName);
        
        // 初始化编译环境
        JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
        if (compiler == null)
            throw new KyouException(KyouErr.Base.Compile.CompilerInitFail);
        
        try {
            // 将被编译的文件装载到编译器中
            final StandardJavaFileManager fileManager = compiler.getStandardFileManager(null, null, null);
            JavaFileObject file = new SimpleJavaFileObject(new URI(name + ".java"), Kind.SOURCE) {
                @Override
                public CharSequence getCharContent(boolean ignoreEncodingErrors) throws IOException {
                    return code;
                }
            };
            
            // 建立编译任务
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            CompilationTask task = compiler.getTask(new OutputStreamWriter(out), fileManager, null, null, null, Arrays.asList(file));
            
            // 执行编译过程
            if (!task.call())
                throw new KyouException(KyouErr.Base.Compile.CompileFail, new String(out.toByteArray()));
            
            // 使用一个自定义的ClassLoader装载编译好的类
            DynamicCompilerClassLoader loader = AccessController.doPrivileged(new PrivilegedAction<DynamicCompilerClassLoader>() {
                
                @Override
                public DynamicCompilerClassLoader run() {
                    return new DynamicCompilerClassLoader(fileManager);
                }
            });
            
            // 返回一个指向期望的方法的引用
            return delegate(loader, name, name, returnType, parameterTypes);
        } catch (Exception ex) {
            throw new KyouException(KyouErr.Base.Compile.CompileFail, ex);
        } finally {
            // 清理掉编译过程中建立的类文件
            File[] files = new File(new File("").getAbsolutePath()).listFiles(new FileFilter() {
                @Override
                public boolean accept(File f) {
                    String fn = f.getName();
                    return fn.equals(name + ".class") || fn.startsWith(name + "$") && fn.endsWith(".class");
                }
            });
            
            for (File file : files)
                file.deleteOnExit();
        }
    }
    
    /**
     * 
     * 
     * @author nuclearg
     */
    private static class DynamicCompilerClassLoader extends ClassLoader {
        private final StandardJavaFileManager fileManager;
        
        DynamicCompilerClassLoader(StandardJavaFileManager fileManager) {
            this.fileManager = fileManager;
        }
        
        @Override
        protected Class<?> findClass(String name) throws ClassNotFoundException {
            try {
                Iterator<? extends JavaFileObject> iterator = this.fileManager.getJavaFileObjects(name + ".class").iterator();
                InputStream in = iterator.next().openInputStream();
                
                byte[] bytes = new byte[in.available()];
                in.read(bytes);
                
                return super.defineClass(name, bytes, 0, bytes.length);
            } catch (Throwable ex) {
                throw new ClassNotFoundException(name);
            }
        }
    }
}
