package ru.practicum.explorewithme.compilation;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.explorewithme.compilation.model.Compilation;

import java.util.List;

public interface CompilationRepository extends JpaRepository<Compilation, Long> {

    @Query("select c from Compilation as c " +
            "where :pinned is null or c.pinned = :pinned")
    List<Compilation> findAllByPinned(@Param("pinned") boolean pinned, Pageable pageable);

    @Modifying
    @Query("update Compilation as c " +
            "set c.pinned = ?1 " +
            "where c.id = ?2")
    void setCompilationPinned(boolean pinned, Long compilationId);
}
