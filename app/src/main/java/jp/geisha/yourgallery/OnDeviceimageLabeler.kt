package jp.geisha.yourgallery

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import androidx.annotation.WorkerThread
import com.bumptech.glide.Glide
import com.google.android.gms.tasks.Tasks
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.label.ImageLabeling
import com.google.mlkit.vision.label.defaults.ImageLabelerOptions
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import timber.log.Timber
import java.util.concurrent.ExecutionException

class OnDeviceImageLabeler {
    companion object {
        private const val OTHERS = "others"
        private val TAG = OnDeviceImageLabeler::class.java.simpleName
        private val options = ImageLabelerOptions.Builder().setConfidenceThreshold(0.6f).build()

        @WorkerThread
        suspend fun detectLabel(context: Context, uri: Uri): String = withContext(Dispatchers.IO) {
            val targetBitmap = getTargetBitmapWithGlide(context, uri) ?: return@withContext OTHERS
            val image = InputImage.fromBitmap(targetBitmap, 0)
            val labeler = ImageLabeling.getClient(options)

            return@withContext try {
                val labels = synchronized(this) {
                    Tasks.await(labeler.process(image))
                }
                if (labels.size == 0) return@withContext OTHERS
                labels[0].text
            } catch (e: ExecutionException) {
                Timber.e("$TAG:$e")
                OTHERS
            } catch (e: InterruptedException) {
                Timber.e("$TAG:$e")
                OTHERS
            } finally {
                labeler.close()
            }
        }

        /**
         * 読み込みを早くするため、centerCrop + 500、500 にリサイズする
         */
        private fun getTargetBitmapWithGlide(context: Context, uri: Uri) = try {
            Glide
                .with(context)
                .asBitmap()
                .centerCrop()
                .override(50, 50)
                .load(uri)
                .submit()
                .get()
                .copy(Bitmap.Config.ARGB_8888, true)
        } catch (e: ExecutionException) {
            Timber.e("$TAG:$e")
            null
        }
    }
}