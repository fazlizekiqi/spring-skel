package com.cepheid.cloud.skel.repository;

import com.cepheid.cloud.skel.model.Review;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReviewRepository extends JpaRepository<Review,Long> {

}
