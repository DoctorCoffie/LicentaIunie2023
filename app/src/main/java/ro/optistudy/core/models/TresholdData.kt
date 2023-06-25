package ro.optistudy.core.models

import java.time.ZonedDateTime

data class TresholdData(val value: Int, val millis: Long, val timestamp: ZonedDateTime, val token: String)
