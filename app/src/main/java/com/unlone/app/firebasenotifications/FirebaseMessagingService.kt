package com.unlone.app.firebasenotifications

import android.app.*
import android.app.NotificationManager.IMPORTANCE_HIGH
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.unlone.app.R
import com.unlone.app.ui.lounge.PostDetailFragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.messaging.Constants.MessageNotificationKeys.TAG
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlin.random.Random

private const val CHANNEL_ID = "my_channel"

/** This services is used for receiving notification when someone commented
 * on the user's post
 */
class FirebaseMessagingService : FirebaseMessagingService() {
    private val mAuth: FirebaseAuth = FirebaseAuth.getInstance()
    private val mFirestore: FirebaseFirestore = FirebaseFirestore.getInstance()

    companion object {
        var sharedPref: SharedPreferences? = null

        var token: String?
        get() {
            return sharedPref?.getString("token", "")
        }
        set(value) {
            sharedPref?.edit()?.putString("token", value)?.apply()
        }

    }

    override fun onNewToken(newToken: String) {
        super.onNewToken(newToken)
        token = newToken
        Log.d(TAG, "Refreshed token: $newToken")

        // If you want to send messages to this application instance or
        // manage this apps subscriptions on the server side, send the
        // FCM registration token to your app server.
        sendRegistrationToFirestore(newToken)
    }

    // We user User document to store the registered notification token
    private fun sendRegistrationToFirestore(token: String) = CoroutineScope(Dispatchers.IO).launch{
        val updatedToken = hashMapOf("notificationToken" to token)
        val docRef = mFirestore.collection("users").document(mAuth.uid.toString())
        docRef.set(updatedToken)
    }

    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)

        Log.d("TAG", "FCM message received: $message")
        // Create an Intent for the activity you want to start
        val resultIntent = Intent(this, PostDetailFragment::class.java)
        resultIntent.putExtra("postId", message.data["pid"])
        Log.d("TAG", "postId to open: ${message.data["pid"]}")
        // Create the TaskStackBuilder
        val resultPendingIntent: PendingIntent? = TaskStackBuilder.create(this).run {
            // Add the intent, which inflates the back stack
            addNextIntentWithParentStack(resultIntent)
            // Get the PendingIntent containing the entire back stack
            getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT)
        }

        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            createNotificationChannel(notificationManager)
        }
        val notificationID = Random.nextInt()
        val builder = NotificationCompat.Builder(this, CHANNEL_ID).apply {
            setContentIntent(resultPendingIntent)
            setSmallIcon(R.drawable.ic_baseline_android_24)
            setContentTitle(message.data["title"])
            setContentText(message.data["message"])
            setAutoCancel(true)
        }
        with(NotificationManagerCompat.from(this)) {
            notify(notificationID, builder.build())
        }

    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun createNotificationChannel(notificationManager: NotificationManager) {
        val channelName = "channelName"
        val channel = NotificationChannel(CHANNEL_ID, channelName, IMPORTANCE_HIGH).apply {
            description = "My channel description"
            enableLights(true)
            lightColor = Color.GREEN
        }
        notificationManager.createNotificationChannel(channel)
    }


}