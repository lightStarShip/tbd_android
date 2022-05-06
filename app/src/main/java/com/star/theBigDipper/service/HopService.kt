package com.star.theBigDipper.service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.graphics.Color
import android.net.VpnService
import android.os.Build
import android.os.Handler
import android.os.Message
import android.os.ParcelFileDescriptor
import android.util.Log
import androidLib.AndroidLib
import androidLib.VpnDelegate
import androidx.annotation.RequiresApi
import com.star.theBigDipper.HopApplication
import com.star.theBigDipper.R
import com.star.theBigDipper.event.EventVPNClosed
import com.star.theBigDipper.event.EventVPNOpen
import com.star.theBigDipper.ui.activity.MainActivity
import com.nbs.android.lib.utils.toast
import org.greenrobot.eventbus.EventBus
import java.io.FileInputStream
import java.io.FileOutputStream
import java.util.concurrent.TimeUnit
import kotlin.jvm.Throws

class HopService : VpnService(), VpnDelegate, Handler.Callback {
    private var mInterface: ParcelFileDescriptor? = null
    private lateinit var mConfigureIntent: PendingIntent
    private var mVpnOutputStream: FileOutputStream? = null
    private lateinit var mHandler: Handler
    override fun onCreate() {
        mHandler = Handler(mainLooper, this)
        mConfigureIntent = PendingIntent.getActivity(this, 0, Intent(this, MainActivity::class.java), PendingIntent.FLAG_UPDATE_CURRENT)
        super.onCreate()
    }

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        startNotification()
        Thread { establishVPN() }.start()
        return super.onStartCommand(intent, flags, startId)
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private fun createNotificationChannel(channelId: String) {
        val chan = NotificationChannel(channelId, "com.hop.pirate.service", NotificationManager.IMPORTANCE_NONE)
        chan.lightColor = Color.BLUE
        chan.lockscreenVisibility = Notification.VISIBILITY_PRIVATE
        val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        manager.createNotificationChannel(chan)
    }

    fun startNotification() {
        val builder = Notification.Builder(this.applicationContext)
        builder.setContentIntent(mConfigureIntent).
        setLargeIcon(BitmapFactory.decodeResource(this.resources, R.drawable.notification_icon)).
        setContentTitle(getString(R.string.notification_title)).
        setSmallIcon(R.drawable.notification_icon).
        setContentText(getString(R.string.notification_title)).
        setWhen(System.currentTimeMillis())

        val channelId = "com.hop.pirate.hop.service.channel"
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createNotificationChannel(channelId)
            builder.setChannelId(channelId)
        }
        val notification = builder.build()
        notification.defaults = Notification.DEFAULT_SOUND
        startForeground(110, notification)
    }

    fun disconnectVPN() {
        try {
            if (mVpnOutputStream != null) {
                mVpnOutputStream!!.close()
                mVpnOutputStream = null
            }
            if (mInterface != null) {
                mInterface!!.close()
                mInterface = null
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d(TAG, "onDestroy: ")
        stop()
    }

    fun establishVPN() {
        try {
            val builder = Builder()
            builder.addAddress(LOCAL_IP, 32)
            builder.addRoute("0.0.0.0", 0)
            builder.setConfigureIntent(mConfigureIntent)
            mInterface = builder.establish()
            val inputStream = FileInputStream(mInterface?.fileDescriptor)
            mVpnOutputStream = FileOutputStream(mInterface?.fileDescriptor)
            AndroidLib.startVPN("$LOCAL_IP:41080", SysConf.CurPoolAddress, SysConf.CurMinerID, this)
            HopApplication.instance.isRunning = true
            Thread(PacketReader(inputStream)).start()
            EventBus.getDefault().post(EventVPNOpen())
        } catch (e: Exception) {
            e.printStackTrace()
            mHandler.sendEmptyMessage(0)
            vpnClosed()
        }
    }

    override fun byPass(fd: Int): Boolean {
        return this.protect(fd)
    }

    override fun log(str: String) {
    }

    override fun vpnClosed() {
        HopApplication.instance.isRunning = false
        disconnectVPN()
        EventBus.getDefault().post(EventVPNClosed())
        stopSelf()

    }

    @Throws(Exception::class)
    override fun write(b: ByteArray): Long {
        mVpnOutputStream?.write(b)
        return b.size.toLong()
    }

    override fun handleMessage(msg: Message): Boolean {
        toast(getString(R.string.home_init_service_fail))
        return false
    }

    companion object {
        val IDLE_INTERVAL_MS = TimeUnit.MILLISECONDS.toMillis(100)
        const val TAG = "HopService"
        private const val LOCAL_IP = "10.8.0.2"
        fun stop() {
            Log.w(TAG, "stop service in android")
            HopApplication.instance.isRunning = false
            AndroidLib.stopVpn()
        }
    }
}