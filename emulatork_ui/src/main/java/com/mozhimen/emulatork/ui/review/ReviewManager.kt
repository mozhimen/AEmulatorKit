package com.mozhimen.emulatork.ui.review

import android.app.Activity
import android.content.Context

/**
 * @ClassName ReviewManager
 * @Description TODO
 * @Author Mozhimen & Kolin Zhao
 * @Date 2024/5/13
 * @Version 1.0
 */
// The real ReviewManager is implemented in the play version. This is basically stubbed.
class ReviewManager {
    suspend fun initialize(context: Context) {}

    suspend fun launchReviewFlow(activity: Activity, sessionTimeMillis: Long) {}
}