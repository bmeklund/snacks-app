package com.demo;

import java.sql.SQLIntegrityConstraintViolationException;
import java.util.List;
import java.util.Optional;

import org.hibernate.exception.ConstraintViolationException;

import io.quarkus.panache.common.Sort;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.PersistenceException;
import jakarta.transaction.Transactional;

@ApplicationScoped
public class SnackService {

    public List<Snack> listAll() {
        return Snack.listAll(Sort.by("name"));
    }

    public Optional<Snack> findById(Long id) {
        return Snack.findByIdOptional(id);
    }

    @Transactional
    public Snack create(Snack snack) {
        ensureNameIsAvailable(snack.name, null);

        try {
            snack.persistAndFlush();
            return snack;
        } catch (PersistenceException exception) {
            throwDuplicateConflictIfApplicable(snack.name, exception);
            throw exception;
        }
    }

    @Transactional
    public Optional<Snack> update(Long id, String name) {
        Optional<Snack> snack = Snack.findByIdOptional(id);

        snack.ifPresent(entity -> {
            ensureNameIsAvailable(name, id);
            entity.name = name;

            try {
                entity.persistAndFlush();
            } catch (PersistenceException exception) {
                throwDuplicateConflictIfApplicable(name, exception);
                throw exception;
            }
        });

        return snack;
    }

    @Transactional
    public boolean delete(Long id) {
        return Snack.deleteById(id);
    }

    private void ensureNameIsAvailable(String name, Long excludedId) {
        long matches = excludedId == null
                ? Snack.count("name", name)
                : Snack.count("name = ?1 and id <> ?2", name, excludedId);

        if (matches > 0) {
            throw new DuplicateSnackException(name);
        }
    }

    private void throwDuplicateConflictIfApplicable(String name, PersistenceException exception) {
        Throwable cause = exception;

        while (cause != null) {
            if (cause instanceof ConstraintViolationException
                    || cause instanceof SQLIntegrityConstraintViolationException) {
                throw new DuplicateSnackException(name, exception);
            }
            cause = cause.getCause();
        }
    }
}
