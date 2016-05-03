package gr.abiss.calipso.tiers.controller;

import gr.abiss.calipso.tiers.service.ModelService;

import java.io.Serializable;

import org.springframework.data.domain.Persistable;

public interface ModelController<T extends Persistable<ID>, ID extends Serializable, S extends ModelService<T, ID>> {

}