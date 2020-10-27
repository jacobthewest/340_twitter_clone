package edu.byu.cs.tweeter.server.dao;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import edu.byu.cs.tweeter.shared.domain.User;
import edu.byu.cs.tweeter.shared.service.request.UpdateFollowRequest;
import edu.byu.cs.tweeter.shared.service.response.UpdateFollowResponse;

public class UpdateFollowDAO {

    // This is the hard coded followee data returned by the 'getFollowees()' method
    private static final String MALE_IMAGE_URL = "https://faculty.cs.byu.edu/~jwilkerson/cs340/tweeter/images/donald_duck.png";
    private static final String FEMALE_IMAGE_URL = "https://faculty.cs.byu.edu/~jwilkerson/cs340/tweeter/images/daisy_duck.png";
    private static final String MIKE = "https://i.imgur.com/VZQQiQ1.jpg";

    private final User user1 = new User("Allen", "Anderson", MALE_IMAGE_URL, "password");
    private final User user2 = new User("Amy", "Ames", FEMALE_IMAGE_URL, "password");
    private final User user3 = new User("Bob", "Bobson", MALE_IMAGE_URL, "password");
    private final User user4 = new User("Bonnie", "Beatty", FEMALE_IMAGE_URL, "password");
    private final User user5 = new User("Chris", "Colston", MALE_IMAGE_URL, "password");
    private final User user6 = new User("Cindy", "Coats", FEMALE_IMAGE_URL, "password");
    private final User user7 = new User("Dan", "Donaldson", MALE_IMAGE_URL, "password");
    private final User user8 = new User("Dee", "Dempsey", FEMALE_IMAGE_URL, "password");
    private final User user9 = new User("Elliott", "Enderson", MALE_IMAGE_URL, "password");
    private final User user10 = new User("Elizabeth", "Engle", FEMALE_IMAGE_URL, "password");
    private final User user11 = new User("Frank", "Frandson", MALE_IMAGE_URL, "password");
    private final User user12 = new User("Fran", "Franklin", FEMALE_IMAGE_URL, "password");
    private final User user13 = new User("Gary", "Gilbert", MALE_IMAGE_URL, "password");
    private final User user14 = new User("Giovanna", "Giles", FEMALE_IMAGE_URL, "password");
    private final User user15 = new User("Henry", "Henderson", MALE_IMAGE_URL, "password");
    private final User user16 = new User("Helen", "Hopwell", FEMALE_IMAGE_URL, "password");
    private final User user17 = new User("Igor", "Isaacson", MALE_IMAGE_URL, "password");
    private final User user18 = new User("Isabel", "Isaacson", FEMALE_IMAGE_URL, "password");
    private final User user19 = new User("Justin", "Jones", MALE_IMAGE_URL, "password");
    private final User user20 = new User("Jill", "Johnson", FEMALE_IMAGE_URL, "password");
    private final User JacobWest = new User("Jacob", "West", "@JacobWest", MIKE, "password");
    private final User RickyMartin = new User("Ricky", "Martin", "@RickyMartin", MIKE, "password");
    private final User RobertGardner = new User("Robert", "Gardner", "@RobertGardner", MIKE, "password");
    private final User Snowden = new User("The", "Snowden", "@Snowden", MIKE, "password");
    private final User TristanThompson = new User("Tristan", "Thompson", "@TristanThompson", MIKE, "password");
    private final User KCP = new User("Kontavius", "Caldwell Pope", "@KCP", MIKE, "password");
    private final User theMedia = new User("the", "Media", "@theMedia", MIKE, "password");
    private final User Rudy = new User("Rudy", "Gobert", "@Rudy", MIKE, "password");
    private final User BillBelichick = new User("Bill", "Belichick", "@BillBelichick", MIKE, "password");
    private final User TestUser = new User("Test", "User", "@TestUser", MALE_IMAGE_URL, "password");
    private final User userBarney = new User("Barney", "Rubble", "", "password");
    private final User DaffyDuck = new User("Daffy", "Duck", "", "password");
    private final User Zoe = new User("Zoe", "Zabriski", "", "password");

    public UpdateFollowResponse updateFollow(UpdateFollowRequest request) {
        assert request.getUser() != null;
        assert request.getFollowUser() != null;

        List<User> dummyFollowees = new ArrayList<>(getDummyFollowees());

        if(request.followTheFollowUser()) { // Then follow the followUser
            dummyFollowees.add(request.getFollowUser());
        } else { // Unfollow the followUser
            dummyFollowees.remove(request.getFollowUser());
        }
        return new UpdateFollowResponse(request.getUser(), request.getFollowUser(), dummyFollowees);
    }

    public List<User> getDummyFollowees() {
        return Arrays.asList(user1, user2, theMedia, user4, user5, user6, user7, RickyMartin, RobertGardner, TristanThompson,
                user8, user9, user10, user11, Snowden, user12, user13, user14, user15, user16, user17, user18, TestUser,
                user19, user20, BillBelichick, KCP, Rudy, TestUser);
    }
}