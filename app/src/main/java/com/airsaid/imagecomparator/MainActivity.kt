package com.airsaid.imagecomparator

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.airsaid.imagecomparator.library.AverageHashComparison
import com.airsaid.imagecomparator.library.DifferencesHashComparison
import com.airsaid.imagecomparator.library.ImageComparator
import com.airsaid.imagecomparator.matisse.Glide4Engine
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomViewTarget
import com.bumptech.glide.request.transition.Transition
import com.zhihu.matisse.Matisse
import com.zhihu.matisse.MimeType
import kotlinx.android.synthetic.main.activity_main.*
import java.io.File

class MainActivity : AppCompatActivity(), View.OnClickListener {

  private var mSourceBitmap: Bitmap? = null
  private var mTargetBitmap: Bitmap? = null

  private val mAHashComparison = AverageHashComparison()
  private val mDHashComparison = DifferencesHashComparison()
  private val mImageComparator = ImageComparator(mAHashComparison)

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_main)
    sourceButton.setOnClickListener(this)
    targetButton.setOnClickListener(this)
    startButton.setOnClickListener(this)
    aHashRadioButton.setOnClickListener(this)
    dHashRadioButton.setOnClickListener(this)
  }

  override fun onClick(v: View) {
    when (v.id) {
      R.id.sourceButton -> selectImage(REQUEST_CODE_SOURCE_IMAGE)
      R.id.targetButton -> selectImage(REQUEST_CODE_TARGET_IMAGE)
      R.id.aHashRadioButton -> mImageComparator.setImageComparison(mAHashComparison)
      R.id.dHashRadioButton -> mImageComparator.setImageComparison(mDHashComparison)
      R.id.startButton -> startComparison()
    }
  }

  private fun selectImage(requestCode: Int) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
      if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED) {
        requestPermissions(arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), requestCode)
      } else {
        selectImageInner(requestCode)
      }
    } else {
      selectImageInner(requestCode)
    }
  }

  override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
    super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    var requestSuccess = false
    if ((requestCode == REQUEST_CODE_SOURCE_IMAGE || requestCode == REQUEST_CODE_TARGET_IMAGE) && grantResults.isNotEmpty()) {
      if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
        requestSuccess = true
      }
    }
    if (requestSuccess) {
      selectImageInner(requestCode)
    } else {
      Snackbar.make(startButton, R.string.no_permission, Snackbar.LENGTH_SHORT).show()
    }
  }

  private fun selectImageInner(requestCode: Int) {
    Matisse.from(this)
      .choose(setOf(MimeType.PNG, MimeType.JPEG))
      .maxSelectable(1)
      .countable(false)
      .imageEngine(Glide4Engine())
      .forResult(requestCode)
  }

  override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
    super.onActivityResult(requestCode, resultCode, data)
    if (resultCode != Activity.RESULT_OK) return
    if (data == null) return

    val obtainResult = Matisse.obtainResult(data) ?: return

    when (requestCode) {
      REQUEST_CODE_SOURCE_IMAGE ->
        showSelectedImageInfo(sourceImage, sourcePath, obtainResult)
      REQUEST_CODE_TARGET_IMAGE ->
        showSelectedImageInfo(targetImage, targetPath, obtainResult)
    }
  }

  private fun showSelectedImageInfo(image: ImageView, path: TextView, data: List<Uri>) {
    if (data.isEmpty()) return

    Glide.with(this)
      .asFile()
      .load(data[0])
      .into(object : CustomViewTarget<ImageView, File>(image) {
        override fun onResourceCleared(placeholder: Drawable?) {}

        override fun onLoadFailed(errorDrawable: Drawable?) {
          Snackbar.make(image, R.string.load_image_failed, Snackbar.LENGTH_SHORT).show()
        }

        override fun onResourceReady(resource: File, transition: Transition<in File>?) {
          val options = BitmapFactory.Options()
          val bitmap = BitmapFactory.decodeFile(resource.path, options)
          if (image.id == R.id.sourceImage) {
            mSourceBitmap = bitmap
          } else {
            mTargetBitmap = bitmap
          }
          path.text = resource.path
          Glide.with(this@MainActivity).load(bitmap).into(image)
        }
      })
  }

  private fun startComparison() {
    val sourceBitmap = mSourceBitmap
    val targetBitmap = mTargetBitmap
    if (sourceBitmap != null && targetBitmap != null) {
      val result = mImageComparator.comparison(sourceBitmap, targetBitmap)
      resultText.text = String.format(getString(R.string.comparison_result, result)).plus("%")
    } else {
      Snackbar.make(startButton, R.string.please_select_image, Snackbar.LENGTH_SHORT).show()
    }
  }

  companion object {
    private const val REQUEST_CODE_SOURCE_IMAGE = 1
    private const val REQUEST_CODE_TARGET_IMAGE = 2
  }
}
