package uk.gov.justice.digital.hmpps.cmd.api.uk.gov.justice.digital.hmpps.cmd.api.client.dto

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty
import io.swagger.annotations.ApiModel
import uk.gov.justice.digital.hmpps.cmd.api.uk.gov.justice.digital.hmpps.cmd.api.domain.ShiftActionType
import java.time.LocalDateTime

data class ShiftNotificationDto @JsonCreator constructor(

        @JsonProperty("quantumId")
        var quantumId: String,

        @JsonProperty("shiftDate")
        var shiftDate: LocalDateTime,

        @JsonProperty("lastModifiedDateTime")
        var shiftModified: LocalDateTime,

        @JsonProperty("taskStartTimeInSeconds")
        var taskStart: Long?,

        @JsonProperty("taskEndTimeInSeconds")
        var taskEnd: Long?,

        @JsonProperty("activity")
        var task: String?,

        @JsonProperty("type")
        var shiftType: String,

        @JsonProperty("actionType")
        var actionType: String = ShiftActionType.ADD.value
)