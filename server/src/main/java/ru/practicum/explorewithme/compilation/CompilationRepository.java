package ru.practicum.explorewithme.compilation;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.practicum.explorewithme.compilation.model.Compilation;

import java.util.List;

@Repository
public interface CompilationRepository extends JpaRepository<Compilation, Long> {

    @Query("SELECT c FROM Compilation AS c " +
            "WHERE :pinned IS NULL OR c.pinned = :pinned")
    List<Compilation> findAllByPinned(@Param("pinned") boolean pinned, Pageable pageable);

    @Modifying
    @Query("update Compilation as c " +
            "set c.pinned = ?1 " +
            "where c.id = ?2")
    void setCompilationPinned(boolean pinned, Long compilationId);
}
