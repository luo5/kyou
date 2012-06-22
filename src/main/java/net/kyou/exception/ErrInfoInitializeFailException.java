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

/**
 * 当初始化kyou的异常信息列表失败时抛出此异常。该异常表示kyou的初始化失败。
 * 
 * @author nuclearg
 */
class ErrInfoInitializeFailException extends RuntimeException {
    private static final long serialVersionUID = -632016606650990197L;

    ErrInfoInitializeFailException() {
        super();
    }

    ErrInfoInitializeFailException(Throwable ex) {
        super(ex);
    }
}
