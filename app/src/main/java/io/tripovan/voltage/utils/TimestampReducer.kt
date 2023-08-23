package io.tripovan.voltage.utils

/**
 * This is some kind of workaround for MPAndroidChart because it can't work with timestamps in Long format, causing inaccurate values (Float)
 * The solution here is:
 * - add / subtract large number from the Float value
 * - reduce time to seconds
 */
class TimestampReducer {

    companion object {
        fun longToFloatTs(value: Long): Float {
            return (reduceMillisToSeconds(value) - Constants.largeNumberToExtractFromTs).toFloat()
        }

        fun floatToLongTs(value: Float): Long {
            return reduceMillisToSeconds(value.toLong() + Constants.largeNumberToExtractFromTs)
        }

        fun reduceMillisToSeconds(value: Long): Long {
            return  (value / 1000) * 1000
        }
    }
}