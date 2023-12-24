package com.tofi.bankingsystem.repositiries;

import com.tofi.bankingsystem.entities.Saving;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SavingRepository extends JpaRepository<Saving, Long> {
}
