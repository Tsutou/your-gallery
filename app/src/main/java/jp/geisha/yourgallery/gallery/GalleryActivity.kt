package jp.geisha.yourgallery.gallery

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import jp.geisha.yourgallery.R
import jp.geisha.yourgallery.label.LabelsAdapter
import kotlinx.android.synthetic.main.activity_gallery.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
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

    private val labelsAdapter = LabelsAdapter()
    private val galleryAdapter = GalleryAdapter()

    @ExperimentalCoroutinesApi
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_gallery)

        labelsRecyclerView.apply {
            layoutManager =
                LinearLayoutManager(this@GalleryActivity, LinearLayoutManager.HORIZONTAL, false)
            adapter = labelsAdapter
        }

        mediasRecyclerView.apply {
            layoutManager = GridLayoutManager(this@GalleryActivity, 3).apply {
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
            adapter = galleryAdapter
        }

        lifecycleScope.launch {
            viewModel.labelLoadState.observe(this@GalleryActivity, Observer {
                it ?: return@Observer
                when (it) {
                    is GalleryViewModel.State.Loading -> {
                        labelsLoading.visibility = View.VISIBLE
                        labelsLoading.setProgressWithAnimation(it.progress, 1000)
                    }
                    GalleryViewModel.State.Success -> labelsLoading.visibility = View.GONE
                }
            })
            viewModel.labelsData.observe(this@GalleryActivity, Observer {
                labelsAdapter.updateLabels(it)
            })
            viewModel.galleryDataFlow.collectLatest {
                galleryAdapter.submitData(it)
            }
        }
    }
}
