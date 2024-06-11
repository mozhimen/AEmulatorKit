package com.mozhimen.emulatork.basic.system

import com.mozhimen.basick.utilk.kotlin.UtilKStrFile
import com.mozhimen.basick.utilk.kotlin.kiloBytes
import com.mozhimen.basick.utilk.kotlin.megaBytes
import com.mozhimen.basick.utilk.kotlin.startsWithAny
import com.mozhimen.basick.utilk.kotlin.indexOf
import timber.log.Timber
import java.io.InputStream
import java.nio.charset.Charset
import kotlin.math.ceil
import kotlin.math.roundToInt

/**
 * @ClassName SerialScanner
 * @Description TODO
 * @Author Mozhimen & Kolin Zhao
 * @Date 2024/5/11
 * @Version 1.0
 */
object SystemSerialExtractor {

    private const val PS_SERIAL_MAX_SIZE = 12

    //////////////////////////////////////////////////////////////////////////

    private val READ_BUFFER_SIZE = 64.kiloBytes()

    @ExperimentalUnsignedTypes
    private val MAGIC_NUMBERS = listOf(
        SystemSerialBundle(
            0x0010,
            ubyteArrayOf(0x53U, 0x45U, 0x47U, 0x41U, 0x44U, 0x49U, 0x53U, 0x43U, 0x53U, 0x59U, 0x53U, 0x54U, 0x45U, 0x4dU).toByteArray(),
            ESystemType.SEGACD
        ),
        SystemSerialBundle(
            0x8008,
            ubyteArrayOf(0x50U, 0x4cU, 0x41U, 0x59U, 0x53U, 0x54U, 0x41U, 0x54U, 0x49U, 0x4fU, 0x4eU).toByteArray(),
            ESystemType.PSX
        ),
        SystemSerialBundle(
            0x9320,
            ubyteArrayOf(0x50U, 0x4cU, 0x41U, 0x59U, 0x53U, 0x54U, 0x41U, 0x54U, 0x49U, 0x4fU, 0x4eU).toByteArray(),
            ESystemType.PSX
        ),
        SystemSerialBundle(
            0x8008,
            ubyteArrayOf(0x50U, 0x53U, 0x50U, 0x20U, 0x47U, 0x41U, 0x4dU, 0x45U).toByteArray(),
            ESystemType.PSP
        ),
    )

    private val SEGA_CD_REGEX = Regex("([A-Z]+)?-?([0-9]+) ?-?([0-9]*)")

    private val PS_SERIAL_REGEX = Regex("^([A-Z]+)-?([0-9]+)")

    private val PS_SERIAL_REGEX2 = Regex("^([A-Z]+)_?([0-9]{3})\\.([0-9]{2})")

    private val PSX_BASE_SERIALS = listOf(
        "CPCS",
        "SCES",
        "SIPS",
        "SLKA",
        "SLPS",
        "SLUS",
        "ESPM",
        "SLED",
        "SCPS",
        "SCAJ",
        "PAPX",
        "SLES",
        "HPS",
        "LSP",
        "SLPM",
        "SCUS",
        "SCED"
    )

    private val PSP_BASE_SERIALS = listOf(
        "ULES",
        "ULUS",
        "ULJS",
        "ULEM",
        "ULUM",
        "ULJM",
        "ULKS",
        "ULAS",
        "UCES",
        "UCUS",
        "UCJS",
        "UCAS",
        "NPEH",
        "NPUH",
        "NPJH",
        "NPEG",
        "NPEX",
        "NPUG",
        "NPJG",
        "NPJJ",
        "NPHG",
        "NPEZ",
        "NPUZ",
        "NPJZ",
        "NPUF",
        "NPUZ",
        "NPUG",
        "NPUX"
    )

    //////////////////////////////////////////////////////////////////////////

    fun extractInfo(fileName: String, inputStream: InputStream): SystemSerial {
        Timber.d("Extracting disk info for $fileName")
        inputStream.buffered(READ_BUFFER_SIZE).use {
            return when (UtilKStrFile.extractExtension(fileName)) {
                "pbp" -> extractInfoForPBP(it)
                "iso", "bin" -> standardExtractInfo(it)
                "3ds" -> extractInfoFor3DS(it)
                else -> SystemSerial(null, null)
            }
        }
    }

    //////////////////////////////////////////////////////////////////////////

    private fun standardExtractInfo(openedStream: InputStream): SystemSerial {
        openedStream.mark(READ_BUFFER_SIZE)
        val header = readByteArray(openedStream, ByteArray(READ_BUFFER_SIZE))

        val detectedSystem = MAGIC_NUMBERS
            .firstOrNull {
                header.copyOfRange(it.offset, it.offset + it.numbers.size).contentEquals(it.numbers)
            }
            ?.eSystemType

        Timber.d("SystemID detected via magic numbers: $detectedSystem")

        openedStream.reset()

        return when (detectedSystem) {
            ESystemType.SEGACD ->
                runCatching { extractInfoForSegaCD(openedStream) }
                    .getOrDefault(SystemSerial(null, ESystemType.SEGACD))

            ESystemType.PSX ->
                runCatching { extractInfoForPSX(openedStream) }
                    .getOrDefault(SystemSerial(null, ESystemType.PSX))

            ESystemType.PSP ->
                runCatching { extractInfoForPSP(openedStream) }
                    .getOrDefault(SystemSerial(null, ESystemType.PSP))

            else -> SystemSerial(null, null)
        }
    }

    private fun extractInfoFor3DS(openedStream: InputStream): SystemSerial {
        Timber.d("Parsing 3DS game")
        openedStream.mark(0x2000)
        openedStream.skip(0x1150)

        val rawSerial = String(readByteArray(openedStream, ByteArray(10)), Charsets.US_ASCII)

        openedStream.reset()

        Timber.d("Found 3DS serial: $rawSerial")
        return SystemSerial(rawSerial, ESystemType.NINTENDO_3DS)
    }

    private fun extractInfoForSegaCD(openedStream: InputStream): SystemSerial {
        Timber.d("Parsing SegaCD game")
        openedStream.mark(20000)
        openedStream.skip(0x193)

        val rawSerial = String(readByteArray(openedStream, ByteArray(16)), Charsets.US_ASCII)

        Timber.d("Detected SegaCD raw serial read: $rawSerial")

        openedStream.reset()
        openedStream.skip(0x200)

        val regionID = String(readByteArray(openedStream, ByteArray(1)), Charsets.US_ASCII)

        Timber.d("Detected SegaCD region: $regionID")

        val groups = SEGA_CD_REGEX.find(rawSerial)?.groupValues

        // The following rules come from here: https://github.com/libretro/RetroArch/pull/11719/files
        // and some guess work. They are by no means complete.
        val prefix = groups?.get(1)
        val num = groups?.get(2)
        var postfix = groups?.get(3)

        if (regionID == "E")
            postfix = "50"

        if (postfix == "00")
            postfix = null

        val finalSerial = sequenceOf(prefix, num, postfix)
            .filterNotNull()
            .filter { it.isNotBlank() }
            .joinToString("-") { it.trim() }

        Timber.i("SegaCD final serial: $finalSerial")
        return SystemSerial(finalSerial, ESystemType.SEGACD)
    }

    private fun extractInfoForPSX(openedStream: InputStream): SystemSerial {
        val headerSize = 64.kiloBytes()
        if (openedStream.available() < headerSize) {
            return SystemSerial(null, null)
        }

        return textSearch(PSX_BASE_SERIALS, openedStream, PS_SERIAL_MAX_SIZE, headerSize)
            .mapNotNull { serial -> parsePSXSerial(serial) }
            .mapNotNull { serial -> SystemSerial(serial, ESystemType.PSX) }
            .firstOrNull() ?: SystemSerial(null, ESystemType.PSX)
    }

    private fun extractInfoForPSP(openedStream: InputStream): SystemSerial {
        val headerSize = 64.kiloBytes()
        if (openedStream.available() < headerSize) {
            return SystemSerial(null, null)
        }

        return textSearch(PSP_BASE_SERIALS, openedStream, PS_SERIAL_MAX_SIZE, headerSize)
            .mapNotNull { serial -> parsePSXSerial(serial) }
            .mapNotNull { serial -> SystemSerial(serial, ESystemType.PSP) }
            .firstOrNull() ?: SystemSerial(null, ESystemType.PSP)
    }

    private fun extractInfoForPBP(openedStream: InputStream): SystemSerial {
        val headerSize = 2.megaBytes()
        if (openedStream.available() < headerSize) {
            return SystemSerial(null, null)
        }

        val queries = (PSP_BASE_SERIALS + PSX_BASE_SERIALS)

        return textSearch(queries, openedStream, PS_SERIAL_MAX_SIZE, headerSize)
            .mapNotNull { serial -> parsePSXSerial(serial) }
            .mapNotNull { serial ->
                when {
                    serial.startsWithAny(PSX_BASE_SERIALS) -> SystemSerial(serial, ESystemType.PSX)
                    serial.startsWithAny(PSP_BASE_SERIALS) -> SystemSerial(serial, ESystemType.PSP)
                    else -> SystemSerial(serial, null)
                }
            }
            .firstOrNull() ?: SystemSerial(null, null)
    }

    private fun parsePSXSerial(serial: String): String? {
        return sequenceOf(
            PS_SERIAL_REGEX.find(serial)?.groupValues?.let { "${it[1]}-${it[2]}" },
            PS_SERIAL_REGEX2.find(serial)?.groupValues?.let { "${it[1]}-${it[2]}${it[3]}" },
        ).filter { it != null }
            .firstOrNull()
    }

    private fun textSearch(
        queries: List<String>,
        openedStream: InputStream,
        resultSize: Int,
        streamSize: Int,
        windowSize: Int = 8.kiloBytes(),
        skipSize: Int = windowSize - resultSize,
        charset: Charset = Charsets.US_ASCII
    ): Sequence<String> {
        val byteQueries = queries.map { it.toByteArray(charset) }
        return movingWidnowSequence(openedStream, windowSize, (skipSize).toLong())
            .take(ceil(streamSize.toDouble() / skipSize.toDouble()).roundToInt())
            .flatMap { serial ->
                byteQueries.asSequence()
                    .map { serial.indexOf(it) }
                    .filter { it >= 0 }
                    .map { serial to it }
            }
            .map { (bytes, index) ->
                val serialBytes = bytes.copyOfRange(index, index + resultSize)
                String(serialBytes, charset)
            }
            .filterNotNull()
    }

    private fun movingWidnowSequence(
        inputStream: InputStream,
        windowSize: Int,
        windowSkip: Long
    ) = sequence {
        val buffer = ByteArray(windowSize)
        do {
            inputStream.mark(windowSize)
            yield(readByteArray(inputStream, buffer))
            inputStream.reset()
        } while (inputStream.skip(windowSkip) != 0L)
    }

    private fun readByteArray(inputStream: InputStream, byteArray: ByteArray): ByteArray {
        val readBytes = inputStream.read(byteArray)
        return if (readBytes < byteArray.size) {
            byteArray.copyOf(readBytes)
        } else {
            byteArray
        }
    }
}
