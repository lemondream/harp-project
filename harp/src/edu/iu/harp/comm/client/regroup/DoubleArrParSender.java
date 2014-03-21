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

package edu.iu.harp.comm.client.regroup;

import org.apache.log4j.Logger;

import edu.iu.harp.arrpar.ArrPartition;
import edu.iu.harp.comm.Constants;
import edu.iu.harp.comm.client.DoubleArrReqSender;
import edu.iu.harp.comm.data.ByteArray;
import edu.iu.harp.comm.data.Commutable;
import edu.iu.harp.comm.data.DoubleArray;
import edu.iu.harp.comm.resource.ResourcePool;

/**
 * Currently we build the logic based on the simple logic. No fault tolerance is
 * considered.
 * 
 */
public class DoubleArrParSender extends DoubleArrReqSender {
  /** Class logger */
  private static final Logger LOG = Logger.getLogger(DoubleArrParSender.class);

  private int partitionID;

  public DoubleArrParSender(String host, int port, ResourcePool pool,
    ArrPartition<DoubleArray> partition) {
    super(host, port, partition.getArray(), pool);
    this.partitionID = partition.getPartitionID();
    LOG.info("partitionID " + partitionID);
    this.setCommand(Constants.BYTE_ARRAY_REQUEST);
  }

  @Override
  protected Commutable processData(Commutable data) throws Exception {
    ByteArray array = (ByteArray) super.processData(data);
    int[] metaData = new int[1];
    metaData[0] = this.partitionID;
    array.setMetaData(metaData);
    return array;
  }
}
