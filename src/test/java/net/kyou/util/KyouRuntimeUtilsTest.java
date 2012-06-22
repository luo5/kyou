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

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.charset.Charset;

import net.kyou.exception.KyouErr;
import net.kyou.exception.KyouException;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class KyouRuntimeUtilsTest {
    @Before
    public void before() {
        new KyouRuntimeUtils();
    }

    @Test
    public void testUtf8() {
        Assert.assertEquals(Charset.forName("utf-8").name(), KyouRuntimeUtils.utf8.name());
    }

    @Test
    public void testDelegate() throws Exception {
        Method method = KyouRuntimeUtils.delegate(Thread.currentThread().getContextClassLoader(), "net.kyou.util.KyouRuntimeUtilsTest", "xxx", String.class);
        Assert.assertEquals(KyouRuntimeUtilsTest.class.getMethod("xxx"), method);
    }

    @Test
    public void testDelegateEx() {
        try {
            KyouRuntimeUtils.delegate(Thread.currentThread().getContextClassLoader(), "XXX", "xxx", null);

            Assert.fail();
        } catch (KyouException ex) {
            Assert.assertEquals(KyouErr.Base.Compile.DelegateStaticMethodFail, ex.err);
            Assert.assertEquals(ClassNotFoundException.class, ex.cause.getClass());
        }

        try {
            KyouRuntimeUtils.delegate(Thread.currentThread().getContextClassLoader(), "net.kyou.util.KyouRuntimeUtilsTest", "asdfasdf", null);
            Assert.fail();
        } catch (KyouException ex) {
            Assert.assertEquals(KyouErr.Base.Compile.DelegateStaticMethodFail, ex.err);
            Assert.assertEquals(NoSuchMethodException.class, ex.cause.getClass());
        }

        try {
            KyouRuntimeUtils.delegate(Thread.currentThread().getContextClassLoader(), "net.kyou.util.KyouRuntimeUtilsTest", "xxx2", String.class);
            Assert.fail();
        } catch (KyouException ex) {
            Assert.assertEquals(KyouErr.Base.Compile.DelegateStaticMethodFail, ex.err);
            Assert.assertEquals(KyouErr.Base.Compile.MethodShouleStatic, ((KyouException) ex.cause).err);
        }

        try {
            KyouRuntimeUtils.delegate(Thread.currentThread().getContextClassLoader(), "net.kyou.util.KyouRuntimeUtilsTest", "xxx", void.class);
            Assert.fail();
        } catch (KyouException ex) {
            Assert.assertEquals(KyouErr.Base.Compile.DelegateStaticMethodFail, ex.err);
            Assert.assertEquals(KyouErr.Base.Compile.MethodReturnTypeMismatch, ((KyouException) ex.cause).err);
        }
    }

    public static String xxx() {
        return null;
    }

    public String xxx2() {
        return null;
    }

    @Test
    public void testCompile() throws Exception {
        String code = "public class test{public static String test(){return \"helloworld\";}}";

        Method method = KyouRuntimeUtils.compile(code, "test", String.class);
        Assert.assertEquals("helloworld", method.invoke(null));
    }

    @Test
    public void testCompile2() throws Exception {
        String code = "public class test{public static String test(){return new AAA().str;} public static class AAA{public final String str =\"helloworld\";}}";

        Method method = KyouRuntimeUtils.compile(code, "test", String.class);
        Assert.assertEquals("helloworld", method.invoke(null));
    }

    @Test
    public void testCompile3() throws Exception {
        String code = "import net.kyou.util.KyouRuntimeUtils; public class test{public static String test(){return KyouRuntimeUtils.utf8.name();}}";

        Method method = KyouRuntimeUtils.compile(code, "test", String.class);
        Assert.assertEquals(Charset.forName("utf-8").name(), method.invoke(null));
    }

    @Test
    public void testCompileEx() throws Exception {
        try {
            KyouRuntimeUtils.compile(null, "xxx", String.class);

            Assert.fail();
        } catch (KyouException ex) {
            Assert.assertEquals(KyouErr.Base.Compile.EmptyCode, ex.err);
        }

        try {
            KyouRuntimeUtils.compile("xxx", null, String.class);

            Assert.fail();
        } catch (KyouException ex) {
            Assert.assertEquals(KyouErr.Base.Compile.EmptyCodeName, ex.err);
        }

        try {

            // Assert.fail();
        } catch (KyouException ex) {
            Assert.assertEquals(KyouErr.Base.Compile.CompilerInitFail, ex.err);
        }

        try {
            KyouRuntimeUtils.compile("XXX", "xxx", String.class);

            Assert.fail();
        } catch (KyouException ex) {
            Assert.assertEquals(KyouErr.Base.Compile.CompileFail, ex.err);
        }

        try {
            Method method = KyouRuntimeUtils.compile("public class AAA{public static void AAA()throws Exception{Class.forName(\"asdfjkl\");}}", "AAA", void.class);
            method.invoke(null);

            Assert.fail();
        } catch (InvocationTargetException ex) {
            Assert.assertEquals(ClassNotFoundException.class, ex.getCause().getClass());
        }
    }
}
