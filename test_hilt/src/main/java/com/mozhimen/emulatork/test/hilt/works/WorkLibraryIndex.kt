package com.mozhimen.emulatork.test.hilt.works

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.ListenableWorker
import androidx.work.WorkerParameters
import com.mozhimen.emulatork.basic.EmulatorKBasic
import com.mozhimen.emulatork.ui.works.AbsWorkLibraryIndex
import com.mozhimen.emulatork.test.hilt.game.GameActivity
import com.mozhimen.emulatork.ui.game.AbsGameActivity
import com.mozhimen.emulatork.ui.works.AbsWorkCoreUpdate
import dagger.Binds
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import dagger.multibindings.IntoMap
import javax.inject.Inject

/**
 * @ClassName LibraryIndexWork
 * @Description TODO
 * @Author Mozhimen / Kolin Zhao
 * @Date 2024/5/20 22:29
 * @Version 1.0
 */
@HiltWorker
class WorkLibraryIndex @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted workerParams: WorkerParameters,
    private val lemuroidLibrary: EmulatorKBasic,
) : AbsWorkLibraryIndex(context, workerParams) {

    ///////////////////////////////////////////////////////////////////////////

    override fun lemuroidLibrary(): EmulatorKBasic {
        return lemuroidLibrary
    }

    override fun gameActivityClazz(): Class<out AbsGameActivity> {
        return GameActivity::class.java
    }

    override fun workCoreUpdateClazz(): Class<out AbsWorkCoreUpdate> {
        return WorkCoreUpdate::class.java
    }

    ///////////////////////////////////////////////////////////////////////////

//    @dagger.Module(subcomponents = [Subcomponent::class])
//    abstract class Module {
//        @Binds
//        @IntoMap
//        @WorkerKey(WorkLibraryIndex::class)
//        abstract fun bindMyWorkerFactory(builder: Subcomponent.Builder): AndroidInjector.Factory<out ListenableWorker>
//    }
//
//    @dagger.Subcomponent
//    interface Subcomponent : AndroidInjector<WorkLibraryIndex> {
//        @dagger.Subcomponent.Builder
//        abstract class Builder : AndroidInjector.Builder<WorkLibraryIndex>()
//    }
}