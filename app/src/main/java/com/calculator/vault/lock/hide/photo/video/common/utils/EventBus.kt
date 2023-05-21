package com.calculator.vault.lock.hide.photo.video.common.utils

import kotlinx.coroutines.*
import kotlinx.coroutines.channels.BroadcastChannel
import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.coroutines.channels.consume
import kotlinx.coroutines.channels.consumeEach

/**
 * Event Bus is used for publishing and listening any events in the Fragments or Activity.
 *
 * Usage of Event Bus:
 * =========================================
 * Subscribe Event:-
 *
 *       Note: "listSubscription" is the list of subscribed events which you can unsubscribe it in
 *       onDestroy() method. However it is already declared in BaseActivity/BaseFragment/BaseDialogFragment class
 *
 *       EventBus.subscribe<Intent>(listSubscription) { intent ->
 *           if(intent.action == this::class.java.simpleName)  ==> To check whether the action is for specific activity/fragment
 *              //Do your stuff with the received intent and update your activity/fragment
 *       }
 *
 *       Other Ex:-
 *       EventBus.subscribe<Int>(listSubscription) { yourInt -> }
 *       EventBus.subscribe<String>(listSubscription) { yourString -> }
 *       EventBus.subscribe<YourModel>(listSubscription) { yourModel -> }
 *
 * =========================================
 * Publish Event:-
 *
 *      val intent = Intent(MainActivity::class.java.simpleName)
 *      intent.putExtra(EXTRAS_REFRESH_MY_GROUPS, EXTRAS_REFRESH_MY_GROUPS)
 *      EventBus.publish(intent)
 *
 *      Other Ex:
 *      EventBus.publish(yourInt)
 *      EventBus.publish(yourString)
 *      EventBus.publish(yourModel)
 * =========================================
 * */

@OptIn(ExperimentalCoroutinesApi::class)
object EventBus {

    val channel = BroadcastChannel<Any>(1)

    fun publish(event: Any) = GlobalScope.launch { channel.send(event) }

    inline fun <reified TYPE> subscribe(
        alSubscription: ArrayList<ReceiveChannel<*>>,
        crossinline block: (TYPE) -> Unit
    ) {
        CoroutineScope(Dispatchers.Main).launch {
            channel.openSubscription().consume {
                this.consumeEach {
                    alSubscription.add(this)
                    if(it is TYPE) block(it as TYPE)
                }
            }
        }
    }
}