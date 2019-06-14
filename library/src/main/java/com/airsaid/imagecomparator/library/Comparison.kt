package com.airsaid.imagecomparator.library

import android.graphics.Bitmap
import android.support.annotation.FloatRange

/**
 * 图片比对接口。
 *
 * @author airsaid
 */
interface Comparison {

  /**
   * 对两个指定的图片进行比对。
   *
   * @param sourceImage 原图。
   * @param targetImage 比对图。
   * @return 百分比结果。0% - 100%，100% 表示最相近。
   */
  @FloatRange(from = 0.0, to = 100.0)
  fun comparison(sourceImage: Bitmap, targetImage: Bitmap): Float

}