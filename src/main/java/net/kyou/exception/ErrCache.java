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

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;

import net.kyou.util.KyouRuntimeUtils;

import org.apache.log4j.Logger;

/**
 * 一个内部全局缓存，用于保存异常的信息。该信息是从err.properties和err.LOCALE.properties中读取的。
 * 
 * @author NuclearG (<a href="nuclearg@163.com">nuclearg@163.com</a>)
 */
class ErrCache {
    private static final Logger logger = Logger.getLogger(ErrCache.class);
    
    /**
     * properties文件中提供的错误信息列表
     */
    private final static Map<String, String> messages = new LinkedHashMap<String, String>();
    
    static {
        // 读取err.properites
        fillErrMessages("net/kyou/exception/err.properties");
        // 读取err.LOCALE.properties 将会覆盖err.properties中的同名条目
        fillErrMessages("net/kyou/exception/err." + Locale.getDefault() + ".properties");
    }
    
    /**
     * 获取当前正被初始化的Err对象的异常描述信息
     * <p>
     * 该函数仅供Err类的构造函数调用
     * </p>
     * 
     * @return 返回当前正被初始化的Err对象的异常描述信息
     */
    static String reflectMessage() {
        try {
            // 获取当前正被初始化的Err对象被定义的类
            Class<?> hostCls = Class.forName(Thread.currentThread().getStackTrace()[4].getClassName());
            
            if (Thread.currentThread().getStackTrace()[3].getMethodName().equals("<clinit>"))
                hostCls = KyouErr.class;
            
            // 遍历该类，取出第一个仍为null的public static final域。这个域应该是当前正被初始化的域。
            for (Field field : hostCls.getDeclaredFields())
                if (field.getModifiers() == (Modifier.PUBLIC | Modifier.STATIC | Modifier.FINAL))
                    if (field.get(null) == null) {
                        // 得到了这个域之后，拼这个域的名称，名称类似于"Err.Base.UnsupportedEncoding"
                        StringBuilder builder = new StringBuilder(field.getName());
                        while (hostCls != KyouErr.class) {
                            builder.insert(0, ".").insert(0, hostCls.getSimpleName());
                            hostCls = hostCls.getDeclaringClass();
                        }
                        builder.insert(0, "Err.");
                        
                        String name = builder.toString();
                        
                        // 在messages中查询，如果存在相应的异常信息的返回该异常信息，否则返回域名称。
                        return messages.containsKey(name) ? messages.get(name) : name;
                    }
            
            throw new ErrInfoInitializeFailException();
        } catch (Exception ex) {
            throw new ErrInfoInitializeFailException(ex);
        }
    }
    
    /**
     * 用于将指定名称的资源以资源文件的形式读入后装载到messages中
     * 
     * @param resourceName
     *            资源的名称 该资源应当能被Thread.currentThread().getContextClassLoader()访问到
     */
    private static void fillErrMessages(String resourceName) {
        InputStream s = Thread.currentThread().getContextClassLoader().getResourceAsStream(resourceName);
        
        Properties p = new Properties();
        
        if (s != null) {
            InputStreamReader r = new InputStreamReader(s, KyouRuntimeUtils.utf8);
            try {
                p.load(r);
            } catch (Exception ex) {
                // 忽略
                logger.warn("Error while loading " + resourceName, ex);
            } finally {
                try {
                    r.close();
                } catch (IOException ex) {
                    // 忽略
                }
                try {
                    s.close();
                } catch (IOException ex) {
                    // 忽略
                }
            }
        }
        
        for (Object key : p.keySet()) {
            String msg = p.getProperty((String) key);
            
            if (msg != null && msg.length() > 0)
                messages.put((String) key, msg);
        }
    }
}
