package edu.byu.cs.tweeter.server.dao.dao_helpers.query;

import com.amazonaws.services.dynamodbv2.document.Item;
import com.amazonaws.services.dynamodbv2.document.ItemCollection;
import com.amazonaws.services.dynamodbv2.document.QueryOutcome;
import com.amazonaws.services.dynamodbv2.document.Table;
import com.amazonaws.services.dynamodbv2.document.spec.QuerySpec;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import edu.byu.cs.tweeter.server.dao.FollowingDAO;
import edu.byu.cs.tweeter.server.dao.dao_helpers.aws.DB;
import edu.byu.cs.tweeter.server.dao.dao_helpers.get.GetStatus;
import edu.byu.cs.tweeter.server.dao.dao_helpers.utils.Converter;
import edu.byu.cs.tweeter.shared.domain.Status;
import edu.byu.cs.tweeter.shared.domain.User;
import edu.byu.cs.tweeter.shared.service.request.FeedRequest;
import edu.byu.cs.tweeter.shared.service.request.FollowingRequest;
import edu.byu.cs.tweeter.shared.service.request.StoryRequest;
import edu.byu.cs.tweeter.shared.service.response.FollowingResponse;

public class QueryStatus {

    public static final String PARTITION_KEY = "username"; // AKA the primary key.
    public static final String SORT_KEY = "timePosted";  // AKA the range key.
    public static final String TABLE_NAME = "status";
    public static int QUERY_LIMIT = 10;
    public static String INDEX_NAME = "follows_index";

    public static List<Status> queryStorySorted(StoryRequest request) {

        String timePosted = null;
        if(request.getLastStatus() != null) {
            String requestTimePosted = request.getLastStatus().getTimePosted();
            timePosted = Converter.convertTimePostedToSortableTime(requestTimePosted);
        }
        String storyUsername = request.getUser().getAlias();
        int limit = request.getLimit();

        HashMap<String, String> nameMap = new HashMap<String, String>();
        nameMap.put("#username", PARTITION_KEY);

        HashMap<String, Object> valueMap = new HashMap<String, Object>();
        valueMap.put(":username_value", storyUsername);

        QuerySpec querySpec;
        String keyConditionExpression = "#" + PARTITION_KEY + " = :username_value";

        if(timePosted == null) {
            // Need the first ten users.
            querySpec = new QuerySpec()
                    .withMaxResultSize(limit)
                    .withScanIndexForward(false)
                    .withKeyConditionExpression(keyConditionExpression)
                    .withNameMap(nameMap)
                    .withValueMap(valueMap);
        } else {
            // Need only the next ten users.
            querySpec = new QuerySpec()
                    .withExclusiveStartKey("username", storyUsername, "timePosted", timePosted)
                    .withMaxResultSize(limit)
                    .withScanIndexForward(false)
                    .withKeyConditionExpression(keyConditionExpression)
                    .withNameMap(nameMap)
                    .withValueMap(valueMap);
        }

        ItemCollection<QueryOutcome> items = null;
        Iterator<Item> iterator = null;
        List<Item> responseItems = new ArrayList<>();

        try {
            Table table = DB.getDatabase(TABLE_NAME);
            items = table.query(querySpec);

            iterator = items.iterator();
            while (iterator.hasNext()) {
                responseItems.add(iterator.next());
            }
        } catch (Exception e) {
            System.err.println("Unable to perform the query.");
            System.err.println(e.getMessage());
            return null;
        }

        List<Status> returnMe = new ArrayList<>();
        for(Item item: responseItems) {
            String tempUsername = (String) item.get("username");
            String tempTimePosted = (String) item.get("timePosted");
            Status s = GetStatus.getStatus(tempUsername, tempTimePosted, false);
            returnMe.add(s);
        }
        return returnMe;
    }

    public static List<Status> queryFeedSorted(FeedRequest request) {

        // Get all of the followees
        List<String> followingAliases = getFollowingAliases(request);
        if(followingAliases == null) {
            return null; // An error happened.
        }

        String timePosted = null;
        if(request.getLastStatus() != null) {
            String requestTimePosted = request.getLastStatus().getTimePosted();
            timePosted = Converter.convertTimePostedToSortableTime(requestTimePosted);
        }

        int limit = request.getLimit();

        HashMap<String, String> nameMap = new HashMap<String, String>();
        nameMap.put("#username", PARTITION_KEY);

        // Dynamically add to the value map all of the aliases we follow.
        HashMap<String, Object> valueMap = getFeedValueMap(followingAliases);

        QuerySpec querySpec;
        // Also dynamically add to keyConditionExpression like this #username = :username_value or ANOTHER_ONE
        String keyConditionExpression = getFeedConditionExpression(followingAliases);

        if(timePosted == null) {
            // Need the first ten users.
            querySpec = new QuerySpec()
                    .withMaxResultSize(limit)
                    .withScanIndexForward(false)
                    .withKeyConditionExpression(keyConditionExpression)
                    .withNameMap(nameMap)
                    .withValueMap(valueMap);
        } else {
            // Need only the next ten users.
            String lastStatusAlias = request.getLastStatus().getUser().getAlias();
            querySpec = new QuerySpec()
                    .withExclusiveStartKey("username", lastStatusAlias, "timePosted", timePosted)
                    .withMaxResultSize(limit)
                    .withScanIndexForward(false)
                    .withKeyConditionExpression(keyConditionExpression)
                    .withNameMap(nameMap)
                    .withValueMap(valueMap);
        }

        ItemCollection<QueryOutcome> items = null;
        Iterator<Item> iterator = null;
        List<Item> responseItems = new ArrayList<>();

        try {
            Table table = DB.getDatabase(TABLE_NAME);
            items = table.query(querySpec);

            iterator = items.iterator();
            while (iterator.hasNext()) {
                responseItems.add(iterator.next());
            }
        } catch (Exception e) {
            System.err.println("Unable to perform the query.");
            System.err.println(e.getMessage());
            return null;
        }

        List<Status> returnMe = new ArrayList<>();
        for(Item item: responseItems) {
            String tempUsername = (String) item.get("username");
            String tempTimePosted = (String) item.get("timePosted");
            Status s = GetStatus.getStatus(tempUsername, tempTimePosted, false);
            returnMe.add(s);
        }
        return returnMe;
    }

    private static List<String> getFollowingAliases(FeedRequest request) {
        List<String> followingAliases = new ArrayList<>();

        User user = request.getUser();
        User lastFollowee = null;
        int limit = 10;
        boolean hasMorePages = true;

        while(hasMorePages) {
            FollowingRequest followingRequest = new FollowingRequest(user, limit, lastFollowee);
            FollowingResponse followingResponse = new FollowingDAO().getFollowees(followingRequest);
            if(followingResponse == null) {
                return null; // An error happened.
            }
            List<User> followees = followingResponse.getFollowees();
            for(User u: followees) {
                followingAliases.add(u.getAlias());
            }

            lastFollowee = followees.get(followees.size() - 1);

            if(followees.size() != limit) {
                hasMorePages = false;
            }
        }

        return followingAliases;
    }

    private static HashMap<String, Object> getFeedValueMap(List<String> followingAliases) {
        HashMap<String, Object> valueMap = new HashMap<String, Object>();

        for(int i = 0; i < followingAliases.size(); i++) {
            valueMap.put(":username_value" + Integer.toString(i), followingAliases.get(i));
        }

        return valueMap;
    }

    private static String getFeedConditionExpression(List<String> followingAliases) {
        String keyConditionExpression = "";

        for(int i = 0; i < followingAliases.size(); i++) {
            if(i == followingAliases.size() - 1) {
                keyConditionExpression += "#" + PARTITION_KEY + " = :username_value" + Integer.toString(i);
            } else {
                keyConditionExpression += "#" + PARTITION_KEY + " = :username_value" + Integer.toString(i) + " or ";
            }
        }
        return keyConditionExpression;
    }
}