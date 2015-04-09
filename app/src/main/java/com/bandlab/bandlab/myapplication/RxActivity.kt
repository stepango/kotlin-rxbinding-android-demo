package com.bandlab.bandlab.myapplication

import android.app.Activity
import android.content.BroadcastReceiver
import android.content.IntentFilter
import android.os.Bundle
import android.os.Handler
import android.support.v4.content.LocalBroadcastManager
import rx.Observable
import rx.android.lifecycle.LifecycleEvent
import rx.subjects.BehaviorSubject
import java.util.LinkedList
import java.util.concurrent.TimeUnit

/**
 * Created by stepangoncarov on 29/03/15.
 */
open class RxActivity : Activity() {

    val receievers = LinkedList<Pair<IntentFilter, BroadcastReceiver>>()

    val lifecycleSubject = BehaviorSubject.create<LifecycleEvent>();

    public fun lifecycle(): Observable<LifecycleEvent> = lifecycleSubject.asObservable()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        registerBroadcastProcessing()
        lifecycleSubject.onNext(LifecycleEvent.CREATE)
    }

    private fun registerBroadcastProcessing() {
        lifecycle()
                .filter { it == LifecycleEvent.RESUME }
                .flatMap { Observable.from(receievers) }
                .subscribe { registerReceiver(it) }
        lifecycle()
                .filter { it == LifecycleEvent.PAUSE }
                .flatMap { Observable.from(receievers) }
                .subscribe { unregisterReceiver(it) }
    }

    private fun unregisterReceiver(pair: Pair<IntentFilter, BroadcastReceiver>) {
        getBroadcastManager().unregisterReceiver(pair.second)
    }

    private fun registerReceiver(pair: Pair<IntentFilter, BroadcastReceiver>) {
        getBroadcastManager().registerReceiver(pair.second, pair.first)
    }

    protected fun registerOnPauseResume(filters: Array<String>, receiver: BroadcastReceiver) {
        filters.forEach { receievers.add(Pair(IntentFilter(it), receiver)) }
    }

    private fun getBroadcastManager() = LocalBroadcastManager.getInstance(this)


    override fun onStart() {
        super.onStart()
        lifecycleSubject.onNext(LifecycleEvent.START)
    }

    override fun onResume() {
        super.onResume()
        lifecycleSubject.onNext(LifecycleEvent.RESUME)
    }

    override fun onPause() {
        lifecycleSubject.onNext(LifecycleEvent.PAUSE)
        super.onPause()
    }

    override fun onStop() {
        lifecycleSubject.onNext(LifecycleEvent.STOP);
        super.onStop()
    }

    override fun onDestroy() {
        lifecycleSubject.onNext(LifecycleEvent.DESTROY);
        super.onDestroy()
    }

}