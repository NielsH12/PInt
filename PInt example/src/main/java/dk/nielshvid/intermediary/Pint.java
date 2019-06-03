package dk.nielshvid.intermediary;

import javax.ws.rs.*;
import javax.ws.rs.core.*;
import java.util.UUID;


@Path("/")
public class Pint{
	private Shield shield = new Shield();

	@Path("authorize")
	@POST
	@Produces(MediaType.APPLICATION_JSON)
	public Response authorizeFreezer (@Context UriInfo info, String body, @Context HttpHeaders headers){
		
		
		// Check policies
		if (shield.postAuthorize("Freezer/authorize", info.getQueryParameters(), body)){
			// Forward request
			return IntermediaryClient.FreezerPOSTauthorize(info, body, headers);
		};
		
		throw new WebApplicationException(Response.Status.INTERNAL_SERVER_ERROR);
		
	}
	
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
		UUID CapabilityID = shield.generateCapability("Freezer/querySample", info.getQueryParameters(), body, false);
		
		// Check policies
		if (shield.authorize("Freezer/querySample", info.getQueryParameters(), body)){
			// Forward request
			return Response.fromResponse(IntermediaryClient.FreezerGETquerySample(info, body, headers)).header("Capability", CapabilityID).build();
		};
		
		throw new WebApplicationException(Response.Status.INTERNAL_SERVER_ERROR);
		
	}
	
	@Path("biostore/authenticate/login")
	@POST
	@Produces(MediaType.APPLICATION_JSON)
	public Response authorizeFFU (@Context UriInfo info, String body, @Context HttpHeaders headers){
		
		
		// Check policies
		if (shield.postAuthorize("FFU/authorize", info.getQueryParameters(), body)){
			// Forward request
			return IntermediaryClient.FFUPOSTauthorize(info, body, headers);
		};
		
		throw new WebApplicationException(Response.Status.INTERNAL_SERVER_ERROR);
		
	}
	
	@Path("biostore/physicalsets/{EntityID}")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Response logicalsetsGETFFU (@Context UriInfo info, String body, @Context HttpHeaders headers, @PathParam("EntityID") String entityID){
		
		if(!entityID.equals(String.valueOf(info.getQueryParameters().getFirst("EntityID")))){
			throw new WebApplicationException(Response.Status.BAD_REQUEST);
		}
		
		// Check policies
		if (shield.authorize("FFU/logicalsetsGET", info.getQueryParameters(), body)){
			// Forward request
			return IntermediaryClient.FFUGETlogicalsetsGET(info, body, headers);
		};
		
		throw new WebApplicationException(Response.Status.INTERNAL_SERVER_ERROR);
		
	}
	
	@Path("biostore/physicalsets/{EntityID}")
	@PUT
	@Produces(MediaType.APPLICATION_JSON)
	public Response logicalsetsPUTFFU (@Context UriInfo info, String body, @Context HttpHeaders headers, @PathParam("EntityID") String entityID){
		
		if(!entityID.equals(String.valueOf(info.getQueryParameters().getFirst("EntityID")))){
			throw new WebApplicationException(Response.Status.BAD_REQUEST);
		}
		
		// Check policies
		if (shield.authorize("FFU/logicalsetsPUT", info.getQueryParameters(), body)){
			// Forward request
			return IntermediaryClient.FFUPUTlogicalsetsPUT(info, body, headers);
		};
		
		throw new WebApplicationException(Response.Status.INTERNAL_SERVER_ERROR);
		
	}
	
}
