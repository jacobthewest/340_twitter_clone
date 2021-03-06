package edu.byu.cs.tweeter.server.service;

import edu.byu.cs.tweeter.server.util.RequestAndResponseChecker;
import edu.byu.cs.tweeter.shared.service.FollowingService;
import edu.byu.cs.tweeter.shared.service.request.FollowingRequest;
import edu.byu.cs.tweeter.shared.service.response.FollowingResponse;
import edu.byu.cs.tweeter.server.dao.FollowingDAO;

/**
 * Contains the business logic for getting the users a user is following.
 */
public class FollowingServiceImpl implements FollowingService {

    /**
     * Returns the users that the user specified in the request is following. Uses information in
     * the request object to limit the number of followees returned and to return the next set of
     * followees after any that were returned in a previous request. Uses the {@link FollowingDAO} to
     * get the followees.
     *
     * @param request contains the data required to fulfill the request.
     * @return the followees.
     */
    @Override
    public FollowingResponse getFollowees(FollowingRequest request) {
        RequestAndResponseChecker checker = new RequestAndResponseChecker();

        // Request:: User, limit, lastFollower(type User)
        checker.checkUserRequest(request.getUser());
        checker.checkLimitRequest(request.getLimit());
        checker.checkUserLastFollowRequest(request.getUser(), request.getLastFollowee());

        FollowingResponse followingResponse = getFollowingDAO().getFollowees(request);

        if(!followingResponse.getSuccess()) {
            throw new RuntimeException("[InternalServerError] " + followingResponse.getMessage());
        }

        // Response:: List<User> followees
        checker.checkUserListResponse(followingResponse.getFollowees(), request.getLimit());

        return followingResponse;
    }

    /**
     * Returns an instance of {@link FollowingDAO}. Allows mocking of the FollowingDAO class
     * for testing purposes. All usages of FollowingDAO should get their FollowingDAO
     * instance from this method to allow for mocking of the instance.
     *
     * @return the instance.
     */
    FollowingDAO getFollowingDAO() {
        return new FollowingDAO();
    }
}
