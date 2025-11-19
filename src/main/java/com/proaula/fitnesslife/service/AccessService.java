package com.proaula.fitnesslife.service;

import com.proaula.fitnesslife.model.Access;
import com.proaula.fitnesslife.model.User;
import com.proaula.fitnesslife.repository.AccessRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class AccessService {

    private final AccessRepository accessRepository;
    private final UserService userService;
    private static final Logger logger = LoggerFactory.getLogger(AccessService.class);

    @Transactional
    public Access createAccess(String userId, String qrCode, String result) {
        logger.info("Creando acceso para usuario: {} con resultado: {}", userId, result);

        User user = userService.getUserByIdOrThrow(userId);

        Access access = Access.builder()
                .user(user)
                .qrCode(qrCode)
                .result(result)
                .accessedAt(LocalDateTime.now())
                .build();

        Access saved = accessRepository.save(access);
        logger.info("Acceso creado con ID: {}", saved.getId());
        return saved;
    }

    public List<Access> getUserAccesses(String userId) {
        User user = userService.getUserByIdOrThrow(userId);
        return accessRepository.findByUserOrderByAccessedAtDesc(user);
    }

    public Access getAccessById(String id) {
        logger.info("Buscando acceso con ID: {}", id);
        return accessRepository.findById(id)
                .orElseThrow(() -> {
                    logger.error("Acceso no encontrado con ID: {}", id);
                    return new RuntimeException("Acceso no encontrado con ID: " + id);
                });
    }

    @Transactional
    public Access updateAccessResult(String id, String newResult) {
        logger.info("Actualizando resultado del acceso {} a {}", id, newResult);

        Access access = getAccessById(id);
        String oldResult = access.getResult();

        access.setResult(newResult);

        Access updated = accessRepository.save(access);
        logger.info("Resultado del acceso actualizado de {} a {}", oldResult, newResult);
        return updated;
    }

    @Transactional
    public void deleteAccess(String id) {
        logger.info("Eliminando acceso con ID: {}", id);
        accessRepository.deleteById(id);
        logger.info("Acceso eliminado exitosamente");
    }

    public Map<String, Object> getAccessStatistics() {
        logger.info("Calculando estadísticas de accesos");

        List<Access> allAccesses = accessRepository.findAll();

        long totalAccesses = allAccesses.size();
        long allowedAccesses = allAccesses.stream()
                .filter(a -> "ALLOWED".equals(a.getResult()))
                .count();
        long deniedAccesses = allAccesses.stream()
                .filter(a -> "DENIED".equals(a.getResult()))
                .count();
        long uniqueUsers = allAccesses.stream()
                .map(access -> access.getUser().getId())
                .distinct()
                .count();

        LocalDateTime todayStart = LocalDateTime.now().withHour(0).withMinute(0).withSecond(0);
        LocalDateTime todayEnd = LocalDateTime.now().withHour(23).withMinute(59).withSecond(59);
        long todayAccesses = accessRepository.countByAccessedAtBetween(todayStart, todayEnd);

        Map<String, Object> stats = new java.util.HashMap<>();
        stats.put("totalAccesses", totalAccesses);
        stats.put("allowedAccesses", allowedAccesses);
        stats.put("deniedAccesses", deniedAccesses);
        stats.put("uniqueUsers", uniqueUsers);
        stats.put("todayAccesses", todayAccesses);

        logger.info("Estadísticas calculadas: {} accesos totales, {} únicos", totalAccesses, uniqueUsers);
        return stats;
    }

    public Page<Access> getAccessesPaginated(int page, int size, String result, String search) {
        logger.info("Obteniendo accesos paginados - Página: {}, Tamaño: {}, Resultado: {}, Búsqueda: {}", 
                   page, size, result, search);
        
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "accessedAt"));
        
        Page<Access> accessesPage;
        
        if (search != null && !search.trim().isEmpty() && result != null && !result.trim().isEmpty()) {
            logger.info("Buscando accesos con resultado '{}' y término '{}'", result, search);
            accessesPage = accessRepository.searchAccessesByResult(result, search, pageable);
        } else if (search != null && !search.trim().isEmpty()) {
            logger.info("Buscando accesos con término '{}'", search);
            accessesPage = accessRepository.searchAccesses(search, pageable);
        } else if (result != null && !result.trim().isEmpty()) {
            logger.info("Filtrando accesos por resultado '{}'", result);
            accessesPage = accessRepository.findByResult(result, pageable);
        } else {
            logger.info("Obteniendo todos los accesos");
            accessesPage = accessRepository.findAll(pageable);
        }
        
        logger.info("Se encontraron {} accesos de {} totales", 
                   accessesPage.getNumberOfElements(), 
                   accessesPage.getTotalElements());
        
        return accessesPage;
    }

    public List<Access> getRecentAccesses(int limit) {
        logger.info("Obteniendo {} accesos recientes", limit);
        return accessRepository.findTopNByOrderByAccessedAtDesc(limit);
    }
}