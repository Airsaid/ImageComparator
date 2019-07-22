/*
 * Copyright 2019 Airsaid. https://github.com/airsaid
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.airsaid.imagecomparator

import android.graphics.Bitmap
import android.media.ThumbnailUtils

/**
 * 使用感知哈希算法（差异哈希 d-hash）对图片进行比对。
 *
 * @author airsaid
 */
class DifferencesHashComparison : Comparison {

  override fun comparison(sourceImage: Bitmap, targetImage: Bitmap): Float {
    // 1. 缩小图片尺寸
    var sourceBitmap = ThumbnailUtils.extractThumbnail(sourceImage, 8, 8)
    var targetBitmap = ThumbnailUtils.extractThumbnail(targetImage, 8, 8)

    // 2. 简化色彩
    sourceBitmap = ComparisonHelper.convertToGreyBitmap(sourceBitmap)
    targetBitmap = ComparisonHelper.convertToGreyBitmap(targetBitmap)

    // 3. 计算 d-hash
    val sourceDHash = getDHash(sourceBitmap)
    val targetDHash = getDHash(targetBitmap)

    // 4. 获取汉明距离
    val distance = ComparisonHelper.getHammingDistance(sourceDHash, targetDHash)

    return ComparisonHelper.getPercent(distance, sourceDHash.length)
  }

  private fun getDHash(bitmap: Bitmap): String {
    val sb = StringBuilder()

    val width = bitmap.width
    val height = bitmap.height
    val pixels = IntArray(width * height)
    bitmap.getPixels(pixels, 0, width, 0, 0, width, height)

    for (i in 0 until height) {
      for (j in 0 until width) {
        val r = j + 1
        if (r >= width) {
          continue
        }

        val leftPixel = pixels[width * i + j]
        val rightPixel = pixels[width * i + r]
        if (leftPixel > rightPixel) {
          sb.append(1)
        } else {
          sb.append(0)
        }
      }
    }

    return sb.toString()
  }

}