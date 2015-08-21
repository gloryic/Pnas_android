package com.ssm.pnas.network.protocol;

public abstract class Protocol {
	protected static final String GROUP_CODE_KEY = "groupCode";
	protected static final String GROUP_ID_KEY = "groupId";
	protected static final String GROUP_NAME_KEY = "groupName";
	protected static final String GROUP_MEMBER_ID_KEY = "groupMemberId";
	protected static final String GROUP_MEMBERS_NUMBER_KEY = "groupMembersNumber";
	protected static final String GROUP_MEMBERS_INFO_KEY = "groupMembersInfo";
	
	
	protected static final String MEMBER_ID_KEY = "memberId";
	protected static final String PARENT_GROUP_MEMBER_ID_KEY = "parentGroupMemberId";
	protected static final String SUB_ROOT_ID_KEY = "subRootId";
	
	protected static final String USER_ID_KEY = "userId";
	protected static final String USER_NUMBER_KEY = "userNumber";
	protected static final String USER_TYPE_KEY = "userType";
	protected static final String USER_NAME_KEY = "userName";
	protected static final String USER_PHONE_KEY = "userPhoneNumber";
	protected static final String USER_ID_OR_PHONE_KEY = "userIdOrPhone";
	protected static final String USER_PASSWORD_KEY = "password";
	
	protected static final String CHILDREN_KEY = "children";
	protected static final String NUMBER_OF_CHILDREN_KEY = "numberOfChildren";
	
	protected static final String MESSAGE_KEY = "message";
	
	protected static final String SEND_COUNT_KEY = "sendCount";
	
	protected static final String LATITUDE_KEY = "latitude";
	protected static final String LONGITUDE_KEY = "longitude";
	protected static final String LAT_KEY = "lat";
	protected static final String LNG_KEY = "lng";
	
	protected static final String OWN_PHONE_KEY = "ownPhoneNumber";
	protected static final String RADIUS_KEY = "radius";
	
	protected static final String STATUS_KEY = "status";
	protected static final String STATUS_CODE_KEY = "statusCode";
	
	protected static final String EDGE_TYPE_KEY = "edgeType";
	protected static final String EDGE_STATUS_KEY = "edgeStatus";
	protected static final String DEVICE_STATUS_KEY = "deviceStatus";
	
	protected static final String MANAGE_MODE_KEY = "manageMode";
	protected static final String MANAGE_LOCATION_RADIUS_KEY = "managedLocationRadius";
	protected static final String MANAGING_TOTAL_NUMBER_KEY = "managingTotalNumber";
	protected static final String MANAGING_NUMBER_KEY = "managingNumber";
	
	protected static final String PARENT_MANAGE_MODE_KEY = "parentManageMode";
	
	protected static final String VALID_USER_NUM_KEY = "validUserNumber";
	
	protected static final String POINTS_KEY = "points";
	
	protected static final String PHONE_PATTERN = "^[0-9]{11}$";
	
	protected static final String PARENT_INFO_KEY = "parentInfo";
	protected static final String CUR_INFO_KEY = "curInfo";
	protected static final String CHILDREN_INFO_KEY = "childrenInfo";
	
	protected static final String ACCUMULATE_WARNING_KEY = "accumulateWarning";
	
	protected static final String FP_ID_KEY = "fpId";
	protected static final String FROM_KEY = "from";
	protected static final String TO_KEY = "to";
	
	protected static final String KEYWORD_KEY = "keyword";
	
	protected static final String DESC_KEY = "desc";
	
	protected static final String PARENTS_KEY = "parents";
	
	protected static final String DEPTH_KEY = "depth";
	
	protected static final String VALIDATION_KEY = "validation";
	protected static final String MY_GROUP_MEMBER_ID_KEY = "myGroupMemberId";
	
	
	/*News RSS URLS*/
	protected static final String mbc_total = "http://imnews.imbc.com/rss/news/news_00.xml";
	protected static final String mbc_total2 = "http://rss.joins.com/joins_news_list.xml";
	
	

	protected static final String ad_login_nickname = "nickname";
	protected static final String ad_countup_id = "id";
	protected static final String ad_countup_type = "type";
	protected static final String ad_countup_count = "count";
	
	
}
