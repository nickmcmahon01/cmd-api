package uk.gov.justice.digital.hmpps.cmd.api.dto

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty
import io.swagger.annotations.ApiModel
import io.swagger.annotations.ApiModelProperty
import uk.gov.justice.digital.hmpps.cmd.api.model.ShiftNotification
import uk.gov.justice.digital.hmpps.cmd.api.uk.gov.justice.digital.hmpps.cmd.api.domain.NotificationDescription
import uk.gov.justice.digital.hmpps.cmd.api.uk.gov.justice.digital.hmpps.cmd.api.model.CommunicationPreference
import java.time.Clock
import java.time.LocalDateTime

@ApiModel(description = "Notification")
data class NotificationDto @JsonCreator constructor(
        @ApiModelProperty(required = true, value = "Description of notification", position = 1, example = "Your shift on 2020-04-20 has changed.")
        @JsonProperty("description")
        val description: String,
        @ApiModelProperty(required = true, value = "When the shift was modified", position = 2, example = "2020-04-20T17:45:55")
        @JsonProperty("shiftModified")
        val shiftModified: LocalDateTime,
        @ApiModelProperty(required = true, value = "Whether the notification has been processed", position = 3, example = "true")
        @JsonProperty("processed")
        val processed: Boolean
) {

    companion object {

        fun from(shiftNotifications: Collection<ShiftNotification>, clock: Clock): Collection<NotificationDto> {
            return shiftNotifications.map {
                from(it, clock)
            }
        }

        fun from(shiftNotification: ShiftNotification, clock: Clock): NotificationDto {
            val description = NotificationDescription.getNotificationDescription(
                    shiftNotification,
                    CommunicationPreference.NONE,
                    clock)
            return NotificationDto(description,
                    shiftNotification.shiftModified,
                    shiftNotification.processed)
        }

    }
}
