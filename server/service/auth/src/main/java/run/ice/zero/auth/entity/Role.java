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
@Table(schema = "zero_open", name = "rbac_role")
public class Role implements Serializer {

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

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "role")
    @JsonIgnore
    @ToString.Exclude
    private List<RolePerm> rolePerms;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "role")
    @JsonIgnore
    @ToString.Exclude
    private List<UserRole> userRoles;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(schema = "zero_open", name = "rbac_role_perm", joinColumns = {@JoinColumn(name = "role_id", referencedColumnName = "id")}, inverseJoinColumns = {@JoinColumn(name = "perm_id", referencedColumnName = "id")})
    @JsonIgnore
    @ToString.Exclude
    private List<Perm> perms;

    @ManyToMany(fetch = FetchType.LAZY, mappedBy = "roles")
    @JsonIgnore
    @ToString.Exclude
    private List<User> users;

}
