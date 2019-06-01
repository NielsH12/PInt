package dk.nielshvid.intermediary;

import javax.ws.rs.*;
import javax.ws.rs.core.*;


@Path("/")
public class Pint{
	private Shield shield = new Shield();

	@Path("insert/{EntityID}")
	@PUT
	@Produces(MediaType.TEXT_PLAIN)
	public Response insertFreezer (@Context UriInfo info, String body, @Context HttpHeaders headers, @PathParam("EntityID") String entityID){

		if(!entityID.equals(String.valueOf(info.getQueryParameters().getFirst("EntityID")))){
			throw new WebApplicationException(Response.Status.BAD_REQUEST);
		}


		// Check policies
		if (shield.authorize("Freezer/insert", info.getQueryParameters(), body)){
			// Forward request
			return IntermediaryClient.FreezerPUTinsert(info, body, headers);
		};

		throw new WebApplicationException(Response.Status.INTERNAL_SERVER_ERROR);

	}

	@Path("retrieve/{EntityID}")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Response retrieveFreezer (@Context UriInfo info, String body, @Context HttpHeaders headers, @PathParam("EntityID") String entityID){

		if(!entityID.equals(String.valueOf(info.getQueryParameters().getFirst("EntityID")))){
			throw new WebApplicationException(Response.Status.BAD_REQUEST);
		}


		// Check policies
		if (shield.authorize("Freezer/retrieve", info.getQueryParameters(), body)){
			// Forward request
			return IntermediaryClient.FreezerGETretrieve(info, body, headers);
		};

		throw new WebApplicationException(Response.Status.INTERNAL_SERVER_ERROR);

	}

	@Path("querysample/{EntityID}")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Response querySampleFreezer (@Context UriInfo info, String body, @Context HttpHeaders headers, @PathParam("EntityID") String entityID){

		if(!entityID.equals(String.valueOf(info.getQueryParameters().getFirst("EntityID")))){
			throw new WebApplicationException(Response.Status.BAD_REQUEST);
		}


		// Check policies
		if (shield.authorize("Freezer/querySample", info.getQueryParameters(), body)){
			// Forward request
			return IntermediaryClient.FreezerGETquerySample(info, body, headers);
		};

		throw new WebApplicationException(Response.Status.INTERNAL_SERVER_ERROR);

	}

}
