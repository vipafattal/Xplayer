package com.brilliancesoft.xplayer.framework.data

import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore

/**
 * Created by  on
 */

fun FirebaseFirestore.playListCollection(): CollectionReference {
    return collection("playlist")
}

fun FirebaseFirestore.getPlayListDocument(playListId: String): DocumentReference {
    return playListCollection().document(playListId)
}


