package com.onlyonegames.eternalfantasia.Interceptor;

import org.springframework.data.repository.CrudRepository;

import javax.transaction.Transactional;

@Transactional
public interface OnlyoneSessionRepository extends CrudRepository<OnlyoneSession, Long> {
}
