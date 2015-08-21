package com.ssm.pnas.tools.time;


public class TimeProvider {
	
	
	/**
	 * 알고싶은 시간(00~24)을 파라메터로 넘겨주면 이를 밀리세컨트로 반환해준다.
	 */
	
	static public long getTime(int hour)
	{
		long currentMillis = System.currentTimeMillis(); // 우리나라는 UTC + 9시간

		long exceedTodayMillis = currentMillis % 86400000 ;
		long todayMillis = currentMillis - exceedTodayMillis; //그리니치 기준 오늘의 00시
		
		long candidate = todayMillis + (3600000 * 15) + (hour * 3600000);
		if(candidate<=currentMillis) return candidate + 86400000;
		return candidate;
	}
	
	
}
