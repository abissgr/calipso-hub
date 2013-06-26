package gr.abiss.calipso.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import gr.abiss.calipso.model.Sample;

public interface SampleRepository extends JpaRepository<Sample, Long> {

}
