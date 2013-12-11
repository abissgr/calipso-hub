package org.springframework.security.acls.domain.jpa;

import org.springframework.security.acls.domain.BasePermission;

public class JpaPermission extends BasePermission {

  public JpaPermission(int mask) {
    super(mask);

  }

}
