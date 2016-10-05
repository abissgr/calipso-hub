package gr.abiss.calipso.friends.model;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.annotation.Nullable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public enum FriendshipStatus {
	NEW, DELETE, PENDING, CONFIRMED, BLOCK, BLOCK_INVERSE;
	

	private static final Logger LOGGER = LoggerFactory.getLogger(FriendshipStatus.class);

	private static Map<FriendshipStatus, Set<FriendshipStatus>> allowedNext = new HashMap<FriendshipStatus, Set<FriendshipStatus>>();
	private static Map<FriendshipStatus, FriendshipStatus> applicableInverse = new HashMap<FriendshipStatus, FriendshipStatus>();
	
		
	static {
		
		allowedNext.put(null, new HashSet<FriendshipStatus>(Arrays.asList(NEW, BLOCK)));
		allowedNext.put(NEW, new HashSet<FriendshipStatus>(Arrays.asList(DELETE)));
		allowedNext.put(PENDING, new HashSet<FriendshipStatus>(Arrays.asList(CONFIRMED, DELETE, BLOCK)));
		allowedNext.put(CONFIRMED, new HashSet<FriendshipStatus>(Arrays.asList(DELETE, BLOCK)));
		allowedNext.put(BLOCK, new HashSet<FriendshipStatus>(Arrays.asList(DELETE)));

		applicableInverse.put(NEW, PENDING);
		applicableInverse.put(CONFIRMED, CONFIRMED);
		applicableInverse.put(DELETE, DELETE);
		applicableInverse.put(BLOCK, BLOCK_INVERSE);

	}

	public static Set<FriendshipStatus> getAllowedNext(@Nullable FriendshipStatus current){
		return allowedNext.get(current);
	}
	
	
	public static boolean isAllowedNext(@Nullable FriendshipStatus current, @Nullable FriendshipStatus next){
		boolean allowed =  allowedNext.get(current).contains(next);
		LOGGER.debug("isAllowedNext, current: {} ,next: {}, allowed: {}" + allowed, current, next);
		return allowed;
	}
	

	public static FriendshipStatus getApplicableInverse(@Nullable FriendshipStatus current){
		return applicableInverse.get(current);
	}
	
}