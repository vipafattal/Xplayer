package com.abed.xplayer.utils

import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.*

/**
 * Created by ${User} on ${Date}
 */


inline fun <T> LiveData<T>.observer(owner: LifecycleOwner, crossinline doOnObserver: (T) -> Unit) =
    observe(owner, Observer {
        doOnObserver(it)
    })


inline fun <reified ModelClass : ViewModel> Fragment.viewModelOf() =
    ViewModelProviders.of(activity!!).get(ModelClass::class.java)


inline fun <reified ModelClass : ViewModel> AppCompatActivity.viewModelOf() =
    ViewModelProviders.of(this).get(ModelClass::class.java)

inline fun <reified ModelClass : ViewModel> AppCompatActivity.viewModelOf(
    factoryViewModel: ViewModelProvider.Factory
) =
    ViewModelProviders.of(this, factoryViewModel).get(ModelClass::class.java)

inline fun <reified ModelClass : ViewModel> Fragment.viewModelOf(
    factoryViewModel: ViewModelProvider.Factory
) =
    ViewModelProviders.of(this, factoryViewModel).get(ModelClass::class.java)
