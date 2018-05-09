/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package kafka.common

import org.apache.kafka.common.protocol.Errors

case class OffsetMetadata(offset: Long, metadata: String = OffsetMetadata.NoMetadata) {
  override def toString = "OffsetMetadata[%d,%s]"
    .format(offset,
    if (metadata != null && metadata.length > 0) metadata else "NO_METADATA")
}
/*
long型的位移信息
字符串表示的metadata信息
*/
object OffsetMetadata {
  val InvalidOffset: Long = -1L
  val NoMetadata: String = ""

  val InvalidOffsetMetadata = OffsetMetadata(OffsetMetadata.InvalidOffset, OffsetMetadata.NoMetadata)
}


case class OffsetAndMetadata(offsetMetadata: OffsetMetadata,
                             commitTimestamp: Long = org.apache.kafka.common.requests.OffsetCommitRequest.DEFAULT_TIMESTAMP,
                             expireTimestamp: Long = org.apache.kafka.common.requests.OffsetCommitRequest.DEFAULT_TIMESTAMP) {

  def offset = offsetMetadata.offset

  def metadata = offsetMetadata.metadata

  override def toString = "[%s,CommitTime %d,ExpirationTime %d]".format(offsetMetadata, commitTimestamp, expireTimestamp)
}

object OffsetAndMetadata {
  def apply(offset: Long, metadata: String, commitTimestamp: Long, expireTimestamp: Long) = new OffsetAndMetadata(OffsetMetadata(offset, metadata), commitTimestamp, expireTimestamp)

  def apply(offset: Long, metadata: String, timestamp: Long) = new OffsetAndMetadata(OffsetMetadata(offset, metadata), timestamp)

  def apply(offset: Long, metadata: String) = new OffsetAndMetadata(OffsetMetadata(offset, metadata))

  def apply(offset: Long) = new OffsetAndMetadata(OffsetMetadata(offset, OffsetMetadata.NoMetadata))
}

case class OffsetMetadataAndError(offsetMetadata: OffsetMetadata, error: Errors = Errors.NONE) {
  def offset = offsetMetadata.offset

  def metadata = offsetMetadata.metadata

  override def toString = "[%s, Error=%s]".format(offsetMetadata, error)
}
/*
NoOffset: 无效位移，无元数据(metadata)，无错误异常

OffsetsLoading: 无效位移，无metadata，位移加载中异常

NotOffsetManagerForGroup: 无效位移，无metadata，consumer无对应coordinator异常

UnknownTopicOrPartition: 无效位移，无metadata，未知topic或分区ID不在有效范围内[0, numPartitions-1]
 */
object OffsetMetadataAndError {
  val NoOffset = OffsetMetadataAndError(OffsetMetadata.InvalidOffsetMetadata, Errors.NONE)
  val GroupLoading = OffsetMetadataAndError(OffsetMetadata.InvalidOffsetMetadata, Errors.COORDINATOR_LOAD_IN_PROGRESS)
  val UnknownMember = OffsetMetadataAndError(OffsetMetadata.InvalidOffsetMetadata, Errors.UNKNOWN_MEMBER_ID)
  val NotCoordinatorForGroup = OffsetMetadataAndError(OffsetMetadata.InvalidOffsetMetadata, Errors.NOT_COORDINATOR)
  val GroupCoordinatorNotAvailable = OffsetMetadataAndError(OffsetMetadata.InvalidOffsetMetadata, Errors.COORDINATOR_NOT_AVAILABLE)
  val UnknownTopicOrPartition = OffsetMetadataAndError(OffsetMetadata.InvalidOffsetMetadata, Errors.UNKNOWN_TOPIC_OR_PARTITION)
  val IllegalGroupGenerationId = OffsetMetadataAndError(OffsetMetadata.InvalidOffsetMetadata, Errors.ILLEGAL_GENERATION)

  def apply(offset: Long) = new OffsetMetadataAndError(OffsetMetadata(offset, OffsetMetadata.NoMetadata), Errors.NONE)

  def apply(error: Errors) = new OffsetMetadataAndError(OffsetMetadata.InvalidOffsetMetadata, error)

  def apply(offset: Long, metadata: String, error: Errors) = new OffsetMetadataAndError(OffsetMetadata(offset, metadata), error)
}



