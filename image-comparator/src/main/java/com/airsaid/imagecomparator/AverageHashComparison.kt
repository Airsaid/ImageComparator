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
 * 使用感知哈希算法（均值哈希 a-hash）对图片进行比对。
 *
 * @author airsaid
 */
class AverageHashComparison : Comparison {

  override fun comparison(sourceImage: Bitmap, targetImage: Bitmap): Float {
    // 1. 缩小图片尺寸
    var sourceBitmap = ThumbnailUtils.extractThumbnail(sourceImage, 8, 8)
    var targetBitmap = ThumbnailUtils.extractThumbnail(targetImage, 8, 8)

    // 2. 简化色彩
    sourceBitmap = ComparisonHelper.convertToGreyBitmap(sourceBitmap)
    targetBitmap = ComparisonHelper.convertToGreyBitmap(targetBitmap)

    // 3. 计算平均值
    val sourceAverage = getAverage(sourceBitmap)
    val targetAverage = getAverage(targetBitmap)

    // 4. 比较像素灰度
    val sourceBinary = getBinary(sourceBitmap, sourceAverage)
    val targetBinary = getBinary(targetBitmap, targetAverage)

    // 5. 计算哈希值
    val sourceHexString = binaryString2HexString(sourceBinary)
    val targetHexString = binaryString2HexString(targetBinary)

    // 6. 获取汉明距离
    val distance = ComparisonHelper.getHammingDistance(sourceHexString, targetHexString)

    return ComparisonHelper.getPercent(distance, sourceHexString.length)
  }

  private fun getAverage(bitmap: Bitmap): Int {
    val width = bitmap.width
    val height = bitmap.height
    val pixels = IntArray(width * height)
    bitmap.getPixels(pixels, 0, width, 0, 0, width, height)

    var avgPixel = 0
    for (pixel in pixels) {
      avgPixel += pixel
    }

    return avgPixel / pixels.size
  }

  private fun getBinary(bitmap: Bitmap, average: Int): String {
    val sb = StringBuilder()

    val width = bitmap.width
    val height = bitmap.height
    val pixels = IntArray(width * height)
    bitmap.getPixels(pixels, 0, width, 0, 0, width, height)

    for (i in 0 until height) {
      for (j in 0 until width) {
        val original = pixels[width * i + j]
        if (original >= average) {
          pixels[width * i + j] = 1
        } else {
          pixels[width * i + j] = 0
        }
        sb.append(pixels[width * i + j])
      }
    }

    return sb.toString()
  }

  private fun binaryString2HexString(binaryString: String): String {
    val sb = StringBuilder()
    var temporary: Int
    var index = 0
    while (index < binaryString.length) {
      temporary = 0
      for (j in 0..3) {
        temporary += Integer.parseInt(binaryString.substring(index + j, index + j + 1)) shl 4 - j - 1
      }
      sb.append(Integer.toHexString(temporary))
      index += 4
    }
    return sb.toString()
  }

}