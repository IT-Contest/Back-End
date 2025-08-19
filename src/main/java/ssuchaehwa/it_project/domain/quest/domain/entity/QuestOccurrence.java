package ssuchaehwa.it_project.domain.quest.domain.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "quest_occurrence",
       uniqueConstraints = {
           @UniqueConstraint(name = "uq_template_period", columnNames = {"template_id", "period_key"})
       },
       indexes = {
           @Index(name = "idx_user_period", columnList = "user_id, period_key"),
           @Index(name = "idx_completed_at", columnList = "completed_at")
       }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class QuestOccurrence {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "template_id", nullable = false)
    private Long templateId;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    // 퀘스트 내용
    @Column(nullable = false, length = 100)
    private String title;

    @Column(name = "quest_type", nullable = false, length = 16)
    private String questType; // DAILY, WEEKLY, MONTHLY, YEARLY

    @Column(name = "period_key", nullable = false)
    private LocalDate periodKey;

    @Column(name = "status", nullable = false, length = 16)
    private String status; // INCOMPLETE, COMPLETED

    @Column(name = "completed_at")
    private LocalDateTime completedAt;

    @Column(name = "expected_start_time")
    private String expectedStartTime;

    @Column(name = "expected_end_time")
    private String expectedEndTime;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    public void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        if (this.status == null) {
            this.status = "INCOMPLETE";
        }
    }

    @PreUpdate
    public void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}
