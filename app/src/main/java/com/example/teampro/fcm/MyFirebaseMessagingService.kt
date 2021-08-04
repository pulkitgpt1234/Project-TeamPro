package com.example.teampro.fcm

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.media.RingtoneManager
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import com.example.teampro.R
import com.example.teampro.activities.MainActivity
import com.example.teampro.activities.SignInActivity
import com.example.teampro.firebase.FireStoreClass
import com.example.teampro.utils.Constants
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class MyFirebaseMessagingService : FirebaseMessagingService() {
    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)

        Log.d(TAG , "FROM: ${remoteMessage.from}")

        remoteMessage.data.isNotEmpty().let{
            Log.d(TAG,"Message data Payload:${remoteMessage.data}")
        }
        remoteMessage.notification?.let{
            Log.d(TAG,"Message Notification Body: ${it.body}")

            val title = remoteMessage.data[Constants.FCM_KEY_TITLE]!!
            val message = remoteMessage.data[Constants.FCM_KEY_MESSAGE]!!

            sendNotification(title,message)
        }
    }

    override fun onNewToken(token:String){
        super.onNewToken(token)

        Log.e(TAG,"Refreshed token : $token")
        sendRegistrationToServer(token)
    }


    private fun sendRegistrationToServer(token:String?){
        val sharedPreferences =
                this.getSharedPreferences(Constants.TEAMPRO_PREFERENCES, Context.MODE_PRIVATE)
        val editor: SharedPreferences.Editor = sharedPreferences.edit()
        editor.putString(Constants.FCM_TOKEN, token)
        editor.apply()
    }

    private fun sendNotification(title:String,message:String){
        val intent = if(FireStoreClass().getCurrentUserId().isNotEmpty()) {
            Intent(this,MainActivity::class.java)
        }else{
            Intent(this,SignInActivity::class.java)
        }
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP)
        val pendingIntent = PendingIntent.getActivity(this,0,intent,PendingIntent.FLAG_ONE_SHOT)
        val channelId = this.resources.getString(R.string.default_notification_channel_id)
        val defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        val notificationBuilder = NotificationCompat.Builder(
            this,channelId
        ).setSmallIcon(R.drawable.ic_stat_ic_notification)
            .setContentTitle(title)
            .setContentText(message)
            .setAutoCancel(true)
            .setSound(defaultSoundUri)
            .setContentIntent(pendingIntent)

        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            val channel = NotificationChannel(channelId,
                "Channel TeamPro Title",NotificationManager.IMPORTANCE_DEFAULT)
            notificationManager.createNotificationChannel(channel)

        }
        notificationManager.notify(0,notificationBuilder.build())
    }

    companion object{
        private const val TAG = "MyFirebaseMsgService"
    }

}