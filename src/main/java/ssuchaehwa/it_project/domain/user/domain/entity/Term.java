package ssuchaehwa.it_project.domain.user.domain.entity;

import jakarta.persistence.*;
import lombok.*;
import ssuchaehwa.it_project.domain.model.entity.BaseTimeEntity;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Term extends BaseTimeEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String url;

    @Column(nullable = false)
    private boolean isRequired;

    @Column(nullable = false, length = 20)
    private String version;
}
