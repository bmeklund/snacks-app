package com.demo;

import java.util.List;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.Response;

import io.quarkus.panache.common.Sort;

@Path("entity/snacks")
@ApplicationScoped
@Produces("application/json")
@Consumes("application/json")
public class SnackResource {

    @GET
    public List<Snack> get() {
        return Snack.listAll(Sort.by("name"));
    }

    @GET
    @Path("{id}")
    public Snack getSingle(Long id) {
        Snack entity = Snack.findById(id);
        if (entity == null) {
            throw new WebApplicationException("Snack with id of " + id + " does not exist.", 404);
        }
        return entity;
    }

    @POST
    @Transactional
    public Response create(Snack snack) {
        if (snack.id != null) {
            throw new WebApplicationException("Id was invalidly set on request.", 422);
        }

        snack.persist();
        return Response.ok(snack).status(201).build();
    }

    @PUT
    @Path("{id}")
    @Transactional
    public Snack update(Long id, Snack snack) {
        if (snack.name == null) {
            throw new WebApplicationException("Snack Name was not set on request.", 422);
        }

        Snack entity = Snack.findById(id);

        if (entity == null) {
            throw new WebApplicationException("Snack with id of " + id + " does not exist.", 404);
        }

        entity.name = snack.name;

        return entity;
    }

    @DELETE
    @Path("{id}")
    @Transactional
    public Response delete(Long id) {
        Snack entity = Snack.findById(id);
        if (entity == null) {
            throw new WebApplicationException("Snaack with id of " + id + " does not exist.", 404);
        }
        entity.delete();
        return Response.status(204).build();
    }

}
