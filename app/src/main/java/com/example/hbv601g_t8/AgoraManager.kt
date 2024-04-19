package com.example.hbv601g_t8

import io.agora.rtc2.*

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import java.lang.Exception
import java.util.HashSet

open class AgoraManager(context: Context){


    protected val mContext : Context = context
    protected var agoraEngine: RtcEngine? = null // The RTCEngine instance
    //protected var mListener: AgoraManagerListener? = null // The event handler to notify the UI of agoraEngine events
    protected val appId: String = "5165382fd1ac41a1a5519fd3fb902a4d" // Your App ID from Agora console
    var channelName: String = "testing" // The name of the channel to join
    var localUid: Int = 0 // UID of the local user
    var remoteUids = HashSet<Int>() // An object to store uids of remote users
    var isJoined = false // Status of the video call
        private set
    var isBroadcaster = true // Local user role

    companion object {
        protected const val PERMISSION_REQ_ID = 22
        protected val REQUESTED_PERMISSIONS = arrayOf(
            Manifest.permission.RECORD_AUDIO,
            Manifest.permission.CAMERA
        )
    }

    protected fun checkSelfPermission(): Boolean {
        return ContextCompat.checkSelfPermission(
            mContext,
            REQUESTED_PERMISSIONS[0]
        ) == PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(
                    mContext,
                    REQUESTED_PERMISSIONS[1]
                ) == PackageManager.PERMISSION_GRANTED
    }

    /*

    if (!checkSelfPermission()) {
        ActivityCompat.requestPermissions(DiscActivity, REQUESTED_PERMISSIONS, PERMISSION_REQ_ID)
    }
    */

    protected open fun setupAgoraEngine(): Boolean {
        try {
            // Set the engine configuration
            val config = RtcEngineConfig()
            config.mContext = mContext
            config.mAppId = appId
            // Assign an event handler to receive engine callbacks
            config.mEventHandler = iRtcEngineEventHandler
            // Create an RtcEngine instance
            agoraEngine = RtcEngine.create(config)
        } catch (e: Exception) {
            println(e.toString())
            return false
        }
        return true
    }

    protected open val iRtcEngineEventHandler: IRtcEngineEventHandler?
        get() = object : IRtcEngineEventHandler() {
            // Listen for a remote user joining the channel.
            override fun onUserJoined(uid: Int, elapsed: Int) {
                println("Remote user joined $uid")
                // Save the uid of the remote user.
                remoteUids.add(uid)
            }


            override fun onJoinChannelSuccess(channel: String, uid: Int, elapsed: Int) {
                // Set the joined status to true.
                isJoined = true
                println("Joined Channel $channel")
                // Save the uid of the local user.
                localUid = uid
                //mListener!!.onJoinChannelSuccess(channel, uid, elapsed)
            }

            override fun onUserOffline(uid: Int, reason: Int) {
                println("Remote user offline $uid $reason")
                // Update the list of remote Uids
                remoteUids.remove(uid)
                // Notify the UI
                //mListener!!.onRemoteUserLeft(uid)
            }

            override fun onError(err: Int) {
                when (err) {
                    ErrorCode.ERR_TOKEN_EXPIRED -> println("Your token has expired")
                    ErrorCode.ERR_INVALID_TOKEN -> println("Your token is invalid")
                    else -> println("Error code: $err")
                }
            }
        }

    open fun joinChannel(channelName: String, token: String?): Int {
        // Ensure that necessary Android permissions have been granted
        if (!checkSelfPermission()) {
            println("Permissions were not granted")
            return -1
        }
        this.channelName = channelName

        // Create an RTCEngine instance
        if (agoraEngine == null) setupAgoraEngine()

        val options = ChannelMediaOptions()
        // For a Video/Voice call, set the channel profile as COMMUNICATION.
        options.channelProfile = Constants.CHANNEL_PROFILE_COMMUNICATION
        // Set the client role to broadcaster or audience
        options.clientRoleType = Constants.CLIENT_ROLE_BROADCASTER

        // Join the channel using a token.
        agoraEngine!!.joinChannel(token, channelName, localUid, options)
        return 0
    }

    fun leaveChannel() {
        if (!isJoined) {
            // Do nothing
        } else {
            // Call the `leaveChannel` method
            agoraEngine!!.leaveChannel()

            // Set the `isJoined` status to false
            isJoined = false
            // Destroy the engine instance
            destroyAgoraEngine()
        }
    }

    protected fun destroyAgoraEngine() {
        // Release the RtcEngine instance to free up resources
        RtcEngine.destroy()
        agoraEngine = null
    }

}