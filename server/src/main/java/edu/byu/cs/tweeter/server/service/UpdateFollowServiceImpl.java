package edu.byu.cs.tweeter.server.service;

import edu.byu.cs.tweeter.server.dao.UpdateFollowDAO;
import edu.byu.cs.tweeter.server.util.RequestAndResponseChecker;
import edu.byu.cs.tweeter.shared.service.UpdateFollowService;
import edu.byu.cs.tweeter.shared.service.request.UpdateFollowRequest;
import edu.byu.cs.tweeter.shared.service.response.UpdateFollowResponse;

public class UpdateFollowServiceImpl implements UpdateFollowService {

    @Override
    public UpdateFollowResponse updateFollow(UpdateFollowRequest request) {
        RequestAndResponseChecker checker = new RequestAndResponseChecker();

        // Request:: User, followUser, boolean followTheFollowUser
        checker.checkUserRequest(request.getUser());
        checker.checkUserRequest(request.getFollowUser());
        checker.checkSameUser(request.getUser(), request.getFollowUser());

        UpdateFollowResponse updateFollowResponse = getUpdateFollowDAO().updateFollow(request);

        if(!updateFollowResponse.getSuccess()) {
            throw new RuntimeException("[InternalServerError] " + updateFollowResponse.getMessage());
        }

        // Response:: User, followUser, List<User> following
        checker.checkUserResponse(updateFollowResponse.getUser());
        checker.checkUserResponse(updateFollowResponse.getFollowUser());

        return updateFollowResponse;
    }

    UpdateFollowDAO getUpdateFollowDAO() {return new UpdateFollowDAO();}
}
