package com.example.aidldemo

import android.content.ComponentName
import android.content.Intent
import android.content.ServiceConnection
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.IBinder
import android.util.Log
import android.widget.Toast
import com.example.aidldemo.data.Book
import com.example.aidldemo.data.TestInBundle
import com.example.aidldemo.databinding.ActivityMainBinding
import kotlin.concurrent.thread
import kotlin.math.log

private const val TAG = "MainActivity"

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    private var iLibraryInterface: ILibraryInterface? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.addBooks.setOnClickListener {
            Log.d(TAG, "addBooks: start")
            val books = listOf(
                Book(name = "西游记", "吴承恩"),
                Book(name = "水浒传", "施耐庵"),
                Book(name = "三国演义", "罗贯中"),
                Book(name = "红楼梦", "曹雪芹"),
                Book(name = "三体", "大刘"),
                Book(name = "流浪地球", "大刘")
            )
            iLibraryInterface?.addBooks(books)
            Log.d(TAG, "addBooks: end")

        }

        binding.getBooks.setOnClickListener {
            Log.d(TAG, "getBooks: ")
            val books = iLibraryInterface?.books
            Log.d(TAG, "getBooks: ${books?.joinToString()}")
        }

        binding.getAuthors.setOnClickListener {
            thread {
                Log.d(TAG, "getAuthors: ")
                val authors = iLibraryInterface?.authors
                Log.d(TAG, "getAuthors: ${authors?.joinToString()}")
            }
        }

        binding.testOnway.setOnClickListener {
            Log.d(TAG, "testOnway start: ")
            iLibraryInterface?.testOnway()
            Log.d(TAG, "testOnway end: ")
        }

        binding.testInOut.setOnClickListener {
            val inBook = Book(name = "inName", authorName = "inAuthor")
            val outBook = Book(name = "outName", authorName = "outAuthor")
            val inoutBook = Book(name = "inOutName", authorName = "inOutAuthor")
            Log.d(TAG, "testInOut start: inBook=$inBook,outBook=$outBook, inoutBook=$inoutBook")
            iLibraryInterface?.testInOut(inBook, outBook, inoutBook)
            Log.d(TAG, "testInOut end: inBook=$inBook,outBook=$outBook, inoutBook=$inoutBook")
            inoutBook.name = "client_inout_change_name"
            inoutBook.authorName = "client_inout_change_authur_name"
        }

        val intent = Intent("com.example.aidldemo.service")
        intent.setPackage("com.example.aidldemo")
        bindService(intent, mConnection, BIND_AUTO_CREATE)

    }

    private val mConnection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName, service: IBinder) {
            iLibraryInterface = ILibraryInterface.Stub.asInterface(service)
            iLibraryInterface?.asBinder()?.linkToDeath(recipient, 0)
            iLibraryInterface?.registerCallback(iRemoteServiceCallback)
            Log.d(TAG, "onServiceConnected: $name, $service")
            Toast.makeText(
                this@MainActivity,
                "连接成功！",
                Toast.LENGTH_SHORT
            ).show()
        }

        override fun onServiceDisconnected(name: ComponentName) {
            Log.d(TAG, "onServiceDisconnected: $name")
            iLibraryInterface?.asBinder()?.unlinkToDeath(recipient, 0)
            iLibraryInterface = null
            Toast.makeText(
                this@MainActivity,
                "断开连接！",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    private val recipient = IBinder.DeathRecipient { Log.d(TAG, "binderDied: ") }

    private val iRemoteServiceCallback = object : IRemoteServiceCallback.Stub() {
        override fun completed(type: Int, bundle: Bundle) {
            //如果您的 AIDL 接口包含接收Bundle作为参数（预计包含 Parcelable 类型）的方法，
            // 则在尝试从Bundle读取之前，请务必通过调用 Bundle.setClassLoader(ClassLoader) 设置Bundle的类加载器。
            // 否则，即使您在应用中正确定义 Parcelable 类型，也会遇到 ClassNotFoundException
            // https://developer.android.google.cn/guide/components/aidl?hl=zh-cn#Bundles
            bundle.classLoader = classLoader
            val logBundle = Bundle()
            logBundle.putAll(bundle)
            Log.d(TAG, "completed: type=$type, bundle=$logBundle")
        }
    }


}