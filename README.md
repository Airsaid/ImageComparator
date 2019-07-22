# ImageComparator
Image comparator on Android.

# Preview
![image](https://github.com/Airsaid/ImageComparator/blob/master/previews/preview.gif)

# Installation
Add the following dependency to your ```build.gradle``` file:
```
dependencies {
    implementation 'com.airsaid:image-comparator:1.0.1'
}
```

# Usage
```
val imageComparator = ImageComparator(AverageHashComparison())
val result = imageComparator.comparison(sourceBitmap, targetBitmap)
```

# ContactMe
- Telegram: [https://t.me/airsaids/](https://t.me/airsaids/)

# License
```
Copyright 2019 Airsaid. https://github.com/airsaid

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

   http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
```