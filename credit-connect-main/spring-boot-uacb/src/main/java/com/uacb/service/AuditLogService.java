package com.uacb.service;

import com.uacb.entity.AuditLog;
import com.uacb.entity.User;
import com.uacb.repository.AuditLogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AuditLogService {
    
    private final AuditLogRepository auditLogRepository;
    
    public enum Action {
        CREATE, UPDATE, DELETE, LOGIN, LOGOUT,
        CREDIT_ISSUED, CREDIT_VERIFIED, CREDIT_REJECTED,
        REGISTRATION, PASSWORD_CHANGE
    }
    
    @Transactional
    public AuditLog log(User user, Action action, String entityType, 
                        Long entityId, String description) {
        AuditLog log = new AuditLog();
        log.setUser(user);
        log.setAction(mapAction(action));
        log.setEntityType(entityType);
        log.setEntityId(entityId);
        log.setDescription(description);
        
        return auditLogRepository.save(log);
    }
    
    @Transactional
    public AuditLog logWithDetails(User user, Action action, String entityType,
                                    Long entityId, String description, 
                                    String oldValue, String newValue) {
        AuditLog log = new AuditLog();
        log.setUser(user);
        log.setAction(mapAction(action));
        log.setEntityType(entityType);
        log.setEntityId(entityId);
        log.setDescription(description);
        log.setOldValue(oldValue);
        log.setNewValue(newValue);
        
        return auditLogRepository.save(log);
    }
    
    public List<AuditLog> getRecentLogs() {
        return auditLogRepository.findTop50ByOrderByCreatedAtDesc();
    }
    
    public Page<AuditLog> getAllLogs(Pageable pageable) {
        return auditLogRepository.findAllByOrderByCreatedAtDesc(pageable);
    }
    
    public List<AuditLog> getLogsByUser(Long userId) {
        return auditLogRepository.findByUserId(userId);
    }
    
    public List<AuditLog> getLogsByDateRange(LocalDateTime start, LocalDateTime end) {
        return auditLogRepository.findByDateRange(start, end);
    }
    
    public List<AuditLog> getLogsByEntity(String entityType, Long entityId) {
        return auditLogRepository.findByEntity(entityType, entityId);
    }
    
    private AuditLog.Action mapAction(Action action) {
        return AuditLog.Action.valueOf(action.name());
    }
}
