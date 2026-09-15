package com.johnny.petscheduler.entities.users;

import com.johnny.petscheduler.entities.Review;
import com.johnny.petscheduler.entities.pet.Pet;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Entity
@Getter @Setter @NoArgsConstructor
public class PetBusiness extends User
{
    @Column(name = "business_name", nullable = false)
    private String businessName;

    @Column(name = "business_addr", nullable = false)
    private String address;

    @Column(name = "city", nullable = false)
    private String city;

    @Column(name = "business_year", nullable = false)
    private Integer establishmentYear;

    @Column(name = "profile_title")
    private String profileTitle;

    @Column(name = "profile_desc")
    private String profileDescription;

    @Column(name = "business_url")
    private String websiteURL;

    @Column(name = "business_phone")
    private String phoneNumber;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Pet> pets = new ArrayList<>();

    @OneToMany(mappedBy = "reviewedUser")
    private List<Review> reviewsReceived = new ArrayList<>();

    // TODO: Add a List of Employees
    // TODO: PetBusiness can register employees
    // TODO: Each employee has their own mini profile
    // TODO: PetOwners can select which employee that want service with

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(
            new SimpleGrantedAuthority("ROLE_" + this.getRole().name())
        );
    }

    @Override
    public String getUsername() {
        return super.getEmail();
    }

    @Override
    public boolean isAccountNonExpired() {
        return super.isAccountNonExpired();
    }

    @Override
    public boolean isAccountNonLocked() {
        return super.isAccountNonLocked();
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return super.isCredentialsNonExpired();
    }

    @Override
    public boolean isEnabled() {
        return super.isEnabled();
    }
}
