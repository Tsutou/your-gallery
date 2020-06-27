package jp.geisha.yourgallery.gallery

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import jp.geisha.yourgallery.R
import kotlinx.android.synthetic.main.activity_medias.*
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class GalleryActivity : AppCompatActivity() {

    companion object {
        fun createIntent(context: Context): Intent {
            return Intent(context, GalleryActivity::class.java)
        }
    }

    private val viewModel by lazy {
        ViewModelProvider(this).get(GalleryViewModel::class.java)
    }

    private val adapter = GalleryAdapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_medias)

        mediasRecyclerView.layoutManager = GridLayoutManager(this, 3).apply {
            spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
                override fun getSpanSize(position: Int): Int {
                    return if (position == 0) {
                        3
                    } else {
                        1
                    }
                }
            }
        }
        mediasRecyclerView.adapter = adapter

        lifecycleScope.launch {
            viewModel.galleryDataFlow.collectLatest {
                adapter.submitData(it)
            }
        }
    }
}
