package com.hbbclub.m4aencoder.aacenc.todoroo.aacenc

import android.content.Context
import io.reactivex.Observable
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileInputStream
import java.io.FileNotFoundException

class PCM2M4A {

    companion object {
        const val BITRATE = 64000
        const val CHANNELS = 1
        const val SAMPLE_RATE = 44100
        const val BITS_PER_SAMPLE = 16
        const val SIZE = 1024
    }

    fun covert(context: Context, path: String): Observable<String> {
        val cache by lazy {
            val path = "${context.externalCacheDir}"
            File(path).let {
                if (!it.exists()) {
                    it.mkdir()
                }
            }
            path
        }

        return Observable.create { emitter ->
            val orFile = File(path)
            if (!orFile.exists()){
                emitter.onError(FileNotFoundException(path))
                return@create
            }
            val encoder = AACEncoder()
            val aacFile = "$cache/aacCache.aac"
            val m4aFile = "$cache/m4aCache.m4a"
            encoder.init(BITRATE, CHANNELS, SAMPLE_RATE, BITS_PER_SAMPLE, aacFile)
            val fis = FileInputStream(orFile)
            val ba = ByteArray(SIZE)
            var size = fis.read(ba)
            val baos = ByteArrayOutputStream()
            baos.reset()
            while (size > 0) {
                baos.write(ba, 0, size)
                size = fis.read(ba)
            }
            encoder.encode(baos.toByteArray())
            encoder.uninit()
            AACToM4A().convert(context, aacFile, m4aFile)
            File(aacFile).delete()
            File(path).delete()
            emitter.onNext(m4aFile)
            emitter.onComplete()
        }
    }
}