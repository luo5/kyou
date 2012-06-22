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
package net.kyou.pack.param;

import org.junit.Test;

public class StringSegmentTest {
    @Test
    public void test() {
        TEST.test("<str>i2s 123</str>", "123");
        TEST.test("<str encoding='gbk'>b2s text.我人有的和</str>", "我人有的和", "gbk");
        TEST.test("<str len='10'>i2s 123</str>", "123       ");
        // TODO fixme
//        TEST.test("<str len='10' align='left' padding='_'>i2s 123</str>", "123_______");
//        TEST.test("<str len='10' align='right' padding='0'>i2s 123</str>", "0000000123");
//        TEST.test("<str len='5' align='right'>i2s 1234567890</str>", "67890");
//        TEST.test("<str len='5' align='left'>i2s 1234567890</str>", "12345");

    }
    
    @Test
    public void testEx() throws SecurityException, NoSuchFieldException, IllegalArgumentException, IllegalAccessException {
        
    }
}