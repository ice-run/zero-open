package run.ice.zero.auth.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import run.ice.zero.common.model.Serializer;

import java.time.LocalDateTime;
import java.util.List;

/**
 * @author DaoDao
 */
@Getter
@Setter
@ToString
@Entity
@DynamicInsert
@DynamicUpdate
@Table(schema = "zero_open", name = "rbac_perm")
public class Perm implements Serializer {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "code")
    private String code;

    @Column(name = "name")
    private String name;

    @Column(name = "create_time")
    private LocalDateTime createTime;

    @Column(name = "update_time")
    private LocalDateTime updateTime;

    @Column(name = "valid")
    private Boolean valid;

    @Version
    @Column(name = "version")
    private Long version;

    @PrePersist
    protected void onCreate() {
        if (createTime == null) {
            createTime = LocalDateTime.now();
        }
        updateTime = LocalDateTime.now();
        if (valid == null) {
            valid = true;
        }
    }

    @PreUpdate
    protected void onUpdate() {
        updateTime = LocalDateTime.now();
    }

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "perm")
    @JsonIgnore
    @ToString.Exclude
    private List<RolePerm> rolePerms;

    @ManyToMany(fetch = FetchType.LAZY, mappedBy = "perms")
    @JsonIgnore
    @ToString.Exclude
    private List<Role> roles;

}
