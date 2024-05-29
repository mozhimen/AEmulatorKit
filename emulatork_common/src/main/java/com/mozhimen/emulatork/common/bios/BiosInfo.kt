package com.mozhimen.emulatork.common.bios

/**
 * @ClassName BiosInfo
 * @Description TODO
 * @Author Mozhimen & Kolin Zhao
 * @Date 2024/5/22
 * @Version 1.0
 */
data class BiosInfo(val detected: List<Bios>, val notDetected: List<Bios>)
