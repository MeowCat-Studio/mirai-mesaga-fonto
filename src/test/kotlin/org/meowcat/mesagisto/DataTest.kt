package org.meowcat.mesagisto

import io.ktor.util.hex
import kotlinx.serialization.InternalSerializationApi
import org.mesagisto.client.Cbor
import org.mesagisto.client.data.Message
import org.mesagisto.client.data.MessageType
import org.mesagisto.client.data.Profile
import org.mesagisto.client.toByteArray

object DataTest {
  @JvmStatic fun main(args: Array<String>) {
    testCbor()
  }

  @OptIn(InternalSerializationApi::class)
  private fun testCbor() {
    val message = Message(
      profile = Profile(ByteArray(0), null, null),
      id = 2222.toByteArray(),
      reply = 123232.toByteArray(),
      chain = listOf(MessageType.Text("this is text 1"))
    )
    val bytes = Cbor.encodeToByteArray(message)
    println(hex(bytes))
    Cbor.decodeFromByteArray<Message>(bytes)
  }
}
