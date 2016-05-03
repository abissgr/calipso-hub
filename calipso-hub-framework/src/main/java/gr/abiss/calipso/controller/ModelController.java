package gr.abiss.calipso.controller;

import gr.abiss.calipso.service.GenericEntityService;

import java.io.Serializable;

import org.springframework.data.domain.Persistable;

public interface ModelController<T extends Persistable<ID>, ID extends Serializable, S extends GenericEntityService<T, ID>> {

}