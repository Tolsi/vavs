package ru.tolsi.aobp.blockchain.base.time

import java.net.InetAddress

import org.apache.commons.net.ntp.NTPUDPClient

import scala.util.Try

// from scorex
abstract class CurrentTimeProvider {
  def nowMillis: Long
}

class SystemCurrentTimeProvider extends CurrentTimeProvider {
  override def nowMillis: Long = System.currentTimeMillis()
}

class NTPCurrentTimeProvider extends CurrentTimeProvider {
  private val TimeTillUpdate = 1000 * 60 * 10L
  private val NtpServer = "pool.ntp.org"

  private var lastUpdate = 0L
  private var offset = 0L

  private def updateOffSet() {
    val client = new NTPUDPClient()
    client.setDefaultTimeout(10000)

    try {
      client.open()

      val info = client.getTime(InetAddress.getByName(NtpServer))
      info.computeDetails()
      if (Option(info.getOffset).isDefined) offset = info.getOffset
    } catch {
      case t: Throwable =>
//        log.warn("Problems with NTP: ", t)
    } finally {
      client.close()
    }
  }

  override def nowMillis: Long = {

    //CHECK IF OFFSET NEEDS TO BE UPDATED
    if (System.currentTimeMillis() > lastUpdate + TimeTillUpdate) {
      Try {
        updateOffSet()
        lastUpdate = System.currentTimeMillis()

        //        log.info("Adjusting time with " + offset + " milliseconds.")
      } recover {
        case e: Throwable =>
        //          log.warn("Unable to get corrected time", e)
      }
    }

    //CALCULATE CORRECTED TIME
    System.currentTimeMillis() + offset
  }
}
