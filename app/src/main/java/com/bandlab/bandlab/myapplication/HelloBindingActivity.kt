package com.bandlab.bandlab.myapplication

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.widget.TextView
import com.ogaclejapan.rx.binding.Rx
import com.ogaclejapan.rx.binding.RxProperty
import com.ogaclejapan.rx.binding.RxView
import rx.Observable
import rx.android.lifecycle.LifecycleObservable
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers
import rx.subjects.BehaviorSubject
import java.util.concurrent.TimeUnit


/**
 * Created by stepangoncarov on 29/03/15.
 */
class HelloBindingActivity : RxActivity() {

    var string = RxProperty.create<String>()
    var text: Rx<TextView>? = null
    var result = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        text = RxView.findById(this, R.id.txt_hello)
        text?.bind(string, { text, string -> text.setText(string) })
        TextObserver(text!!).asObservable()
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ s -> Log.d("TextObserver", s) })
        LifecycleObservable.bindActivityLifecycle(
                lifecycle(),
                Observable.interval(1, TimeUnit.SECONDS)
                        .subscribeOn(Schedulers.newThread())
                        .observeOn(AndroidSchedulers.mainThread())
        ).subscribe({
            string.set(currentSeconds())
            Log.d("Second", currentSeconds())
        })
        lifecycle().subscribe({ event -> Log.d("Event", event.toString()) })

    }

    fun currentSeconds() = ((System.currentTimeMillis() / 1000) % 60).toString()

    class TextObserver : TextWatcher {

        var textListener = BehaviorSubject.create<String>();

        constructor(tv: TextView) {
            tv.addTextChangedListener(this);
        }

        constructor(rx: Rx<TextView>) {
            rx.get().addTextChangedListener(this);
        }

        override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
        }

        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
        }

        override fun afterTextChanged(s: Editable?) {
            textListener.onNext(s.toString())
        }

        public fun asObservable(): Observable<String> = textListener.asObservable()

    }

}