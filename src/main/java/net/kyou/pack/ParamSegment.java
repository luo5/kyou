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
package net.kyou.pack;

import net.kyou.pack.param.Param;
import net.kyou.util.KyouByteOutputStream;

class ParamSegment extends Segment {
    private final Param param;
    
    ParamSegment(Param param) {
        this.param = param;
    }
    
    @Override
    void export(PackContext context, KyouByteOutputStream s) {
        this.param.export(context, s);
    }
}
