package jp.geisha.yourgallery

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import permissions.dispatcher.*

@RuntimePermissions
class HomeActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_medias)
        startMediaActivityWithPermissionCheck()
    }

    @NeedsPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
    fun startMediaActivity() {
        startActivity(MediaActivity.createIntent(this))
        finish()
    }

    @OnShowRationale(Manifest.permission.READ_EXTERNAL_STORAGE)
    fun onShowRationaleStorage(request: PermissionRequest) {
        showRationaleDialog(request)
    }

    @OnNeverAskAgain(Manifest.permission.READ_EXTERNAL_STORAGE)
    internal fun onNeverAskAgainStorage() {
        showNeverAskAgainDialog()
    }

    @OnPermissionDenied(Manifest.permission.READ_EXTERNAL_STORAGE)
    internal fun onPermissionDeniedStorage() {
        Toast.makeText(this, "おい", Toast.LENGTH_LONG).show()
        finish()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 0) {
            if (resultCode == Activity.RESULT_OK) {
                setResult(resultCode, data)
            }
            finish()
        }
    }

    private fun showRationaleDialog(request: PermissionRequest) {
        AlertDialog.Builder(this)
            .setMessage("")
            .setPositiveButton(android.R.string.ok) { _, _ -> request.proceed() }
            .setNegativeButton(android.R.string.cancel) { _, _ -> request.cancel() }
            .setCancelable(false)
            .show()
    }

    private fun showNeverAskAgainDialog() {
        AlertDialog.Builder(this)
            .setMessage("")
            .setPositiveButton(android.R.string.ok) { _, _ -> finish() }
            .setCancelable(false)
            .show()
    }
}