package ro.optistudy.core.providers.firebase

import android.content.Context
import android.util.Log
import com.google.firebase.FirebaseApp
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.messaging.FirebaseMessaging
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.merge
import kotlinx.coroutines.launch
import ro.optistudy.core.models.TresholdData
import java.time.ZonedDateTime
import java.util.UUID

class FirebaseProvider {

    companion object {
        private var sFirebaseProvider: FirebaseProvider? = null

        private val lock = Any()

        fun getInstance(): FirebaseProvider {
            synchronized(lock) {
                if (sFirebaseProvider == null) {
                    sFirebaseProvider = FirebaseProvider()
                }
                return sFirebaseProvider!!
            }
        }
    }

    private var mDefaultScope = CoroutineScope(Job() + Dispatchers.Default)
    private var _mJob1: Job? = null
    private var _mJob2: Job? = null

    private var _mAppToken: String? = null
    private var _mUserToken: String? = UUID.randomUUID().toString()
    private var _mDb: FirebaseFirestore? = null
    private var _mLogTimestamp: Long = 0L

    private val _mTresholdStateFlow = MutableSharedFlow<Int>(replay = 1)
    var mTresholdStateFlow = merge(
        _mTresholdStateFlow.asSharedFlow().distinctUntilChanged().filter {
            it == 0
        }.debounce(5000),
        _mTresholdStateFlow.asSharedFlow().distinctUntilChanged().filter {
            it != 0
        }
    )

    init {
        FirebaseMessaging.getInstance().token.addOnCompleteListener {
            task ->
                if (task.isSuccessful) {
                    _mUserToken = task.result
                } else {
                    Log.d("[ERROR]", " - user firebase token cannot be retrieved")
                }
        }
    }

    fun initialize(context: Context) {
        FirebaseApp.initializeApp(context)
        _mLogTimestamp = ZonedDateTime.now().toInstant().toEpochMilli()
        _mAppToken = UUID.randomUUID().toString()

        _mJob1 = mDefaultScope.launch {
            mTresholdStateFlow.collect {
                addTresholdData(it)
            }
        }
    }

    fun emit(treshold: Int) {
        _mJob2 = mDefaultScope.launch {
            _mTresholdStateFlow.emit(treshold)
        }
    }

    fun addTresholdData(treshold: Int) {
        val db = FirebaseFirestore.getInstance()
        if (db != null) {
            val prevTimestamp = _mLogTimestamp
            _mLogTimestamp = ZonedDateTime.now().toInstant().toEpochMilli();
            val timediff = _mLogTimestamp - prevTimestamp
            val tresholdData = TresholdData(treshold, timediff, ZonedDateTime.now(), _mAppToken?:"anonymous")
            db.collection("learning")
                .document("tresholds")
                .set(tresholdData)
                .addOnSuccessListener {
                    // Data successfully written
                    Log.d("[FirebaseFirestore]", " - send treshold data: $tresholdData")
                }
                .addOnFailureListener { e ->
                    // Error occurred while writing data
                    Log.d("[FirebaseFirestore]", " - ERROR cannot send data: $tresholdData")
                }
        }
    }

    fun clearAll() {
        _mJob1?.cancel()
        _mJob2?.cancel()
    }

}


