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

import io.onedev.server.entitymanager.IssueCommentManager;
import io.onedev.server.model.IssueComment;
import io.onedev.server.security.SecurityUtils;

@Path("/issue-comments")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@Singleton
public class IssueCommentResource {

	private final IssueCommentManager commentManager;

	@Inject
	public IssueCommentResource(IssueCommentManager commentManager) {
		this.commentManager = commentManager;
	}

	@Path("/{commentId}")
	@GET
	public IssueComment get(@PathParam("commentId") Long commentId) {
		IssueComment comment = commentManager.load(commentId);
    	if (!SecurityUtils.canAccess(comment.getIssue().getProject()))  
			throw new UnauthorizedException();
    	return comment;
	}
	
	@POST
	public Long save(@NotNull IssueComment comment) {
    	if (!SecurityUtils.canAccess(comment.getIssue().getProject()) || 
    			!SecurityUtils.isAdministrator() && !comment.getUser().equals(SecurityUtils.getUser())) { 
			throw new UnauthorizedException();
    	}
    	
		commentManager.save(comment);
		return comment.getId();
	}
	
	@Path("/{commentId}")
	@DELETE
	public Response delete(@PathParam("commentId") Long commentId) {
		IssueComment comment = commentManager.load(commentId);
    	if (!SecurityUtils.canModifyOrDelete(comment)) 
			throw new UnauthorizedException();
		commentManager.delete(comment);
		return Response.ok().build();
	}
	
}
