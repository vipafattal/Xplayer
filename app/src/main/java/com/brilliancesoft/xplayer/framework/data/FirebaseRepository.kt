package com.brilliancesoft.xplayer.framework.data

import android.app.Activity
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.brilliancesoft.xplayer.model.Media
import com.brilliancesoft.xplayer.model.Playlist
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.toObjects
import io.reactivex.subjects.BehaviorSubject

/**
 * Created by Abed on 2019-9-17
 */
class FirebaseRepository(
    private val firestore: FirebaseFirestore,
    private val firebaseAuth: FirebaseAuth
) {
    val currentUser :FirebaseUser? = firebaseAuth.currentUser

    fun getUserPlayLists(): LiveData<List<Playlist>> {
        val playListCollection = firestore.playListCollection()
        return playListCollection.whereEqualTo("userId", currentUser!!.uid).getDataFromSnapshot()
    }

    fun savePlayList(playlist: Playlist): MutableLiveData<Boolean?> {
        val result = MutableLiveData<Boolean?>()
        result.value = null

        firestore.playListCollection().document(playlist.id).set(playlist).addOnCompleteListener {
            result.postValue(it.isSuccessful)
        }

        return result
    }

    fun deletePlaylist(id: String): MutableLiveData<Boolean?> {
        val result = MutableLiveData<Boolean?>()

        firestore.playListCollection().document(id).delete().addOnCompleteListener {
            result.postValue(it.isSuccessful)
        }

        result.value = null

        return result
    }

    fun updateMediaInPlaylist(
        media: Media,
        playListId: String,
        isAdd: Boolean
    ): MutableLiveData<Boolean?> {
        val result = MutableLiveData<Boolean?>()
        result.value = null
        firestore.playListCollection().document(playListId).update(
            "list",
            if (isAdd) FieldValue.arrayUnion(media)
            else FieldValue.arrayRemove(media)
        ).addOnCompleteListener {
            result.postValue(it.isSuccessful)
        }

        return result
    }


    private inline fun <reified T : Any> Query.getDataFromSnapshot(): MutableLiveData<List<T>> {
        val playListLiveData = MutableLiveData<List<T>>()
        addSnapshotListener { snapshot, error ->

            if (error != null) {
                errorStream.onNext("An error occurred while getting your playlists")
                Log.v("Firestore Logger", error.message ?: "Unknown error")
                return@addSnapshotListener
            }
            if (snapshot != null) {
                val dataList = snapshot.toObjects<T>()
                playListLiveData.postValue(dataList)
            }
        }

        return playListLiveData
    }

    private inline fun <reified T : Any> CollectionReference.getDataFromSnapshot(
        activity: Activity
    ): MutableLiveData<List<T>> {
        val playListLiveData = MutableLiveData<List<T>>()
        addSnapshotListener(activity) { snapshot, error ->
            if (error != null) {
                errorStream.onNext("An error occurred while getting your playlists")
                Log.v("Firestore Logger", error.message ?: "Unknown error")
                return@addSnapshotListener
            }
            if (snapshot != null) {
                val dataList = snapshot.toObjects<T>()
                playListLiveData.postValue(dataList)
            }
        }

        return playListLiveData
    }

    companion object {
        val errorStream: BehaviorSubject<String> = BehaviorSubject.create()
    }

}