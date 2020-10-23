package com.cepheid.cloud.skel.repository;

import com.cepheid.cloud.skel.model.Reader;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface ReaderRepository extends JpaRepository<Reader,Long> {

    @Query("SELECT r FROM Reader r WHERE r.mUsername=(?1)")
    Optional<Reader> findByUsername(String username);
}
