package uk.gov.justice.digital.hmpps.cmd.api.model

import uk.gov.justice.digital.hmpps.cmd.api.client.CsrModifiedDetailDto
import uk.gov.justice.digital.hmpps.cmd.api.domain.CommunicationPreference
import uk.gov.justice.digital.hmpps.cmd.api.domain.DetailModificationType
import uk.gov.justice.digital.hmpps.cmd.api.uk.gov.justice.digital.hmpps.cmd.api.domain.DetailParentType
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import javax.persistence.*

@Entity
@Table(name = "notification")
data class Notification(
        @Id
        @Column(name = "ID")
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        var id: Long? = null,

        @Column(nullable = false)
        var quantumId: String,

        @Column(nullable = false)
        var shiftModified: LocalDateTime,

        @Column(nullable = false)
        var detailStart: LocalDateTime,

        @Column(nullable = false)
        var detailEnd: LocalDateTime,

        @Column
        var activity: String?,

        @Enumerated(EnumType.STRING)
        @Column(nullable = false)
        var parentType: DetailParentType,

        @Enumerated(EnumType.STRING)
        @Column(nullable = false)
        var actionType: DetailModificationType,

        @Column(nullable = false)
        var processed: Boolean
) {
        fun getNotificationDescription(communicationPreference: CommunicationPreference): String {

                val bulletPoint = getOptionalBulletPoint(communicationPreference)
                val date = this.detailStart.getDateTimeFormattedForTemplate()
                val taskTime = getOptionalTaskDescription(this.detailStart, this.detailEnd, this.activity)
                val shiftActionType = this.actionType
                val taskTo = getOptionalTaskTo(this.activity, communicationPreference, shiftActionType)
                val shiftNotificationType = getNotificationType(this.parentType, this.activity)

                return "${bulletPoint}Your $shiftNotificationType on $date ${taskTime}has ${shiftActionType.description}${taskTo}."
        }

        companion object {

                fun fromDto(dtoCsrs: Collection<CsrModifiedDetailDto>): Collection<Notification> {
                        return dtoCsrs.map { fromDto(it) }
                }

                fun fromDto(dtoCsr: CsrModifiedDetailDto): Notification {
                        return Notification(
                                quantumId = dtoCsr.quantumId,
                                shiftModified = dtoCsr.shiftModified,
                                detailStart = dtoCsr.detailStart,
                                detailEnd = dtoCsr.detailEnd,
                                activity = dtoCsr.activity,
                                parentType = dtoCsr.shiftType,
                                actionType = dtoCsr.actionType,
                                processed = false)
                }

                fun LocalDateTime.getDateTimeFormattedForTemplate(): String {
                        val day = this.dayOfMonth
                        val ordinal = if (day in 11..13) {
                                "th"
                        } else when (day % 10) {
                                1 -> "st"
                                2 -> "nd"
                                3 -> "rd"
                                else -> "th"
                        }

                        return DateTimeFormatter.ofPattern("EEEE, d'$ordinal' MMMM").format(this)
                }

                private fun getNotificationType(shiftNotificationType: DetailParentType, activity: String?) : String {
                        return if(shiftNotificationType == DetailParentType.SHIFT) {
                                if(activity == null) {
                                        "shift"
                                } else {
                                        "detail"
                                }
                        } else {
                                if(activity == null) {
                                        "overtime shift"
                                } else {
                                        "overtime detail"
                                }
                        }
                }

                private fun getOptionalTaskDescription(from: LocalDateTime, to: LocalDateTime, task: String?): String {
                        return if (!task.isNullOrEmpty())
                        {
                                if(from.toLocalTime().isAfter(LocalTime.MIN) && to.toLocalTime().isAfter(LocalTime.MIN)) {
                                        "(${from.toLocalTime()} - ${to.toLocalTime()}) "
                                } else {
                                        "(full day) "
                                }
                        } else ""
                }

                private fun getOptionalTaskTo(task: String?, communicationPreference: CommunicationPreference, shiftActionType: DetailModificationType): String {
                        return if (communicationPreference == CommunicationPreference.NONE && task != null && task.isNotEmpty()) {
                                when(shiftActionType) {
                                        DetailModificationType.ADD -> {
                                                " as $task"
                                        }
                                        DetailModificationType.EDIT -> {
                                                " to $task"
                                        }
                                        DetailModificationType.DELETE -> {
                                                " (was $task)"
                                        }
                                        else -> {
                                                ""
                                        }
                                }

                        } else ""
                }

                // Notify supports bullet points for Email but not Sms
                private fun getOptionalBulletPoint(communicationPreference: CommunicationPreference): String {
                        return if (communicationPreference == CommunicationPreference.EMAIL) {
                                "* "
                        } else ""
                }
        }
}
