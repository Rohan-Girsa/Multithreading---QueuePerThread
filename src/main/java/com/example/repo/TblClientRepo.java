package com.example.repo;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.entities.TblClient;

@Repository
@Transactional
public interface TblClientRepo extends JpaRepository<TblClient, Long>{

}
