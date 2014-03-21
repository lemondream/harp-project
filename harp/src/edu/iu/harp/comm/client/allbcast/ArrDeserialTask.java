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

package edu.iu.harp.comm.client.allbcast;

import edu.iu.harp.arrpar.ArrPartition;
import edu.iu.harp.collective.Task;
import edu.iu.harp.comm.client.regroup.ArrParGetter;
import edu.iu.harp.comm.data.Array;
import edu.iu.harp.comm.data.ByteArray;
import edu.iu.harp.comm.resource.ResourcePool;

public class ArrDeserialTask<A extends Array<?>> extends
  Task<ByteArray, ArrPartition<A>> {
  private final ResourcePool resourcePool;
  private final Class<A> aClass;

  public ArrDeserialTask(ResourcePool pool, Class<A> aClass) {
    this.resourcePool = pool;
    this.aClass = aClass;
  }

  @Override
  public ArrPartition<A> run(ByteArray byteArray) throws Exception {
    int partitionID = byteArray.getMetaData()[0];
    A array = ArrParGetter.desiealizeToArray(byteArray, resourcePool, aClass);
    if (array != null) {
      ArrPartition<A> partition = new ArrPartition<A>(array, partitionID);
      resourcePool.getByteArrayPool().releaseArrayInUse(byteArray.getArray());
      return partition;
    }
    return null;
  }
}
