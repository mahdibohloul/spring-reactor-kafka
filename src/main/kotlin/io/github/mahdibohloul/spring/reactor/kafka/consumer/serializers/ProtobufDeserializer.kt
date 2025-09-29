package io.github.mahdibohloul.spring.reactor.kafka.consumer.serializers

import com.google.protobuf.GeneratedMessageV3
import com.google.protobuf.InvalidProtocolBufferException
import org.apache.kafka.common.header.Headers
import org.apache.kafka.common.serialization.Deserializer
import org.slf4j.LoggerFactory
import kotlin.jvm.Throws

class ProtobufDeserializer<T : GeneratedMessageV3>(val parser: Parser<T>) : Deserializer<T> {
  override fun deserialize(topic: String?, data: ByteArray?): T = parser.parse(data)
  override fun deserialize(topic: String, headers: Headers, data: ByteArray): T {
    if (headers.lastHeader("eventId") == null || headers.lastHeader("eventId").value() == null) {
      logger.warn("Missing eventId header for topic $topic")
    }
    return this.deserialize(topic, data)
  }

  interface Parser<T> {
    @Throws(InvalidProtocolBufferException::class)
    fun parse(data: ByteArray?): T
  }

  companion object {
    private val logger = LoggerFactory.getLogger(this::class.java)
  }
}
