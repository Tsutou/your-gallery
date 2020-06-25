package jp.geisha.yourgallery

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import kotlinx.android.synthetic.main.activity_medias.*

class MediaActivity : AppCompatActivity() {

    companion object {
        fun createIntent(context: Context): Intent {
            return Intent(context, MediaActivity::class.java)
        }
    }

    private val viewModel by lazy {
        ViewModelProvider(this).get(MediasViewModel::class.java)
    }

    private val adapter = MediaAdapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_medias)

        recyclerView.layoutManager = GridLayoutManager(this, 3)
        recyclerView.adapter = adapter

        viewModel.photosData.observe(this, Observer {
            adapter.submitList(it)
        })

        viewModel.getPhotos()
    }
}
