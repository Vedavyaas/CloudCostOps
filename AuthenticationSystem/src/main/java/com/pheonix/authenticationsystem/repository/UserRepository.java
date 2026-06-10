package com.pheonix.authenticationsystem.repository;

import com.pheonix.authenticationsystem.assets.Role;
import com.pheonix.authenticationsystem.assets.UserDTO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<UserEntity, Long> {
    boolean existsByUserName(String userName);

    Optional<UserEntity> findByUserName(String userName);

    @Query("SELECT new com.pheonix.authenticationsystem.assets.UserDTO(u.userName, u.company.companyName, u.email, u.role) " +
           "FROM UserEntity u WHERE u.userName = :userName")
    Optional<UserDTO> findUserDTOByUserName(@Param("userName") String userName);

    Object countByCompany_CompanyNameAndRole(String companyCompanyName, Role role);

    @Query("SELECT new com.pheonix.authenticationsystem.assets.UserDTO(u.userName, u.company.companyName, u.email, u.role) " +
           "FROM UserEntity u WHERE u.company.companyName = :companyName AND u.role = :role")
    List<UserDTO> findAnalystsByCompanyNameAndRole(@Param("companyName") String companyName, @Param("role") Role role);
}
