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