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

/**
 * @author airsaid
 */
object ComparisonHelper {

  fun convertToGreyBitmap(bitmap: Bitmap): Bitmap {
    val width = bitmap.width
    val height = bitmap.height
    val pixels = IntArray(width * height)
    bitmap.getPixels(pixels, 0, width, 0, 0, width, height)

    val alpha = 0xFF shl 24
    for (i in 0 until height) {
      for (j in 0 until width) {
        val original = pixels[width * i + j]
        val red = original and 0x00FF0000 shr 16
        val green = original and 0x0000FF00 shr 8
        val blue = original and 0x000000FF
        var grey = (red.toFloat() * 0.3 + green.toFloat() * 0.59 + blue.toFloat() * 0.11).toInt()
        grey = alpha or (grey shl 16) or (grey shl 8) or grey
        pixels[width * i + j] = grey
      }
    }

    val result = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565)
    result.setPixels(pixels, 0, width, 0, 0, width, height)
    return result
  }

  fun getHammingDistance(hash1: String, hash2: String): Int {
    val chars1 = hash1.toCharArray()
    val chars2 = hash2.toCharArray()

    var distance = 0
    for (i in chars1.indices) {
      if (chars1[i] != chars2[i]) {
        distance++
      }
    }

    return distance
  }

  fun getPercent(distance: Int, maxDistance: Int): Float {
    return 100f.div(maxDistance).times(maxDistance - distance)
  }
}