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

package org.apache.hadoop.mapred;

import java.io.IOException;
import java.net.InetSocketAddress;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.mapreduce.MRConfig;
import org.apache.hadoop.mapreduce.protocol.ClientProtocol;
import org.apache.hadoop.mapreduce.protocol.ClientProtocolProvider;

/**
 * A new ClientProtocalProvider to support map-collective job,
 * according to YarnClientProtocolProvider
 * 
 * @author zhangbj
 *
 */
public class MapCollectiveClientProtocolProvider extends ClientProtocolProvider {

  public static String MAP_COLLECTIVE_FRAMEWORK_NAME = "map-collective";
  @Override
  public ClientProtocol create(Configuration conf) throws IOException {
    if (MAP_COLLECTIVE_FRAMEWORK_NAME.equals(conf.get(MRConfig.FRAMEWORK_NAME))) {
      return new MapCollectiveRunner(conf);
    }
    return null;
  }

  @Override
  public ClientProtocol create(InetSocketAddress addr, Configuration conf)
      throws IOException {
    return create(conf);
  }

  @Override
  public void close(ClientProtocol clientProtocol) throws IOException {
    // nothing to do
  }
}
