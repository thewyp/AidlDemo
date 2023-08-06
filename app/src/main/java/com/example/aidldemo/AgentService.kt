package com.example.aidldemo

import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.os.IBinder.DeathRecipient
import android.util.Log
import androidx.core.os.bundleOf
import com.example.aidldemo.data.Author
import com.example.aidldemo.data.Book
import com.example.aidldemo.data.TestInBundle
import kotlin.concurrent.thread

private const val TAG = "AgentService"

class AgentService : Service() {

    private var iRemoteServiceCallback: IRemoteServiceCallback? = null

    override fun onCreate() {
        super.onCreate()
        Log.d(TAG, "onCreate: ")
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.d(TAG, "onStartCommand: $intent, $flags, $startId")
        return super.onStartCommand(intent, flags, startId)
    }

    override fun onUnbind(intent: Intent?): Boolean {
        Log.d(TAG, "onUnbind: $intent")
        return super.onUnbind(intent)
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d(TAG, "onDestroy: ")
    }

//    override fun onBind(intent: Intent): IBinder? {
//        return null
//    }

    override fun onBind(intent: Intent): IBinder {
        return libraryInterface
    }

    private val libraryBooks = mutableListOf<Book>()

    private val libraryInterface = object : ILibraryInterface.Stub() {

        override fun addBooks(books: List<Book>) {
            Log.d(TAG, "addBooks: ${books.joinToString()}")
            libraryBooks.addAll(books)
        }

        override fun getBooks(): List<Book> {
            Log.d(TAG, "getBooks: start")
            return libraryBooks
        }

        override fun getAuthors(): List<Author> {
            Log.d(TAG, "getAuthors: start")
            return libraryBooks.groupBy { it.authorName }.map {
                Author(it.key, it.value)
            }
        }

        override fun testInOut(inBook: Book?, outBook: Book?, inoutBook: Book?) {
            Log.d(TAG, "testInOut start: inBook=$inBook,outBook=$outBook, inoutBook=$inoutBook")
            inBook?.name = "server_in_change_name"
            inBook?.authorName = "server_in_change_author_name"

            outBook?.name = "server_out_change_name"
            outBook?.authorName = "server_out_change_author_name"

            inoutBook?.name = "server_inout_change_name"
            inoutBook?.authorName = "server_inout_change_author_name"

            Log.d(TAG, "testInOut end: inBook=$inBook,outBook=$outBook, inoutBook=$inoutBook")
        }

        override fun testOnway() {
            Log.d(TAG, "testOnway: start")
            try {
                Thread.sleep(3_000)
                iRemoteServiceCallback?.completed(100, bundleOf("key" to TestInBundle(1000)))
            } catch (e: Exception) {
                e.printStackTrace()
            }
            Log.d(TAG, "testOnway: end")
            thread {
                val a = 100 / 0
            }
        }

        override fun registerCallback(callback: IRemoteServiceCallback) {
            iRemoteServiceCallback = callback
            iRemoteServiceCallback?.asBinder()?.linkToDeath(recipient, 0)
        }
    }

    private val recipient = DeathRecipient { Log.d(TAG, "binderDied: ") }

}