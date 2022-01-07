package org.meowcat.mesagisto.mirai

import net.mamoe.mirai.console.plugin.jvm.JvmPluginDescription
import net.mamoe.mirai.console.plugin.jvm.KotlinPlugin
import net.mamoe.mirai.event.Listener
import net.mamoe.mirai.event.events.GroupMessageEvent
import net.mamoe.mirai.event.globalEventChannel
import net.mamoe.mirai.event.subscribeAlways
import net.mamoe.mirai.message.data.Image
import net.mamoe.mirai.message.data.Image.Key.queryUrl
import org.meowcat.mesagisto.client.*
import org.meowcat.mesagisto.mirai.handlers.MiraiListener

object Plugin : KotlinPlugin(
  JvmPluginDescription(
    id = "org.meowcat.mesagisto",
    name = "Mesagisto",
    version = "1.0.0"
  )
) {
  private val eventChannel = globalEventChannel()
  private lateinit var listener: Listener<GroupMessageEvent>
  private lateinit var commandsListener: Listener<GroupMessageEvent>

  override fun onEnable() {
    Config.reload()
    if (!Config.enable) {
      logger.warning("Mesagisto未启用!")
      return
    }
    Logger.bridgeToMirai(logger)
    MesagistoConfig.builder {
      name = "mirai"
      natsAddress = Config.nats.address
      cipherEnable = Config.cipher.enable
      cipherKey = Config.cipher.key
      cipherRefusePlain = Config.cipher.refusePlain
      proxyEnable = Config.proxy.enable
      proxyUri = Config.proxy.address
      resolvePhotoUrl = { uid, _ ->
        runCatching {
          val image = Image(uid.toString(charset = Charsets.UTF_8))
          image.queryUrl()
        }
      }
    }.apply()
    listener = eventChannel.subscribeAlways(MiraiListener::handle)
    commandsListener = eventChannel.subscribeAlways(Command::handle)
    Logger.info { "信使已启用" }
  }

  override fun onDisable() {
    if (!Config.enable) return
    listener.complete()
    commandsListener.complete()
    Logger.info { "信使已禁用" }
  }
}
