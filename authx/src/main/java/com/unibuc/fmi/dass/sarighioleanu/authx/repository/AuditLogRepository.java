package com.unibuc.fmi.dass.sarighioleanu.authx.repository;


import com.unibuc.fmi.dass.sarighioleanu.authx.model.AuditLog;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AuditLogRepository extends JpaRepository<AuditLog,Long> {

}
