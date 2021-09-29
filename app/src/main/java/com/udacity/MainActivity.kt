package com.udacity

import android.app.DownloadManager
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.database.Cursor
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_main.*

private const val NOTHING_CHECKED_CONSTANT = -1
private const val GLIDE_CONSTANT = 0
private const val LOAD_APP_CONSTANT = 1
private const val RETROFIT_CONSTANT = 2

class MainActivity : AppCompatActivity() {

    private lateinit var downloadManager : DownloadManager
    private var downloadID: Long = 0
    private lateinit var notificationManager: NotificationManager
    private lateinit var pendingIntent: PendingIntent
    private lateinit var action: NotificationCompat.Action

    /*
        GLIDE_CONSTANT corresponds to 0th radio button https://github.com/bumptech/glide
        LOAD_APP_CONSTANT corresponds to 1st radio button https://github.com/udacity/nd940-c3-advanced-android-programming-project-starter
        RETROFIT_CONSTANT corresponds to 2nd radio button https://github.com/square/retrofit
    */
    private var selectedRadioButton: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        registerReceiver(receiver, IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE))

        custom_button.setOnClickListener {
            // Get the radio button value here
            if (radioButtons.getCheckedRadioButtonId() == -1)
            {
                // no radio buttons are checked
                selectedRadioButton = NOTHING_CHECKED_CONSTANT
            }
            else
            {
                if (radioGlide.isChecked) {
                    selectedRadioButton = GLIDE_CONSTANT
                }
                else if (radioLoadApp.isChecked) {
                    selectedRadioButton = LOAD_APP_CONSTANT
                }
                else {
                    selectedRadioButton = RETROFIT_CONSTANT
                }
            }

            download(selectedRadioButton)
        }
    }

    private val receiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            val id = intent?.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1)

            // Check the download status
            val query = DownloadManager.Query()
            if (id != null) {
                query.setFilterById(id)
            }

            val cursor = downloadManager.query(query)

            if (cursor.moveToFirst()) {
                if (cursor.count > 0) {
                    val statusOfTheDownload: Int =
                        cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_STATUS))

                    /*
                        8 = Success
                        16 = Fail
                    */
                    // Why does this always show up as successful?
                    Log.i("myTag", statusOfTheDownload.toString())
                }
            }
        }
    }

    private fun download(radioButton: Int) {
        val url = when (radioButton) {
            0 -> URL_Glide
            1 -> URL_ProjectStarter
            2 -> URL_Retrofit
            else -> {
                // Nothing selected
                Toast.makeText(applicationContext, "Please select one of the buttons", Toast.LENGTH_LONG).show()
                return
            }
        }

        val request =
            DownloadManager.Request(Uri.parse(url))
                .setTitle(getString(R.string.app_name))
                .setDescription(getString(R.string.app_description))
                .setRequiresCharging(false)
                .setAllowedOverMetered(true)
                .setAllowedOverRoaming(true)

        downloadManager = getSystemService(DOWNLOAD_SERVICE) as DownloadManager

        downloadID =
            downloadManager.enqueue(request)// enqueue puts the download request in the queue.
    }

    companion object {
        private const val URL_Glide =
            "https://github.com/bumptech/glide"
        private const val URL_ProjectStarter =
            "https://github.com/udacity/nd940-c3-advanced-android-programming-project-starter/archive/master.zip"
        private const val URL_Retrofit =
            "https://github.com/square/retrofit"
        private const val CHANNEL_ID = "channelId"
    }
}
