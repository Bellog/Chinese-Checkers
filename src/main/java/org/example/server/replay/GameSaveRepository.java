package org.example.server.replay;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GameSaveRepository extends JpaRepository<GameSave, Integer> {

    @Query(value = "SELECT id FROM game_save", nativeQuery = true)
    List<Integer> getAllIds();
}
