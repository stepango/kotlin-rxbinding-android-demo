package com.bandlab.bandlab.myapplication

import android.app.Activity
import android.os.Bundle
import rx.Observable
import rx.android.lifecycle.LifecycleEvent
import rx.subjects.BehaviorSubject

/**
 * Created by stepangoncarov on 29/03/15.
 */
open class RxActivity : Activity() {
    val lifecycleSubject = BehaviorSubject.create<LifecycleEvent>();

    public fun lifecycle(): Observable<LifecycleEvent> = lifecycleSubject.asObservable()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        lifecycleSubject.onNext(LifecycleEvent.CREATE)
    }

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