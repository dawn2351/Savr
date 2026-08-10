package com.zarnth.savr.data.crash

import android.content.Context
import com.zarnth.savr.data.local.dao.CrashLogDao
import com.zarnth.savr.data.local.entity.CrashLogEntity
import java.io.PrintWriter
import java.io.StringWriter
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit

class CrashHandler(
    private val context: Context,
    private val crashLogDao: CrashLogDao
) : Thread.UncaughtExceptionHandler {

    private val defaultHandler: Thread.UncaughtExceptionHandler? = Thread.getDefaultUncaughtExceptionHandler()

    override fun uncaughtException(thread: Thread, throwable: Throwable) {
        try {
            val packageInfo = runCatching {
                context.packageManager.getPackageInfo(context.packageName, 0)
            }.getOrNull()

            val stackTrace = StringWriter().use { writer ->
                PrintWriter(writer).use { throwable.printStackTrace(it) }
                writer.toString()
            }

            val entity = CrashLogEntity(
                timestamp = System.currentTimeMillis(),
                versionName = packageInfo?.versionName ?: "unknown",
                versionCode = packageInfo?.longVersionCode ?: 0L,
                androidVersion = android.os.Build.VERSION.RELEASE,
                sdkInt = android.os.Build.VERSION.SDK_INT,
                manufacturer = android.os.Build.MANUFACTURER,
                model = android.os.Build.MODEL,
                brand = android.os.Build.BRAND,
                exceptionClass = throwable.javaClass.name,
                message = throwable.message.orEmpty(),
                stackTrace = stackTrace,
                threadName = thread.name
            )

            val latch = CountDownLatch(1)
            Thread {
                try {
                    crashLogDao.insert(entity)
                } catch (_: Throwable) {
                } finally {
                    latch.countDown()
                }
            }.start()
            latch.await(2, TimeUnit.SECONDS)
        } catch (_: Throwable) {
        }
        defaultHandler?.uncaughtException(thread, throwable)
    }
}