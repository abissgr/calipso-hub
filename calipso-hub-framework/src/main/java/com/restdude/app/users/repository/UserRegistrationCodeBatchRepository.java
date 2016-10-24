/**
 * calipso-hub-framework - A full stack, high level framework for lazy application hackers.
 * Copyright © 2005 Manos Batsis (manosbatsis gmail)
 * <p>
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * <p>
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 * <p>
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.restdude.app.users.repository;

import com.restdude.app.users.model.UserRegistrationCodeBatch;
import com.restdude.app.users.model.UserRegistrationCodeInfo;
import gr.abiss.calipso.tiers.repository.ModelRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserRegistrationCodeBatchRepository extends ModelRepository<UserRegistrationCodeBatch, String> {

    @Query("select new com.restdude.app.users.model.UserRegistrationCodeInfo(code.id, code.available, code.credentials, code.batch) from UserRegistrationCode code where code.batch.id = ?1")
    List<UserRegistrationCodeInfo> findBatchCodes(String batchId);

    @Query("select batch.name from UserRegistrationCodeBatch batch where batch.id = ?1")
    String findBatchName(String batchId);

}
