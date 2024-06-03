package com.mozhimen.emulatork.basic.bios

/**
 * @ClassName BiosInfo
 * @Description TODO
 * @Author Mozhimen & Kolin Zhao
 * @Date 2024/5/22
 * @Version 1.0
 */
data class BiosProcessor(val detected: List<Bios>, val notDetected: List<Bios>)
