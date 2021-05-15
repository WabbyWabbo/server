package io.onedev.server.rest;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.validation.constraints.NotNull;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.shiro.authz.UnauthorizedException;

import io.onedev.server.entitymanager.IssueWatchManager;
import io.onedev.server.model.IssueWatch;
import io.onedev.server.security.SecurityUtils;

@Path("/issue-watches")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@Singleton
public class IssueWatchResource {

	private final IssueWatchManager watchManager;

	@Inject
	public IssueWatchResource(IssueWatchManager watchManager) {
		this.watchManager = watchManager;
	}

	@Path("/{watchId}")
	@GET
	public IssueWatch get(@PathParam("watchId") Long watchId) {
		IssueWatch watch = watchManager.load(watchId);
		if (!SecurityUtils.canAccess(watch.getIssue().getProject()))
			throw new UnauthorizedException();
		return watch;
	}
	
	@POST
	public Long save(@NotNull IssueWatch watch) {
		if (!SecurityUtils.canAccess(watch.getIssue().getProject()) 
				|| !SecurityUtils.isAdministrator() && !watch.getUser().equals(SecurityUtils.getUser())) {
			throw new UnauthorizedException();
		}
		watchManager.save(watch);
		return watch.getId();
	}
	
	@Path("/{watchId}")
	@DELETE
	public Response delete(@PathParam("watchId") Long watchId) {
		IssueWatch watch = watchManager.load(watchId);
		if (!SecurityUtils.isAdministrator() && !watch.getUser().equals(SecurityUtils.getUser())) 
			throw new UnauthorizedException();
		
		watchManager.delete(watch);
		return Response.ok().build();
	}
	
}
