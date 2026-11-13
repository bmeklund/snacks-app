package com.demo;

import java.util.List;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.Response;
import org.jboss.logging.Logger;

@Path("entity/snacks")
@ApplicationScoped
@Produces("application/json")
@Consumes("application/json")
public class SnackResource {

    private static final Logger LOG = Logger.getLogger(SnackResource.class);
    private final SnackService snackService;

    public SnackResource(SnackService snackService) {
        this.snackService = snackService;
    }

    @GET
    public List<Snack> get() {
        LOG.info("Listing existing snacks");
        return snackService.listAll();
    }

    @GET
    @Path("{id}")
    public Snack getSingle(Long id) {
        return snackService.findById(id)
                .orElseThrow(() -> new WebApplicationException("Snack with id of " + id + " does not exist.", 404));
    }

    @POST
    public Response create(Snack snack) {
        if (snack.id != null) {
            throw new WebApplicationException("Id was invalidly set on request.", 422);
        }
        LOG.info("Storing a new snack: " + snack.name);
        Snack createdSnack = snackService.create(snack);
        return Response.status(Response.Status.CREATED).entity(createdSnack).build();
    }

    @PUT
    @Path("{id}")
    public Snack update(Long id, Snack snack) {
        if (snack.name == null) {
            throw new WebApplicationException("Snack Name was not set on request.", 422);
        }

        return snackService.update(id, snack.name)
                .orElseThrow(() -> new WebApplicationException("Snack with id of " + id + " does not exist.", 404));
    }

    @DELETE
    @Path("{id}")
    public Response delete(Long id) {
        if (!snackService.delete(id)) {
            throw new WebApplicationException("Snaack with id of " + id + " does not exist.", 404);
        }
        LOG.info("DELETING A snack with id:" + id);
        return Response.status(204).build();
    }

}
