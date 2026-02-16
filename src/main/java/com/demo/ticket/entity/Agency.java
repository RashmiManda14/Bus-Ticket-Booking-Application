package com.demo.ticket.entity;

 
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
 
import java.util.ArrayList;
import java.util.List;
 
@Entity
@Table(name = "agencies")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Agency {
 
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "agency_id")
    private Integer id;
 
    @NotBlank
    @Column(name = "name", length = 255, nullable = false)
    private String name;
 
    @Column(name = "contact_person_name", length = 48)
    private String contactPersonName;
 
    @Email
    @Column(name = "email", length = 255)
    private String email;
 
    @Column(name = "phone", length = 15)
    private String phone;
 
    // Use PERSIST + MERGE so saving/updating Agency also saves Offices
    // Keep orphanRemoval=false unless you want removing from the list to delete trdhe office row
    @OneToMany(
        mappedBy = "agency",
        fetch = FetchType.LAZY,
        cascade = { CascadeType.PERSIST, CascadeType.MERGE },
        orphanRemoval = false
    )
    @Builder.Default
    private List<AgencyOffice> offices = new ArrayList<>();
 
    /** Helper methods to keep both sides in sync */
    public void addOffice(AgencyOffice office) {
        offices.add(office);
        office.setAgency(this);
    }
 
    public void removeOffice(AgencyOffice office) {
        offices.remove(office);
        office.setAgency(null);
    }
}
 
