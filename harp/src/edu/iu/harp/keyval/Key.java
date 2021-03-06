/*
 * Copyright 2014 Indiana University
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package edu.iu.harp.keyval;

import edu.iu.harp.comm.data.StructObject;

public abstract class Key extends StructObject {

  public abstract int hashCode();

  public boolean equals(Object obj) {
    if (this.getClass().equals(obj.getClass())) {
      return this.hashCode() == obj.hashCode();
    } else {
      return false;
    }
  }
}
