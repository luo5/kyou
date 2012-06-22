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

import org.apache.commons.lang.SystemUtils;
import org.apache.commons.lang.exception.ExceptionUtils;

/**
 * kyou抛出的异常
 * <p>
 * kyou只会抛出这一种类型的异常
 * </p>
 * 
 * @author NuclearG (<a href="nuclearg@163.com">nuclearg@163.com</a>)
 */
public class KyouException extends RuntimeException {
    private static final long serialVersionUID = -2524598532485352517L;
    
    /**
     * 该异常的条目
     */
    public final KyouErr err;
    /**
     * 引发该异常的异常
     */
    public final Throwable cause;
    
    /**
     * 初始化一个异常
     * 
     * @param err
     *            错误信息条目
     */
    public KyouException(KyouErr err) {
        super(err.toString() + printCurrentThreadStackTrace(3));
        this.err = err;
        this.cause = null;
    }
    
    /**
     * 初始化一个异常
     * 
     * @param err
     *            错误信息条目
     * @param msg
     *            对该异常的描述信息
     */
    public KyouException(KyouErr err, String msg) {
        super(err.toString() + " - " + msg + printCurrentThreadStackTrace(3));
        this.err = err;
        this.cause = null;
    }
    
    /**
     * 初始化一个异常
     * 
     * @param err
     *            错误信息条目
     * @param cause
     *            引发该异常的异常
     */
    public KyouException(KyouErr err, Throwable cause) {
        super(err + "\r\n >> " + printEx(cause));
        this.err = err;
        this.cause = cause;
    }
    
    /**
     * 初始化一个异常
     * 
     * @param err
     *            错误信息条目
     * @param msg
     *            对该异常的描述信息
     * @param cause
     *            引发该异常的异常
     */
    public KyouException(KyouErr err, String msg, Throwable cause) {
        super(err + " - " + msg + "\r\n >> " + printEx(cause));
        this.err = err;
        this.cause = cause;
    }
    
    /**
     * 格式化异常信息
     * 
     * @param ex
     *            异常对象
     * @return 该异常的格式化后的信息
     */
    private static String printEx(Throwable ex) {
        if (ex instanceof KyouException)
            return ex.getMessage();
        else
            return ExceptionUtils.getFullStackTrace(ex);
    }
    
    /**
     * 获取当前线程的调用栈的字符串形式，其形式类似于异常的printStackTrace的输出结果
     * 
     * @param ignoreLevels
     *            忽略掉的调用层数
     * @return 当前线程的调用栈的字符串形式
     */
    private static String printCurrentThreadStackTrace(int ignoreLevels) {
        StackTraceElement[] stacktraces = Thread.currentThread().getStackTrace();
        
        StringBuilder builder = new StringBuilder();
        for (int i = ignoreLevels; i < stacktraces.length; i++)
            builder.append(SystemUtils.LINE_SEPARATOR).append("at ").append(stacktraces[i]);
        
        return builder.toString();
    }
}
