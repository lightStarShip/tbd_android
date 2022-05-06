package com.star.theBigDipper.service

import android.util.Log
import androidLib.AndroidLib
import com.star.theBigDipper.HopApplication
import java.io.FileInputStream
import java.nio.ByteBuffer
import java.util.*

internal class PacketReader(private val mReader: FileInputStream) : Runnable {
    private val readerBuf = ByteBuffer.allocate(MAX_PACKET_SIZE)
    var app = HopApplication.instance
    override fun run() {
        try {
            var dst: ByteArray
            var length: Int
            while (app.isRunning) {
                length = mReader.read(readerBuf.array())
                if (length == 0) {
                    Thread.sleep(HopService.IDLE_INTERVAL_MS)
                    continue
                }
                readerBuf.limit(length)
                dst = Arrays.copyOf(readerBuf.array(), length)
                AndroidLib.inputPacket(dst)
            }
        } catch (e: Exception) {
            e.printStackTrace()
            if (HopApplication.instance.isRunning) {
                HopService.stop()
            }
        }
        Log.w("HopService", "------>Packet reading thread exit......")
    }

    companion object {
        private const val MAX_PACKET_SIZE = Short.MAX_VALUE.toInt()
    }

}