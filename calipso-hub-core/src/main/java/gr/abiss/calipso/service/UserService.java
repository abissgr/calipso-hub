package gr.abiss.calipso.service;

import gr.abiss.calipso.model.User;

import org.resthub.common.service.CrudService;

/**
 * This class describes a service interface that could
 * be useful for RPC clients.
 * This contract module can be distributed to RPC clients, since it's got no hard dependency.
 */
public interface UserService extends CrudService<User, String> {

	User findByCredentials(String userNameOrEmail, String password);

}